package org.smartwork.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.service.KafkaProducers;
import org.forbes.pay.comm.context.PayContext;
import org.forbes.pay.comm.model.PayOrderDto;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.biz.service.IPayOrderService;
import org.smartwork.comm.NotifyStatusEnum;
import org.smartwork.comm.OrderTypeEnum;
import org.smartwork.constant.PayConstant;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.MchNotify;
import org.smartwork.util.PayDigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.SuccessCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
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
    @Autowired
    IMchNotifyService mchNotifyService;
    @Autowired
    KafkaProducers kafkaProducers;

    /***
     * 组装回调参数
     * @param payOrder
     * @return
     */
    private Map<String, Object> createNotifyUrl(PayOrderDto payOrder) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrderId", payOrder.getPayOrderId() == null ? "" : payOrder.getPayOrderId());           // 支付订单号
        paramMap.put("mchId", payOrder.getMchId() == null ? "" : payOrder.getMchId());                      	// 商户ID
        paramMap.put("mchOrderNo", payOrder.getMchOrderNo() == null ? "" : payOrder.getMchOrderNo());       	// 商户订单号
        return paramMap;
    }


    /**通知商家
     */
    public  void notifyMch(OrderTypeEnum orderType){
        PayOrderDto payOrderDto = PayContext.TH_PAY_ORDER.get();
        Map<String,Object> sendData = this.createNotifyUrl(payOrderDto);
        String notifyUrl =  payOrderDto.getNotifyUrl();
        kafkaProducers.msgProducer(notifyUrl, JSON.toJSONString(sendData),o ->{
            MchNotify mchNotify =  MchNotify.MchNotifyBuild.build()
                    .setOrderId(payOrderDto.getPayOrderId())
                    .setMchId(payOrderDto.getMchId())
                    .setMchOrderNo(payOrderDto.getMchOrderNo())
                    .setOrderType(orderType.getCode())
                    .setNotifyUrl(notifyUrl)
                    .setNotifyCount(1)
                    .setStatus(Integer.parseInt(NotifyStatusEnum.IN_NOTICE.getCode()))
                    .setLastNotifyTime(new Date());
            mchNotifyService.save(mchNotify);
        },t ->{
            MchNotify mchNotify =  MchNotify.MchNotifyBuild.build()
                    .setOrderId(payOrderDto.getPayOrderId())
                    .setMchId(payOrderDto.getMchId())
                    .setMchOrderNo(payOrderDto.getMchOrderNo())
                    .setOrderType(orderType.getCode())
                    .setNotifyUrl(notifyUrl)
                    .setNotifyCount(1)
                    .setStatus(Integer.parseInt(NotifyStatusEnum.FAILURE.getCode()))
                    .setLastNotifyTime(new Date());
            mchNotifyService.save(mchNotify);
        });
    }

}
