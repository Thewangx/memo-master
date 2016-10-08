package com.giot.memo.feedback;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.util.SnackBarUtil;
import com.giot.memo.util.SysConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends BaseActivity implements FeedbackContract.View {

    @BindView(R.id.toolbar_feedback)
    Toolbar feedbackToolbar;
    @BindView(R.id.editText_feedback_email)
    EditText emailEditText;
    @BindView(R.id.btn_feedback_submit)
    Button submitBtn;
    @BindView(R.id.editText_feedback_content)
    EditText contentEditText;

    private FeedbackContract.Presenter mPresenter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initListener();
        new FeedbackPresenter(this);
        mPresenter.start();
        sensorManager = null;
        shakeListener = null;
    }

    private void initListener() {
        feedbackToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String content = contentEditText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    SnackBarUtil.show(v, "请输入您的邮箱以方便我们联系您");
                    return;
                }
                if (!email.matches(SysConstants.RuleMail)) {
                    SnackBarUtil.show(v, "您输入的邮箱格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    SnackBarUtil.show(v, "您的意见还没填哦");
                    return;
                }
                mPresenter.submitFeedback(email, content);
            }
        });
    }

    @Override
    public void setPresenter(FeedbackContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("the feedback presenter is null");
        }
        mPresenter = presenter;
    }

    //展示进度对话框
    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, null, "正在发送您的意见......", true, true);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mPresenter.cancelRequest();
            }
        });
    }

    //关闭进度对话框
    @Override
    public void dismissProgress() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //反馈成功后提示信息
    @Override
    public void showTint() {
        Snackbar.make(submitBtn, "感谢您的反馈, 我们将尽快处理", Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                finish();
            }
        }).show();
    }

    @Override
    public void showError() {
        SnackBarUtil.show(submitBtn, "反馈失败, 请检查您的网络后重试!");
    }

}
