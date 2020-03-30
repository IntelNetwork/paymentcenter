package org.smartwork.api;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.forbes.comm.service.KafkaProducers;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.utils.SpringContextUtils;
import org.forbes.pay.comm.channel.wechat.WxPayProperties;
import org.forbes.pay.comm.channel.wechat.WxPayUtil;
import org.forbes.pay.comm.context.PayContext;
import org.forbes.pay.comm.model.PayOrderDto;
import org.smartwork.comm.OrderTypeEnum;
import org.smartwork.constant.PayConstant;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.PayChannel;
import org.smartwork.dal.entity.PayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Api(tags = {"微信支付回调"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/pay-notify-wx")
public class NotifyWxPayProvider extends NotifyBasePay{


    @Autowired
    KafkaProducers kafkaProducers;

    /****
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @ApiOperation("微信回调")
    @RequestMapping(value = "/res",method = RequestMethod.POST)
    public String doWxPayRes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String logPrefix = "【微信支付回调通知】";
        log.info("====== 开始接收微信支付回调通知 ======");
        try {
            Map<String,Object> resultMap = new HashMap<>();
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            WxPayService wxPayService = new WxPayServiceImpl();
            WxPayOrderNotifyResult result = WxPayOrderNotifyResult.fromXML(xmlResult);
            // 验证业务数据是否正确,验证通过后返回PayOrder和WxPayConfig对象
            if(!verifyWxPayParams(result,resultMap)) {
                return WxPayNotifyResponse.fail((String) resultMap.get("retMsg"));
            }
            PayOrderDto payOrderDto = PayContext.TH_PAY_ORDER.get();
            WxPayConfig wxPayConfig = (WxPayConfig) resultMap.get("wxPayConfig");
            wxPayService.setConfig(wxPayConfig);
            // 这里做了签名校验(这里又做了一次xml转换对象,可以考虑优化)
            wxPayService.parseOrderNotifyResult(xmlResult);
            // 处理订单
            Integer payStatus = payOrderDto.getStatus(); // 0：订单生成，1：支付中，-1：支付失败，2：支付成功，3：业务处理完成，-2：订单过期
            if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                boolean updatePayOrderRows = payOrderService.update(new UpdateWrapper<PayOrder>()
                        .set("status",PayConstant.PAY_STATUS_SUCCESS)
                        .set("pay_succ_time",System.currentTimeMillis())
                        .eq("pay_order_id",payOrderDto.getPayOrderId())
                        .eq("status",PayConstant.PAY_STATUS_PAYING));
                if (!updatePayOrderRows) {
                    log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrderDto.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    return WxPayNotifyResponse.fail("处理订单失败");
                }
                log.error("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrderDto.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                payOrderDto.setStatus((int) PayConstant.PAY_STATUS_SUCCESS);
            }
            // 业务系统后端通知
            if(ConvertUtils.isNotEmpty(payOrderDto.getNotifyUrl())){
                notifyMch(OrderTypeEnum.PAY,payOrderDto.getNotifyUrl(),payOrderDto.getPayOrderId(),payOrderDto.getMchId(),payOrderDto.getMchOrderNo());
            }
            log.info("====== 完成接收微信支付回调通知 ======");
            return WxPayNotifyResponse.success("处理成功");
        } catch (WxPayException e) {
            //出现业务错误
            log.error("微信回调结果异常,异常原因",e);
            log.info("{}请求数据result_code=FAIL", logPrefix);
            log.info("err_code:", e.getErrCode());
            log.info("err_code_des:", e.getErrCodeDes());
            return WxPayNotifyResponse.fail(e.getMessage());
        } catch (Exception e) {
            log.error("微信回调结果异常,异常原因",e);
            return WxPayNotifyResponse.fail(e.getMessage());
        } finally {
            PayContext.TH_PAY_ORDER.remove();
        }
    }

    /**
     * 验证微信支付通知参数
     * @return
     */
    public boolean verifyWxPayParams(WxPayOrderNotifyResult notifyResult,Map<String,Object> resultMap) {
        //校验结果是否成功
        if (!PayConstant.RETURN_VALUE_SUCCESS.equalsIgnoreCase(notifyResult.getResultCode())
                && !PayConstant.RETURN_VALUE_SUCCESS.equalsIgnoreCase(notifyResult.getReturnCode())) {
            log.error("returnCode={},resultCode={},errCode={},errCodeDes={}", notifyResult.getReturnCode(), notifyResult.getResultCode(), notifyResult.getErrCode(), notifyResult.getErrCodeDes());
            resultMap.put("retMsg", "notify data failed");
            return false;
        }
        Integer total_fee = notifyResult.getTotalFee();   			// 总金额
        String out_trade_no = notifyResult.getOutTradeNo();			// 商户系统订单号
        // 查询payOrder记录
        String payOrderId = out_trade_no;
        PayOrder payOrder = payOrderService.getOne(new QueryWrapper<PayOrder>().eq("pay_order_id",payOrderId));
        if (payOrder==null) {
            log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            resultMap.put("retMsg", "Can't found payOrder");
            return false;
        }
        // 查询payChannel记录
        String mchId = notifyResult.getMchId();
        MchInfo mchInfo =  mchInfoService.getOne(new QueryWrapper<MchInfo>()
                .eq("mch_id",mchId));
        if(mchInfo == null) {
            log.error("Can't found mchInfo form db. mchId={} channelId={}, ", payOrderId, mchId);
            resultMap.put("retMsg", "Can't found mchInfo");
            return false;
        }
        String channelId = payOrder.getChannelId();
        PayChannel payChannel = payChannelService.getOne(new QueryWrapper<PayChannel>()
                .eq("mch_id",mchId)
                .eq("channel_id",channelId));
        if(payChannel == null) {
            log.error("Can't found payChannel form db. mchId={} channelId={}, ", payOrderId, mchId, channelId);
            resultMap.put("retMsg", "Can't found payChannel");
            return false;
        }
        String channelParam = payChannel.getParam();
        WxPayProperties wxPayProperties = SpringContextUtils.getBean(WxPayProperties.class);
        PayOrderDto payOrderDto = PayOrderDto.PayOrderDtoBuild
                .build()
                .setPayOrderId(payOrderId)
                .setPayChannelParam(channelParam)
                .setTradeType(notifyResult.getTradeType())
                .setMchId(mchId)
                .setMchOrderNo(payOrder.getMchOrderNo())
                .setStatus(payOrder.getStatus())
                .setMchKey(mchInfo.getResKey())
                .setNotifyUrl(payOrder.getNotifyUrl())
                .setWxPayProperties(wxPayProperties);
        resultMap.put("wxPayConfig", WxPayUtil.getWxPayConfig(payOrderDto.getMchId(),payOrderDto.getMchKey(),payOrderDto.getTradeType(),wxPayProperties,channelParam));
        PayContext.TH_PAY_ORDER.set(payOrderDto);
        // 核对金额
        long wxPayAmt = new BigDecimal(total_fee).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != wxPayAmt) {
            log.error("db payOrder record payPrice not equals total_fee. total_fee={},payOrderId={}", total_fee, payOrderId);
            resultMap.put("retMsg", "total_fee is not the same");
            return false;
        }
        resultMap.put("payOrder", payOrder);
        return true;
    }

}
