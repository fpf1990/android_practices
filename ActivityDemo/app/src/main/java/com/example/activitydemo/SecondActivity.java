package com.example.activitydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    private static String TAG="SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "onCreate:***************************************************** ");
        TextView view=findViewById(R.id.txtMsg);

        Intent intent=getIntent();
//        String useerName=intent.getStringExtra("userName");
//        String pwd=intent.getStringExtra("pwd");
//        view.setText(useerName+ "\r\n"+pwd);
        //接收类对象
        User user = intent.getParcelableExtra("user");
//        view.setText(user.getUserName()+ "\r\n"+user.getPwd());
        if(user.getPwd().equals("111")){
            //设置返回结果
            intent.putExtra("msg",user.getUserName()+ ","+user.getPwd());
            setResult(2,intent);
        }
        else{
            intent.putExtra("msg",user.getUserName()+ ","+user.getPwd());
            setResult(3,intent);
        }
        //这个必须写，结束当前页面，返回上一个页面，否则一直停留在这个页面看不到返回结果
        finish();
    }
}