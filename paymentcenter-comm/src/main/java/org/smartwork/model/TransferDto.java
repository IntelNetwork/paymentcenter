package org.smartwork.model;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.forbes.comm.vo.Result;
import org.forbes.pay.comm.model.PayeeInfo;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.util.PayUtil;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(description = "转账请求对象")
public class TransferDto implements Serializable {


    private static final long serialVersionUID = -569719186361363071L;


    @ApiModelProperty(value = "商户ID")
    private String mchId;


    @ApiModelProperty(value = "币种")
    private  String currency;


    @ApiModelProperty(value = "设备")
    private  String device;

    @ApiModelProperty(value = "产品编码")
    private String productCode;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "商户订单号")
    private String outBizNo;


    @ApiModelProperty(value = "订单总金额，单位为元，精确到小数点后两位，")
    private String transAmount;

    @ApiModelProperty(value = "描述特定的业务场景")
    private String bizScene;

    @ApiModelProperty(value = "转账业务的标题")
    private  String orderTitle;

    @ApiModelProperty(value = "收款方信息")
    public PayeeInfo payeeInfo;


    @ApiModelProperty(value = "签名")
    private  String sign;

    /***
     *  验证签名
     * @param result
     * @return
     */
    public Result verifyPaySign(Result result, String reqtKey){
        String reqJson = JSON.toJSONString(this);
        // 验证签名数据
        boolean verifyFlag = PayUtil.verifyPaySign(JSON.parseObject(reqJson, Map.class), reqtKey);
        if(!verifyFlag) {
            result.setBizCode(PayBizResultEnum.SIGN_ERROR.getBizCode());
            result.setMessage(PayBizResultEnum.SIGN_ERROR.getBizMessage());
            return result;
        }
        return  result;
    }
}
