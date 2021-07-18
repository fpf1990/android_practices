package com.example.broadcastdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppStateChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "AppStateChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(Intent.ACTION_PACKAGE_ADDED.equals(action)){
            Log.d(TAG, "onReceive:应用安装了 "+intent.getData());
        }
        else if(Intent.ACTION_PACKAGE_REMOVED.equals(action)){
            Log.d(TAG, "onReceive: 应用卸载了 "+intent.getData());
        }
    }
}
