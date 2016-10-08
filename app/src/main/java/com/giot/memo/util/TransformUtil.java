package com.giot.memo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 类型转换工具类
 * Created by reed on 16/7/25.
 */
public class TransformUtil {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    public static SimpleDateFormat requestFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.CHINA);

    public static String dateToString(Date date) {
        return format.format(date);
    }
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
    public static String formatRequest(Date date) {
        return requestFormat.format(date);
    }

    public static Date stringToDate(String date) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取出金额末尾多余的零
     * @param money 金额
     * @return 去除零之后的结果
     */
    public static String deleteZero(String money) {
        if (money.charAt(money.length() - 1) == '0') {//判断倒数第一位是否为0
            money = money.substring(0, money.length() - 2);
        }
        return money;
    }
}
