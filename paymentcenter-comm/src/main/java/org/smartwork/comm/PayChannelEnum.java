package org.smartwork.comm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;
import org.forbes.pay.comm.enums.BizSceneEnum;

/***
 * PayChannelEnum概要说明：支付渠道
 * @author Huanghy
 */
public enum PayChannelEnum {


    PAY_CHANNEL_WX_JSAPI("WX_JSAPI", "微信公众号支付", "WX","JSAPI"),
    PAY_CHANNEL_WX_NATIVE("WX_NATIVE", "微信原生扫码支付", "WX","NATIVE"),
    PAY_CHANNEL_WX_APP("WX_APP", "微信APP支付", "WX","APP"),
    PAY_CHANNEL_WX_MWEB("WX_MWEB", "微信H5支付", "WX","MWEB"),
    PAY_CHANNEL_IAP("IAP", "苹果应用内支付", "IAP",""),
    PAY_CHANNEL_ALIPAY_MOBILE("ALIPAY_MOBILE", "支付宝移动支付", "ALIPAY",""),
    PAY_CHANNEL_ALIPAY_PC("ALIPAY_PC", "支付宝PC支付", "ALIPAY",""),
    PAY_CHANNEL_ALIPAY_WAP("ALIPAY_WAP", "支付宝WAP支付", "ALIPAY",""),
    PAY_CHANNEL_ALIPAY_QR("ALIPAY_QR", "支付宝当面付之扫码支付", "ALIPAY",""),
    PAY_CHANNEL_ALIPAY_TRANSFER("ALIPAY_TRANSFER", "支付宝资金转账", "ALIPAY","TRANS_ACCOUNT_NO_PWD,TRANS_BANKCARD_NO_PWD"),
    PAY_CHANNEL_WX_TRANSFER("WX_TRANSFER", "微信企业支付", "WX","WECHAT_TO_WECHAT");

    private String code;
    private String name;
    private String groupCode;
    private String tradeType;


    /***
     *
     * @return
     */
    public static List<ResultEnum> resultEnums(){
        return Arrays.asList(PayChannelEnum.values())
                .stream().map(payChannel -> ResultEnum.ResultEnumBuild
                        .build()
                        .setCode(payChannel.getCode())
                        .setName(payChannel.getName())).collect(Collectors.toList());
    }

    /***
     *   判断是否存在
     * @param code
     * @return
     */
    public static boolean existsCode(Object code){
        return Arrays.asList(PayChannelEnum.values()).stream()
                .filter(payChannel -> ConvertUtils.isNotEmpty(code)&&payChannel.getCode().equals(code))
                .count() >=  1;
    }

    /***
     *
     * @param code
     * @return
     */
    public static String receTradeType(String code){
        Optional<String> optionTradeType =  Arrays.asList(PayChannelEnum.values()).stream()
                .filter(payChannel -> ConvertUtils.isNotEmpty(code)&&payChannel.getCode().equals(code)).map(payChannel->payChannel.getTradeType()).findFirst();
        if(optionTradeType.isPresent()){
            return optionTradeType.get();
        } else {
            return null;
        }
    }


    /***
     *
     * @param productCode
     * @return
     */
    public static String receCode(String productCode){
        Optional<String> optionServcie = Arrays.asList(PayChannelEnum.values()).stream().filter(tEnum->{
            return  tEnum.getTradeType().contains(productCode);
        }).map(tEnum -> tEnum.getCode()).findFirst();
        if(optionServcie.isPresent()){
            return optionServcie.get();
        } else {
            return null;
        }
    }

    /***
     *
     * 构造函数:
     * @param code
     * @param name
     */
    PayChannelEnum(String code, String name, String groupCode,String tradeType) {
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
        this.tradeType = tradeType;
    }

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return groupCode
     */
    public String getGroupCode() {
        return groupCode;
    }

    public String getTradeType() {
        return tradeType;
    }
}
