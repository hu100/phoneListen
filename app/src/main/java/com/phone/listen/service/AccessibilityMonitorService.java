package com.phone.listen.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.phone.listen.listener.CustomPhoneStateListener;
import com.phone.listen.util.Constant;
import com.phone.listen.util.FileUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessibilityMonitorService extends AccessibilityService {
    String TAG = this.getClass().getSimpleName();
    private String mWindowClassName;
    private String mCurrentPackage;

    @Override
    public void onCreate() {
        super.onCreate();
        registerPhoneStateListener();
//        startHeartbeat();
    }

    private void startHeartbeat() {
        new Thread(() -> {
            while (true) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                FileUtil.writeToFile(Constant.LOG_PATH, "心跳.txt", sdf.format(new Date()), true);
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void registerPhoneStateListener() {
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        switch (type) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mWindowClassName = event.getClassName().toString();
                mCurrentPackage = event.getPackageName() == null ? "" : event.getPackageName().toString();
                Log.e(TAG, "onAccessibilityEvent: "+mWindowClassName+"..."+mCurrentPackage);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
