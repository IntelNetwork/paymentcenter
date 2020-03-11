package org.smartwork.dal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import org.forbes.comm.entity.BaseEntity;

/**
 * Table: fb_mch_notify
 */
@Data
@ApiModel(description="商户通知")
@Accessors(chain = true)
@TableName("fb_mch_notify")
public class MchNotify extends BaseEntity {
    /**
     * 订单ID
     *
     * Table:     fb_mch_notify
     * Column:    order_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "订单ID",example="")
    private String orderId;

    /**
     * 商户ID
     *
     * Table:     fb_mch_notify
     * Column:    mch_id
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户ID",example="")
    private String mchId;

    /**
     * 商户订单号
     *
     * Table:     fb_mch_notify
     * Column:    mch_order_no
     * Nullable:  false
     */
    @ApiModelProperty(value = "商户订单号",example="")
    private String mchOrderNo;

    /**
     * 订单类型:1-支付,2-转账,3-退款
     *
     * Table:     fb_mch_notify
     * Column:    order_type
     * Nullable:  false
     */
    @ApiModelProperty(value = "订单类型:1-支付,2-转账,3-退款",example="0")
    private String orderType;

    /**
     * 通知地址
     *
     * Table:     fb_mch_notify
     * Column:    notify_url
     * Nullable:  false
     */
    @ApiModelProperty(value = "通知地址",example="")
    private String notifyUrl;

    /**
     * 通知次数
     *
     * Table:     fb_mch_notify
     * Column:    notify_count
     * Nullable:  false
     */
    @ApiModelProperty(value = "通知次数",example="0")
    private Integer notifyCount;

    /**
     * 通知响应结果
     *
     * Table:     fb_mch_notify
     * Column:    result
     * Nullable:  true
     */
    @ApiModelProperty(value = "通知响应结果",example="")
    private String result;

    /**
     * 通知状态,1-通知中,2-通知成功,3-通知失败
     *
     * Table:     fb_mch_notify
     * Column:    status
     * Nullable:  false
     */
    @ApiModelProperty(value = "通知状态,1-通知中,2-通知成功,3-通知失败",example="0")
    private Integer status;

    /**
     * 最后一次通知时间
     *
     * Table:     fb_mch_notify
     * Column:    last_notify_time
     * Nullable:  true
     */
    @ApiModelProperty(value = "最后一次通知时间",example="")
    private Date lastNotifyTime;


    /***静态方法
     */
    public  static class  MchNotifyBuild{
        public static MchNotify build(){
            return new MchNotify();
        }
    }
}