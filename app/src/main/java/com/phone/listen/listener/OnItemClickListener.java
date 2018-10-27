package com.phone.listen.listener;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zyx on 16-6-12.
 */
public interface OnItemClickListener<T> {
    void onItemClick(ViewGroup parent, View view, T t, int position);
    boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
}
