package com.giot.memo.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.http.ResponseEntityFunc;
import com.giot.memo.util.DaoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * to sync the bills' data
 * Created by reed on 16/8/16.
 */
public class MainService extends Service {

    private Subscriber<String> subscriber;
    private static final long delay = 1000;
    private static final long period = 1000 * 60 * 3;
    private List<Bill> bills;
    private Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (subscriber == null) {
            subscriber = new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    if (bills != null && bills.size() > 0) {
                        DaoUtil.getBillDao().updateInTx(bills);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(String s) {

                }
            };
        }
        if (timer == null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    User user = ((App) getApplication()).getUser();
                    if (user != null) {
                        JSONArray billArray = new JSONArray();
                        List<Bill> billList = DaoUtil.getBillDao().queryBuilder().where(BillDao.Properties.UserId.eq(user.getId()), BillDao.Properties.Sync.notEq(Bill.SYNC)).build().list();
                        bills = new ArrayList<>();
                        for (Bill bill : billList) {
                            bill.setSync(Bill.SYNC);
                            bills.add(bill);
                            try {
                                billArray.put(new JSONObject(MemoRetrofit.gson.toJson(bill)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if (bills.size() > 0) {
                            MemoRetrofit.getService().upBill(billArray)
                                    .map(new ResponseEntityFunc<String>())
                                    .subscribeOn(Schedulers.io())
                                    .unsubscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(subscriber);
                        }
                    }
                }
            };
            timer = new Timer();
            timer.scheduleAtFixedRate(task, delay, period);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
