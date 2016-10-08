package com.giot.memo.result;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.util.DaoUtil;

import java.util.List;

/**
 * the result presenter
 * Created by reed on 16/8/5.
 */
public class ResultPresenter implements ResultContract.Presenter {

    private ResultContract.View mView;

    private String key;

    public ResultPresenter(ResultContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void start(String key) {
        this.key = key;
        loadResult();
    }

    public void loadResult() {
        int count = 0;
        User user = ((App) App.getContext()).getUser();
        List<Bill> bills;
        if (user != null) {
            bills = DaoUtil.getBillDao().queryBuilder().where(DaoUtil.getBillDao().queryBuilder().or(BillDao.Properties.Remark.like("%" + key + "%"), BillDao.Properties.Type.like("%" + key + "%")), BillDao.Properties.UserId.eq(user.getId())).build().list();
        } else {
            bills = DaoUtil.getBillDao().queryBuilder().where(DaoUtil.getBillDao().queryBuilder().or(BillDao.Properties.Remark.like("%" + key + "%"), BillDao.Properties.Type.like("%" + key + "%")), BillDao.Properties.UserId.isNull()).build().list();
        }
        for (Bill bill : bills
                ) {
            if (bill.getMode() == Bill.PAY) {
                count -= bill.getMoney();
            } else {
                count += bill.getMoney();
            }
        }
        mView.showBill(bills, count);
    }
}
