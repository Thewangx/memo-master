package com.giot.memo.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.SnackBarUtil;
import com.giot.memo.util.SysConstants;
import com.giot.memo.wxapi.WeChatUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements LoginContract.View {


    @BindView(R.id.textView_login_weChat)
    public TextView loginTextView;
    @BindView(R.id.toolbar_login)
    Toolbar loginToolbar;

    private LoginContract.Presenter mPresenter;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        new LoginPresenter(this);
        loginTextView.getBackground().setAlpha(127);
        loginToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //permissionForWeChat();
                loginWithWeChat();
            }
        });
        mPresenter.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //此Activity的启动模式设置成了SingleTask
        //微信授权回调后进入此Activity在此处获得code
        super.onNewIntent(intent);
        String code = intent.getStringExtra(SysConstants.CODE);
        if (code != null) {
            LogUtil.i("login", code);
            mPresenter.loginWithWeChat(code);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean result = false;
        switch (requestCode) {
            case REQUEST_CODE:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        result = true;
                    } else {
                        result = false;
                        showPermissionMsg();
                        break;
                    }
                }
                break;
        }
        if (result) {
            loginWithWeChat();
        }
    }*/

    /**
     * 微信登录需要的权限(电话和存储权限)
     * 后来发现即使不赋予这两项权限也可以使用微信登录, 故此处弃用该方法, 直接使用{@link #loginWithWeChat()}
     */
    @Deprecated
    private void permissionForWeChat() {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Map<String, Boolean> permissionMap = permissionNeeded(LoginActivity.this, permissions);
        if (permissionMap.size() == 0) {
            loginWithWeChat();
        } else {
            List<String> permissionList = permissionShow(permissionMap);
            if (permissionList.size() == 0) {
                //进行权限请求
                ActivityCompat.requestPermissions(LoginActivity.this, permissionMap.keySet().toArray(new String[permissionMap
                        .size()]), REQUEST_CODE);
            } else {
                //需要进行权限提示
                showRequestMessage(this, "使用微信登录需要获取您的电话权限和存储权限", permissionMap.keySet().toArray(new String[permissionMap
                        .size()]));
            }
        }
    }

    //微信登录
    private void loginWithWeChat() {
        if (!WeChatUtil.getInstance(LoginActivity.this).requestCode()) {
            String errMsg = "没有检测到微信,请安装微信后重新尝试";
            SnackBarUtil.show(loginTextView, errMsg);
        }
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("presenter is null");
        }
        mPresenter = presenter;
    }

    /*public void showPermissionMsg() {
        String errMsg = "您没有赋予本应用相关权限, 无法进行微信登录";
        SnackBarUtil.show(loginTextView, errMsg);
    }*/

    @Override
    public Context getContext() {
        return LoginActivity.this;
    }

    /**
     * 如果登录前存在账单, 询问用户是否合并
     *
     * @param isEmpty 登录前是否存在账单, 有为false, 没有为true
     */ 
    @Override
    public void loginSuccess(boolean isEmpty) {
        if (isEmpty) {
            downloadBill();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("登录成功, 您需要将未登录状态下的账单合并么?");
            builder.setCancelable(false);
            builder.setPositiveButton("合并", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.mergeData();
                    downloadBill();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadBill();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, null, "登录中......", true, true);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mPresenter.cancelRequest();
            }
        });
    }

    @Override
    public void dismissProgress() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void showError() {
        SnackBarUtil.show(loginTextView, "登录失败, 请稍后重试!");
    }

    @Override
    public void finishView() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        finish();
    }

    private void downloadBill() {
        dialog = ProgressDialog.show(this, null, "正在同步您的账户数据, 请稍等片刻......", true, false);
        dialog.show();
        mPresenter.downloadBill();
    }


}
