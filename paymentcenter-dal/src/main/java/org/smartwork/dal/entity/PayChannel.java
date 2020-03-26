package org.smartwork.dal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.forbes.comm.annotations.QueryColumn;
import org.forbes.comm.annotations.ValidEnum;
import org.forbes.comm.annotations.ValidUnique;
import org.forbes.comm.constant.SaveValid;
import org.forbes.comm.constant.UpdateValid;
import org.forbes.comm.entity.BaseEntity;
import org.smartwork.comm.ChannelNameEnum;
import org.smartwork.comm.MchStateEnum;
import org.smartwork.comm.PayChannelEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Table: fb_pay_channel
 */
@Data
@ApiModel(description="支付渠道")
@TableName("fb_pay_channel")
public class PayChannel extends BaseEntity {
    /**
     * 渠道ID
     *
     * Table:     fb_pay_channel
     * Column:    channel_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道ID",example="")
    @NotEmpty(message = "渠道ID为空",groups = {SaveValid.class, UpdateValid.class})
    @ValidEnum(bizCode = "007002002",bizErrorMsg = "%s支付渠道ID不存在",classzz = PayChannelEnum.class)
    @ValidUnique(column = "channel_id,channel_name,mch_id",bizCode = "007002003",bizErrorMsg = "商户已存在%s支付渠道")
    private String channelId;

    /**
     * 渠道名称,如:alipay,wechat
     *
     * Table:     fb_pay_channel
     * Column:    channel_name
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道名称,如:alipay,wechat",example="")
    @NotEmpty(message = "渠道名称为空",groups = {SaveValid.class, UpdateValid.class})
    @ValidEnum(bizCode = "007002001",bizErrorMsg = "%s支付渠道不存在", classzz = ChannelNameEnum.class)
    @TableField(value = "channel_name")
    @QueryColumn(column = "channel_name",sqlKeyword = SqlKeyword.LIKE)
    private String channelName;

    /**
     * 渠道商户ID
     *
     * Table:     fb_pay_channel
     * Column:    channel_mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道商户ID",example="")
    @NotEmpty(message = "渠道商户ID为空",groups = {SaveValid.class, UpdateValid.class})
    private String channelMchId;

    /**
     * 商户ID
     *
     * Table:     fb_pay_channel
     * Column:    mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户ID",example="")
    @NotEmpty(message = "商户ID为空",groups = {SaveValid.class, UpdateValid.class})
    @TableField(value = "mch_id")
    private String mchId;

    /**
     * 渠道状态,0-停止使用,1-使用中
     *
     * Table:     fb_pay_channel
     * Column:    state
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道状态,0-停止使用,1-使用中",example="0")
    @NotNull(message = "状态为空",groups = {SaveValid.class, UpdateValid.class})
    @ValidEnum(bizCode = "007001002",bizErrorMsg = "%s渠道状态不存在",classzz = MchStateEnum.class)
    private Integer state;


    private String operType;

    /**
     * 配置参数,json字符串
     *
     * Table:     fb_pay_channel
     * Column:    param
     * Nullable:  false
     */
    @ApiModelProperty(value = "配置参数,json字符串",example="")
    @NotEmpty(message = "配置参数为空",groups = {SaveValid.class, UpdateValid.class})
    private String param;

    /**
     * 备注
     *
     * Table:     fb_pay_channel
     * Column:    remark
     * Nullable:  true
     */
    @ApiModelProperty(value = "备注",example="")
    private String remark;
}