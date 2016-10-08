package com.giot.memo.add;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.Bill;

import java.util.Date;

/**
 * 添加账单的Contract
 * Created by reed on 16/8/1.
 */
public interface AddContract {

    interface View extends BaseView<Presenter> {

        void changeMode(int mode);

        void saveBill();

        void initUIData(Bill bill);

        void setType(String type);
    }

    interface Presenter extends BasePresenter {
        void changeMode(int mode);

        void setType(String type);

        void saveBill(String value, String remark);

        String[] loadRemarkHistory();

        void setModify(boolean modify, Bill bill);

        void setDate(Date date);
    }
}
