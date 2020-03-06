package org.smartwork.comm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;

/***
 * ChannelNameEnum概要说明：渠道名称
 * @author Huanghy
 */
public enum ChannelNameEnum {


    CHANNEL_NAME_WX("WX", "微信"),
    CHANNEL_NAME_ALIPAY("ALIPAY", "支付宝");

    private String code;
    private String name;

    /***
     *
     * @return
     */
    public static List<ResultEnum> resultEnums(){
        return Arrays.asList(ChannelNameEnum.values())
                .stream().map(channelName -> ResultEnum.ResultEnumBuild
                        .build()
                        .setCode(channelName.getCode())
                        .setName(channelName.getName())).collect(Collectors.toList());
    }

    /***
     *   判断是否存在
     * @param code
     * @return
     */
    public static boolean existsCode(Object code){
        return Arrays.asList(ChannelNameEnum.values()).stream()
                .filter(channelName-> ConvertUtils.isNotEmpty(code)&&channelName.getCode().equals(code))
                .count() >=  1;
    }

    /***
     *
     * 构造函数:
     * @param code
     * @param name
     */
    ChannelNameEnum(String code, String name) {
        this.code = code;
        this.name = name;
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
}
