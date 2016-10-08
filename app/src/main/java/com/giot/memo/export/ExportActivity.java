package com.giot.memo.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.login.LoginActivity;
import com.giot.memo.util.SnackBarUtil;
import com.giot.memo.util.SysConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExportActivity extends BaseActivity implements ExportContract.View, AdapterView.OnItemSelectedListener {

    @BindView(R.id.toolbar_export)
    Toolbar exportToolbar;
    @BindView(R.id.editText_export_email)
    EditText exportEditText;
    @BindView(R.id.btn_export)
    Button exportBtn;
    @BindView(R.id.spinner_export_date)
    AppCompatSpinner dateSpinner;

    private ExportContract.Presenter mPresenter;
    private int position = 0;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        ButterKnife.bind(this);
        initListener();
        new ExportPresenter(this);
        mPresenter.start();
    }

    private void initListener() {
        exportToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = exportEditText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    SnackBarUtil.show(exportBtn, "请输入邮箱");
                } else if (!email.matches(SysConstants.RuleMail)) {
                    SnackBarUtil.show(exportBtn, "您输入的邮箱格式错误");
                } else {
                    mPresenter.exportToExcel(email, position);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_export_date, R.layout.spinner_text);
        adapter.setDropDownViewResource(R.layout.spinner_export_date);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void showDialog() {
        dialog = ProgressDialog.show(this, null, "正在导出账单......", true, true);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mPresenter.cancelRequest();
            }
        });
    }

    /**
     * 导出账单成功的提示信息
     */
    @Override
    public void showTint() {
        Snackbar.make(exportBtn, "账单的excel表格已发送至您的邮箱, 请查收!", Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                finish();
            }
        }).show();
    }

    @Override
    public Context getContext() {
        return ExportActivity.this;
    }

    @Override
    public void gotoLogin() {
        Snackbar.make(exportBtn, "您需要登录后才能导出输入", Snackbar.LENGTH_SHORT).setActionTextColor(Color.YELLOW).setAction("登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExportActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }).show();
    }

    @Override
    public void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 导出账单失败, 提示信息
     */
    @Override
    public void showError() {
        SnackBarUtil.show(exportBtn, "导出账单失败, 请重试!");
    }

    @Override
    public void setPresenter(ExportContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("the export presenter is null");
        }
        mPresenter = presenter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
