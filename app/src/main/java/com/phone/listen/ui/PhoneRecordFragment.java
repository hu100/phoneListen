package com.phone.listen.ui;

import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.phone.listen.AppApplication;
import com.phone.listen.R;
import com.phone.listen.adapter.CommonAdapter;
import com.phone.listen.adapter.OnItemClickListener;
import com.phone.listen.adapter.ViewHolder;
import com.phone.listen.base.BaseFragment;
import com.phone.listen.bean.CallRecordBean;
import com.phone.listen.greendao.CallRecordBeanDao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_phone_record)
public class PhoneRecordFragment extends BaseFragment {

    @ViewById(R.id.rv_phone_record)
    RecyclerView mRvPhoneRecord;
    List<CallRecordBean> mRecordList = new ArrayList<>();
    private CallRecordBeanDao mRecordBeanDao;
    private CommonAdapter<CallRecordBean> mCallRecordAdapter;

    @AfterViews
    public void init() {
        initRecyclerView();
        mRecordBeanDao = AppApplication.getInstance().getDaoSession().getCallRecordBeanDao();
    }

    private void initRecyclerView() {
        mRvPhoneRecord.setLayoutManager(new LinearLayoutManager(mContext));
        mRvPhoneRecord.setItemAnimator(new DefaultItemAnimator());
        mCallRecordAdapter = new CommonAdapter<CallRecordBean>(mContext, R.layout.item_phone_record, mRecordList) {
            @Override
            public void convert(ViewHolder holder, CallRecordBean bean) {
                holder.setText(R.id.phone_number, bean.getNumber())
                        .setText(R.id.number_area, bean.getBelongArea())
                        .setText(R.id.phone_date, bean.getDate())
                        .setText(R.id.phone_time, bean.getTime())
                        .setText(R.id.intercept_status, bean.getNeedIntercept()?(bean.getIntercepted() ? "拦截\n成功" : "拦截\n失败"):"无需\n拦截")
                        .setTextColor(R.id.intercept_status, bean.getIntercepted() ? Color.BLACK : Color.RED);
            }
        };
        mRvPhoneRecord.setAdapter(mCallRecordAdapter);
        mCallRecordAdapter.setOnItemClickListener(new OnItemClickListener<CallRecordBean>() {
            @Override
            public void onItemClick(ViewGroup parent, View view, CallRecordBean bean, int position) {

            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, CallRecordBean bean, int position) {
                mRecordBeanDao.deleteByKey(bean.getId());
                mRecordList.remove(position);
                mCallRecordAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getPhoneRecord();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPhoneRecord();
    }

    @Background
    public void getPhoneRecord() {
        mRecordList.clear();
        mRecordList.addAll(mRecordBeanDao.loadAll());
        getActivity().runOnUiThread(() -> {
            mCallRecordAdapter.notifyDataSetChanged();
        });
    }
}
