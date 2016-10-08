package com.giot.memo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.giot.memo.data.entity.User;

/**
 * sharedPreference工具类
 * Created by reed on 16/7/25.
 */
public class SharedPreferencesUtil {

    private SharedPreferences pref;

    public SharedPreferencesUtil(Context context, String name) {
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void exitLogin() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    //保存用户登录信息
    public void saveLoginStatus(User user) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SysConstants.ID, user.getId());
        editor.putString(SysConstants.EXTRA_ID, user.getExid());
        editor.putString(SysConstants.NICKNAME, user.getNickName());
        editor.putString(SysConstants.PHONE, user.getPhone());
        editor.putString(SysConstants.PASSWORD, user.getPassword());
        editor.putString(SysConstants.IMAGE, user.getImage());
        editor.putInt(SysConstants.TYPE, user.getType());
        editor.putString(SysConstants.DATE, TransformUtil.dateToString(user.getCreateTime()));
        editor.apply();
    }

    //获取用户的登录信息
    public User getUser() {
        User user = new User();
        user.setId(pref.getString(SysConstants.ID, null));
        if (TextUtils.isEmpty(user.getId())) {
            return null;
        }
        user.setExid(pref.getString(SysConstants.EXTRA_ID, null));
        user.setNickName(pref.getString(SysConstants.NICKNAME, null));
        user.setPassword(pref.getString(SysConstants.PASSWORD, null));
        user.setPhone(pref.getString(SysConstants.PHONE, null));
        user.setType(pref.getInt(SysConstants.TYPE, 0));
        user.setImage(pref.getString(SysConstants.IMAGE, null));
        user.setCreateTime(TransformUtil.stringToDate(pref.getString(SysConstants.DATE, null)));
        return user;
    }

    //保存是否云同步状态
    public void saveSyncStatus(boolean status) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SysConstants.SYNC, status);
        editor.apply();
    }

    //获取云同步状态
    public boolean getSyncStatus() {
        return pref.getBoolean(SysConstants.SYNC, false);
    }
}
