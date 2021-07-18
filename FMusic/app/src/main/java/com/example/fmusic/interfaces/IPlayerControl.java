package com.example.fmusic.interfaces;

import java.io.IOException;

public interface IPlayerControl {
    /**
     * 把UI的控制接口设置给逻辑层
     * @param controller
     */
    void registerViewController(IPlayerViewControl controller);

    /**
     * 取消UI接口注册
     */
    void unregisterViewController();

    //播放器状态
    int STATE_PLAY=1;
    int STATE_PAUSE=2;
    int STATE_STOP=3;


    void play() throws IOException;
    void pause();
    void resume();
    void stop();
    void seekTo(int seek);
}
