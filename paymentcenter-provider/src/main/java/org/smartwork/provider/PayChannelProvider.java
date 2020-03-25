package org.smartwork.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.comm.*;
import org.smartwork.dal.entity.PayChannel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags={"支付渠道管理"})
@RestController
@RequestMapping("/paychannel")
public class PayChannelProvider extends BaseProvider<IPayChannelService, PayChannel> {


    /***
     *
     * @return
     */
    @ApiOperation("渠道分组")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/channes",method = RequestMethod.GET)
    public List<ResultEnum> channelName(){
        return ChannelNameEnum.resultEnums();
    }


    /***
     *
     * @return
     */
    @ApiOperation("所有渠道")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/pay-channes",method = RequestMethod.GET)
    public List<ResultEnum> PayChanne(){
        return PayChannelEnum.resultEnums();
    }

    /***
     *
     * @return
     */
    @ApiOperation("启用状态")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/channel-states",method = RequestMethod.GET)
    public List<ResultEnum> ChannelState(){
        return ChannelStateEnum.resultEnums();
    }

}