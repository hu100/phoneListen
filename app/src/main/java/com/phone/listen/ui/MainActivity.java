package com.phone.listen.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.phone.listen.PhoneListenService;
import com.phone.listen.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

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
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnRegister = findViewById(R.id.register_service);
        mBtnExit = findViewById(R.id.btn_exit);
        mTvDescription = findViewById(R.id.tv_description);
        mBtnRegister.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        WebView webView = new WebView(this);
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
                    }
                });
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.register_service:
                mBtnRegister.setText("正在监听");
                mTvDescription.setVisibility(View.VISIBLE);
                mBtnExit.setVisibility(View.VISIBLE);
                registerPhoneStateListener();
                break;
            case R.id.btn_exit:
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void registerPhoneStateListener() {
        Intent intent = new Intent(this, PhoneListenService.class);
        intent.setAction(PhoneListenService.ACTION_REGISTER_LISTENER);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
