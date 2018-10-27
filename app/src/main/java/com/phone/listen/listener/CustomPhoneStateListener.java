package com.phone.listen.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phone.listen.AppApplication;
import com.phone.listen.bean.CallRecordBean;
import com.phone.listen.bean.TelephoneNumberZone;
import com.phone.listen.greendao.CallRecordBeanDao;
import com.phone.listen.greendao.TelephoneNumberZoneDao;
import com.phone.listen.greendao.WhiteListBeanDao;
import com.phone.listen.util.TelephonyUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 来去电监听
 */

public class CustomPhoneStateListener extends PhoneStateListener {

    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private final CallRecordBeanDao mRecordBeanDao;
    private final WhiteListBeanDao mWhiteListDao;
    private final TelephoneNumberZoneDao mZoneDao;

    public CustomPhoneStateListener(Context context) {
        mContext = context;
        mRecordBeanDao = AppApplication.getInstance().getDaoSession().getCallRecordBeanDao();
        mWhiteListDao = AppApplication.getInstance().getDaoSession().getWhiteListBeanDao();
        mZoneDao = AppApplication.getInstance().getDaoSession().getTelephoneNumberZoneDao();
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
                handleNumber(incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:    // 来电接通 或者 去电，去电接通  但是没法区分
                break;
        }
    }

    private void handleNumber(String incomingNumber) {
        Log.e(TAG, "onCallStateChanged:" + incomingNumber);
        DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        String[] split = format.format(new Date()).split(" ");
        String date = split[0];
        String time = split[1];
        CallRecordBean recordBean = new CallRecordBean();
        recordBean.setNumber(incomingNumber);
        recordBean.setDate(date);
        recordBean.setTime(time);
        if (incomingNumber.startsWith("0")) {
            try {
                TelephoneNumberZone numberZone = TelephonyUtil.queryCityByZone(incomingNumber, mZoneDao);
                recordBean.setBelongArea(numberZone == null ? "中国" : numberZone.getCity());
            } catch (Exception e) {
                e.printStackTrace();
                recordBean.setBelongArea("中国");
            }
        } else {
            recordBean.setBelongArea("中国");
        }
        if (TelephonyUtil.shouldIntercept(mWhiteListDao.loadAll(), incomingNumber)) {
            boolean endCall = TelephonyUtil.endCall(mContext);
            recordBean.setIntercepted(endCall);
            recordBean.setNeedIntercept(true);
        }
        mRecordBeanDao.insert(recordBean);
    }

}
