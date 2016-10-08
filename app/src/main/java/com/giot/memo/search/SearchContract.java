package com.giot.memo.search;

import com.giot.memo.BasePresenter;
import com.giot.memo.BaseView;
import com.giot.memo.data.entity.History;

import java.util.List;

/**
 * search Contract
 * Created by reed on 16/8/4.
 */
public class SearchContract {

    interface View extends BaseView<Presenter> {
        void showResult();
        void loadHistories(List<History> histories);
        void setSearchKey(String key);
        void delHistory(int position);
    }

    interface Presenter extends BasePresenter {
        void setSearchKey(int position);
        void delHistory(int position);
        void saveHistory(String key);
    }
}
