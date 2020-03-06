package org.smartwork.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.forbes.comm.annotations.ValidEnum;
import org.forbes.comm.annotations.ValidUnique;
import org.forbes.comm.constant.SaveValid;
import org.forbes.comm.constant.UpdateValid;
import org.forbes.comm.entity.BaseEntity;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.comm.MchTypeEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Table: fb_mch_info
 */
@Data
@ApiModel(description="商户信息")
@TableName("fb_mch_info")
public class MchInfo extends BaseEntity {
    /**
     * 商户ID
     *
     * Table:     fb_mch_info
     * Column:    mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户ID",example="")
    @NotEmpty(message = "商户ID为空",groups = {SaveValid.class, UpdateValid.class})
    @ValidUnique(column = "mch_id",bizCode = "007001003",bizErrorMsg = "%s商户ID已存在")
    private String mchId;

    /**
     * 名称
     *
     * Table:     fb_mch_info
     * Column:    name
     * Nullable:  false
     */
    @ApiModelProperty(value = "名称",example="")
    @NotEmpty(message = "商户名称为空",groups = {SaveValid.class, UpdateValid.class})
    private String name;

    /**
     * 类型
     *
     * Table:     fb_mch_info
     * Column:    type
     * Nullable:  false
     */
    @ApiModelProperty(value = "类型",example="")
    @NotEmpty(message = "商户类型为空",groups = {SaveValid.class, UpdateValid.class})
    @ValidEnum(bizCode = "007001001",bizErrorMsg = "%s商户类型不存在",classzz = MchTypeEnum.class)
    private String type;

    /**
     * 请求私钥
     *
     * Table:     fb_mch_info
     * Column:    req_key
     * Nullable:  false
     */
    @ApiModelProperty(value = "请求私钥",example="")
    @NotEmpty(message = "请求私钥为空",groups = {SaveValid.class, UpdateValid.class})
    private String reqKey;

    /**
     * 响应私钥
     *
     * Table:     fb_mch_info
     * Column:    res_key
     * Nullable:  false
     */
    @ApiModelProperty(value = "响应私钥",example="")
    @NotEmpty(message = "响应私钥为空",groups = {SaveValid.class, UpdateValid.class})
    private String resKey;

    /**
     * 商户状态,0-停止使用,1-使用中
     *
     * Table:     fb_mch_info
     * Column:    state
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户状态,0-停止使用,1-使用中",example="")
    @NotNull(message = "商户状态为空",groups = {SaveValid.class, UpdateValid.class})
    @ValidEnum(bizCode = "007001002",bizErrorMsg = "%s商户状态不存在",classzz = MchStateEnum.class)
    private Integer state;
}