package com.phone.listen.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
/*
* 白名单
* */
@Entity
public class WhiteListBean {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String number;
    private int len;//长度，如果是11位，说明是整个号码，否则说明是号码的开头len个数字
    private String belongArea;
    @Generated(hash = 1364749623)
    public WhiteListBean(Long id, String name, String number, int len,
            String belongArea) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.len = len;
        this.belongArea = belongArea;
    }
    @Generated(hash = 701801215)
    public WhiteListBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public int getLen() {
        return this.len;
    }
    public void setLen(int len) {
        this.len = len;
    }
    public String getBelongArea() {
        return this.belongArea;
    }
    public void setBelongArea(String belongArea) {
        this.belongArea = belongArea;
    }
}