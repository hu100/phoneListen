package com.phone.listen;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phone.listen.bean.CallRecordBean;
import com.phone.listen.greendao.CallRecordBeanDao;
import com.phone.listen.util.HangUpTelephonyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 来去电监听
 */

public class CustomPhoneStateListener extends PhoneStateListener {

    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private final CallRecordBeanDao mRecordBeanDao;
    private final SimpleDateFormat mDateFormat;

    public CustomPhoneStateListener(Context context) {
        mContext = context;
        mRecordBeanDao = AppApplication.getInstance().getDaoSession().getCallRecordBeanDao();
        mDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
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
                boolean endCall = HangUpTelephonyUtil.endCall(mContext, incomingNumber.trim());
                Log.e(TAG, "onCallStateChanged:" + incomingNumber);
                String date = mDateFormat.format(new Date());
                CallRecordBean recordBean = new CallRecordBean(null,incomingNumber,date,"未知归属地",endCall);
                mRecordBeanDao.insert(recordBean);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:    // 来电接通 或者 去电，去电接通  但是没法区分
                break;
        }
    }

}
