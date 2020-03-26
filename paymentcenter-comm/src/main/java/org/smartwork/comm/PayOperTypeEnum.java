package org.smartwork.comm;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.ResultEnum;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/***
 * 支付操作类型
 */
public enum PayOperTypeEnum {


    PAY("0", "支付"),
    TRANSFER("1", "转账"),
    REFUND("2", "退款");

    private String code;
    private String name;


    /***
     *
     * @return
     */
    public static List<ResultEnum> resultEnums(){
        return Arrays.asList(PayOperTypeEnum.values())
                .stream().map(mchType -> ResultEnum.ResultEnumBuild
                        .build()
                        .setCode(mchType.getCode())
                        .setName(mchType.getName())).collect(Collectors.toList());
    }

    /***
     *   判断是否存在
     * @param code
     * @return
     */
    public static boolean existsCode(Object code){
        return Arrays.asList(PayOperTypeEnum.values()).stream()
                .filter(mchType -> ConvertUtils.isNotEmpty(code)&&mchType.getCode().equals(code))
                .count() >=  1;
    }

    /***
     *
     * 构造函数:
     * @param code
     * @param name
     */
    PayOperTypeEnum(String code, String name) {
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
