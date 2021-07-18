package com.example.activitydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        TextView view=findViewById(R.id.txtMsg2);

        Intent intent=getIntent();
        String useerName=intent.getStringExtra("userName");
        String pwd=intent.getStringExtra("pwd");

        view.setText(useerName+ "\r\n"+pwd);
    }
}