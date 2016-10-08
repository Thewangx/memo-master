package com.giot.memo.feedback;

import com.giot.memo.http.MemoRetrofit;
import com.giot.memo.http.ResponseEntityFunc;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.SignatureUtil;
import com.giot.memo.util.SysConstants;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * feedback presenter
 * Created by reed on 16/8/4.
 */
public class FeedbackPresenter implements FeedbackContract.Presenter {

    private static final String TAG = FeedbackPresenter.class.getSimpleName();

    private FeedbackContract.View mView;
    private Subscriber<String> subscriber;

    public FeedbackPresenter(FeedbackContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    /**
     * send the feedback to the server
     * @param email the feedback's email
     * @param content the feedback
     */
    @Override
    public void submitFeedback(String email, String content) {
        subscriber = new Subscriber<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mView.showProgress();
            }

            @Override
            public void onCompleted() {
                mView.dismissProgress();
                mView.showTint();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i(TAG, "反馈出错: " + e.getMessage());
                mView.dismissProgress();
                mView.showError();
            }

            @Override
            public void onNext(String s) {

            }
        };
        Map<String, String> map = new HashMap<>();
        map.put(SysConstants.EMAIL, email);
        map.put(SysConstants.FEEDBACK, content);
        String sig = SignatureUtil.genSig(map);
        MemoRetrofit.getService().feedback(SysConstants.VERSION, SysConstants.DEV_TYPE, sig, email, content)
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
}
