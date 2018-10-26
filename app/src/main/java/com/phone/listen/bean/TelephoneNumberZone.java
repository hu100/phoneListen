package com.phone.listen.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TelephoneNumberZone {

    @Id(autoincrement = true)
    Long id;
    private String zone;//区号
    private String city;
    @Generated(hash = 451978844)
    public TelephoneNumberZone(Long id, String zone, String city) {
        this.id = id;
        this.zone = zone;
        this.city = city;
    }
    @Generated(hash = 1138729408)
    public TelephoneNumberZone() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getZone() {
        return this.zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }

}
