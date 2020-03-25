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
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.comm.MchTypeEnum;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.dal.entity.MchInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags={"商户信息管理"})
@RestController
@RequestMapping("/mchinfo")
public class MchInfoProvider extends BaseProvider<IMchInfoService, MchInfo> {


    /***
     *
     * @return
     */
    @ApiOperation("商家类型")
    @ApiResponses(value = {
            @ApiResponse(code=200,message = Result.COMM_ACTION_MSG),
            @ApiResponse(code=500,message = Result.COMM_ACTION_ERROR_MSG)
    })
    @RequestMapping(value = "/mch-types",method = RequestMethod.GET)
    public List<ResultEnum> mchTypes(){
        return MchTypeEnum.resultEnums();
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
    @RequestMapping(value = "/mch-states",method = RequestMethod.GET)
    public List<ResultEnum> mchState(){
        return MchStateEnum.resultEnums();
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
    @RequestMapping(value = "/mch-state",method = RequestMethod.PUT)
    public Result<MchInfo> changeMchState(@PathVariable Long id, @RequestParam(value = "state") Integer state){
        MchInfo mchInfo = baseService.getById(id);
        Result<MchInfo> result = new Result<MchInfo>();
        if(ConvertUtils.isEmpty(mchInfo)){
            result.setBizCode(BizResultEnum.ENTITY_EMPTY.getBizCode());
            result.setMessage(BizResultEnum.ENTITY_EMPTY.getBizMessage());
            return result;
        }
        if(!MchStateEnum.existsCode(state)){
            result.setBizCode(PayBizResultEnum.MCH_STATE_NOT_EXISTS.getBizCode());
            result.setMessage(String.format(PayBizResultEnum.MCH_STATE_NOT_EXISTS.getBizFormateMessage(),state));
            return result;
        }
        // 发布
        if(MchStateEnum.ACTIVITY.getCode().equals(state)
                && !MchStateEnum.STOP.getCode().equals(mchInfo.getState())){
            result.setBizCode(PayBizResultEnum.MCH_RELEASE_OR_CANCELLED.getBizCode());
            result.setMessage(String.format(PayBizResultEnum.MCH_RELEASE_OR_CANCELLED.getBizFormateMessage(),state));
            return result;
        }
        baseService.update(new UpdateWrapper<MchInfo>()
                .set("state",state)
                .eq("id",mchInfo.getId()));
        return result;
    }

}