package com.giot.memo.main;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.giot.memo.App;
import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.add.AddActivity;
import com.giot.memo.analysis.AnalysisActivity;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.export.ExportActivity;
import com.giot.memo.feedback.FeedbackActivity;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.login.LoginActivity;
import com.giot.memo.personal.PersonalActivity;
import com.giot.memo.search.SearchActivity;
import com.giot.memo.util.ImageLoader;
import com.giot.memo.util.SnackBarUtil;
import com.giot.memo.util.SysConstants;
import com.giot.memo.util.TransformUtil;
import com.giot.memo.view.ZoomOutPageTransformer;
import com.giot.memo.wxapi.WeChatUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.tv_main_date)
    public TextView dateTextView;
    @BindView(R.id.toolbar_main)
    public Toolbar mToolbar;
    @BindView(R.id.fab_main_add)
    public FloatingActionButton addFAB;
    @BindView(R.id.nav_view)
    public NavigationView mNav;
    @BindView(R.id.dl_main)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.viewPager_main)
    ViewPager mainViewPager;
    @BindView(R.id.textView_main_hint)
    TextView hintTextView;
    private TextView userTextView;
    private ImageView userImageView;
    private LinearLayout userLinear;

    private long exitTime;

    private MainContract.Presenter mPresenter;
    private BillFragmentAdapter fragmentAdapter;
    private User user;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initListener();
        new MainPresenter(this);
        mPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.refreshBill();
        user = ((App) getApplication()).getUser();
        if (user != null) {
            userTextView.setText(user.getNickName());
            if (user.getType() == User.FROM_WE_CHAT) {
                ImageLoader.with(this, user.getImage(), userImageView);
            } else {
                ImageLoader.with(this, MemoRetrofit.baseUrl + user.getImage(), userImageView);
            }
        } else {
            userTextView.setText(R.string.login_register);
            userImageView.setImageResource(R.mipmap.default_head);
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, MainService.class);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.analysis:
                Intent analysisIntent = new Intent(MainActivity.this, AnalysisActivity.class);
                startActivity(analysisIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                SnackBarUtil.show(mDrawerLayout, R.string.exit);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //初始化界面
    private void initView() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        userTextView = (TextView) mNav.getHeaderView(0).findViewById(R.id.textView_nav_nickname);
        userImageView = (ImageView) mNav.getHeaderView(0).findViewById(R.id.imageView_nav_head);
        userLinear = (LinearLayout) mNav.getHeaderView(0).findViewById(R.id.linear_nav_user);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        drawerToggle.syncState();
        mDrawerLayout.addDrawerListener(drawerToggle);
        fragmentAdapter = new BillFragmentAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(fragmentAdapter);
        mainViewPager.setCurrentItem(1, false);
    }


    //初始化点击事件
    private void initListener() {
        mNav.setNavigationItemSelectedListener(this);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("date", mPresenter.getSelectedDate());
                startActivity(intent);
            }
        });
        userLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
                    startActivity(intent);
                }
            }
        });
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    return;
                }
                //mainViewPager.setCurrentItem(1, false);
                if (position == 0) {
                    mPresenter.preBill();
                } else if (position == 2) {
                    mPresenter.nextBill();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //设置选择的时间
    @Override
    public void setDate(Date date) {
        dateTextView.setText(TransformUtil.formatDate(date));
    }

    //点击日期弹出时间选择对话框
    @Override
    public void showDateDialog() {
        Calendar c = Calendar.getInstance();
        c.setTime(mPresenter.getSelectedDate());
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                mPresenter.changeDate(calendar.getTime());
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 显示账单
     *
     * @param bills     当前日期的账单
     * @param preBills  前一天的账单
     * @param nextBills 后一天的账单
     */
    @Override
    public void showBill(List<Bill> bills, List<Bill> preBills, List<Bill> nextBills) {
        fragmentAdapter.setBills(preBills, bills, nextBills);
        if (mainViewPager != null) {
            mainViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            mainViewPager.setAdapter(fragmentAdapter);
            mainViewPager.setCurrentItem(1, true);
        }
    }

    @Override
    public void startSync() {
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
    }

    @Override
    public Context getContext() {
        return MainActivity.this;
    }

    @Override
    public void alertUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("检查到有新版本")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestForPermission();
                    }
                })
                .setNegativeButton("下次", null)
                .setCancelable(true);
        builder.create().show();
    }

    @Override
    public void showProgress(int value) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setMessage("正在下载新版本");
            mDialog.setIndeterminate(false);
            mDialog.setMax(100);
            mDialog.show();
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mPresenter.cancelDownload();
                }
            });
        }
        mDialog.setProgress(value);
    }

    @Override
    public void dismissDownload() {
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    @Override
    public void showErrMsg(String msg) {
        SnackBarUtil.show(mDrawerLayout, msg);
    }

    @Override
    public void installNewVer(File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void hideHint() {
        hintTextView.setVisibility(View.GONE);
    }

    @Override
    public void showHint() {
        hintTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void setPresenter(MainContract.Presenter presenter) {

        if (presenter == null) {
            throw new NullPointerException("MainPresenter is null");
        }
        mPresenter = presenter;
    }

    //侧滑抽屉的菜单点击事件
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_export:
                Intent exportIntent = new Intent(MainActivity.this, ExportActivity.class);
                startActivity(exportIntent);
                break;
            case R.id.nav_clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.confirm_clear);
                builder.setPositiveButton(R.string.clear_data, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.clearData();
                        SnackBarUtil.show(mDrawerLayout, "账单数据已清空");
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_share:
                showShareDialog();
                break;
            case R.id.nav_feedback:
                Intent feedbackIntent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(feedbackIntent);
                break;
        }
        return false;
    }

    @Override
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
            mPresenter.downloadApk();
        }
    }

    private void showPermissionMsg() {
        String errMsg = "您没有赋予本应用存储权限, 无法更新应用!请前往设置里赋予权限";
        SnackBarUtil.show(mDrawerLayout, errMsg);
    }

    private void requestForPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Map<String, Boolean> permissionMap = permissionNeeded(MainActivity.this, permissions);
        if (permissionMap.size() == 0) {
            mPresenter.downloadApk();
        } else {
            List<String> permissionList = permissionShow(permissionMap);
            if (permissionList.size() == 0) {
                //进行权限请求
                ActivityCompat.requestPermissions(MainActivity.this, permissionMap.keySet().toArray(new String[permissionMap
                        .size()]), REQUEST_CODE);
            } else {
                //需要进行权限提示
                showRequestMessage(this, "更新应用需要您赋予存储权限", permissionMap.keySet().toArray(new String[permissionMap
                        .size()]));
            }
        }
    }

    //分享应用到微信
    public void showShareDialog() {
        Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_Light_Dialog);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        View dialogView = View.inflate(this, R.layout.dialog_share, null);
        dialog.setContentView(dialogView);
        dialog.show();
        TextView friendsTextView = (TextView) dialogView.findViewById(R.id.textView_share_friends);
        TextView timelineTextView = (TextView) dialogView.findViewById(R.id.textView_share_timeline);
        friendsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = WeChatUtil.getInstance(MainActivity.this).share(SysConstants.SHARE_APP_URL, SysConstants.SHARE_APP_TITLE, SysConstants.SHARE_APP_DESCRIPTION, SendMessageToWX.Req.WXSceneSession);
                if (!result) {
                    SnackBarUtil.show(mDrawerLayout, "未检测到微信,请安装微信后重试");
                }
            }
        });
        timelineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = WeChatUtil.getInstance(MainActivity.this).share(SysConstants.SHARE_APP_URL, SysConstants.SHARE_APP_TITLE, SysConstants.SHARE_APP_DESCRIPTION, SendMessageToWX.Req.WXSceneTimeline);
                if (!result) {
                    SnackBarUtil.show(mDrawerLayout, "未检测到微信或微信版本过低, 请安装最新版本微信");
                }
            }
        });
    }
}
