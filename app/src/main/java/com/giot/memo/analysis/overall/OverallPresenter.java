package com.giot.memo.analysis.overall;

import android.database.Cursor;

import com.giot.memo.App;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.CountBill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.util.CutUtils;
import com.giot.memo.util.DaoUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 总体分析Presenter
 * Created by reed on 16/8/16.
 */
public class OverallPresenter implements OverallContract.Presenter {

    private OverallContract.View mView;

    private List<CountBill> countBills;
    private List<CountBill> countBillsCut;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.CHINA);

    public OverallPresenter(OverallContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        countBills = new ArrayList<>();
        Date firstDate;
        Bill firstBill = DaoUtil.getBillDao().queryBuilder().orderAsc(BillDao.Properties.Date).limit(1).build().unique();
        if (firstBill == null) {
            return;
        }
        firstDate = firstBill.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        Bill endBill = DaoUtil.getBillDao().queryBuilder().orderDesc(BillDao.Properties.Date).limit(1).build().unique();
        Calendar end = Calendar.getInstance();
        end.setTime(endBill.getDate());
        end.add(Calendar.MONTH, 1);
        while (!format.format(calendar.getTime()).equals(format.format(end.getTime()))) {
            CountBill countBill = getData(calendar.getTime());
            if (countBills.size() == 0) {
                countBill.setLast(0);
            } else {
                countBill.setLast(countBills.get(countBills.size() - 1).getTotal());
            }
            countBills.add(countBill);
            calendar.add(Calendar.MONTH, 1);
        }
        mView.initChart(countBills);
        countBillsCut=CutUtils.cut(countBills);
        mView.showDetail(countBillsCut);
    }

    private CountBill getData(Date startDate) {
        User user = ((App) App.getContext()).getUser();
        String userSql;
        if (user != null) {
            userSql = " = " + user.getId();
        } else {
            userSql = " is null";
        }
        CountBill countBill = new CountBill();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        countBill.setMonth(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        String sql = "select sum(" +
                BillDao.Properties.Money.columnName +
                ") from " +
                BillDao.TABLENAME +
                " where " +
                BillDao.Properties.Mode.columnName +
                " = " +
                Bill.INCOME +
                " and " +
                BillDao.Properties.Date.columnName +
                " between " +
                formatInit(startDate).getTime() +
                " and " +
                formatInit(endDate).getTime() +
                " and " +
                BillDao.Properties.UserId.columnName +
                userSql;
        Cursor cursor = DaoUtil.getBillDao().getDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            countBill.setIncome(cursor.getFloat(0));
        } else {
            countBill.setIncome(0);
        }
        cursor.close();
        sql = "select sum(" +
                BillDao.Properties.Money.columnName +
                ") from " +
                BillDao.TABLENAME +
                " where " +
                BillDao.Properties.Mode.columnName +
                " = " +
                Bill.PAY +
                " and " +
                BillDao.Properties.Date.columnName +
                " between " +
                formatInit(startDate).getTime() +
                " and " +
                formatInit(endDate).getTime() +
                " and " +
                BillDao.Properties.UserId.columnName +
                userSql;
        cursor = DaoUtil.getBillDao().getDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            countBill.setExpenditure(cursor.getFloat(0));
        } else {
            countBill.setExpenditure(0);
        }
        cursor.close();
        countBill.setTotal(countBill.getIncome() - countBill.getExpenditure());
        return countBill;
    }

    private Date formatInit(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public void showDetail(int position) {
        mView.showDetail(countBills);
    }
}
