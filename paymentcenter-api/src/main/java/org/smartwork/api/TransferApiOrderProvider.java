package org.smartwork.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.PayChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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
            objMap.put(mchInfo.getMchId(),mchInfo.getReflectPoints());
        }
        resultMap.setResult(objMap);
        return resultMap;
    }
}
