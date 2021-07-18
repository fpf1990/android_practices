package com.example.fmusic.presenters;

import android.media.MediaPlayer;
import android.os.Binder;

import com.example.fmusic.interfaces.IPlayerControl;
import com.example.fmusic.interfaces.IPlayerViewControl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerPresenter extends Binder implements IPlayerControl {

    private IPlayerViewControl viewController;
    private MediaPlayer player;
    private int currentPlayerState;
    private Timer timer;
    private SeekTask seekTask;

    @Override
    public void registerViewController(IPlayerViewControl controller) {
        this.viewController=controller;
    }

    @Override
    public void unregisterViewController() {
        this.viewController=null;
    }

    @Override
    public void play() throws IOException{
        //初始化播放器
        intiPlayer();
        //设置数据源
        if(currentPlayerState==0){
            if(player!=null){
                player.setDataSource("https://wsaudiobssdlbig.yun.kugou.com/202107181100/12ce17ce0ce9b7eadad2637d726ff102/bss/extname/wsaudio/42077c8c8c67c3c1786d64683ae77666.mp3");
                player.prepare();
                player.start();
                currentPlayerState=STATE_PLAY;
                startTimer();
            }
        }
        else if(currentPlayerState==STATE_PLAY){
            if(player!=null){
                player.pause();
                currentPlayerState=STATE_PAUSE;
                stopTimer();
            }
        }
        else if(currentPlayerState==STATE_PAUSE){
            if(player!=null){
                player.start();
                currentPlayerState=STATE_PLAY;
                startTimer();
            }
        }
        //通知UI更新界面
        if(viewController!=null){
            viewController.onPlayerStateChange(currentPlayerState);
        }
    }

    private void intiPlayer() {
        if(player==null){
            player=new MediaPlayer();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {
        if(player!=null){
            player.stop();
            player.release();
            currentPlayerState=STATE_STOP;
            stopTimer();
            //通知UI更新界面
            if(viewController!=null){
                viewController.onPlayerStateChange(currentPlayerState);
            }
            player=null;
        }
    }

    @Override
    public void seekTo(int seek) {
        if(player!=null){
            int targetSeek=(int)(seek*1.0f/100*player.getDuration());
            player.seekTo(targetSeek);
        }
    }

    /**
     * 开启定时器
     */
    private void startTimer(){
        if(timer==null){
            timer=new Timer();
        }
        if(seekTask==null){
            seekTask=new SeekTask();
        }
        //每500ms执行一次
        timer.schedule(seekTask,0,500);
    }

    /**
     * 关闭定时器
     */
    private void stopTimer(){
        if(timer==null){
            return;
        }
        timer.cancel();
        timer=null;
        if(seekTask==null){
            return;
        }
        seekTask.cancel();
        seekTask=null;
    }

    private class SeekTask extends TimerTask{
        @Override
        public void run() {
            //获取当前的播放进度并刷新界面
            int currentPos=player.getCurrentPosition();
            int total=player.getDuration();
            int currentSeek=(int)(currentPos*1.0f/total*100);
            viewController.onSeekChange(currentSeek);
        }
    }
}
