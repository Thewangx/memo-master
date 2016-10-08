package com.giot.memo.export;

import com.giot.memo.App;
import com.giot.memo.data.entity.User;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.http.ResponseEntityFunc;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.TransformUtil;

import java.util.Calendar;
import java.util.Date;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * export presenter
 * Created by reed on 16/8/4.
 */
public class ExportPresenter implements ExportContract.Presenter {

    private static final String TAG = ExportPresenter.class.getSimpleName();

    private ExportContract.View mView;
    private Subscriber<String> subscriber;

    public ExportPresenter(ExportContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void exportToExcel(String email, int position) {
        User user = ((App) mView.getContext().getApplicationContext()).getUser();
        if (user == null) {
            mView.gotoLogin();
            return;
        }
        subscriber = new Subscriber<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mView.showDialog();
            }

            @Override
            public void onCompleted() {
                mView.dismissDialog();
                mView.showTint();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "导出账单出错: " + e.getMessage());
                mView.dismissDialog();
                mView.showError();
            }

            @Override
            public void onNext(String str) {

            }
        };
        Date startTime;
        Calendar minCal = Calendar.getInstance();
        minCal.setTime(new Date());
        minCal.set(Calendar.HOUR_OF_DAY, 0);
        minCal.set(Calendar.MINUTE, 0);
        minCal.set(Calendar.SECOND, 0);
        switch (position) {
            case 0:
                startTime = minCal.getTime();
                break;
            case 1:
                startTime = new Date(minCal.getTimeInMillis() - 24 * 60 * 60 * 1000 * 3);
                break;
            case 2:
                startTime = new Date(minCal.getTimeInMillis() - 24*60*60*1000*7);
                break;
            case 3:
                minCal.set(Calendar.DAY_OF_MONTH, 1);
                startTime = minCal.getTime();
                break;
            default:
                startTime = new Date(0);
                break;
        }
        MemoRetrofit.getService().exportBill(email, user.getId(), TransformUtil.formatRequest(startTime), TransformUtil.formatRequest(new Date()))
                .map(new ResponseEntityFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    @Override
    public void cancelRequest() {
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @Override
    public void start() {

    }
}
