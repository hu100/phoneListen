package com.phone.listen.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "phone_listen";
    private static SharedPreferences mSharedPreferences;
    private static PreferenceUtils mPreferenceUtils;
    private static SharedPreferences.Editor editor;
    private static String FIRST_START = "first_start";

    private PreferenceUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 单例模式，获取instance实例
     *
     * @param
     * @return
     */
    public static PreferenceUtils getInstance(Context context) {
        if (mPreferenceUtils == null) {
            synchronized (PreferenceUtils.class) {
                if (mPreferenceUtils == null) {
                    mPreferenceUtils = new PreferenceUtils(context);
                }
            }
        }
        editor = mSharedPreferences.edit();
        return mPreferenceUtils;
    }

    public static void setFirstStart(boolean firstStart) {
        editor.putBoolean(FIRST_START, false);
        editor.commit();
    }

    public boolean getFirstStart() {
        return mSharedPreferences.getBoolean(FIRST_START, true);
    }

}
