package org.smartwork.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.forbes.pay.comm.model.PayOrderDto;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.biz.service.IPayOrderService;
import org.smartwork.constant.PayConstant;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.util.PayDigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class NotifyBasePay {


    @Autowired
    IMchInfoService mchInfoService;
    @Autowired
    IPayOrderService payOrderService;
    @Autowired
    IPayChannelService payChannelService;

    /***
     * 组装回调参数
     * @param payOrder
     * @param backType
     * @return
     */
    public Map<String, Object> createNotifyUrl(PayOrderDto payOrder, String backType) {
        String mchId = payOrder.getMchId();
        MchInfo mchInfo = mchInfoService.getOne(new QueryWrapper<MchInfo>()
                .eq("mch_id",mchId));
        String resKey = mchInfo.getResKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrderId", payOrder.getPayOrderId() == null ? "" : payOrder.getPayOrderId());           // 支付订单号
        paramMap.put("mchId", payOrder.getMchId() == null ? "" : payOrder.getMchId());                      	// 商户ID
        paramMap.put("mchOrderNo", payOrder.getMchOrderNo() == null ? "" : payOrder.getMchOrderNo());       	// 商户订单号
        paramMap.put("channelId", payOrder.getChannelId() == null ? "" : payOrder.getChannelId());              // 渠道ID
        paramMap.put("amount", payOrder.getAmount() == null ? "" : payOrder.getAmount());                      	// 支付金额
        paramMap.put("currency", payOrder.getCurrency() == null ? "" : payOrder.getCurrency());                 // 货币类型
        paramMap.put("status", payOrder.getStatus() == null ? "" : payOrder.getStatus());               		// 支付状态
        paramMap.put("clientIp", payOrder.getClientIp()==null ? "" : payOrder.getClientIp());   				// 客户端IP
        paramMap.put("device", payOrder.getDevice()==null ? "" : payOrder.getDevice());               			// 设备
        paramMap.put("subject", payOrder.getSubject()==null ? "" : payOrder.getSubject());     	   				// 商品标题
        paramMap.put("channelOrderNo", payOrder.getChannelOrderNo()==null ? "" : payOrder.getChannelOrderNo()); // 渠道订单号
        paramMap.put("param1", payOrder.getParam1()==null ? "" : payOrder.getParam1());               		   	// 扩展参数1
        paramMap.put("param2", payOrder.getParam2()==null ? "" : payOrder.getParam2());               		   	// 扩展参数2
        paramMap.put("paySuccTime", payOrder.getPaySuccTime()==null ? "" : payOrder.getPaySuccTime());			// 支付成功时间
        paramMap.put("backType", backType==null ? "" : backType);
        // 先对原文签名
        String reqSign = PayDigestUtil.getSign(paramMap, resKey);
        paramMap.put("sign", reqSign);   // 签名
        // 签名后再对有中文参数编码
        try {
            paramMap.put("device", URLEncoder.encode(payOrder.getDevice()==null ? "" : payOrder.getDevice(), PayConstant.RESP_UTF8));
            paramMap.put("subject", URLEncoder.encode(payOrder.getSubject()==null ? "" : payOrder.getSubject(), PayConstant.RESP_UTF8));
            paramMap.put("param1", URLEncoder.encode(payOrder.getParam1()==null ? "" : payOrder.getParam1(), PayConstant.RESP_UTF8));
            paramMap.put("param2", URLEncoder.encode(payOrder.getParam2()==null ? "" : payOrder.getParam2(), PayConstant.RESP_UTF8));
        }catch (UnsupportedEncodingException e) {
            log.error("URL Encode exception.", e);
            return null;
        }
        return paramMap;
    }
}
