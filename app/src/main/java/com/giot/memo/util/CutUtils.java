package com.giot.memo.util;

import com.giot.memo.data.entity.CountBill;

import java.util.List;

/**
 * Created by Mr.wang on 2016/9/30.
 */

public class CutUtils {
    public static List<CountBill> cut(List<CountBill> countBills){
        for (int i=0;i<countBills.size();i++){
            if (countBills.get(i).getIncome()==0&&countBills.get(i).getTotal()==0){
                countBills.remove(i);
            }
        }
        return countBills;
    }
}
