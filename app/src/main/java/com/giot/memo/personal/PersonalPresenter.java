package com.giot.memo.personal;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.http.ResponseEntityFunc;
import com.giot.memo.util.DaoUtil;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.SharedPreferencesUtil;
import com.giot.memo.util.SysConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * the presenter for personal information
 * Created by reed on 16/7/28.
 */
public class PersonalPresenter implements PersonalContract.Presenter {

    private static final String TAG = PersonalPresenter.class.getSimpleName();

    private PersonalContract.View mView;
    private User user;
    private Subscriber<String> subscriber;
    private List<Bill> bills;

    public PersonalPresenter(PersonalContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void modifyImage() {

    }

    @Override
    public void modifyNickname() {

    }

    /**
     * 退出登录, 之后跳转至登录界面
     */
    @Override
    public void logout() {
        SharedPreferencesUtil userUtil = new SharedPreferencesUtil(mView.getContext(), SysConstants.USER_PREFERENCE_NAME);
        userUtil.exitLogin();
        SharedPreferencesUtil syncUtil = new SharedPreferencesUtil(mView.getContext(), SysConstants.SYNC_PREFERENCE_NAME);
        syncUtil.saveSyncStatus(false);
        ((App) mView.getContext().getApplicationContext()).setUser(null);
        ((App) mView.getContext().getApplicationContext()).setSync(false);
        mView.gotoLogin();

    }

    @Override
    public void changeSync(boolean status) {
        SharedPreferencesUtil util = new SharedPreferencesUtil(mView.getContext(), SysConstants.SYNC_PREFERENCE_NAME);
        util.saveSyncStatus(status);
        ((App) mView.getContext().getApplicationContext()).setSync(status);
        mView.showSync(status);
    }

    /**
     * 同步数据到云端
     */
    @Override
    public void syncData() {
        subscriber = new Subscriber<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mView.showProgress();
            }

            @Override
            public void onCompleted() {
                mView.dismissProgress();
                logout();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "同步数据出错: " + e.getMessage());
                mView.dismissProgress();
            }

            @Override
            public void onNext(String s) {
                if (bills != null && bills.size() > 0) {
                    DaoUtil.getBillDao().updateInTx(bills);
                }
            }
        };
        bills = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        List<Bill> billList = DaoUtil.getBillDao().queryBuilder().where(BillDao.Properties.UserId.eq(user.getId()),
                BillDao.Properties.Sync.eq(Bill.UN_SYNC)).build().list();
        for (Bill bill : billList) {
            bill.setSync(Bill.SYNC);
            bills.add(bill);
            try {
                jsonArray.put(new JSONObject(MemoRetrofit.gson.toJson(bill)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MemoRetrofit.getService().upBill(jsonArray)
                .map(new ResponseEntityFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 取消数据请求
     */
    @Override
    public void cancelRequest() {
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @Override
    public void start() {
        user = ((App) mView.getContext().getApplicationContext()).getUser();
        mView.loadInformation(user);
        mView.showSync(((App) mView.getContext().getApplicationContext()).isSync());
    }
}
