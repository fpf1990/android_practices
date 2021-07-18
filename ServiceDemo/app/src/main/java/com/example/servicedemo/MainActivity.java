package com.example.servicedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.servicedemo.interfaces.ICommunication;
import com.example.servicedemo.services.MyFirstService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ICommunication binder;

    /**
     *生命周期：
     * 开启服务方式：onCreate  onStartCommand  onDestory 除非停止服务，否则服务一直在后台运行，但不可以和ui通讯交互
     * 如果服务已经启动，不会再走onCreate方法
     *
     * 绑定服务方式：onCreate  onBind  onUnbind onDestory 服务无法一直在后台运行，但可以通讯交互
     * 如果不解绑，会发生内存泄漏，如果解绑则服务停止
     *
     *
     * 混合开发服务的生命周期：
     * 1.开启服务，然后去绑定服务，如果不取消绑定，那么就无法停止服务
     * 2.开启服务后，多次绑定-解绑服务，服务不会被停止，只能通过stopService()来停止服务
     * 推荐的混合开启服务模式
     * 1.开启服务->为了确保服务长期在后台运行
     * 2.绑定服务->为了可以进行通讯
     * 3.调用服务内部的方法->执行业务逻辑
     * 4.退出Activity要解绑服务->释放资源
     * 5.如果不使用服务了，调用stopService()来停止服务，否则否无一直在后台运行
     *
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 开启服务
     * @param view
     */
    public void btnStartClick(View view){
        Intent intent=new Intent();
        intent.setClass(this, MyFirstService.class);
        startService(intent);
    }

    /**
     * 停止服务
     * @param view
     */
    public void btnStopClick(View view){
        Intent intent=new Intent();
        intent.setClass(this,MyFirstService.class);
        stopService(intent);
    }

    /**
     * 绑定服务
     * @param view
     */
    public void btnBindClick(View view){
        Intent intent=new Intent();
        intent.setClass(this,MyFirstService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            binder=(ICommunication)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceConnected: ");
            binder=null;
        }
    };

    /**
     * 解绑服务
     * @param view
     */
    public void btnUnbindClick(View view){
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
    }

    /**
     * 执行服务内部方法
     * @param view
     */
    public void btnExecClick(View view){
        Log.d(TAG, "btnExecClick: ");
        binder.callServiceInnerMethod();
    }

}