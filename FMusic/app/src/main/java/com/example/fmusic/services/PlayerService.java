package com.example.fmusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.fmusic.interfaces.ICommunication;
import com.example.fmusic.presenters.PlayerPresenter;

public class PlayerService extends Service {

    private PlayerPresenter playerPresenter;

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(playerPresenter==null){
            playerPresenter = new PlayerPresenter();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playerPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerPresenter=null;
    }
}
