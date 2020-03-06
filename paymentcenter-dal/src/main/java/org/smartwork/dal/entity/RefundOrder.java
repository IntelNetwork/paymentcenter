package org.smartwork.dal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import org.forbes.comm.entity.BaseEntity;

/**
 * Table: fb_refund_order
 */
@Data
@ApiModel(description="退款订单")
@TableName("fb_refund_order")
public class RefundOrder extends BaseEntity {
    /**
     * 退款订单号
     *
     * Table:     fb_refund_order
     * Column:    refund_order_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "退款订单号",example="")
    private String refundOrderId;

    /**
     * 支付订单号
     *
     * Table:     fb_refund_order
     * Column:    pay_order_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "支付订单号",example="")
    private String payOrderId;

    /**
     * 渠道支付单号
     *
     * Table:     fb_refund_order
     * Column:    channel_pay_order_no
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道支付单号",example="")
    private String channelPayOrderNo;

    /**
     * 商户ID
     *
     * Table:     fb_refund_order
     * Column:    mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户ID",example="")
    private String mchId;

    /**
     * 商户退款单号
     *
     * Table:     fb_refund_order
     * Column:    mch_ref_und_no
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户退款单号",example="")
    private String mchRefUndNo;

    /**
     * 渠道ID
     *
     * Table:     fb_refund_order
     * Column:    channel_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道ID",example="")
    private String channelId;

    /**
     * 支付金额,单位分
     *
     * Table:     fb_refund_order
     * Column:    pay_amount
     * Nullable:  false
     */
    @ApiModelProperty(value = "支付金额,单位分",example="0")
    private Long payAmount;

    /**
     * 退款金额,单位分
     *
     * Table:     fb_refund_order
     * Column:    refund_amount
     * Nullable:  false
     */
    @ApiModelProperty(value = "退款金额,单位分",example="0")
    private Long refundAmount;

    /**
     * 三位货币代码,人民币:cny
     *
     * Table:     fb_refund_order
     * Column:    currency
     * Nullable:  false
     */
    @ApiModelProperty(value = "三位货币代码,人民币:cny",example="")
    private String currency;

    /**
     * 退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-业务处理完成
     *
     * Table:     fb_refund_order
     * Column:    status
     * Nullable:  false
     */
    @ApiModelProperty(value = "退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-业务处理完成",example="")
    private Byte status;

    /**
     * 退款结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败
     *
     * Table:     fb_refund_order
     * Column:    result
     * Nullable:  false
     */
    @ApiModelProperty(value = "退款结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败",example="")
    private Byte result;

    /**
     * 客户端IP
     *
     * Table:     fb_refund_order
     * Column:    client_ip
     * Nullable:  true
     */
    @ApiModelProperty(value = "客户端IP",example="")
    private String clientIp;

    /**
     * 设备
     *
     * Table:     fb_refund_order
     * Column:    device
     * Nullable:  true
     */
    @ApiModelProperty(value = "设备",example="")
    private String device;

    /**
     * 备注
     *
     * Table:     fb_refund_order
     * Column:    remark_info
     * Nullable:  true
     */
    @ApiModelProperty(value = "备注",example="")
    private String remarkInfo;

    /**
     * 渠道用户标识,如微信openId,支付宝账号
     *
     * Table:     fb_refund_order
     * Column:    channel_user
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道用户标识,如微信openId,支付宝账号",example="")
    private String channelUser;

    /**
     * 用户姓名
     *
     * Table:     fb_refund_order
     * Column:    user_name
     * Nullable:  true
     */
    @ApiModelProperty(value = "用户姓名",example="")
    private String userName;

    /**
     * 渠道商户ID
     *
     * Table:     fb_refund_order
     * Column:    channel_mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "渠道商户ID",example="")
    private String channelMchId;

    /**
     * 渠道订单号
     *
     * Table:     fb_refund_order
     * Column:    channel_order_no
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道订单号",example="")
    private String channelOrderNo;

    /**
     * 渠道错误码
     *
     * Table:     fb_refund_order
     * Column:    channel_err_code
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道错误码",example="")
    private String channelErrCode;

    /**
     * 渠道错误描述
     *
     * Table:     fb_refund_order
     * Column:    channel_err_msg
     * Nullable:  true
     */
    @ApiModelProperty(value = "渠道错误描述",example="")
    private String channelErrMsg;

    /**
     * 特定渠道发起时额外参数
     *
     * Table:     fb_refund_order
     * Column:    extra
     * Nullable:  true
     */
    @ApiModelProperty(value = "特定渠道发起时额外参数",example="")
    private String extra;

    /**
     * 通知地址
     *
     * Table:     fb_refund_order
     * Column:    notify_url
     * Nullable:  false
     */
    @ApiModelProperty(value = "通知地址",example="")
    private String notifyUrl;

    /**
     * 扩展参数1
     *
     * Table:     fb_refund_order
     * Column:    param1
     * Nullable:  true
     */
    @ApiModelProperty(value = "扩展参数1",example="")
    private String param1;

    /**
     * 扩展参数2
     *
     * Table:     fb_refund_order
     * Column:    param2
     * Nullable:  true
     */
    @ApiModelProperty(value = "扩展参数2",example="")
    private String param2;

    /**
     * 订单失效时间
     *
     * Table:     fb_refund_order
     * Column:    expire_time
     * Nullable:  true
     */
    @ApiModelProperty(value = "订单失效时间",example="")
    private Date expireTime;

    /**
     * 订单退款成功时间
     *
     * Table:     fb_refund_order
     * Column:    refund_succ_time
     * Nullable:  true
     */
    @ApiModelProperty(value = "订单退款成功时间",example="")
    private Date refundSuccTime;
}