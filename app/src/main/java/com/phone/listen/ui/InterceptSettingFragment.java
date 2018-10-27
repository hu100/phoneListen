package com.phone.listen.ui;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.phone.listen.AppApplication;
import com.phone.listen.R;
import com.phone.listen.adapter.CommonAdapter;
import com.phone.listen.listener.OnItemClickListener;
import com.phone.listen.adapter.ViewHolder;
import com.phone.listen.base.BaseFragment;
import com.phone.listen.bean.WhiteListBean;
import com.phone.listen.greendao.WhiteListBeanDao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_intercept_setting)
public class InterceptSettingFragment extends BaseFragment {

    @ViewById(R.id.rv_intercept_rule)
    RecyclerView mRvInterceptRule;
    @ViewById(R.id.et_number)
    EditText mEtNumber;
    @ViewById(R.id.btn_ok)
    Button mBtnOk;
    private CommonAdapter<WhiteListBean> mAdapter;
    List<WhiteListBean> mWhiteList = new ArrayList<>();
    private WhiteListBeanDao mWhiteListBeanDao;

    @AfterViews
    public void init() {
        mWhiteListBeanDao = AppApplication.getInstance().getDaoSession().getWhiteListBeanDao();
        mRvInterceptRule.setLayoutManager(new LinearLayoutManager(mContext));
        mRvInterceptRule.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CommonAdapter<WhiteListBean>(mContext, R.layout.item_text, mWhiteList) {
            @Override
            public void convert(ViewHolder holder, WhiteListBean whiteListBean) {
                holder.setText(R.id.tv_text, "不拦截 " + whiteListBean.getNumber() + " 开头的号码");
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener<WhiteListBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, WhiteListBean whiteListBean, int position) {

            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, WhiteListBean whiteListBean, int position) {
                mWhiteListBeanDao.deleteByKey(whiteListBean.getId());
                mWhiteList.remove(position);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        mRvInterceptRule.setAdapter(mAdapter);
        getWhiteList();
    }

    @Background
    public void getWhiteList() {
        mWhiteList.clear();
        mWhiteList.addAll(mWhiteListBeanDao.loadAll());
        getActivity().runOnUiThread(() -> {
            mAdapter.notifyDataSetChanged();
        });
    }

    @Click({R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                insertWhiteNum();
                break;
        }
    }

    private void insertWhiteNum() {
        String num = mEtNumber.getText().toString().trim();
        mEtNumber.setText("");
        WhiteListBean bean = new WhiteListBean(null, "", num, num.length(), "");
        mWhiteList.add(bean);
        mAdapter.notifyDataSetChanged();
        mWhiteListBeanDao.insert(bean);
    }
}
