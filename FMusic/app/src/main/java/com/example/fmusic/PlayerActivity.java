package com.example.fmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.fmusic.interfaces.IPlayerControl;
import com.example.fmusic.interfaces.IPlayerViewControl;
import com.example.fmusic.services.PlayerService;

import java.io.IOException;

import static com.example.fmusic.interfaces.IPlayerControl.STATE_PAUSE;
import static com.example.fmusic.interfaces.IPlayerControl.STATE_PLAY;
import static com.example.fmusic.interfaces.IPlayerControl.STATE_STOP;

public class PlayerActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private Button btnPlay;
    private Button btnStop;
    private PlayerServiceCollection playerServiceCollection;
    private IPlayerControl playerControl;
    private boolean isUserTouchSeekBar=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //初始化UI
        initView();
        //绑定事件
        initEvent();
        //开启服务
        initService();
        //绑定服务
        initBindService();
    }

    private void initService() {
        startService(new Intent(this, PlayerService.class));
    }

    private void initBindService() {
        Intent intent=new Intent(this,PlayerService.class);
        playerServiceCollection = new PlayerServiceCollection();
        if(playerServiceCollection ==null){
            playerServiceCollection =new PlayerServiceCollection();
        }
        bindService(intent,playerServiceCollection,BIND_AUTO_CREATE);
    }


    private class PlayerServiceCollection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerControl=(IPlayerControl)iBinder;
            //服务启动后注册UI接口
            playerControl.registerViewController(playerViewControl);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playerServiceCollection=null;
//            binder.unregisterViewController();
        }
    }


    private void initEvent() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserTouchSeekBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int per=seekBar.getProgress();
                if(playerControl!=null){
                    playerControl.seekTo(per);
                }
                isUserTouchSeekBar=false;
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playerControl!=null){
                    try {
                        playerControl.play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playerControl!=null){
                    playerControl.stop();
                }
            }
        });
    }

    private void initView() {
        seekBar = findViewById(R.id.sb_progress);
        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playerServiceCollection!=null){
            //解除UI控制
            playerControl.unregisterViewController();
            //解绑服务
            unbindService(playerServiceCollection);
        }
    }

    private IPlayerViewControl playerViewControl=new IPlayerViewControl() {
        @Override
        public void onPlayerStateChange(int state) {
            switch (state){
                case STATE_PLAY:
                    btnPlay.setText("暂停");
                    break;
                case STATE_PAUSE:
                case STATE_STOP:
                    btnPlay.setText("播放");
                    break;
                default:
                    break;

            }
        }

        @Override
        public void onSeekChange(final int seek) {
            if(!isUserTouchSeekBar){
                //手松开了更新进度
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(seek);
                    }
                });
            }
        }
    };

}