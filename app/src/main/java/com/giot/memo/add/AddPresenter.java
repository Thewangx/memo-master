package com.giot.memo.add;

import android.text.TextUtils;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.History;
import com.giot.memo.data.gen.HistoryDao;
import com.giot.memo.util.DaoUtil;

import java.util.Date;
import java.util.List;

/**
 * 增加账单的presenter
 * Created by reed on 16/8/1.
 */
public class AddPresenter implements AddContract.Presenter {

    private int mode;

    private AddContract.View view;

    private String type;

    private boolean isModify = false;

    private Bill mBill;

    private Date mDate;

    @Override
    public void setModify(boolean modify, Bill bill) {
        isModify = modify;
        mBill = bill;
    }

    @Override
    public void setDate(Date date) {
        mDate = date;
    }

    public AddPresenter(AddContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        if (isModify) {
            view.initUIData(mBill);
            view.changeMode(mBill.getMode());
            type = mBill.getType();
            view.setType(type);
        } else {
            mode = Bill.PAY;
            type = "一般";
        }

    }

    /**
     * change the mode of bill.
     * @param mode {@link Bill#PAY} or {@link Bill#INCOME}
     */
    @Override
    public void changeMode(int mode) {
        this.mode = mode;
        type = "一般";
    }

    /**
     * set the bill's property of type.
     * @param type the bill's type, such as normal, restaurant and so on.
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     * save bill in database.
     * Also, save the remark in the database if the remark is not null or empty.
     * when save the remark, if the remark exists in the database, then make the remark's count increase one,
     * or create a new item in the database.
     * @param value bill's money
     * @param remark bill's remark
     */
    @Override
    public void saveBill(String value, String remark) {
        if (isModify) {
            mBill.setSync(Bill.MODIFY);
            mBill.setMode(mode);
            mBill.setRemark(remark);
            mBill.setType(type);
            mBill.setRemark(remark);
            mBill.setMoney(Float.valueOf(value));
            DaoUtil.getBillDao().update(mBill);//update a bill into the database
        } else {
            Bill bill = new Bill();
            bill.setMode(mode);
            bill.setType(type);
            bill.setDate(mDate);
            bill.setRemark(remark);
            bill.setMoney(Float.valueOf(value));
            bill.setSync(Bill.UN_SYNC);
            if (((App) App.getContext()).getUser() != null) {
                bill.setUserId(((App) App.getContext()).getUser().getId());
            }
            DaoUtil.getBillDao().insert(bill);//insert a bill into the database
        }
        if (!TextUtils.isEmpty(remark)) {
            History find = DaoUtil.getHistoryDao().queryBuilder().where(HistoryDao.Properties.From.eq(History.REMARK), HistoryDao.Properties.Content.eq(remark)).build().unique();
            if (find == null) {
                History history = new History();
                history.setContent(remark);
                history.setCount(1);
                history.setFrom(History.REMARK);
                DaoUtil.getHistoryDao().insert(history);//insert a new remark into the database
            } else {
                find.setCount(find.getCount() + 1);
                DaoUtil.getHistoryDao().update(find);//update the remark in the database with the count increasing one
            }
        }
        view.saveBill();
    }

    /**
     * find all remarks which the property of from is remark and order by count desc
     * @return the array of remarks
     */
    @Override
    public String[] loadRemarkHistory() {
        List<History> histories = DaoUtil.getHistoryDao().queryBuilder().where(HistoryDao.Properties.From.eq(History.REMARK)).orderDesc(HistoryDao.Properties.Count).build().list();
        String[] remark = new String[histories.size()];
        for (int i = 0; i < histories.size(); i++) {
            remark[i] = histories.get(i).getContent();
        }
        return remark;
    }
}
