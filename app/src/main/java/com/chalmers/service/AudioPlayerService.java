package com.chalmers.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.chalmers.bean.AudioItem;
import com.chalmers.interfaces.AudioService;
import com.chalmers.interfaces.IService;
import com.chalmers.interfaces.Keys;
import com.chalmers.interfaces.UIActivity;
import com.chalmers.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 播放音乐服务
 */
public class AudioPlayerService extends Service implements AudioService{

    private int position = -1;
    private ArrayList<AudioItem> audioItems = null;
    private AudioItem audioItem = null;

    private MediaPlayer mediaPlayer = null;
    private UIActivity uiActivity;

    private int curPosition_playing = 0;

    private int CUR_MODE = 0;

    public static final int UPDATETIME = 99;

    private Handler handlerSer = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case IService.AUDIO_ACTIVITY_OBJ:
                    uiActivity = (UIActivity) msg.obj;

                    //接收来自Activity的Messenger
                    Messenger messengerAct = msg.replyTo;
                    Message message = new Message();
                    message.what = IService.PLAY_SERVICE_OBJ;
                    message.obj = AudioPlayerService.this;

                    try {
                        //将Message对象发送到Activity中
                        messengerAct.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case UPDATETIME:
                    setTimeText();
                    break;
            }
        }
    };

    private Messenger messengerSer = new Messenger(handlerSer);

    @Override
    public IBinder onBind(Intent intent) {
        return messengerSer.getBinder();
    }

    @Override
    public void onCreate() {
        Logger.d(this,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        audioItems = (ArrayList<AudioItem>) intent.getSerializableExtra(Keys.AUDIO_LIST);
        position = intent.getIntExtra(Keys.CURPOSITION,-1);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        Logger.d(this,"bindService");
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(this,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Logger.d(this,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void start() {
        if(mediaPlayer != null){
            mediaPlayer.start();
            curPosition_playing = position;
        }
    }

    @Override
    public void next() {

    }

    @Override
    public void pre() {

    }

    @Override
    public void pause() {
        if( mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            handlerSer.removeMessages(UPDATETIME);
        }
    }

    @Override
    public void seekTo() {

    }

    @Override
    public void getCurrentPosition() {

    }

    @Override
    public void getDuration() {

    }

    @Override
    public void playAudio() {
        if(audioItems == null || audioItems.isEmpty() || position == -1){
            return ;
        }

        if(mediaPlayer != null && position == curPosition_playing){
            return ;
        }

        /**
         * 中断音乐的播放
         */
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);
        //释放资源
        release();

        mediaPlayer = new MediaPlayer();
        audioItem = audioItems.get(position);

        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        try {
            //设置音频文件路径
            mediaPlayer.setDataSource(this, Uri.parse(audioItem.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //异步准备
        mediaPlayer.prepareAsync();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    private void release() {
        if (mediaPlayer != null) {
            //重置
            mediaPlayer.reset();
            //释放资源
            mediaPlayer.release();
            //置空
            mediaPlayer = null;
        }
    }

    /**
     * 切换播放模式
     */
    public void changeMode(){
        CUR_MODE = (CUR_MODE+1) % 3;
    }

    /**
     * 获得播放模式
     */
    public int getPlayMode(){

        return CUR_MODE;
    }

    /**
     * 播放下一个一个音频
     */
    public void playNextAudio(){
        if(CUR_MODE == AudioService.SINGLE_MODE){
            position = position;
        }else if(CUR_MODE == AudioService.LIST_MODE){
//            position = (position+1) % audioItems.size();
            position = (position+1)==audioItems.size() ? 0 : position+1;
        }else if(CUR_MODE == AudioService.RANDOM_MODE){
            position = new Random().nextInt(audioItems.size());
        }

        playAudio();
    }

    /**
     * 播放上一个音频
     */
    public void playPreAudio(){
        if(CUR_MODE == AudioService.SINGLE_MODE){
            position = position;
        }else if(CUR_MODE == AudioService.LIST_MODE){
//            position = (position-1) % audioItems.size();
            position = position == 0? audioItems.size()-1 : position-1;
        }else if(CUR_MODE == AudioService.RANDOM_MODE){
            position = new Random().nextInt(audioItems.size());
        }

        playAudio();
    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //当音乐资源准备完成后就开始播放音乐
            start();
            //开始播放后，更新界面
            uiActivity.updateUI(audioItem);
            setTimeText();
        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            release();
            handlerSer.removeMessages(UPDATETIME);
            playNextAudio();
        }
    };

    /**
     * 设置界面上的时间
     */
    public void setTimeText(){
        long totleTime = mediaPlayer.getDuration();
        long curTime = mediaPlayer.getCurrentPosition();

        uiActivity.updateTimeText((int)totleTime,(int)curTime);
        handlerSer.sendEmptyMessageDelayed(UPDATETIME,1000);
    }

    /**
     * 快进快退
     */
    public void seekTo(int duration){
        mediaPlayer.seekTo(duration);
    }
}