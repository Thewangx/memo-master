package com.giot.memo;

import android.app.Application;
import android.content.Context;

import com.giot.memo.data.entity.User;
import com.giot.memo.util.SharedPreferencesUtil;
import com.giot.memo.util.SysConstants;

/**
 * the application
 * Created by reed on 16/7/22.
 */
public class App extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    private User user;

    private boolean isSync;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPreferencesUtil syncUtil = new SharedPreferencesUtil(this, SysConstants.SYNC_PREFERENCE_NAME);
        isSync = syncUtil.getSyncStatus();
        SharedPreferencesUtil userUtil = new SharedPreferencesUtil(this, SysConstants.USER_PREFERENCE_NAME);
        user = userUtil.getUser();

    }
}
