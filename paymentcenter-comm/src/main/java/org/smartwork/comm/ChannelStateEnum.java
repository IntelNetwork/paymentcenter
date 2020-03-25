package org.smartwork.comm;

import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * MchStateEnum概要说明：渠道状态
 * @author Huanghy
 */
public enum ChannelStateEnum {


    STOP(0, "停止使用"),
    ACTIVITY(1, "使用中");

    private Integer code;
    private String name;


    /***
     *
     * @return
     */
    public static List<ResultEnum> resultEnums(){
        return Arrays.asList(ChannelStateEnum.values())
                .stream().map(mchState -> ResultEnum.ResultEnumBuild
                        .build()
                        .setCode(mchState.getCode())
                        .setName(mchState.getName())).collect(Collectors.toList());
    }

    /***
     *   判断是否存在
     * @param code
     * @return
     */
    public static boolean existsCode(Object code){
        return Arrays.asList(ChannelStateEnum.values()).stream()
                .filter(mchState -> ConvertUtils.isNotEmpty(code)&&mchState.getCode().equals(code))
                .count() >=  1;
    }

    /***
     *
     * 构造函数:
     * @param code
     * @param name
     */
    ChannelStateEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }
}
