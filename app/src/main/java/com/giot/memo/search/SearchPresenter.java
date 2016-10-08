package com.giot.memo.search;

import android.text.TextUtils;

import com.giot.memo.data.entity.History;
import com.giot.memo.data.gen.HistoryDao;
import com.giot.memo.util.DaoUtil;

import java.util.List;

/**
 * search presenter
 * Created by reed on 16/8/4.
 */
public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;
    private List<History> histories;


    public SearchPresenter(SearchContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {
        histories = DaoUtil.getHistoryDao().queryBuilder().where(HistoryDao.Properties.From.eq(History.SEARCH)).orderDesc(HistoryDao.Properties.Count).build().list();
        mView.loadHistories(histories);
    }

    /**
     * 设置点击的历史记录到输入框中
     * @param position 点击的位置
     */
    @Override
    public void setSearchKey(int position) {
        mView.setSearchKey(histories.get(position).getContent());
        mView.showResult();
    }

    /**
     * 删除单条历史记录
     * @param position 历史记录条目位置
     */
    @Override
    public void delHistory(int position) {
        DaoUtil.getHistoryDao().delete(histories.get(position));
        histories.remove(position);
        mView.delHistory(position);
    }

    /**
     * 保存搜索历史记录到数据库
     * @param key 搜索关键字
     */
    @Override
    public void saveHistory(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        History find = DaoUtil.getHistoryDao().queryBuilder().where(HistoryDao.Properties.From.eq(History.SEARCH), HistoryDao.Properties.Content.eq(key)).build().unique();
        if (find == null) {
            History history = new History();
            history.setContent(key);
            history.setCount(1);
            history.setFrom(History.SEARCH);
            DaoUtil.getHistoryDao().insert(history);//如果关键字不存在, 则插入一条新纪录
        } else {
            find.setCount(find.getCount() + 1);//如果关键字已存在, 则搜索次数加1
            DaoUtil.getHistoryDao().update(find);
        }
    }
}
