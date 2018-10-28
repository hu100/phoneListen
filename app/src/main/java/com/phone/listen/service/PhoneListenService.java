package com.phone.listen.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.phone.listen.R;
import com.phone.listen.listener.CustomPhoneStateListener;
import com.phone.listen.listener.ScreenListener;
import com.phone.listen.receiver.ScreenReceiver;
import com.phone.listen.ui.MainActivity_;
import com.phone.listen.util.Constant;
import com.phone.listen.util.FileUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 来去电监听服务
 */

@SuppressLint("OverrideAbstract")
public class PhoneListenService extends NotificationListenerService {

    public final String TAG = getClass().getSimpleName();
    private static final int NOTIFY_ID = 236;
    private static final int NOTIFY_ID2 = 237;
    public static final String ACTION_REGISTER_LISTENER = "action_register_listener";
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        initNotify();
        initScreenReceiver();
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

    private void initWakeLock() {
        if (mWakeLock != null) return;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void initScreenReceiver() {
        ScreenReceiver screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, filter);
        screenReceiver.setScreenListener(new ScreenListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String time = sdf1.format(new Date());
                String[] split = time.split(":");
                int hour = Integer.parseInt(split[0]);
                Log.e(TAG, "onScreenOff: " + time + "..." + hour);
                if (hour < 8 || hour > 22) {
                    releaseWakeLock();
                } else {
                    initWakeLock();
                }

            }

            @Override
            public void onUserPresent() {

            }
        });
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
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent intent = new Intent(this, MainActivity_.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 66, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("监听中...")
                .setContentText("正在拦截骚扰电话")
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0})
                .setSound(null)
                .setDefaults(Notification.DEFAULT_ALL)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        if (Build.VERSION.SDK_INT >= 26) {
            String id = "my_channel_01";
            String name = "我是渠道名字";
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(false);
            mChannel.enableVibration(false);
            mChannel.setVibrationPattern(new long[]{0});
            mChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(mChannel);
            builder.setChannelId(id);
        }
        mNotification = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(NOTIFY_ID, mNotification);
            mNotificationManager.notify(NOTIFY_ID, mNotification);
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
        mNotificationManager.cancel(NOTIFY_ID);
        stopSelf();
        super.onDestroy();
    }
}
