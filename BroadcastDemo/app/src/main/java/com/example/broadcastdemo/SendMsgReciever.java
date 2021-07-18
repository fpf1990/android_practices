package com.example.broadcastdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendMsgReciever extends BroadcastReceiver {
    private static final String TAG = "SendMsgReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        String msg=intent.getStringExtra(ConstVar.KEY_CONTENT);
        Log.d(TAG, "onReceive: ***************************"+msg);
    }
}
