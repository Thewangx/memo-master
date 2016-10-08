package com.giot.memo.feedback;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;

/**
 * feedback contract
 * Created by reed on 16/8/4.
 */
public class FeedbackContract {

    interface View extends BaseView<Presenter> {

        void showProgress();

        void dismissProgress();

        void showTint();

        void showError();

    }

    interface Presenter extends BasePresenter {

        void submitFeedback(String email, String feedback);

        void cancelRequest();
    }
}
