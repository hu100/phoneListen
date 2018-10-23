package com.phone.listen.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CallRecordBean {
    @Id(autoincrement = true)
    private long id;
    private String number;
    private String date;//来电日期
    private String belongArea;//归属地
    private boolean intercepted;//是否被拦截
    @Generated(hash = 1510099120)
    public CallRecordBean(long id, String number, String date, String belongArea,
            boolean intercepted) {
        this.id = id;
        this.number = number;
        this.date = date;
        this.belongArea = belongArea;
        this.intercepted = intercepted;
    }
    @Generated(hash = 727764067)
    public CallRecordBean() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getBelongArea() {
        return this.belongArea;
    }
    public void setBelongArea(String belongArea) {
        this.belongArea = belongArea;
    }
    public boolean getIntercepted() {
        return this.intercepted;
    }
    public void setIntercepted(boolean intercepted) {
        this.intercepted = intercepted;
    }
}
