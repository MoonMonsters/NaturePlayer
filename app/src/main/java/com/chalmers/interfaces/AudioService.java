package com.chalmers.interfaces;

/**
 * Created by Chalmers on 2016-05-18 21:43.
 * email:qxinhai@yeah.net
 */
public interface AudioService {

    int LIST_MODE = 0;
    int RANDOM_MODE = 2;
    int SINGLE_MODE = 1;

    /**
     * 开始播放
     */
    void start();

    /**
     * 下一首歌曲
     */
    void next();

    /**
     * 上一首歌曲
     */
    void pre();

    /**
     * 暂停
     */
    void pause();

    /**
     * 跳转
     */
    void seekTo();

    /**
     * 当前音乐播放位置
     */
    void getCurrentPosition();

    /**
     * 播放总时长
     */
    void getDuration();

    /**
     * 播放视频前的准备
     */
    void playAudio();

    /**
     * 是否正在播放
     * @return true:是
     */
    boolean isPlaying();

    /**
     * 切换播放模式
     */
    void changeMode();

    /**
     * 获得当前播放模式
     * @return 当前模式
     */
    int getPlayMode();

    /**
     * 播放上一个音频
     */
     void playPreAudio();

    /**
     * 播放下一个一个音频
     */
    void playNextAudio();

    /**
     * 快进快退
     * @param duration 位置
     */
    void seekTo(int duration);
}
