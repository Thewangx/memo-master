package com.giot.memo.analysis.detail;

import android.content.Context;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.Analysis;

import java.util.List;

/**
 * 账单分析详情contract
 * Created by reed on 16/8/16.
 */
public class DetailContract {

    interface View extends BaseView<Presenter>{

        void updateSpinner(String[] items, int position);

        void updateRecycler(List<Analysis> analysisList, String count);

        void updateChart(float[] percent);

        void switchChart(String text);

    }

    interface Presenter extends BasePresenter {

        void setSpinnerMode(int spinnerMode);

        void switchBillMode();

        void preDate();

        void nextDate();

    }
}
