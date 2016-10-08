package com.giot.memo.analysis.detail;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;

import com.giot.memo.App;
import com.giot.memo.R;
import com.giot.memo.data.entity.Analysis;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.User;
import com.giot.memo.data.gen.BillDao;
import com.giot.memo.util.DaoUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 账单分析详情presenter
 * Created by reed on 16/8/16.
 */
public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View mView;
    private static final int SPINNER_MODE_MONTH = 0;//月模式
    private int spinnerMode = SPINNER_MODE_MONTH;//spinner模式, 分为周模式和月模式
    private int billMode = Bill.PAY;
    private Date date;//当前日期(不是当天日期)
    private Date startDate;//分析日期的开始时间
    private Date endDate;//分析日期的结束时间
    private Context context;

    public DetailPresenter(DetailContract.View mView, Context context) {
        this.mView = mView;
        mView.setPresenter(this);
        this.context = context;
    }

    @Override
    public void start() {
        date = new Date();
        setSpinnerItem();
    }

    /**
     * 设置spinner模式
     * @param spinnerMode {@link #spinnerMode}
     */
    @Override
    public void setSpinnerMode(int spinnerMode) {
        this.spinnerMode = spinnerMode;
        analysisData();

    }

    /**
     * 设置订单分析的模式{@link Bill#mode}
     */
    @Override
    public void switchBillMode() {
        String text;
        if (billMode == Bill.PAY) {
            billMode = Bill.INCOME;
            text = "收入";
        } else {
            billMode = Bill.PAY;
            text = "支出";
        }
        mView.switchChart(text);
        analysisData();
    }

    @Override
    public void preDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (spinnerMode == SPINNER_MODE_MONTH) {
            calendar.add(Calendar.MONTH, -1);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -7);
        }
        date = calendar.getTime();
        setSpinnerItem();
    }

    @Override
    public void nextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (spinnerMode == SPINNER_MODE_MONTH) {
            calendar.add(Calendar.MONTH, 1);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        }
        date = calendar.getTime();
        setSpinnerItem();
    }

    //设置spinner的arrays
    private void setSpinnerItem() {
        String[] item = new String[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        item[0] = (calendar.get(Calendar.MONTH) + 1) + "月";
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
        int start = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        int end = calendar.get(Calendar.DAY_OF_MONTH);
        item[1] = start + "日---" + end + "日";
        mView.updateSpinner(item, spinnerMode);
        analysisData();//更新数据
    }

    //分析数据库中账单数据
    private void analysisData() {
        User user = ((App) App.getContext()).getUser();
        setDateRange();
        List<Bill> bills;
        if (user != null) {
            bills = DaoUtil.getBillDao().queryBuilder().where(
                    BillDao.Properties.UserId.eq(user.getId()),
                    BillDao.Properties.Mode.eq(billMode),
                    BillDao.Properties.Date.between(startDate, endDate)).build().list();
        } else {
            bills = DaoUtil.getBillDao().queryBuilder().where(
                    BillDao.Properties.UserId.isNull(),
                    BillDao.Properties.Mode.eq(billMode),
                    BillDao.Properties.Date.between(startDate, endDate)).build().list();
        } //获取区间范围内的所有账单
        Map<String, Float> map = new TreeMap<>();
        for(Bill bill : bills) {
            if (map.containsKey(bill.getType())) {
                map.put(bill.getType(), map.get(bill.getType()) + bill.getMoney());
            } else {
                map.put(bill.getType(), bill.getMoney());
            }
        }//将账单按照类型进行分类
        List<Map.Entry<String, Float>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> lhs, Map.Entry<String, Float> rhs) {
                return rhs.getValue().compareTo(lhs.getValue());
            }
        });//分类完成后按照金额进行排序
        List<Analysis> analysisList = new ArrayList<>();
        int count = 0;
        float value = 0;
        float total = 0;
        String type;

        //获取类型图标数组
        TypedArray images;
        String[] types;
        if (billMode == Bill.INCOME) {
            types = context.getResources().getStringArray(R.array.type_income);
            images = context.getResources().obtainTypedArray(R.array.type_income_ic);
        } else {
            types = context.getResources().getStringArray(R.array.type_expenditure);
            images = context.getResources().obtainTypedArray(R.array.type_expenditure_ic);
        }

        for (Map.Entry<String, Float> mapping : list) {
            count++;
            if (count >= 6) {
                value += mapping.getValue();
                if (count >= list.size()) {
                    type = "其他";
                } else {
                    continue;
                }
            } else {
                type = mapping.getKey();
                value = mapping.getValue();
            } //取出前五类类型, 后者都统一归为其他类
            Analysis analysis = new Analysis();
            analysis.setMode(billMode);
            analysis.setType(type);
            analysis.setMoney(value);
            total += value;
            if (!type.equals("其他")) {
                //查找对应的图标
                int temp = -1;
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equals(type)) {
                        temp = i;
                        break;
                    }
                }
                analysis.setDrawable(images.getDrawable(temp));
            } else {
                analysis.setDrawable(ContextCompat.getDrawable(context, R.mipmap.other));
            }
            analysisList.add(analysis);
        }
        images.recycle();
        mView.updateChart(getPercent(analysisList, total));
        mView.updateRecycler(analysisList, getCount(total));
    }


    //根据当前日期计算需要分析的区间范围
    private void setDateRange() {
        if (spinnerMode == SPINNER_MODE_MONTH) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            endDate = calendar.getTime();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            int day = calendar.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            endDate = calendar.getTime();
        }
    }

    //获取总计
    private String getCount(float total) {
        String result;
        if (billMode == Bill.PAY) {
            result = "- " + total;
        } else {
            result = "+ " + total;
        }
        if (result.charAt(result.length() - 1) == '0') {//判断倒数第一位是否为0
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

    private float[] getPercent(List<Analysis> analysisList, float count) {
        float[] percent = new float[analysisList.size()];
        for (int i = 0; i < analysisList.size(); i++) {
            percent[i] = analysisList.get(i).getMoney() / count;
        }
        return percent;
    }
}
