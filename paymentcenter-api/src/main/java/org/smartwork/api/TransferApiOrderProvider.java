package org.smartwork.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.vo.Result;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.PayChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"转账订单"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/transfer")
public class TransferApiOrderProvider {


    @Autowired
    IMchInfoService mhInfoService;

    /***
     *获取支付渠道
     * @return
     */
    @ApiOperation("获取支付渠道")
    @RequestMapping(value = "/pay-channels",method = RequestMethod.GET)
    public Result<Map<String,Object>> payChannels(){
        Result<Map<String,Object>>  resultMap = new Result<>();
        List<MchInfo> mchInfos = mhInfoService.list();
        Map<String,Object> payChannelMap = new HashMap<>();
        mchInfos.stream().forEach(mchInfo -> {
            if(MchStateEnum.ACTIVITY.getCode().equals(mchInfo.getState())){
                payChannelMap.put(mchInfo.getMchId(),mchInfo.getReflectPoints());
            }

        });
        resultMap.setResult(payChannelMap);
        return resultMap;
    }
}
