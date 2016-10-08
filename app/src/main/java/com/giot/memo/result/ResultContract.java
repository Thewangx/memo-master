package com.giot.memo.result;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.Bill;

import java.util.List;

/**
 * the result contract
 * Created by reed on 16/8/5.
 */
public class ResultContract {

    interface View extends BaseView<Presenter> {
        void showBill(List<Bill> bills, int count);
    }

    interface Presenter {

        void start(String key);
    }

}
