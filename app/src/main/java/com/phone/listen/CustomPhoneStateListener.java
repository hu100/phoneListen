package com.phone.listen;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phone.listen.bean.CallRecordBean;
import com.phone.listen.bean.TelephoneNumberZone;
import com.phone.listen.bean.WhiteListBean;
import com.phone.listen.greendao.CallRecordBeanDao;
import com.phone.listen.greendao.TelephoneNumberZoneDao;
import com.phone.listen.greendao.WhiteListBeanDao;
import com.phone.listen.util.HangUpTelephonyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 来去电监听
 */

public class CustomPhoneStateListener extends PhoneStateListener {

    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private final CallRecordBeanDao mRecordBeanDao;
    private final SimpleDateFormat mDateFormat;
    private final WhiteListBeanDao mWhiteListDao;
    private final TelephoneNumberZoneDao mZoneDao;

    public CustomPhoneStateListener(Context context) {
        mContext = context;
        mRecordBeanDao = AppApplication.getInstance().getDaoSession().getCallRecordBeanDao();
        mWhiteListDao = AppApplication.getInstance().getDaoSession().getWhiteListBeanDao();
        mZoneDao = AppApplication.getInstance().getDaoSession().getTelephoneNumberZoneDao();
        mDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                break;
            case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                Log.e(TAG, "onCallStateChanged:" + incomingNumber);
                String format = mDateFormat.format(new Date());
                String[] split = format.split(" ");
                String date = split[0];
                String time = split[1];
                CallRecordBean recordBean = new CallRecordBean();
                recordBean.setNumber(incomingNumber);
                recordBean.setDate(date);
                recordBean.setTime(time);
                if (incomingNumber.startsWith("0")) {
                    TelephoneNumberZone numberZone = queryCityByZone(incomingNumber.substring(0, 3));
                    if (numberZone != null && numberZone.getCity()!= null) {
                        recordBean.setBelongArea(numberZone.getCity());
                    }else {
                        recordBean.setBelongArea("中国");
                    }
                } else {
                    recordBean.setBelongArea("中国");
                }
                if (shouldIntercept(incomingNumber)) {
                    boolean endCall = HangUpTelephonyUtil.endCall(mContext, incomingNumber.trim());
                    recordBean.setIntercepted(endCall);
                    recordBean.setNeedIntercept(true);
                } else {
                    recordBean.setNeedIntercept(false);
                    recordBean.setIntercepted(false);
                }
                mRecordBeanDao.insert(recordBean);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:    // 来电接通 或者 去电，去电接通  但是没法区分
                break;
        }
    }

    /*
     * 通过区号查城市
     * */
    private TelephoneNumberZone queryCityByZone(String zone) {
        if (zone == null || zone.length() < 3) {
            return null;
        }
        TelephoneNumberZone numberZone = null;
        String city;
        if (zone.length() == 3) {
            switch (zone) {
                case "010":
                    city = "北京";
                    break;
                case "020":
                    city = "广东广州";
                    break;
                case "021":
                    city = "上海";
                    break;
                case "022":
                    city = "天津";
                    break;
                case "023":
                    city = "重庆";
                    break;
                case "024":
                    city = "辽宁沈阳";
                    break;
                case "025":
                    city = "江西南京";
                case "027":
                    city = "湖北武汉";
                case "028":
                    city = "四川成都";
                case "029":
                    city = "陕西西安";
                    break;
                default:
                    city = null;
            }
            if (city != null) {
                numberZone = new TelephoneNumberZone(null,zone,city);
            }
        } else {
            numberZone = mZoneDao.queryBuilder().where(TelephoneNumberZoneDao.Properties.Zone.eq(zone.substring(0, 4))).unique();
        }
        return numberZone;
    }

    private boolean shouldIntercept(String incomingNumber) {
        if (incomingNumber == null) {
            return true;
        }
        List<WhiteListBean> whiteListBeans = mWhiteListDao.loadAll();
        for (WhiteListBean bean : whiteListBeans) {
            if (bean.getNumber().equals(incomingNumber.substring(0, bean.getLen()))) {
                return false;
            }
        }
        return true;
    }

}
