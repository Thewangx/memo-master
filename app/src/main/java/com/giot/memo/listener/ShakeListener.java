package com.giot.memo.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.giot.memo.R;
import com.giot.memo.feedback.FeedbackActivity;
import com.giot.memo.util.LogUtil;

/**
 * 摇一摇监听事件
 * Created by reed on 16/7/26.
 */
public class ShakeListener implements SensorEventListener {

    private Context context;

    private AlertDialog mDialog;

    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;

    private long mLastTime;

    private static final int FORCE_THRESHOLD = 1200;

    private static final int TIME_THRESHOLD = 100;

    public ShakeListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long now = System.currentTimeMillis();

        // 传感器信息改变时执行该方法
        float[] values = event.values;
        float x = values[0]; // x轴方向的重力加速度，向右为正
        float y = values[1]; // y轴方向的重力加速度，向前为正
        float z = values[2]; // z轴方向的重力加速度，向上为正

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if ((now - mLastTime) > TIME_THRESHOLD) {
                long diff = now - mLastTime;
                float speed = Math.abs(event.values[0] + event.values[1]
                        + event.values[2] - mLastX - mLastY - mLastZ)
                        / diff * 10000;
                if (speed > FORCE_THRESHOLD) {
                    if (mDialog == null) {
                        createDialog();
                        mDialog.show();
                    } else if (!mDialog.isShowing()) {
                        mDialog.show();
                    }
                }
                mLastTime = now;
                mLastX = event.values[0];
                mLastY = event.values[1];
                mLastZ = event.values[2];

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.shake_feedback);
        builder.setPositiveButton(R.string.feedback, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        mDialog = builder.create();
    }
}
