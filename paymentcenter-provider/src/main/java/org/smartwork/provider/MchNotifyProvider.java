package org.smartwork.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IMchNotifyService;
import org.smartwork.comm.NotifyStatusEnum;
import org.smartwork.comm.OrderTypeEnum;
import org.smartwork.comm.PayEnum;
import org.smartwork.dal.entity.MchNotify;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags={"商户通知管理"})
@RestController
@RequestMapping("/mchnotify")
public class MchNotifyProvider extends BaseProvider<IMchNotifyService, MchNotify> {



    /***
     *
     * @return
     */
    @ApiOperation("支付订单类型")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/order-type",method = RequestMethod.GET)
    public List<ResultEnum> orderType(){
        return OrderTypeEnum.resultEnums();
    }


    /***
     *
     * @return
     */
    @ApiOperation("通知状态")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/notify-status",method = RequestMethod.GET)
    public List<ResultEnum> notifyStatus(){
        return NotifyStatusEnum.resultEnums();
    }


}