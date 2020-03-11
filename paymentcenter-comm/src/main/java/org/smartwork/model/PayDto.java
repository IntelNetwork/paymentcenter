package org.smartwork.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.forbes.pay.comm.model.Extra;
import org.smartwork.comm.PayBizResultEnum;
import org.smartwork.constant.PayConstant;
import org.smartwork.util.PayUtil;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(description = "支付请求对象")
public class PayDto implements Serializable {

    private static final long serialVersionUID = -7461754727345999037L;

    @ApiModelProperty(value = "商户ID")
    @NotEmpty(message = "商户ID为空")
    private  String mchId;

    @ApiModelProperty(value = "商户订单号")
    @NotEmpty(message = "商户订单为空")
    private  String mchOrderNo;

    @ApiModelProperty(value = "渠道ID")
    @NotEmpty(message = "渠道ID为空")
    private  String channelId;

    @ApiModelProperty(value = "支付金额（单位分）")
    @NotEmpty(message = "支付金额（单位分）为空")
    private  String amount;

    @ApiModelProperty(value = "币种")
    @NotEmpty(message = "币种为空")
    private  String currency;


    @ApiModelProperty(value = "客户端IP")
    private  String clientIp ;

    @ApiModelProperty(value = "设备")
    private  String device;

    @ApiModelProperty(value = "特定渠道发起时额外参数")
    private Extra extra;

    @ApiModelProperty(value = "扩展参数1")
    private  String param1;

    @ApiModelProperty(value = "扩展参数2")
    private  String param2;

    @ApiModelProperty(value = "支付结果回调主题")
    @NotEmpty(message = "支付结果回调主题为空")
    private  String notifyUrl;


    @ApiModelProperty(value = "签名")
    @NotEmpty(message = "签名为空")
    private  String sign;

    @ApiModelProperty(value = "商品主题")
    @NotEmpty(message = "商品主题为空")
    private  String subject;


    @ApiModelProperty(value = "商品描述信息")
    @NotEmpty(message = "商品描述信息为空")
    private  String body;

    @ApiModelProperty(value = "请求key")
    private String reqKey;


    /***
     * 判断extra参数
     * @param result
     * @return
     */
    public Result validateExtra(Result result){
        // 根据不同渠道,判断extra参数
        if(PayConstant.PAY_CHANNEL_WX_JSAPI.equalsIgnoreCase(channelId)) {
            if(ConvertUtils.isEmpty(extra)) {
                result.setMessage(PayBizResultEnum.EXTRA_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.EXTRA_EMPTY.getBizCode());
                return result;
            }
            if(ConvertUtils.isEmpty(extra.getOpenId())){
                result.setMessage(PayBizResultEnum.OPENID_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.OPENID_EMPTY.getBizCode());
                return result;
            }
        }else if(PayConstant.PAY_CHANNEL_WX_NATIVE.equalsIgnoreCase(channelId)) {
            if(ConvertUtils.isEmpty(extra)) {
                result.setMessage(PayBizResultEnum.EXTRA_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.EXTRA_EMPTY.getBizCode());
                return result;
            }
            if(ConvertUtils.isEmpty(extra.getProductId())){
                result.setMessage(PayBizResultEnum.PRODUCT_ID_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.PRODUCT_ID_EMPTY.getBizCode());
                return result;
            }
        }else if(PayConstant.PAY_CHANNEL_WX_MWEB.equalsIgnoreCase(channelId)) {
            if(ConvertUtils.isEmpty(extra)) {
                result.setMessage(PayBizResultEnum.EXTRA_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.EXTRA_EMPTY.getBizCode());
                return result;
            }
            if(ConvertUtils.isEmpty(extra.getSceneInfo())){
                result.setMessage(PayBizResultEnum.SCENEINFO_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.SCENEINFO_EMPTY.getBizCode());
                return result;
            }
            if(ConvertUtils.isEmpty(clientIp)){
                result.setMessage(PayBizResultEnum.CLIENT_IP_EMPTY.getBizMessage());
                result.setBizCode(PayBizResultEnum.CLIENT_IP_EMPTY.getBizCode());
                return result;
            }
        }
        return result;
    }

    /***
     *  验证签名
     * @param result
     * @return
     */
    public Result verifyPaySign(Result result,String reqtKey){
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


    @Override
    public String toString() {
        return "PayDto{" +
                "mchId='" + mchId + '\'' +
                ", mchOrderNo='" + mchOrderNo + '\'' +
                ", channelId='" + channelId + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", device='" + device + '\'' +
                ", extra=" + extra +
                ", param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", sign='" + sign + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", reqKey='" + reqKey + '\'' +
                '}';
    }
}
