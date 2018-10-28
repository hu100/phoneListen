package com.phone.listen.ui.fragment;

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
import com.phone.listen.adapter.ViewHolder;
import com.phone.listen.base.BaseFragment;
import com.phone.listen.bean.BlackListBean;
import com.phone.listen.greendao.BlackListBeanDao;
import com.phone.listen.listener.OnItemClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_blacklist)
public class BlacklistSettingFragment extends BaseFragment {

    @ViewById(R.id.rv_intercept_rule)
    RecyclerView mRvInterceptRule;
    @ViewById(R.id.et_number)
    EditText mEtNumber;
    @ViewById(R.id.btn_ok)
    Button mBtnOk;
    private CommonAdapter<BlackListBean> mAdapter;
    List<BlackListBean> mBlackList = new ArrayList<>();
    private BlackListBeanDao mBlackListDao;

    @AfterViews
    public void init() {
        mBlackListDao = AppApplication.getInstance().getDaoSession().getBlackListBeanDao();
        mRvInterceptRule.setLayoutManager(new LinearLayoutManager(mContext));
        mRvInterceptRule.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CommonAdapter<BlackListBean>(mContext, R.layout.item_text, mBlackList) {
            @Override
            public void convert(ViewHolder holder, BlackListBean blackListBean,int position) {
                holder.setText(R.id.tv_text, "拦截 " + blackListBean.getNumber() + " 开头的号码");
                holder.setOnClickListener(R.id.iv_delete, view -> {
                    mBlackListDao.deleteByKey(blackListBean.getId());
                    mBlackList.remove(position);
                    mAdapter.notifyDataSetChanged();
                });
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener<BlackListBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, BlackListBean blackListBean, int position) {

            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, BlackListBean blackListBean, int position) {
                mBlackListDao.deleteByKey(blackListBean.getId());
                mBlackList.remove(position);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        mRvInterceptRule.setAdapter(mAdapter);
        getBlacklist();
    }

    @Background
    public void getBlacklist() {
        List<BlackListBean> list = mBlackListDao.queryBuilder()
                .orderDesc(BlackListBeanDao.Properties.Id)
                .list();
        mBlackList.clear();
        mBlackList.addAll(list);
        getActivity().runOnUiThread(() -> {
            mAdapter.notifyDataSetChanged();
        });
    }

    @Click({R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                insertBlackNum();
                break;
        }
    }

    private void insertBlackNum() {
        String num = mEtNumber.getText().toString().trim();
        if (num.length() == 0) return;
        mEtNumber.setText("");
        BlackListBean bean = new BlackListBean(null, "", num, num.length(), "");
        mBlackList.add(0, bean);
        mAdapter.notifyDataSetChanged();
        mBlackListDao.insert(bean);
    }
}
