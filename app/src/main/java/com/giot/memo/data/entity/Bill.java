package com.giot.memo.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

/**
 * 账单模型
 * Created by reed on 16/7/23.
 */
@Entity
public class Bill implements Serializable {

    @Transient
    public static final int INCOME = 0;//收入
    @Transient
    public static final int PAY = 1;//支出
    @Transient
    public static final int SYNC = 1;//同步
    @Transient
    public static final int UN_SYNC = 0;//未同步
    @Transient
    public static final int MODIFY = 2;//修改状态

    @Id(autoincrement = true)
    private Long id;
    private float money;
    private int mode;//0是收入，1是支出, 2是修改过
    private String type;
    private String remark;
    @Property(nameInDb = "CREATE_TIME")
    private Date date;
    private String userId;
    private int sync;//该数据是否同步过, 0是未同步, 1是同步

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public float getMoney() {
        return this.money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSync() {
        return this.sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    @Generated(hash = 1572921443)
    public Bill(Long id, float money, int mode, String type, String remark,
            Date date, String userId, int sync) {
        this.id = id;
        this.money = money;
        this.mode = mode;
        this.type = type;
        this.remark = remark;
        this.date = date;
        this.userId = userId;
        this.sync = sync;
    }

    @Generated(hash = 1399599325)
    public Bill() {
    }
}
