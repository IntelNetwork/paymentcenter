package org.smartwork.util;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.smartwork.comm.PayEnum;
import org.smartwork.constant.PayConstant;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
public class PayUtil {

    public static Map<String, Object> makeRetMap(String retCode, String retMsg, String resCode, String errCode, String errCodeDesc) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if(retCode != null) retMap.put(PayConstant.RETURN_PARAM_RETCODE, retCode);
        if(retMsg != null) retMap.put(PayConstant.RETURN_PARAM_RETMSG, retMsg);
        if(resCode != null) retMap.put(PayConstant.RESULT_PARAM_RESCODE, resCode);
        if(errCode != null) retMap.put(PayConstant.RESULT_PARAM_ERRCODE, errCode);
        if(errCodeDesc != null) retMap.put(PayConstant.RESULT_PARAM_ERRCODEDES, errCodeDesc);
        return retMap;
    }

    public static Map<String, Object> makeRetMap(String retCode, String retMsg, String resCode, PayEnum payEnum) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if(retCode != null) retMap.put(PayConstant.RETURN_PARAM_RETCODE, retCode);
        if(retMsg != null) retMap.put(PayConstant.RETURN_PARAM_RETMSG, retMsg);
        if(resCode != null) retMap.put(PayConstant.RESULT_PARAM_RESCODE, resCode);
        if(payEnum != null) {
            retMap.put(PayConstant.RESULT_PARAM_ERRCODE, payEnum.getCode());
            retMap.put(PayConstant.RESULT_PARAM_ERRCODEDES, payEnum.getMessage());
        }
        return retMap;
    }

    public static String makeRetData(Map retMap, String resKey) {
        if(retMap.get(PayConstant.RETURN_PARAM_RETCODE).equals(PayConstant.RETURN_VALUE_SUCCESS)) {
            String sign = PayDigestUtil.getSign(retMap, resKey, "payParams");
            retMap.put(PayConstant.RESULT_PARAM_SIGN, sign);
        }
        log.info("生成响应数据:{}", retMap);
        return JSON.toJSONString(retMap);
    }

    public static String makeRetFail(Map retMap) {
        log.info("生成响应数据:{}", retMap);
        return JSON.toJSONString(retMap);
    }


    /**
     * 连接Map键值对
     *
     * @param map              Map
     * @param prefix           前缀
     * @param suffix           后缀
     * @param separator        连接符
     * @param ignoreEmptyValue 忽略空值
     * @param ignoreKeys       忽略Key
     * @return 字符串
     */
    public static String joinKeyValue(Map<String, Object> map, String prefix, String suffix, String separator, boolean ignoreEmptyValue, String... ignoreKeys) {
        List<String> list = new ArrayList<>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = ConvertUtils.convert(entry.getValue());
                if (StringUtils.isNotEmpty(key) && !ArrayUtils.contains(ignoreKeys, key) && (!ignoreEmptyValue || StringUtils.isNotEmpty(value))) {
                    list.add(key + "=" + (value != null ? value : StringUtils.EMPTY));
                }
            }
        }
        return (prefix != null ? prefix : StringUtils.EMPTY) + StringUtils.join(list, separator) + (suffix != null ? suffix : StringUtils.EMPTY);
    }

    public static void main(String[] args) {
        String reqJson = "{ \"mchId\" :\"1504615171\",\"notifyUrl\":\"pay\", \"mchOrderNo\" :\"555555\", \"channelId\" :\"WX_NATIVE\", \"amount\" :\"1\", \"currency\" :\"cny\", \"subject\" :\"测试商品\", \"body\" :\"测试商品\", \"extra\" :{ \"productId\" :\"111111\" } }";
        String str = PayDigestUtil.getSign(JSON.parseObject(reqJson,Map.class), "X3QCQVxXMPqgSIyN3W04u1NxogiUshLP");
        System.out.println("=========str========="+str);
    }


    /**
     * 验证支付中心签名
     * @param params
     * @return
     */
    public static boolean verifyPaySign(Map<String,Object> params, String key) {
        String sign = (String)params.get("sign"); // 签名
        params.remove("sign");	// 不参与签名
        String checkSign = PayDigestUtil.getSign(params, key);
        if (!checkSign.equalsIgnoreCase(sign)) {
            return false;
        }
        return true;
    }

    /**
     * 验证VV平台支付中心签名
     * @param params
     * @return
     */
    public static boolean verifyPaySign(Map<String,Object> params, String key, String... noSigns) {
        String sign = (String)params.get("sign"); // 签名
        params.remove("sign");	// 不参与签名
        if(noSigns != null && noSigns.length > 0) {
            for (String noSign : noSigns) {
                params.remove(noSign);
            }
        }
        String checkSign = PayDigestUtil.getSign(params, key);
        if (!checkSign.equalsIgnoreCase(sign)) {
            return false;
        }
        return true;
    }

    public static String genUrlParams(Map<String, Object> paraMap) {
        if(paraMap == null || paraMap.isEmpty()) return "";
        StringBuffer urlParam = new StringBuffer();
        Set<String> keySet = paraMap.keySet();
        int i = 0;
        for(String key:keySet) {
            urlParam.append(key).append("=").append(paraMap.get(key));
            if(++i == keySet.size()) break;
            urlParam.append("&");
        }
        return urlParam.toString();
    }

    /**
     * 发起HTTP/HTTPS请求(method=POST)
     * @param url
     * @return
     */
    public static String call4Post(String url) {
        try {
            URL url1 = new URL(url);
            if("https".equals(url1.getProtocol())) {
                return HttpClient.callHttpsPost(url);
            }else if("http".equals(url1.getProtocol())) {
                return HttpClient.callHttpPost(url);
            }else {
                return "";
            }
        } catch (MalformedURLException e) {
            log.error("=========",e);
        }
        return "";
    }
}
