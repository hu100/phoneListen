package com.phone.listen.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class BaseFragment extends android.support.v4.app.Fragment {

   protected Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
}
