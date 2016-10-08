package com.giot.memo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.giot.memo.listener.ShakeListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础Activity,封装了权限请求和友盟统计
 * Created by reed on 16/7/25.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final static int REQUEST_CODE = 1;

    protected SensorManager sensorManager;
    protected ShakeListener shakeListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeListener = new ShakeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getActivityName());
        MobclickAgent.onResume(this);
        if (sensorManager != null) {
            sensorManager.registerListener(shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getActivityName());
        MobclickAgent.onPause(this);
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onKillProcess(this);
        if (sensorManager != null) {
            sensorManager = null;
            shakeListener = null;
        }
    }

    /**
     * 用于判断需要请求的权限中哪些是已经获取的，并判断未获取权限中哪些是需要进行说明提示
     *
     * @param activity    传入的activity
     * @param permissions 需要申请的权限数组
     * @return 以Map<String.Boolean>的形式返回需要用户确认的权限，key是权限名，value是Boolean值，需要提示说明的权限的value值是false
     */
    protected final Map<String, Boolean> permissionNeeded(Activity activity, String[] permissions) {
        Map<String, Boolean> map = new HashMap<>();
        for (String permission : permissions) {
            int hasPermission = ContextCompat.checkSelfPermission(activity, permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    map.put(permission, false);
                } else {
                    map.put(permission, true);
                }
            }
        }
        return map;
    }

    /**
     * 展示权限说明提示
     *
     * @param activity    当前activity
     * @param message     提示说明
     * @param permissions 所有需要用户手动确认的权限数组
     */
    protected final void showRequestMessage(final Activity activity, String message, final String[] permissions) {
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("我已了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        grantedPermission(activity, permissions);
                    }
                })
                .create()
                .show();
    }

    /**
     * 找出需要进行提示的权限
     *
     * @param permissionMap permission的Map集合
     * @return 需要进行提示的权限集合
     */
    protected final List<String> permissionShow(Map<String, Boolean> permissionMap) {
        List<String> showPermission = new ArrayList<>();
        for (String permission : permissionMap.keySet()) {
            if (!permissionMap.get(permission)) {
                showPermission.add(permission);
            }
        }
        return showPermission;
    }

    /**
     * 请求权限
     *
     * @param activity    当前activity
     * @param permissions 权限说明
     */
    protected final void grantedPermission(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
    }

    protected final String getActivityName() {
        String str = this.toString();
        return str.substring(str.lastIndexOf(".") + 1, str.indexOf("@"));
    }
}
