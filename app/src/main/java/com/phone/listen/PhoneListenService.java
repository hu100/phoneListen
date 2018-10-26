package com.phone.listen;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phone.listen.ui.MainActivity_;

/**
 * 来去电监听服务
 */

@SuppressLint("OverrideAbstract")
public class PhoneListenService extends NotificationListenerService {

    public final String TAG = getClass().getSimpleName();

    public static final String ACTION_REGISTER_LISTENER = "action_register_listener";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        initNotify();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand:");
        registerPhoneStateListener();
        return START_STICKY;
    }

    @Override
    public void onListenerConnected() {
        Log.e(TAG, "onListenerConnected");
        super.onListenerConnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Bundle bundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            bundle = sbn.getNotification().extras;
            String pkgName = sbn.getPackageName();
            String title = bundle.getString("android.title");
            String text = ((Bundle) bundle).getString("android.text");
            Log.e(TAG, "Notification posted [" + pkgName + "]:" + title + " & " + text);
        }
    }

    private void initNotify() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(this, MainActivity_.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 66, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("监听中...")
                .setContentText("正在拦截骚扰电话")
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= 26) {
            String id = "my_channel_01";
            String name = "我是渠道名字";
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId(id);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(1, builder.build());
            notificationManager.notify(1, builder.build());
        }
    }

    private void registerPhoneStateListener() {
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind action: " + intent.getAction());
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        stopSelf();
    }
}
