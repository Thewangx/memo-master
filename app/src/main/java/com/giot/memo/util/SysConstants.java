package com.giot.memo.util;


/**
 * 常量
 * Created by reed on 16/7/25.
 */
public class SysConstants {

    public static final String BASE_URL = "http://www.geariot.com/memo/";

    public static final String USER_PREF = "user_pref";//保存用户登录信息的sharedPreference名称

    public static final String PHONE = "phone";
    public static final String ID = "id";
    public static final String EXTRA_ID = "extraId";
    public static final String NICKNAME = "nickname";
    public static final String PASSWORD = "password";
    public static final String IMAGE = "image";
    public static final String TYPE = "type";
    public static final String DATE = "date";

    public static final String SYNC = "sync";

    public static final String KEY = "key";

    public static final String CURRENT = "current";

    public static final String SHARE_APP_URL = BASE_URL + "share.html";
    public static final String SHARE_APP_TITLE = "HI, 我在用\"Memo\"记账, 真的很好用哦";
    public static final String SHARE_APP_DESCRIPTION = "\"Memo\"改变你的记账习惯, 点击下载使用";

    public static final String VER = "ver";
    public static final String DEV = "dev";
    public static final String SIG = "sig";
    public static final String CODE = "code";
    public static final String USER_ID = "userId";

    public static final String EMAIL = "email";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String DATA = "data";
    public static final String FEEDBACK = "opinion";

    public static final String SECRET_KEY = "memogiot";

    public static final String VERSION = "1.0.0";//当版本升级时要同时修改此处, 保证与服务器一致
    public static final String DEV_TYPE = "1";

    public final static String RuleMail = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";

    public final static String USER_PREFERENCE_NAME = "user_data";

    public final static String SYNC_PREFERENCE_NAME = "sync";

    public final static String WEB_URL = "web_url";
}
