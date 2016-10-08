package com.giot.memo.login;

import android.content.Context;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.User;

/**
 * This specifies the contract between the view and the presenter.
 * Created by reed on 16/7/25.
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        Context getContext();

        void loginSuccess(boolean isEmpty);

        void showProgress();

        void dismissProgress();

        void showError();

        void finishView();

    }

    interface Presenter extends BasePresenter {

        void loginWithAccount();

        void loginWithWeChat(String code);

        void cancelRequest();

        void mergeData();

        void downloadBill();
    }
}
