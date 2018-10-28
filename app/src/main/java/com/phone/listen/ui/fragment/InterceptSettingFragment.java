package com.phone.listen.ui.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.phone.listen.R;
import com.phone.listen.base.BaseFragment;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_intercept_setting)
public class InterceptSettingFragment extends BaseFragment {

    @ViewById(R.id.tv_whitelist_set)
    TextView mTvWhitelistSet;
    @ViewById(R.id.tv_blacklist_set)
    TextView mTvBlacklistSet;
    private WhitelistSettingFragment mWhitelistFm;
    private BlacklistSettingFragment mBlacklistFm;
    private FragmentManager mFragmentManager;

    @AfterViews
    public void init() {
        mWhitelistFm = new WhitelistSettingFragment_();
        mBlacklistFm = new BlacklistSettingFragment_();
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_content, mWhitelistFm);
        fragmentTransaction.add(R.id.fl_content, mBlacklistFm);
        fragmentTransaction.hide(mBlacklistFm);
        fragmentTransaction.commit();
    }

    @Click({R.id.tv_blacklist_set, R.id.tv_whitelist_set})
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.tv_whitelist_set:
                mTvWhitelistSet.setBackground(getResources().getDrawable(R.drawable.shape_left_corners_blue_solid));
                mTvBlacklistSet.setBackground(getResources().getDrawable(R.drawable.shape_right_corners_gray_solid));
//                fragmentTransaction.replace(R.id.fl_content,mWhitelistFm);
                fragmentTransaction.hide(mBlacklistFm);
                fragmentTransaction.show(mWhitelistFm);
                break;
            case R.id.tv_blacklist_set:
                mTvBlacklistSet.setBackground(getResources().getDrawable(R.drawable.shape_right_corners_blue_solid));
                mTvWhitelistSet.setBackground(getResources().getDrawable(R.drawable.shape_left_corners_gray_solid));
//                fragmentTransaction.replace(R.id.fl_content,mBlacklistFm);
                fragmentTransaction.hide(mWhitelistFm);
                fragmentTransaction.show(mBlacklistFm);
                break;
        }
        fragmentTransaction.commit();
    }
}
