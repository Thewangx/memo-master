package com.giot.memo.util;

import java.util.Arrays;
import java.util.Map;

/**
 * 签名工具类
 * Created by reed on 16/8/10.
 */
public class SignatureUtil {

    private static final String TAG = SignatureUtil.class.getSimpleName();

    private static String concatParams(Map<String, String> params2) {
        Object[] key_arr = params2.keySet().toArray();
        Arrays.sort(key_arr);
        String str = "";

        for (Object key : key_arr) {
            String val = params2.get(key.toString());
            str += key.toString() + "=" + val;
        }
        return str;
    }

    /**
     * 返回正确的signature，用于参数验证
     *
     * @param params  参数map
     * @return signature
     */
    public static String genSig(Map<String, String> params) {
        params.put(SysConstants.DEV, SysConstants.DEV_TYPE);
        params.put(SysConstants.VER, SysConstants.VERSION);
        String str = concatParams(params);
        str += SysConstants.SECRET_KEY;
        String sig = MD5Util.compute(str);
        return sig;
    }

}
