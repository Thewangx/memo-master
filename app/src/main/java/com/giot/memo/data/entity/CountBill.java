package com.giot.memo.data.entity;

/**
 * 账单统计信息
 * Created by reed on 16/8/22.
 */
public class CountBill {

    private float total;

    private float income;

    private float expenditure;

    private String month;

    private float last;

    public float getLast() {
        return last;
    }

    public void setLast(float last) {
        this.last = last;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public float getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(float expenditure) {
        this.expenditure = expenditure;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "CountBill{" +
                "total=" + total +
                ", income=" + income +
                ", expenditure=" + expenditure +
                ", month=" + month +
                ", last=" + last +
                '}';
    }
}
