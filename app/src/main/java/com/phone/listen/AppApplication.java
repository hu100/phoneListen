package com.phone.listen;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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
