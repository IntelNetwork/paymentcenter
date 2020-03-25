package org.smartwork.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.utils.SpringContextUtils;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.pay.PayPlugService;
import org.forbes.pay.comm.channel.wechat.WxPayProperties;
import org.forbes.pay.comm.enums.PayServiceEnum;
import org.forbes.pay.comm.model.PayOrderDto;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.biz.service.IPayOrderService;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.comm.PayChannelEnum;
import org.smartwork.constant.PayConstant;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.PayChannel;
import org.smartwork.dal.entity.PayOrder;
import org.smartwork.model.PayDto;
import org.smartwork.util.PayDigestUtil;
import org.smartwork.util.PaySeq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Api(tags = {"支付订单创建"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/pay")
public class PayApiOrderProvider {

    // 服务注册
    @Autowired
    private Registration registration;
    // 服务发现客户端
    @Autowired
    private DiscoveryClient client;
    @Autowired
    IMchInfoService mhInfoService;
    @Autowired
    IPayChannelService payChannelService;
    @Autowired
    IPayOrderService   payOrderService;


    /***
     * 获取支付渠道说明
     * @return
     */
    @ApiOperation("获取支付渠道说明")
    @RequestMapping(value = "/channels",method = RequestMethod.GET)
    public Result<List<ResultEnum>> channels(){
        Result<List<ResultEnum>> result = new Result<>();
        result.setResult(PayChannelEnum.resultEnums());
        return result;
    }


    /***
     *获取支付渠道
     * @return
     */
    @ApiOperation("获取支付渠道")
    @RequestMapping(value = "/pay-channels",method = RequestMethod.GET)
    public Result<Map<String,String>> payChannels(){
        Result<Map<String,String>>  resultMap = new Result<>();
        List<PayChannel> payChannels = payChannelService.list();
        Map<String,String> payChannelMap = new HashMap<>();
        payChannels.stream().forEach(payChannel -> {
            if(MchStateEnum.ACTIVITY.getCode().equals(payChannel.getState())){
                payChannelMap.put(payChannel.getChannelId(),payChannel.getMchId());
            }

        });
        resultMap.setResult(payChannelMap);
         return resultMap;
    }

    /***
     *
     * @param request
     * @returncd
     */
    public  String getIPAddress(HttpServletRequest request) {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }
        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }
        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }

    /***
     *
     * @return
     */
    private ServiceInstance  receServiceInstance(){
        List<ServiceInstance> instances =  client.getInstances(registration.getServiceId());
        Optional<ServiceInstance> optServiceInstance = instances.stream().filter(tinstance -> 8867 == tinstance.getPort()).findFirst();
        if(optServiceInstance.isPresent()){
            return optServiceInstance.get();
        }
        return null;
    }


    /**
     * 统一下单接口:
     * 1)先验证接口参数以及签名信息
     * 2)验证通过创建支付订单
     * 3)根据商户选择渠道,调用支付服务进行下单
     * 4)返回下单数据
     * @return
     */
    @ApiOperation("创建支付订单")
    @RequestMapping(value = "/create-order",method = RequestMethod.POST)
    public Result payOrder(@RequestBody PayDto payDto,HttpServletRequest request) {
        String logPrefix = "【商户统一下单】";
        String clientIp = this.getIPAddress(request);
        ServiceInstance instance = receServiceInstance();
        log.info("{}/pay/create_order, host:{}, service_id:{}, params:{}", logPrefix, instance.getHost(), instance.getServiceId(), payDto.toString());
        Result<Map<String,Object>> result = new Result<Map<String,Object> >();
        try {
            payDto.validateExtra(result);
            if("0000".equalsIgnoreCase(result.getBizCode())){
                MchInfo mchInfo =  mhInfoService.getOne(new QueryWrapper<MchInfo>()
                        .eq("mch_id",payDto.getMchId()));
                if(ConvertUtils.isEmpty(mchInfo)){
                    result.setBizCode(PayBizResultEnum.MCH_ID_NOT_EXISTS.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.MCH_ID_NOT_EXISTS.getBizFormateMessage(),payDto.getMchId()));
                    return result;
                }
                if(MchStateEnum.STOP.getCode().equals(mchInfo.getState())){
                    result.setBizCode(PayBizResultEnum.MCH_ID_STOP.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.MCH_ID_STOP.getBizFormateMessage(),payDto.getMchId()));
                    return result;
                }
                /***验证渠道*/
                PayChannel payChannel = payChannelService.getOne(new QueryWrapper<PayChannel>()
                        .eq("mch_id",payDto.getMchId())
                        .eq("channel_id",payDto.getChannelId()));
                if(ConvertUtils.isEmpty(payChannel)){
                    result.setBizCode(PayBizResultEnum.CHANNEL_ID_NOT_EXISTS.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.CHANNEL_ID_NOT_EXISTS.getBizFormateMessage(),payDto.getChannelId()));
                    return result;
                }
                if(MchStateEnum.STOP.getCode().equals(payChannel.getState())){
                    result.setBizCode(PayBizResultEnum.CHANNEL_ID_STOP.getBizCode());
                    result.setBizCode(String.format(PayBizResultEnum.CHANNEL_ID_STOP.getBizFormateMessage(),payDto.getMchId()));
                    return result;
                }
                String reqKey = mchInfo.getReqKey();
                String reqJson = JSON.toJSONString(payDto);
                payDto.setSign(PayDigestUtil.getSign(JSON.parseObject(reqJson,Map.class), reqKey));
                payDto.verifyPaySign(result,reqKey);
                if("0000".equalsIgnoreCase(result.getBizCode())){
                    payDto.setReqKey(reqKey);
                    String extraStr = JSON.toJSONString(payDto.getExtra());
                    PayOrder payOrder = new PayOrder();
                    payOrder.setPayOrderId(PaySeq.getPay());
                    payOrder.setMchId(payDto.getMchId());
                    payOrder.setMchOrderNo(payDto.getMchOrderNo());
                    payOrder.setChannelId(payDto.getChannelId());
                    payOrder.setAmount(Long.parseLong(payDto.getAmount()));
                    payOrder.setCurrency(payDto.getCurrency());
                    payOrder.setClientIp(clientIp);
                    payOrder.setDevice(payDto.getDevice());
                    payOrder.setSubject(payDto.getSubject());
                    payOrder.setStatus((int) PayConstant.PAY_STATUS_INIT);
                    payOrder.setBody(payDto.getBody());
                    payOrder.setExtra(extraStr);
                    payOrder.setParam1(payDto.getParam1());
                    payOrder.setParam2(payDto.getParam2());
                    payOrder.setNotifyUrl(payDto.getNotifyUrl());
                    payOrder.setChannelMchId(payChannel.getChannelMchId());
                    payOrderService.save(payOrder);
                    PayOrderDto payOrderDto = PayOrderDto.PayOrderDtoBuild
                            .build()
                            .setPayOrderId(payOrder.getPayOrderId())
                            .setMchId(payDto.getMchId())
                            .setMchOrderNo(payDto.getMchOrderNo())
                            .setChannelId(payDto.getChannelId())
                            .setAmount(new BigDecimal(payDto.getAmount()))
                            .setCurrency(payDto.getCurrency())
                            .setClientIp(clientIp)
                            .setDevice(payDto.getDevice())
                            .setParam1(payDto.getParam1())
                            .setParam2(payDto.getParam2())
                            .setNotifyUrl(payDto.getNotifyUrl())
                            .setSubject(payDto.getSubject())
                            .setPayChannelParam(payChannel.getParam())
                            .setExtra(payDto.getExtra())
                            .setBody(payDto.getBody());
                    String channelParam = payChannel.getParam();
                    JSONObject channelParamObj = JSON.parseObject(channelParam);
                    String serviceNname = PayServiceEnum.receServcieName(payDto.getChannelId());
                    PayPlugService payPlugService = (PayPlugService) SpringContextUtils.getBean(serviceNname);
                    WxPayProperties wxPayProperties = SpringContextUtils.getBean(WxPayProperties.class);
                    if(ConvertUtils.isNotEmpty(wxPayProperties)){
                        payOrderDto.setWxPayProperties(wxPayProperties);
                    }
                    payPlugService.beforePay(payOrderDto.setMchKey(mchInfo.getResKey())
                            .setTradeType(PayChannelEnum.receTradeType(payDto.getChannelId())));
                     Map<String,Object> reponseMap = payPlugService.payReq(null,((payOrderId, payOrderNo) -> {
                        boolean updatePayOrderRows = payOrderService.update(new UpdateWrapper<PayOrder>()
                                .set("status", PayConstant.PAY_STATUS_PAYING)
                                .set("pay_succ_time",System.currentTimeMillis())
                                .set("channel_order_no",payOrderNo)
                                .eq("pay_order_id",payOrderId)
                                .eq("status",PayConstant.PAY_STATUS_INIT));
                        return  updatePayOrderRows;
                    }));
                    payPlugService.afterPay();
                    result.setResult(reponseMap);
                }
            }
        }catch (Exception e) {
            result.error500(e.getMessage());
            log.error("====下单异常===========",e);
        }
        return result;
    }
}
