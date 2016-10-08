package com.giot.memo.analysis.overall;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.CountBill;

import java.util.List;

/**
 * 总体分析contract
 * Created by reed on 16/8/16.
 */
public class OverallContract {

    interface View extends BaseView<Presenter> {
        void initChart(List<CountBill> countBills);

        void showDetail(List<CountBill> countBills);
    }

    interface Presenter extends BasePresenter {
        void showDetail(int position);
    }
}
