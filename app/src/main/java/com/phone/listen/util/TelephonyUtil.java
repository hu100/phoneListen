package com.phone.listen.util;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.phone.listen.bean.TelephoneNumberZone;
import com.phone.listen.bean.WhiteListBean;
import com.phone.listen.greendao.TelephoneNumberZoneDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 封装挂断电话接口
 */

public class TelephonyUtil {

    static String TAG = "TelephonyUtil";

    public static boolean endCall(Context context) {
        boolean endCallSuccess = false;
        ITelephony telephonyService = getTelephonyService(context);
        try {
            if (telephonyService != null) {
                endCallSuccess = telephonyService.endCall();
                Log.e(TAG, "endCall: " + endCallSuccess);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "endCall remoteException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "endCalException: " + e.getMessage());
            e.printStackTrace();
        }
        if (endCallSuccess == false) {
            Executor eS = Executors.newSingleThreadExecutor();
            eS.execute(new Runnable() {
                @Override
                public void run() {
                    disconnectCall();
                }
            });
        }
        return endCallSuccess;
    }

    private static ITelephony getTelephonyService(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class clazz;
        try {
            clazz = Class.forName(telephonyManager.getClass().getName());
            Method method = clazz.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            return (ITelephony) method.invoke(telephonyManager);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean disconnectCall() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("service call phone 5 \n");
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    // 使用endCall挂断不了，再使用killCall反射调用再挂一次
    public static boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception ex) { // Many things can go wrong with reflection calls
            return false;
        }
        return true;
    }

    public static boolean endCall2() {
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            boolean endCall = telephony.endCall();
            Log.e(TAG, "endCall2: " + endCall);
            return endCall;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean shouldIntercept(List<WhiteListBean> whiteList, String incomingNumber) {
        if (incomingNumber == null) {
            return true;
        }
        if (incomingNumber.startsWith("0")) {
            if (whiteList == null) return true;
            for (WhiteListBean bean : whiteList) {
                if (bean.getNumber().equals(incomingNumber.substring(0, bean.getLen()))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /*
     * 通过区号查城市
     * */
    public static TelephoneNumberZone queryCityByZone(String zone, TelephoneNumberZoneDao zoneDao) throws Exception {
        if (zone == null || zone.length() < 3) {
            throw new Exception("被查询的区号长度小于3");
        }
        String prefix;
        if (zone.startsWith("01") || zone.startsWith("02")) {
            prefix = zone.substring(0, 3);
        } else {
            prefix = zone.substring(0, 4);
        }
        List<TelephoneNumberZone> zones = zoneDao.queryBuilder()
                .where(TelephoneNumberZoneDao.Properties.Zone.eq(prefix))
                .build()
                .list();
        if (zones != null && zones.size() > 0) {
            return zones.get(0);
        } else {
            return null;
        }
    }

}
