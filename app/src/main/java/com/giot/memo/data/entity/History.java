package com.giot.memo.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * 历史记录
 * Created by reed on 16/7/25.
 */
@Entity
public class History {

    @Transient
    public static final int SEARCH = 0;
    @Transient
    public static final int REMARK = 1;

    @Id(autoincrement = true)
    private Long id;
    private String content;
    private int from;//0是来自搜索,1是来自账单备注
    private int count;//添加次数

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFrom() {
        return this.from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1016355107)
    public History(Long id, String content, int from, int count) {
        this.id = id;
        this.content = content;
        this.from = from;
        this.count = count;
    }

    @Generated(hash = 869423138)
    public History() {
    }
}
