package org.smartwork.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.pay.comm.channel.alipay.AlipayConfig;
import org.forbes.pay.comm.context.PayContext;
import org.forbes.pay.comm.model.PayOrderDto;
import org.smartwork.comm.OrderTypeEnum;
import org.smartwork.constant.PayConstant;
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
import java.util.Iterator;
import java.util.Map;

@Api(tags = {"支付宝支付回调"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/pay-notify-ali")
public class NotifyAliPayProvider extends NotifyBasePay {

    @Autowired
    protected AlipayConfig alipayConfig;

    /***
     * 支付宝回调
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @ApiOperation("支付宝回调")
    @RequestMapping(value = "/res",method = RequestMethod.POST)
    public String doAliPayRes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String logPrefix = "【支付宝支付回调通知】";
            log.info("====== 开始接收支付宝支付回调通知 ======");
            Map<String,Object> resultMap = new HashMap<>();
            //获取支付宝POST过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            log.info("{}通知请求数据:reqStr={}", logPrefix, params);
            if(params.isEmpty()) {
                log.error("{}请求参数为空", logPrefix);
                return PayConstant.RETURN_ALIPAY_VALUE_FAIL;
            }
            if(!verifyAliPayParams(params,resultMap)) {
                return PayConstant.RETURN_ALIPAY_VALUE_FAIL;
            }
            PayOrderDto payOrderDto = PayContext.TH_PAY_ORDER.get();
            log.info("{}验证请求数据及签名通过", logPrefix);
            String trade_status = params.get("trade_status");		// 交易状态
            // 支付状态成功或者完成
            if (trade_status.equals(PayConstant.AlipayConstant.TRADE_STATUS_SUCCESS) ||
                    trade_status.equals(PayConstant.AlipayConstant.TRADE_STATUS_FINISHED)) {
                boolean updatePayOrderRows;
                Integer payStatus = payOrderDto.getStatus(); // 0：订单生成，1：支付中，-1：支付失败，2：支付成功，3：业务处理完成，-2：订单过期
                if (payStatus != PayConstant.PAY_STATUS_SUCCESS && payStatus != PayConstant.PAY_STATUS_COMPLETE) {
                    updatePayOrderRows = payOrderService.update(new UpdateWrapper<PayOrder>()
                            .set("status",PayConstant.PAY_STATUS_SUCCESS)
                            .set("pay_succ_time",System.currentTimeMillis())
                            .eq("pay_order_id",payOrderDto.getPayOrderId())
                            .eq("status",PayConstant.PAY_STATUS_PAYING));
                    if (!updatePayOrderRows) {
                        log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrderDto.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                        log.info("{}响应给支付宝结果：{}", logPrefix, PayConstant.RETURN_ALIPAY_VALUE_FAIL);
                        return PayConstant.RETURN_ALIPAY_VALUE_FAIL;
                    }
                    log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrderDto.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                    payOrderDto.setStatus((int) PayConstant.PAY_STATUS_SUCCESS);
                }
            }else{
                // 其他状态
                log.info("{}支付状态trade_status={},不做业务处理", logPrefix, trade_status);
                log.info("{}响应给支付宝结果：{}", logPrefix, PayConstant.RETURN_ALIPAY_VALUE_SUCCESS);
                return PayConstant.RETURN_ALIPAY_VALUE_SUCCESS;
            }
            // 业务系统后端通知
            if(ConvertUtils.isNotEmpty(payOrderDto.getNotifyUrl())){
                notifyMch(OrderTypeEnum.PAY);
            }
            log.info("====== 完成接收支付宝支付回调通知 ======");
            return PayConstant.RETURN_ALIPAY_VALUE_SUCCESS;
        }catch(Exception e){
            log.error("支付宝回调结果异常,异常原因",e);
            return PayConstant.RETURN_ALIPAY_VALUE_FAIL;
        } finally {
            PayContext.TH_PAY_ORDER.remove();
        }
    }

    /**
     * 验证支付宝支付通知参数
     * @return
     */
    public boolean verifyAliPayParams(Map<String,String> params,Map<String,Object> resultMap) {
        String out_trade_no = params.get("out_trade_no");		// 商户订单号
        String total_amount = params.get("total_amount"); 		// 支付金额
        if (ConvertUtils.isEmpty(out_trade_no)) {
            log.error("AliPay Notify parameter out_trade_no is empty. out_trade_no={}", out_trade_no);
            resultMap.put("retMsg", "out_trade_no is empty");
            return false;
        }
        if (ConvertUtils.isEmpty(total_amount)) {
            log.error("AliPay Notify parameter total_amount is empty. total_fee={}", total_amount);
            resultMap.put("retMsg", "total_amount is empty");
            return false;
        }
        String errorMessage;
        // 查询payOrder记录
        String payOrderId = out_trade_no;
        PayOrder payOrder = payOrderService.getOne(new QueryWrapper<PayOrder>().eq("pay_order_id",payOrderId));
        if (payOrder == null) {
            log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            resultMap.put("retMsg", "Can't found payOrder");
            return false;
        }
        // 查询payChannel记录
        String mchId = payOrder.getMchId();
        String channelId = payOrder.getChannelId();
        PayChannel payChannel = payChannelService.getOne(new QueryWrapper<PayChannel>()
                .eq("mch_id",mchId)
                .eq("channel_id",channelId));
        if(payChannel == null) {
            log.error("Can't found payChannel form db. mchId={} channelId={}, ", payOrderId, mchId, channelId);
            resultMap.put("retMsg", "Can't found payChannel");
            return false;
        }
        boolean verify_result = false;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig.init(payChannel.getParam())
                    .getAlipay_public_key(), AlipayConfig.CHARSET,
                    "RSA2");
        } catch (AlipayApiException e) {
            log.error("AlipaySignature.rsaCheckV1 error",e);
        }
        // 验证签名
        if (!verify_result) {
            errorMessage = "rsaCheckV1 failed.";
            log.error("AliPay Notify parameter {}", errorMessage);
            resultMap.put("retMsg", errorMessage);
            return false;
        }
        // 核对金额
        long aliPayAmt = new BigDecimal(total_amount).movePointRight(2).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != aliPayAmt) {
            log.error("db payOrder record payPrice not equals total_amount. total_amount={},payOrderId={}", total_amount, payOrderId);
            resultMap.put("retMsg", "");
            return false;
        }
        PayOrderDto payOrderDto = PayOrderDto.PayOrderDtoBuild
                .build()
                .setPayOrderId(payOrderId)
                .setPayChannelParam(payChannel.getParam())
                .setMchOrderNo(payOrder.getMchOrderNo())
                .setNotifyUrl(payOrder.getNotifyUrl())
                .setMchId(mchId)
                .setStatus(payOrder.getStatus());
        PayContext.TH_PAY_ORDER.set(payOrderDto);
        return true;
    }
}
