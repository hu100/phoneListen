package com.phone.listen.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilityMonitorService extends AccessibilityService {
    String TAG = this.getClass().getSimpleName();
    private String mWindowClassName;
    private String mCurrentPackage;

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
