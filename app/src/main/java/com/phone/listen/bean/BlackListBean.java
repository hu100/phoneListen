package com.phone.listen.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BlackListBean {

    private String number;

    @Generated(hash = 129846173)
    public BlackListBean(String number) {
        this.number = number;
    }

    @Generated(hash = 1796685466)
    public BlackListBean() {
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
