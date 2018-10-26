package com.phone.listen.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phone.listen.PhoneListenService;
import com.phone.listen.R;
import com.phone.listen.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    String TAG = "MainActivity";
    private Button mBtnRegister;
    private TextView mTvDescription;
    private Button mBtnExit;
    RxPermissions rxPermissions;

    //需要检查的权限
    private final String[] mPermissionList = new String[]{
            //获取电话状态
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnRegister = findViewById(R.id.register_service);
        mBtnExit = findViewById(R.id.btn_exit);
        mTvDescription = findViewById(R.id.tv_description);
        mTvDescription.setOnClickListener(this::onClick);
        mBtnRegister.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        rxPermissions = new RxPermissions(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionAndThenLoad();
        }
    }

    @SuppressLint("CheckResult")
    private void checkPermissionAndThenLoad() {
        rxPermissions.request(mPermissionList)
                .subscribe(granted -> {
                    if (granted) {
                    } else {
                        Toast.makeText(this, "必须授予权限", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.register_service:
                mBtnRegister.setText("正在监听");
                mBtnRegister.setBackgroundColor(Color.GREEN);
                mTvDescription.setVisibility(View.VISIBLE);
                mBtnExit.setVisibility(View.VISIBLE);
                registerPhoneStateListener();
                if (!isEnabled()) {
                    openNotificationListenSettings();
                }
                break;
            case R.id.btn_exit:
                openNotificationListenSettings();
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                break;
            case R.id.tv_description:
                openNotificationListenSettings();
                break;
        }
    }

    //判断是否有通知使用权
    private boolean isEnabled() {
        String str = getPackageName();
        String localObject = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(localObject)) {
            Log.e(TAG, "isEnabled:" + localObject);
            String[] strArr = (localObject).split(":");
            int i = 0;
            while (i < strArr.length) {
                ComponentName localComponentName = ComponentName.unflattenFromString(strArr[i]);
                if ((localComponentName != null) && (TextUtils.equals(str, localComponentName.getPackageName())))
                    return true;
                i += 1;
            }
        }
        return false;
    }

    private void startOtherApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.phone.hangup", "com.phone.hangup.MainActivity");
        intent.setComponent(cn);
        context.startActivity(intent);
    }

    private void openNotificationListenSettings() {
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void registerPhoneStateListener() {
        Intent intent = new Intent(this, PhoneListenService.class);
        intent.setAction(PhoneListenService.ACTION_REGISTER_LISTENER);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
