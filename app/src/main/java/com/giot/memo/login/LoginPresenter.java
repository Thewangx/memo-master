package com.giot.memo.login;

import android.support.annotation.NonNull;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.http.ResponseEntityFunc;
import com.giot.memo.util.DaoUtil;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.SharedPreferencesUtil;
import com.giot.memo.util.SignatureUtil;
import com.giot.memo.util.SysConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by reed on 16/7/25.
 * Listens to user actions from the UI ({@link LoginActivity}), retrieves the data and updates
 * the UI as required.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    @NonNull
    private LoginContract.View mView;

    private Subscriber<User> subscriber;

    public LoginPresenter(@NonNull LoginContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void loginWithAccount() {

    }

    @Override
    public void loginWithWeChat(String code) {
        Map<String, String> map = new HashMap<>();
        map.put(SysConstants.CODE, code);
        String sig = SignatureUtil.genSig(map);
        subscriber = new Subscriber<User>() {

            @Override
            public void onStart() {
                super.onStart();
                mView.showProgress();
            }

            @Override
            public void onCompleted() {
                mView.dismissProgress();
                loginSuccess();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "微信登录: " + e.toString());
                mView.dismissProgress();
                mView.showError();
            }

            @Override
            public void onNext(User user) {
                saveUser(user);
            }
        };
        MemoRetrofit.getService().loginWithWeChat(SysConstants.VERSION, SysConstants.DEV_TYPE, sig, code)
                .map(new ResponseEntityFunc<User>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 取消请求
     */
    @Override
    public void cancelRequest() {
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    /**
     * 合并数据, 将应用中userId为空的账单更新成当前用户
     */
    @Override
    public void mergeData() {
        List<Bill> bills = DaoUtil.getBillDao().queryBuilder().where(BillDao.Properties.UserId.isNull()).build().list();
        for (Bill bill : bills) {
            bill.setUserId(((App) mView.getContext().getApplicationContext()).getUser().getId());
            DaoUtil.getBillDao().update(bill);
        }
    }

    /**
     * 登录成功之后下载账单到本地
     */
    @Override
    public void downloadBill() {
        Subscriber<List<Bill>> billSubscriber = new Subscriber<List<Bill>>() {
            @Override
            public void onCompleted() {
                mView.finishView();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "downloadBill: " + e.toString());
                mView.finishView();

            }

            @Override
            public void onNext(List<Bill> bills) {
                for (Bill bill : bills) {
                    Bill temp = DaoUtil.getBillDao().queryBuilder().where(BillDao.Properties.Id.eq(bill.getId())).build().unique();
                    if (temp != null) {
                        DaoUtil.getBillDao().update(bill);
                    } else {
                        DaoUtil.getBillDao().insert(bill);
                    }
                }
            }
        };
        MemoRetrofit.getService().downloadBill(((App) mView.getContext().getApplicationContext()).getUser().getId())
                .map(new ResponseEntityFunc<List<Bill>>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(billSubscriber);
    }

    /**
     * 登录成功之后保存用户信息
     * @param user user对象
     */
    private void saveUser(User user) {
        SharedPreferencesUtil util = new SharedPreferencesUtil(mView.getContext(), SysConstants.USER_PREFERENCE_NAME);
        util.saveLoginStatus(user);
        ((App) mView.getContext().getApplicationContext()).setUser(user);
    }

    /**
     * 登录成功后判断登录前是否存在未登录状态下的订单
     */
    private void loginSuccess() {
        List<Bill> bills = DaoUtil.getBillDao().queryBuilder().where(BillDao.Properties.UserId.isNull()).build().list();
        mView.loginSuccess(bills.size() == 0);
    }

    @Override
    public void start() {

    }
}
