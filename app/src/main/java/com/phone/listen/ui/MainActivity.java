package com.phone.listen.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.phone.listen.AppApplication;
import com.phone.listen.R;
import com.phone.listen.base.BaseActivity;
import com.phone.listen.bean.TelephoneNumberZone;
import com.phone.listen.bean.WhiteListBean;
import com.phone.listen.greendao.TelephoneNumberZoneDao;
import com.phone.listen.ui.fragment.InterceptSettingFragment_;
import com.phone.listen.ui.fragment.MainFragment_;
import com.phone.listen.ui.fragment.PhoneRecordFragment_;
import com.phone.listen.util.PreferenceUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    String TAG = "MainActivity";

    @ViewById(R.id.activity_main)
    LinearLayout mActivityMain;
    @ViewById(R.id.vp_main)
    ViewPager mVpMain;
    @ViewById(R.id.tab)
    TabLayout mTab;

    RxPermissions rxPermissions;
    //需要检查的权限
    private final String[] mPermissionList = new String[]{
            //获取电话状态
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionAndThenLoad();
        }
    }

    @AfterViews
    public void init() {
        initViewPage();
        initNumberZone();
    }

    @Background
    public void initNumberZone() {
        if (PreferenceUtils.getInstance(this).getFirstStart()) {
            PreferenceUtils.getInstance(this).setFirstStart(true);
            WhiteListBean bean = new WhiteListBean(null, "", "020", 3, "广东广州");
            AppApplication.getInstance().getDaoSession().getWhiteListBeanDao().insert(bean);
            //往数据库写入区号
            TelephoneNumberZoneDao zoneDao = AppApplication.getInstance().getDaoSession().getTelephoneNumberZoneDao();
            zoneDao.deleteAll();
            AssetManager assets = getResources().getAssets();
            try {
                InputStream is = assets.open("zone.json");
                int size = is.available();
                byte[] bytes = new byte[size];
                is.read(bytes);
                is.close();
                String content = new String(bytes);
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String city = jsonArray.getJSONObject(i).optString("city", "中国");
                    String zone = jsonArray.getJSONObject(i).getString("zone");
                    TelephoneNumberZone numberZone = new TelephoneNumberZone(null, zone, city);
                    zoneDao.insert(numberZone);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initViewPage() {
        List<String> tabTexts = new ArrayList<>();
        tabTexts.add("首页");
        tabTexts.add("记录");
        tabTexts.add("设置");
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MainFragment_());
        fragmentList.add(new PhoneRecordFragment_());
        fragmentList.add(new InterceptSettingFragment_());
        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());
        mVpMain.setOffscreenPageLimit(6);
        mVpMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabTexts.get(position);
            }
        });
        mTab.setupWithViewPager(mVpMain);
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
    }

    private void startOtherApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.phone.hangup", "com.phone.hangup.MainActivity");
        intent.setComponent(cn);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
