package com.phone.listen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.phone.listen.listener.ScreenListener;

public class ScreenReceiver extends BroadcastReceiver {

    String TAG = ScreenReceiver.class.getSimpleName();
    ScreenListener mScreenListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_USER_PRESENT.equals(action)){
            Log.e(TAG, "onReceive:解锁");
            if (mScreenListener != null){
                mScreenListener.onUserPresent();
            }
        }else if (Intent.ACTION_SCREEN_OFF.equals(action)){
            Log.e(TAG, "onReceive:息屏");
            if (mScreenListener != null){
                mScreenListener.onScreenOff();
            }
        }else if (Intent.ACTION_SCREEN_ON.equals(action)){
            Log.e(TAG, "onReceive:亮屏");
            if (mScreenListener != null){
                mScreenListener.onScreenOn();
            }
        }
    }

    public void setScreenListener(ScreenListener screenListener) {
        mScreenListener = screenListener;
    }
}
