package com.giot.memo.data.entity;

import java.util.Date;

/**
 * user model
 * Created by reed on 16/7/25.
 */
public class User {

    public static final int FROM_WE_CHAT = 1;

    public static final int FROM_PHONE = 0;

    private String id;
    private String exid;//附加id(微信等)
    private String nickName;//昵称
    private String phone;
    private String password;
    private String image;
    private int type;//0是手机注册,1是微信用户
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExid() {
        return exid;
    }

    public void setExid(String exid) {
        this.exid = exid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", exid='" + exid + '\'' +
                ", nickName='" + nickName + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                '}';
    }
}
