package com.giot.memo.util;

import com.giot.memo.data.entity.CountBill;

import java.util.List;

/**
 * Created by Mr.wang on 2016/10/4.
 */

public class MaxValueUtil {
    public static int getMaxValue(List<CountBill> countBills){
        int maxValue = 0;
        for (int i = 0; i < countBills.size(); i++) {
            if (maxValue < countBills.get(i).getIncome()) {
                maxValue = (int)(countBills.get(i).getIncome());
            }
        }
        for (int i = 0; i < countBills.size(); i++) {
            if (maxValue < countBills.get(i).getExpenditure()) {
                maxValue = (int)(countBills.get(i).getExpenditure());
            }
        }
        return maxValue;
    }
}
