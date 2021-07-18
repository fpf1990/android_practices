package com.example.broadcastdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtBattaryPerView;
    private Button btnSend;
    private EditText txtMsg;

    //1、首先声明一个数组permissions，将所有需要申请的权限都放在里面
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECEIVE_BOOT_COMPLETED};
    //2、创建一个mPermissionList，逐个判断哪些权限未授权，将未授权的权限存储到mPermissionList中
    List<String> mPermissionList = new ArrayList<>();
    //3、声明一个请求码，在请求权限的回调方法onRequestPermissionsResult中需要判断使用
    private final int mRequestCode = 100;//权限请求码
    //4、权限判断和申请
    private void initPermission(){
        mPermissionList.clear();//清空已经允许的没有通过的权限
        //逐个判断是否还有未通过的权限
        for (int i = 0;i<permissions.length;i++){
            if (ContextCompat.checkSelfPermission(this,permissions[i])!=
                    PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);//添加还未授予的权限到mPermissionList中
            }
        }
        //申请权限
        if (mPermissionList.size()>0){//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this,permissions,mRequestCode);
        }else {
            //权限已经都通过了，可以将程序继续打开了
            init();
        }
    }
    /**
     * 5.请求权限后回调的方法
     * @param requestCode 是我们自己定义的权限请求码
     * @param permissions 是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限
     *                     名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode==requestCode){
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==-1){
                    hasPermissionDismiss=true;
                    break;
                }
            }
        }
        if (hasPermissionDismiss){//如果有没有被允许的权限
            showPermissionDialog();
        }else {
            //权限已经都通过了，可以将程序继续打开了
            init();
        }
    }
    /**
     *  6.不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;
    String mPackName = "crazystudy.com.crazystudy";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.
                                    ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();
//                            SplashActivity.this.finish();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }
    //7、关闭对话框
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }
    //init方法
    private void init(){
        txtBattaryPerView=findViewById(R.id.txtBattaryPer);
        txtBattaryPerView.setText("未获取");
        btnSend=findViewById(R.id.btnSend);
        txtMsg=findViewById(R.id.txtMsg1);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMsg.getText().toString();
                Intent intent=new Intent();
                intent.setAction(ConstVar.ACTION_SEND_MSG);
                intent.putExtra(ConstVar.KEY_CONTENT,msg);
                //发送广播
                sendBroadcast(intent);
            }
        });
    }


    BatteryLevvelReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        init();
        //要接收的频道：电量变化
        IntentFilter intentFilter=new IntentFilter();
        //设置频道：电量变化
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //设置频道：USB连接
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        //设置频道：USB断开
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(ConstVar.ACTION_SEND_MSG);
        //创建收音机
        receiver=new BatteryLevvelReceiver(new Handler());
        //注册广播
        this.registerReceiver(receiver,intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            //销毁时取消注册广播（动态注册的需要取消，静态注册的不需要（manifest文件中静态注册））
            this.unregisterReceiver(receiver);
        }
    }

    /**
     * 电量收音机（广播接收器）
     */
    private class BatteryLevvelReceiver extends BroadcastReceiver {

        private static final String TAG = "BatteryLevvelReceiver";

        private Handler handler;

        public BatteryLevvelReceiver(Handler handler){
            this.handler=handler;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if(Intent.ACTION_BATTERY_CHANGED==action){
                int current=intent.getExtras().getInt(BatteryManager.EXTRA_LEVEL);//获得当前电量
                int total=intent.getExtras().getInt(BatteryManager.EXTRA_SCALE);//获得总电量
                final int percent=current*100/total;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        txtBattaryPerView.setText("现在的电量是"+percent+"%。");
                    }
                });
            }
            else if(Intent.ACTION_POWER_CONNECTED.equals(action)){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"USB已连接",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if(Intent.ACTION_POWER_DISCONNECTED.equals(action)){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"USB已断开",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if(ConstVar.ACTION_SEND_MSG.equals(action)){
                final String msg=intent.getStringExtra(ConstVar.KEY_CONTENT);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"收到广播消息"+msg,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}
