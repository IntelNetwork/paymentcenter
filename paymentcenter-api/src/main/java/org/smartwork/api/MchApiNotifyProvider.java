package org.smartwork.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.constant.UpdateValid;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.comm.NotifyStatusEnum;
import org.smartwork.dal.entity.MchNotify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api(tags = {"商户异步通知接口"})
@Slf4j
@RestController
@RequestMapping("${smartwork.verision}/mch-notify")
public class MchApiNotifyProvider {


    @Autowired
    IMchNotifyService mchNotifyService;

    /***
     * 执行成功通知
     * @param orderId
     * @return
     */
    @ApiOperation("执行成功通知")
    @ApiImplicitParam(name = "orderId",value="订单ID")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/notify-success",method = RequestMethod.PUT)
    public  Result<Object>  notifySuccess(@RequestParam(value = "orderId",required = true)String orderId){
        log.debug("传入的参数为" + orderId);
        Result<Object> result=new Result<Object>();
        try {
            MchNotify mchNotify = mchNotifyService.getOne(new QueryWrapper<MchNotify>().eq("order_id",orderId));
            if(ConvertUtils.isEmpty(mchNotify)){
                result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
                result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
                return result;
            }
            mchNotifyService.update(new UpdateWrapper<MchNotify>()
                    .set("status", NotifyStatusEnum.SUCCESS.getCode())
                    .set("last_notify_time",new Date())
                    .eq("order_id",orderId));
            result.setResult(mchNotify);
        } catch (Exception e) {
            result.error500(e.getMessage());
        }
        return result;
    }
}
