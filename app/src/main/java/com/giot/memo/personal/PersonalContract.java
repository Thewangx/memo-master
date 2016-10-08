package com.giot.memo.personal;

import android.content.Context;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.User;

/**
 * the personal information contract
 * Created by reed on 16/7/28.
 */
public interface PersonalContract {

    interface View extends BaseView<Presenter> {

        void modifyImage();

        void modifyNickname();

        void loadInformation(User user);

        Context getContext();

        void gotoLogin();

        void showSync(boolean isSync);

        void showProgress();

        void dismissProgress();

    }

    interface Presenter extends BasePresenter {

        void modifyImage();

        void modifyNickname();

        void logout();

        void changeSync(boolean status);

        void syncData();

        void cancelRequest();

    }
}
