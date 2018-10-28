package com.phone.listen.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phone.listen.R;
import com.phone.listen.base.BaseFragment;
import com.phone.listen.service.PhoneListenService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment {

    String TAG = getClass().getSimpleName();
    @ViewById(R.id.btn_register_service)
    Button mBtnRegisterService;
    @ViewById(R.id.btn_exit)
    Button mBtnExit;
    @ViewById(R.id.tv_description)
    TextView mTvDescription;

    @AfterViews
    public void init() {
    }

    @Click({R.id.btn_register_service, R.id.btn_exit, R.id.tv_description})
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_register_service:
                mBtnRegisterService.setText("正在监听");
                mBtnRegisterService.setBackgroundColor(getResources().getColor(R.color.green_bg));
                mBtnExit.setVisibility(View.VISIBLE);
                mTvDescription.setVisibility(View.VISIBLE);
                registerPhoneStateListener();
                if (!isEnabled()) {
                    openNotificationListenSettings();
                }
                break;
            case R.id.btn_exit:
                Intent intent = new Intent(getActivity(), PhoneListenService.class);
                getActivity().stopService(intent);
                openNotificationListenSettings();
                getActivity().finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                break;
            case R.id.tv_description:
                openNotificationListenSettings();
                break;
        }
    }

    private void registerPhoneStateListener() {
        Intent intent = new Intent(getActivity(), PhoneListenService.class);
        intent.setAction(PhoneListenService.ACTION_REGISTER_LISTENER);
        getActivity().startService(intent);
    }

    private void openNotificationListenSettings() {
        try {
            Intent intent;
            intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "打开界面出错", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    //判断是否有通知使用权
    private boolean isEnabled() {
        String str = mContext.getPackageName();
        String localObject = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
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
}
