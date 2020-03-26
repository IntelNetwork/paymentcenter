package org.smartwork.api;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.service.KafkaProducers;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.biz.service.IPayOrderService;
import org.smartwork.comm.NotifyStatusEnum;
import org.smartwork.comm.OrderTypeEnum;
import org.smartwork.dal.entity.MchNotify;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
     * @param payOrderId
     * @param mchId
     * @param mchOrderNo
     * @return
     */
    private Map<String, Object> createNotifyUrl(String payOrderId,String mchId,String mchOrderNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrderId", payOrderId == null ? "" : payOrderId);           // 支付订单号
        paramMap.put("mchId", mchId == null ? "" : mchId);                      	// 商户ID
        paramMap.put("mchOrderNo", mchOrderNo == null ? "" :mchOrderNo);       	// 商户订单号
        return paramMap;
    }


    /**通知商家
     */
    public  void notifyMch(OrderTypeEnum orderType,String notifyUrl,String payOrderId,String mchId,String mchOrderNo){
        Map<String,Object> sendData = this.createNotifyUrl(payOrderId,mchId,mchOrderNo);
        kafkaProducers.msgProducer(notifyUrl, JSON.toJSONString(sendData),o ->{
            MchNotify mchNotify =  MchNotify.MchNotifyBuild.build()
                    .setOrderId(payOrderId)
                    .setMchId(mchId)
                    .setMchOrderNo(mchOrderNo)
                    .setOrderType(orderType.getCode())
                    .setNotifyUrl(notifyUrl)
                    .setNotifyCount(1)
                    .setStatus(Integer.parseInt(NotifyStatusEnum.IN_NOTICE.getCode()))
                    .setLastNotifyTime(new Date());
            mchNotifyService.save(mchNotify);
        },t ->{
            MchNotify mchNotify =  MchNotify.MchNotifyBuild.build()
                    .setOrderId(payOrderId)
                    .setMchId(mchId)
                    .setMchOrderNo(mchOrderNo)
                    .setOrderType(orderType.getCode())
                    .setNotifyUrl(notifyUrl)
                    .setNotifyCount(1)
                    .setStatus(Integer.parseInt(NotifyStatusEnum.FAILURE.getCode()))
                    .setLastNotifyTime(new Date());
            mchNotifyService.save(mchNotify);
        });
    }

}
