package org.smartwork.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IPayOrderService;
import org.smartwork.comm.ChannelNameEnum;
import org.smartwork.comm.PayEnum;
import org.smartwork.comm.RetEnum;
import org.smartwork.comm.RpcSignTypeEnum;
import org.smartwork.dal.entity.PayOrder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags={"支付订单管理"})
@RestController
@RequestMapping("/payorder")
public class PayOrderProvider extends BaseProvider<IPayOrderService, PayOrder> {

    /***
     *
     * @return
     */
    @ApiOperation("支付返回码")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/channes",method = RequestMethod.GET)
    public List<ResultEnum> channelName(){
        return PayEnum.resultEnums();
    }

    /***
     * 调用错误码
     * @return
     */
    @ApiOperation("调用错误码")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/rets",method = RequestMethod.GET)
    public List<ResultEnum> ret(){
        return RetEnum.resultEnums();
    }


    /***
     * 通讯层签名
     * @return
     */
    @ApiOperation("通讯层签名")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/rpc-sign-types",method = RequestMethod.GET)
    public List<ResultEnum> rpcSignType(){
        return RpcSignTypeEnum.resultEnums();
    }
}