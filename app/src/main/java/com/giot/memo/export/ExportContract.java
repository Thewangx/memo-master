package com.giot.memo.export;

import android.content.Context;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;

/**
 * export contract
 * Created by reed on 16/8/4.
 */
public class ExportContract {

    interface View extends BaseView<Presenter> {

        void showDialog();

        void showTint();

        Context getContext();

        void gotoLogin();

        void dismissDialog();

        void showError();

    }

    interface Presenter extends BasePresenter {
        void exportToExcel(String email, int position);
        void cancelRequest();
    }
}
