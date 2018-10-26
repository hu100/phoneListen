package com.phone.listen.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CallRecordBean {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String number;
    private String date;//来电日期
    private String time;
    private String belongArea;//归属地
    private boolean needIntercept;//是否需要拦截
    private boolean intercepted;//是否被拦截
    @Generated(hash = 64293534)
    public CallRecordBean(Long id, String name, String number, String date,
            String time, String belongArea, boolean needIntercept,
            boolean intercepted) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.date = date;
        this.time = time;
        this.belongArea = belongArea;
        this.needIntercept = needIntercept;
        this.intercepted = intercepted;
    }
    @Generated(hash = 727764067)
    public CallRecordBean() {
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
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getBelongArea() {
        return this.belongArea;
    }
    public void setBelongArea(String belongArea) {
        this.belongArea = belongArea;
    }
    public boolean getNeedIntercept() {
        return this.needIntercept;
    }
    public void setNeedIntercept(boolean needIntercept) {
        this.needIntercept = needIntercept;
    }
    public boolean getIntercepted() {
        return this.intercepted;
    }
    public void setIntercepted(boolean intercepted) {
        this.intercepted = intercepted;
    }
}
