package com.phone.listen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.phone.listen.util.Constant;
import com.phone.listen.util.FileUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by popfisher on 2017/11/6.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        String resultData = this.getResultData();

        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // 去电，可以用定时挂断
            // 双卡的手机可能不走这个Action
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        } else {
            // 来电去电都会走
            // 获取当前电话状态
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            // 获取电话号码
            String extraIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
//                TelephonyUtil.endCall(context);
                DateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                String time = format.format(new Date());
                FileUtil.writeToFile(Constant.LOG_PATH, "记录.txt", time + "      " + extraIncomingNumber, true);
            }
        }
    }
}
