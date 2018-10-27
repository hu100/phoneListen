package com.phone.listen;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.phone.listen.greendao.DaoMaster;
import com.phone.listen.greendao.DaoSession;

public class AppApplication extends Application {

    private SQLiteDatabase mDb;
    private DaoSession mDaoSession;
    private static AppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setDateBase();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AppApplication getInstance(){
        return instance;
    }

    private void setDateBase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "phoneNumber-db");
        mDb = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(mDb);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession(){
        return mDaoSession;
    }

    public SQLiteDatabase getDb(){
        return mDb;
    }
}
