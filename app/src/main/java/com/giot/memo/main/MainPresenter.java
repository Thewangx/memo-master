package com.giot.memo.main;

import android.os.Environment;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.http.ResponseEntityFunc;
import com.giot.memo.util.DaoUtil;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.SysConstants;
import com.giot.memo.util.TransformUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by reed on 16/7/26.
 * Listens to user actions from the UI ({@link MainActivity}), retrieves the data and updates
 * the UI as required.
 */
public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private MainContract.View mView;

    private Date mDate;

    private User user;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    private Subscriber<ResponseBody> downloadSubscriber;

    /**
     * 更改日期
     *
     * @param date 日期
     */
    @Override
    public void changeDate(Date date) {
        mDate = date;
        mView.setDate(date);
        loadBill(date);
    }

    /**
     * 加载账单数据
     *
     * @param date 当前选择日期
     */
    @Override
    public void loadBill(Date date) {
        Calendar minCal = Calendar.getInstance();
        Calendar maxCal = Calendar.getInstance();
        minCal.setTime(date);
        maxCal.setTime(date);
        minCal.set(Calendar.HOUR_OF_DAY, 0);
        maxCal.set(Calendar.HOUR_OF_DAY, 23);
        minCal.set(Calendar.MINUTE, 0);
        maxCal.set(Calendar.MINUTE, 59);
        minCal.set(Calendar.SECOND, 0);
        maxCal.set(Calendar.SECOND, 59);
        user = ((App) App.getContext()).getUser();
        List<Bill> bills;
        List<Bill> preBills;
        List<Bill> nextBills;
        if (user != null) {
            bills = DaoUtil.getBillDao().queryBuilder()
                    .where(BillDao.Properties.Date.between(minCal.getTime(), maxCal.getTime()),
                            BillDao.Properties.UserId.eq(user.getId()))
                    .build().list();
            preBills = DaoUtil.getBillDao().queryBuilder()
                    .where(BillDao.Properties.Date.between(new Date(minCal.getTimeInMillis() - 24 * 60 * 60 * 1000), new Date(maxCal.getTimeInMillis() - 24 * 60 * 60 * 1000)),
                            BillDao.Properties.UserId.eq(user.getId()))
                    .build().list();
            nextBills = DaoUtil.getBillDao().queryBuilder()
                    .where(BillDao.Properties.Date.between(new Date(minCal.getTimeInMillis() + 24 * 60 * 60 * 1000), new Date(maxCal.getTimeInMillis() + 24 * 60 * 60 * 1000)),
                            BillDao.Properties.UserId.eq(user.getId()))
                    .build().list();
        } else {
            bills = DaoUtil.getBillDao().queryBuilder()
                    .where(BillDao.Properties.Date.between(minCal.getTime(), maxCal.getTime()),
                            BillDao.Properties.UserId.isNull())
                    .build().list();
            preBills = DaoUtil.getBillDao().queryBuilder()
                    .where(BillDao.Properties.Date.between(new Date(minCal.getTimeInMillis() - 24 * 60 * 60 * 1000), new Date(maxCal.getTimeInMillis() - 24 * 60 * 60 * 1000)),
                            BillDao.Properties.UserId.isNull())
                    .build().list();
            nextBills = DaoUtil.getBillDao().queryBuilder()
                    .where(BillDao.Properties.Date.between(new Date(minCal.getTimeInMillis() + 24 * 60 * 60 * 1000), new Date(maxCal.getTimeInMillis() + 24 * 60 * 60 * 1000)),
                            BillDao.Properties.UserId.isNull())
                    .build().list();
        }
        mView.showBill(bills, preBills, nextBills);
        if (TransformUtil.formatDate(mDate).equals(TransformUtil.formatDate(new Date())) && bills.isEmpty()) {
            mView.showHint();
        } else {
            mView.hideHint();
        }

    }

    @Override
    public Date getSelectedDate() {
        return mDate;
    }

    @Override
    public void refreshBill() {
        loadBill(mDate);
    }

    /**
     * 查看前一天账单
     */
    @Override
    public void preBill() {
        changeDate(new Date(mDate.getTime() - 24 * 60 * 60 * 1000));
    }

    /**
     * 查看后一天账单
     */
    @Override
    public void nextBill() {
        changeDate(new Date(mDate.getTime() + 24 * 60 * 60 * 1000));
    }

    /**
     * 清楚本地账单数据
     */
    @Override
    public void clearData() {
        DaoUtil.getBillDao().deleteAll();
        refreshBill();
    }

    /**
     * 检查更新
     */
    @Override
    public void checkUpdate() {
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "检查更新失败: " + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                if (integer == 1) {
                    mView.alertUpdate();
                }
            }
        };
        MemoRetrofit.getService().checkUpdate(SysConstants.VERSION, SysConstants.DEV_TYPE)
                .map(new ResponseEntityFunc<Integer>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @Override
    public void downloadApk() {
        downloadSubscriber = new Subscriber<ResponseBody>() {

            @Override
            public void onStart() {
                super.onStart();
                mView.showProgress(0);
            }

            @Override
            public void onCompleted() {
                mView.dismissDownload();
                mView.installNewVer(new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "memo.apk"));
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "下载失败: " + e.getMessage());
                mView.dismissDownload();
                mView.showErrMsg("下载失败");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                if (responseBody != null) {
                    try {
                        //文件总长度
                        long fileSize = responseBody.contentLength();
                        long fileSizeDownloaded = 0;
                        InputStream is = responseBody.byteStream();
                        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "memo.apk");
                        FileOutputStream fos = new FileOutputStream(file);

                        int count;
                        byte[] buffer = new byte[1024];
                        while ((count = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);
                            fileSizeDownloaded += count;
                            mView.showProgress((int) (100 * fileSizeDownloaded / fileSize));
                        }
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        MemoRetrofit.getService().downloadApk()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(downloadSubscriber);
    }

    @Override
    public void cancelDownload() {
        if (!downloadSubscriber.isUnsubscribed()) {
            downloadSubscriber.unsubscribe();
        }
    }

    @Override
    public void start() {
        mDate = new Date();
        mView.setDate(mDate);
        boolean status = ((App) mView.getContext().getApplicationContext()).isSync();
        if (status && user != null) {
            mView.startSync();
        }
        checkUpdate();
    }
}
