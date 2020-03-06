package org.smartwork.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.forbes.comm.vo.Result;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.provider.BaseProvider;
import org.smartwork.biz.service.IMchInfoService;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.comm.MchTypeEnum;
import org.smartwork.dal.entity.MchInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}