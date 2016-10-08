package com.giot.memo.main;

import android.content.Context;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.Bill;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * This specifies the contract between the main view and the presenter.
 * Created by reed on 16/7/26.
 */
public interface MainContract {

    interface View extends BaseView<Presenter> {

        void setDate(Date date);

        void showDateDialog();

        void showBill(List<Bill> bills, List<Bill> preBills, List<Bill> nextBills);

        void startSync();

        Context getContext();

        void alertUpdate();

        void showProgress(int value);

        void dismissDownload();

        void showErrMsg(String msg);

        void installNewVer(File file);

        void hideHint();

        void showHint();


    }

    interface Presenter extends BasePresenter {

        void changeDate(Date date);

        void loadBill(Date date);

        Date getSelectedDate();

        void refreshBill();

        void preBill();

        void nextBill();

        void clearData();

        void checkUpdate();

        void downloadApk();

        void cancelDownload();

    }
}
