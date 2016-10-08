package com.giot.memo.data.entity;

import android.graphics.drawable.Drawable;

/**
 * 账单分析的模型
 * Created by reed on 16/8/17.
 */
public class Analysis {

    private String type;//类型名称
    private Drawable drawable;//图标
    private float money;//合计金额
    /**
     * {@link Bill#mode}
     */
    private int mode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
