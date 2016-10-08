package com.giot.memo.personal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giot.memo.App;
import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.data.entity.User;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.login.LoginActivity;
import com.giot.memo.main.MainService;
import com.giot.memo.util.ImageLoader;
import com.giot.memo.util.LogUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalActivity extends BaseActivity implements PersonalContract.View {

    private static final String TAG = PersonalActivity.class.getSimpleName();

    @BindView(R.id.toolbar_personal)
    Toolbar personalToolbar;
    @BindView(R.id.imageView_personal_head)
    ImageView headImageView;
    @BindView(R.id.textView_personal_nickname)
    TextView nicknameTextView;
    @BindView(R.id.textView_personal_exit)
    TextView exitTextView;
    @BindView(R.id.img_person_sync)
    ImageView synImageView;

    private PersonalContract.Presenter mPresenter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        initListener();
        new PersonalPresenter(this);
        mPresenter.start();
    }

    private void initListener() {
        personalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalActivity.this);
                builder.setCancelable(true);
                builder.setMessage("您即将退出登录, 是否将本地账单数据同步到云端?");
                builder.setPositiveButton("同步", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.syncData();
                    }
                });
                builder.setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.logout();
                    }
                });
                builder.create().show();
            }
        });
        synImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((App) getApplication()).isSync()) {
                    mPresenter.changeSync(false);
                    Intent intent = new Intent(PersonalActivity.this, MainService.class);
                    stopService(intent);//停止后台同步数据
                } else {
                    mPresenter.changeSync(true);
                    Intent intent = new Intent(PersonalActivity.this, MainService.class);
                    startService(intent);//开启后台同步数据
                }
            }
        });

    }

    @Override
    public void modifyImage() {

    }

    @Override
    public void modifyNickname() {

    }

    /**
     * 加载个人信息
     * @param user 个人信息
     */
    @Override
    public void loadInformation(User user) {
        LogUtil.i(TAG, "用户信息: " + user.toString());
        String url;
        if (user.getType() == User.FROM_PHONE) {
            url = MemoRetrofit.baseUrl + user.getImage();
        } else {
            url = user.getImage();
        }
        ImageLoader.with(this, url, headImageView);
        nicknameTextView.setText(user.getNickName());
    }

    @Override
    public Context getContext() {
        return PersonalActivity.this;
    }

    @Override
    public void gotoLogin() {
        Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showSync(boolean isSync) {
        if (isSync) {
            synImageView.setImageResource(R.mipmap.open);
        } else {
            synImageView.setImageResource(R.mipmap.close);
        }
    }

    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, null, "同步中, 请稍后.......", true, true);
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
    public void setPresenter(PersonalContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("personal presenter is null");
        }
        mPresenter = presenter;
    }
}
