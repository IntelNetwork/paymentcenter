package org.smartwork.comm;

import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * RpcSignTypeEnum概要说明：RPC通讯层签名计算方法枚举类
 * @author Huanghy
 */
public enum RpcSignTypeEnum {

    NOT_SIGN(0,"明文"),// 明文
    SHA1_SIGN(1," SHA-1签名");// SHA-1签名

    private Integer code;
    private  String name;

    private RpcSignTypeEnum(Integer code,String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return name;
    }

    /***
     *
     * @return
     */
    public static List<ResultEnum> resultEnums(){
        return Arrays.asList(RpcSignTypeEnum.values())
                .stream().map(rpcSignType -> ResultEnum.ResultEnumBuild
                        .build()
                        .setCode(rpcSignType.getCode())
                        .setName(rpcSignType.getName())).collect(Collectors.toList());
    }

    /***
     *   判断是否存在
     * @param code
     * @return
     */
    public static boolean existsCode(Object code){
        return Arrays.asList(RpcSignTypeEnum.values()).stream()
                .filter(rpcSignType -> ConvertUtils.isNotEmpty(code)&&rpcSignType.getCode().equals(code))
                .count() >=  1;
    }

}
