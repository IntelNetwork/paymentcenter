package org.smartwork.api;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.service.KafkaProducers;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.utils.IpAddressUtil;
import org.forbes.comm.utils.SpringContextUtils;
import org.forbes.comm.vo.Result;
import org.forbes.pay.comm.channel.wechat.WxPayProperties;
import org.forbes.pay.comm.enums.BizSceneEnum;
import org.forbes.pay.comm.enums.PayServiceEnum;
import org.forbes.pay.comm.model.TransferOrderDto;
import org.forbes.transfer.TransferPlugService;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.biz.service.ITransOrderService;
import org.smartwork.comm.*;
import org.smartwork.constant.PayConstant;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.MchNotify;
import org.smartwork.dal.entity.PayChannel;
import org.smartwork.dal.entity.TransOrder;
import org.smartwork.model.TransferDto;
import org.smartwork.util.PayDigestUtil;
import org.smartwork.util.PaySeq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"转账订单"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/transfer")
public class TransferApiOrderProvider extends NotifyBasePay {


    @Autowired
    IMchInfoService mhInfoService;
    @Autowired
    IPayChannelService payChannelService;
    @Autowired
    ITransOrderService transOrderService;
    @Autowired
    IMchNotifyService mchNotifyService;

    /***
     *获取结算方式
     * @return
     */
    @ApiOperation("获取结算方式")
    @RequestMapping(value = "/settl-channels",method = RequestMethod.GET)
    public Result<Map<String,Object>> payChannels(){
        Result<Map<String,Object>>  resultMap = new Result<>();
        List<MchInfo> mchInfos = mhInfoService.list();
        Map<String,Object> payChannelMap = new HashMap<>();
        mchInfos.stream().forEach(mchInfo -> {
            if(MchStateEnum.ACTIVITY.getCode().equals(mchInfo.getState())){
                Map<String, BigDecimal> returnMap = Maps.newConcurrentMap();
                returnMap.put(mchInfo.getChannel(),mchInfo.getReflectPoints());
                payChannelMap.put(mchInfo.getMchId(),returnMap);
            }
        });
        resultMap.setResult(payChannelMap);
        return resultMap;
    }


    /**获取结算商户
     * @param channel
     * @return
     */
    @ApiOperation("获取结算商户")
    @RequestMapping(value = "/channel-mch-info",method = RequestMethod.GET)
    public Result<Map<String,Object>> channelMchInfo(@RequestParam(value = "channel",required = true)String channel){
        Result<Map<String,Object>>  resultMap = new Result<>();
        Map<String,Object> objMap = Maps.newHashMap();
        MchInfo mchInfo = mhInfoService.getOne(new QueryWrapper<MchInfo>().eq("channel",channel));
        if(ConvertUtils.isNotEmpty(mchInfo)){
            objMap.put("mchId",mchInfo.getMchId());
            objMap.put("reflectPoints",mchInfo.getReflectPoints());
        }
        resultMap.setResult(objMap);
        return resultMap;
    }


    /***
     * 创建转账订单
     * @param transferDto
     * @param request
     * @return
     */
    @ApiOperation("创建转账订单")
    @RequestMapping(value = "/create-order",method = RequestMethod.POST)
    public Result<Map<String,Object>> transferOrder(@RequestBody TransferDto transferDto, HttpServletRequest request) {
        String logPrefix = "【统一转账订单】";
        String clientIp = IpAddressUtil.getIPAddress(request);
        Result<Map<String,Object>> result = new Result<Map<String,Object> >();
        try {MchInfo mchInfo =  mhInfoService.getOne(new QueryWrapper<MchInfo>()
                        .eq("mch_id",transferDto.getMchId()));
                if(ConvertUtils.isEmpty(mchInfo)){
                    result.setBizCode(PayBizResultEnum.MCH_ID_NOT_EXISTS.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.MCH_ID_NOT_EXISTS.getBizFormateMessage(),transferDto.getMchId()));
                    return result;
                }
                if(MchStateEnum.STOP.getCode().equals(mchInfo.getState())){
                    result.setBizCode(PayBizResultEnum.MCH_ID_STOP.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.MCH_ID_STOP.getBizFormateMessage(),transferDto.getMchId()));
                    return result;
                }
                /***验证渠道*/
                String channelId = PayChannelEnum.receCode(transferDto.getProductCode());
                PayChannel payChannel = payChannelService.getOne(new QueryWrapper<PayChannel>()
                        .eq("mch_id",transferDto.getMchId())
                        .eq("channel_id", channelId));
                if(ConvertUtils.isEmpty(payChannel)){
                    result.setBizCode(PayBizResultEnum.CHANNEL_ID_NOT_EXISTS.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.CHANNEL_ID_NOT_EXISTS.getBizFormateMessage(),channelId));
                    return result;
                }
                if(MchStateEnum.STOP.getCode().equals(payChannel.getState())){
                    result.setBizCode(PayBizResultEnum.CHANNEL_ID_STOP.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.CHANNEL_ID_STOP.getBizFormateMessage(),transferDto.getMchId()));
                    return result;
                }
                String reqKey = mchInfo.getReqKey();
               String reqJson = JSON.toJSONString(transferDto);
               transferDto.setSign(PayDigestUtil.getSign(JSON.parseObject(reqJson,Map.class), reqKey));
                transferDto.verifyPaySign(result,reqKey);
                if("0000".equalsIgnoreCase(result.getBizCode())){
                    TransOrder transOrder = new TransOrder();
                    transOrder.setTransOrderId(PaySeq.getTrans());
                    transOrder.setMchId(transferDto.getMchId());
                    transOrder.setMchTransNo(transferDto.getOutBizNo());
                    transOrder.setChannelId(channelId);
                    transOrder.setAmount(new BigDecimal(transferDto.getTransAmount()));
                    transOrder.setCurrency(transferDto.getCurrency());
                    transOrder.setClientIp(clientIp);
                    transOrder.setDevice(transferDto.getDevice());
                    transOrder.setStatus((int) PayConstant.TRANS_STATUS_INIT);
                    transOrder.setExtra(payChannel.getParam());
                    transOrder.setNotifyUrl("");
                    transOrder.setChannelMchId(payChannel.getChannelMchId());
                    transOrderService.save(transOrder);
                    String serviceNname = PayServiceEnum.receServcieName(channelId);
                    TransferPlugService transferPlugService = (TransferPlugService) SpringContextUtils.getBean(serviceNname);
                    TransferOrderDto transferOrderDto = TransferOrderDto
                            .TransferOrderDtoBuild.build()
                            .setTransOrderId(transOrder.getTransOrderId())
                            .setMchId(mchInfo.getMchId())
                            .setMchKey(reqKey)
                            .setClientIp(clientIp)
                            .setDevice(transOrder.getDevice())
                            .setOutBizNo(transferDto.getOutBizNo())
                            .setTransAmount(transferDto.getTransAmount())
                            .setProductCode(transferDto.getProductCode())
                            .setBizScene(BizSceneEnum.receCode(transferDto.getProductCode()))
                            .setPayChannelParam(payChannel.getParam())
                            .setOrderTitle(transferDto.getOrderTitle())
                            .setPayeeInfo(transferDto.getPayeeInfo());
                    WxPayProperties wxPayProperties = SpringContextUtils.getBean(WxPayProperties.class);
                    if(ConvertUtils.isNotEmpty(wxPayProperties)){
                        transferOrderDto.setWxPayProperties(wxPayProperties);
                    }
                    transferPlugService.beforePay(transferOrderDto);
                    Map<String,Object> reponseMap = transferPlugService.transferReq(null,((payOrderId, payOrderNo) -> {
                        boolean isUpdate = transOrderService.update(new UpdateWrapper<TransOrder>()
                                .set("status",PayConstant.TRANS_STATUS_SUCCESS)
                                .set("trans_succ_time",new Date())
                                .set("channel_order_no",payOrderNo)
                                .eq("trans_order_id",payOrderId));
                        return  isUpdate;
                    }));
                    transferPlugService.afterPay();
                    /****异常***/
                    if(reponseMap.containsKey("errorCode")
                            && !"0000".equalsIgnoreCase(reponseMap.get("errorCode").toString())){
                        transOrderService.update(new UpdateWrapper<TransOrder>()
                                .set("status",PayConstant.TRANS_STATUS_FAIL)
                                .set("channel_err_code",reponseMap.get("errorCode").toString())
                                .set("channel_err_msg",reponseMap.get("errorCode").toString())
                                .eq("trans_order_id",transOrder.getTransOrderId()));
                        result.setBizCode(reponseMap.get("errorCode").toString());
                        result.setMessage(reponseMap.get("errorCode").toString());
                    }
                    /***异步通知***/
                    else {
                        super.notifyMch(OrderTypeEnum.TRANSFER,transferDto.getNotifyUrl(),reponseMap.get("payOrderId").toString(),transferDto.getMchId(),reponseMap.get("outBizNo").toString());
                    }
                    result.setResult(reponseMap);
                }
        }catch (Exception e) {
            result.error500(e.getMessage());
            log.error("====下单异常===========",e);
        }
        return result;
    }
}
