package org.smartwork.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.forbes.comm.entity.BaseEntity;

/**
 * Table: fb_trans_order
 */
@Data
@ApiModel(description="转账订单")
@TableName("fb_trans_order")
public class TransOrder extends BaseEntity {
    /**
     * 转账订单号
     *
     * Table:     fb_trans_order
     * Column:    trans_order_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "转账订单号",example="")
    private String transOrderId;

    /**
     * 商户ID
     *
     * Table:     fb_trans_order
     * Column:    mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户ID",example="")
    private String mchId;

    /**
     * 商户转账单号
     *
     * Table:     fb_trans_order
     * Column:    mch_trans_no
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户转账单号",example="")
    private String mchTransNo;

    /**
     * 渠道ID
     *
     * Table:     fb_trans_order
     * Column:    channel_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道ID",example="")
    private String channelId;

    /**
     * 转账金额,单位分
     *
     * Table:     fb_trans_order
     * Column:    amount
     * Nullable:  false
     */
    @ApiModelProperty(value = "转账金额",example="0")
    private BigDecimal amount;

    /**
     * 三位货币代码,人民币:cny
     *
     * Table:     fb_trans_order
     * Column:    currency
     * Nullable:  false
     */
    @ApiModelProperty(value = "三位货币代码,人民币:cny",example="")
    private String currency;

    /**
     * 转账状态:0-订单生成,1-转账中,2-转账成功,3-转账失败,4-业务处理完成
     *
     * Table:     fb_trans_order
     * Column:    status
     * Nullable:  false
     */
    @ApiModelProperty(value = "转账状态:0-订单生成,1-转账中,2-转账成功,3-转账失败,4-业务处理完成",example="")
    private Integer status;

    /**
     * 转账结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败
     *
     * Table:     fb_trans_order
     * Column:    result
     * Nullable:  false
     */
    @ApiModelProperty(value = "转账结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败",example="")
    private Integer result;

    /**
     * 客户端IP
     *
     * Table:     fb_trans_order
     * Column:    client_ip
     * Nullable:  true
     */
    @ApiModelProperty(value = "客户端IP",example="")
    private String clientIp;

    /**
     * 设备
     *
     * Table:     fb_trans_order
     * Column:    device
     * Nullable:  true
     */
    @ApiModelProperty(value = "设备",example="")
    private String device;

    /**
     * 备注
     *
     * Table:     fb_trans_order
     * Column:    remark_info
     * Nullable:  true
     */
    @ApiModelProperty(value = "备注",example="")
    private String remarkInfo;

    /**
     * 渠道用户标识,如微信openId,支付宝账号
     *
     * Table:     fb_trans_order
     * Column:    channel_user
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道用户标识,如微信openId,支付宝账号",example="")
    private String channelUser;

    /**
     * 用户姓名
     *
     * Table:     fb_trans_order
     * Column:    user_name
     * Nullable:  true
     */
    @ApiModelProperty(value = "用户姓名",example="")
    private String userName;

    /**
     * 渠道商户ID
     *
     * Table:     fb_trans_order
     * Column:    channel_mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道商户ID",example="")
    private String channelMchId;

    /**
     * 渠道订单号
     *
     * Table:     fb_trans_order
     * Column:    channel_order_no
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道订单号",example="")
    private String channelOrderNo;

    /**
     * 渠道错误码
     *
     * Table:     fb_trans_order
     * Column:    channel_err_code
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道错误码",example="")
    private String channelErrCode;

    /**
     * 渠道错误描述
     *
     * Table:     fb_trans_order
     * Column:    channel_err_msg
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道错误描述",example="")
    private String channelErrMsg;

    /**
     * 特定渠道发起时额外参数
     *
     * Table:     fb_trans_order
     * Column:    extra
     * Nullable:  true
     */
    @ApiModelProperty(value = "特定渠道发起时额外参数",example="")
    private String extra;

    /**
     * 通知地址
     *
     * Table:     fb_trans_order
     * Column:    notify_url
     * Nullable:  false
     */
    @ApiModelProperty(value = "通知地址",example="")
    private String notifyUrl;

    /**
     * 扩展参数1
     *
     * Table:     fb_trans_order
     * Column:    param1
     * Nullable:  true
     */
    @ApiModelProperty(value = "扩展参数1",example="")
    private String param1;

    /**
     * 扩展参数2
     *
     * Table:     fb_trans_order
     * Column:    param2
     * Nullable:  true
     */
    @ApiModelProperty(value = "扩展参数2",example="")
    private String param2;

    /**
     * 订单失效时间
     *
     * Table:     fb_trans_order
     * Column:    expire_time
     * Nullable:  true
     */
    @ApiModelProperty(value = "订单失效时间",example="")
    private Date expireTime;

    /**
     * 订单转账成功时间
     *
     * Table:     fb_trans_order
     * Column:    trans_succ_time
     * Nullable:  true
     */
    @ApiModelProperty(value = "订单转账成功时间",example="")
    private Date transSuccTime;
}