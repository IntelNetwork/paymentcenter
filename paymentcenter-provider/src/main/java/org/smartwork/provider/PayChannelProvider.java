package org.smartwork.provider;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.forbes.comm.enums.BizResultEnum;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IPayChannelService;
import org.smartwork.comm.*;
import org.smartwork.dal.entity.MchInfo;
import org.smartwork.dal.entity.PayChannel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags={"支付渠道管理"})
@RestController
@RequestMapping("/paychannel")
public class PayChannelProvider extends BaseProvider<IPayChannelService, PayChannel> {


    /***
     * 支付操作类型
     * @return
     */
    @ApiOperation("渠道分组")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/pay-oper-types",method = RequestMethod.GET)
    public List<ResultEnum> payOperTypes(){
        return PayOperTypeEnum.resultEnums();
    }



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

    /***
     *
     * @param id
     * @param state
     * @return
     */
    @ApiOperation("改变发布状态")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/pay-channel-state",method = RequestMethod.PUT)
    public Result<PayChannel> changePayState(@PathVariable Long id, @RequestParam(value = "state") Integer state){
        PayChannel payChannel = baseService.getById(id);
        Result<PayChannel> result = new Result<PayChannel>();
        if(ConvertUtils.isEmpty(payChannel)){
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        if(!ChannelStateEnum.existsCode(state)){
            result.setBizCode(PayBizResultEnum.CHANNEL_NAME_NOT_EXISTS.getBizCode());
            result.setMessage(String.format(PayBizResultEnum.CHANNEL_NAME_NOT_EXISTS.getBizFormateMessage(),state));
            return result;
        }
        // 发布
        if(ChannelStateEnum.ACTIVITY.getCode().equals(state)
                && !ChannelStateEnum.STOP.getCode().equals(payChannel.getState())){
            result.setBizCode(PayBizResultEnum.PAY_RELEASE_OR_CANCELLED.getBizCode());
            result.setMessage(String.format(PayBizResultEnum.PAY_RELEASE_OR_CANCELLED.getBizFormateMessage(),state));
            return result;
        }
        baseService.update(new UpdateWrapper<PayChannel>()
                .set("state",state)
                .eq("id",payChannel.getId()));
        return result;
    }

}