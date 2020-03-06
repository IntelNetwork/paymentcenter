package org.smartwork.comm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;

/***
 * PayChannelEnum概要说明：支付渠道
 * @author Huanghy
 */
public enum PayChannelEnum {


    PAY_CHANNEL_WX_JSAPI("WX_JSAPI", "微信公众号支付", "WX"),
    PAY_CHANNEL_WX_NATIVE("WX_NATIVE", "微信原生扫码支付", "WX"),
    PAY_CHANNEL_WX_APP("WX_APP", "微信APP支付", "WX"),
    PAY_CHANNEL_WX_MWEB("WX_MWEB", "微信H5支付", "WX"),
    PAY_CHANNEL_IAP("IAP", "苹果应用内支付", "IAP"),
    PAY_CHANNEL_ALIPAY_MOBILE("ALIPAY_MOBILE", "支付宝移动支付", "ALIPAY"),
    PAY_CHANNEL_ALIPAY_PC("ALIPAY_PC", "支付宝PC支付", "ALIPAY"),
    PAY_CHANNEL_ALIPAY_WAP("ALIPAY_WAP", "支付宝WAP支付", "ALIPAY"),
    PAY_CHANNEL_ALIPAY_QR("ALIPAY_QR", "支付宝当面付之扫码支付", "ALIPAY");

    private String code;
    private String name;
    private String groupCode;


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
     * 构造函数:
     * @param code
     * @param name
     */
    PayChannelEnum(String code, String name, String groupCode) {
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
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


}
