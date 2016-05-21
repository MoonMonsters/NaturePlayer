package com.chalmers.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chalmers.bean.AudioItem;
import com.chalmers.interfaces.AudioService;
import com.chalmers.interfaces.IService;
import com.chalmers.interfaces.Keys;
import com.chalmers.interfaces.UIActivity;
import com.chalmers.service.AudioPlayerService;

import java.text.SimpleDateFormat;

public class AudioPlayerActivity extends BaseActivity implements UIActivity{

    private ServiceConnection conn;
    private AudioService audioService = null;

    private SeekBar sb_audio_progress = null;

    private Handler handlerAct = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case IService.PLAY_SERVICE_OBJ:
                    audioService = (AudioPlayerService) msg.obj;
                    //在Activity中通过Service的对象调用方法
                    audioService.playAudio();
                    break;
            }
        }
    };

    private Messenger messengerAct = new Messenger(handlerAct);

    @Override
    public void initListener() {
        sb_audio_progress.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    @Override
    public void initView() {
        useAnim();
        sb_audio_progress = findView(R.id.sb_audio_progress);
    }

    /**
     * 动画效果
     */
    private  void useAnim(){
        //要实现动画的位置
        ImageView iv_anim = findView(R.id.iv_anim);
        //设置背景图片资源，不要设置错方法了
        iv_anim.setBackgroundResource(R.drawable.view_anim);

        //转换成AnimationDrawable 类
        AnimationDrawable background = (AnimationDrawable) iv_anim.getBackground();
        //启动动画
        background.start();
    }

    @Override
    public void initData() {
        Intent intent = new Intent(AudioPlayerActivity.this, AudioPlayerService.class);
        intent.putExtra(Keys.AUDIO_LIST,getIntent().getSerializableExtra(Keys.AUDIO_LIST));
        intent.putExtra(Keys.CURPOSITION,getIntent().getIntExtra(Keys.CURPOSITION,-1));
        //启动服务
        startService(intent);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                //获得Service的Messenge对象
                Messenger messengerSer = new Messenger(binder);

                Message msg = new Message();
                msg.what = IService.AUDIO_ACTIVITY_OBJ;
                msg.obj = AudioPlayerActivity.this;
                //将Activity中的Messenger通过Message发送到Service中去
                msg.replyTo = messengerAct;

                try {
                    messengerSer.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        //绑定服务
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_audio_player;
    }

    @Override
    public void onClick(View view, int id) {
        switch (id){
            case R.id.btn_audio_play:
                play();
                break;
            case R.id.btn_audio_random:
                changeMode();
                break;
            case R.id.btn_audio_next:
                playNext();
                break;
            case R.id.btn_audio_pre:
                playPre();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        unbindService(conn);
    }

    @Override
    public void updateUI(AudioItem audioItem) {
        updatePlayBtnBg();
        updateArtist(audioItem.getArtist());
        updateTitle(audioItem.getTitle());
        changeModeBg();
    }

    private void play(){
        if(audioService.isPlaying()){
            audioService.pause();
        }else{
            audioService.start();
        }

        updatePlayBtnBg();
    }

    /**
     * 更新按钮背景
     */
    private void updatePlayBtnBg(){
        int resId = 0;
        if(audioService.isPlaying()){
            resId = R.drawable.selector_btn_audio_pause;
        }else{
            resId = R.drawable.selector_btn_audio_play;
        }

        Button btn_audio_play = findView(R.id.btn_audio_play);
        btn_audio_play.setBackgroundResource(resId);
    }

    /**
     * 更新歌曲名
     * @param text 歌曲名
     */
    private void updateTitle(String text){
        TextView tv_title = findView(R.id.tv_title_bar);
        tv_title.setText(text);
    }

    /**
     * 更新艺术家
     * @param text 艺术家名
     */
    private void updateArtist(String text){
        if(TextUtils.isEmpty(text)){
            text = "未命名";
        }
        TextView tv_artist = findView(R.id.tv_act_audio_artist);
        tv_artist.setText(text);
    }

    /**
     * 切换模式
     */
    private void changeMode(){
        audioService.changeMode();

        changeModeBg();
    }

    /**
     * 改变切换模式背景图片
     */
    private void changeModeBg(){
        int resId = 0;
        if(audioService.getPlayMode() == AudioService.LIST_MODE){
            resId = R.drawable.selector_mode_list;
        }else if(audioService.getPlayMode() == AudioService.RANDOM_MODE){
            resId = R.drawable.selector_mode_random;
        }else if(audioService.getPlayMode() == AudioService.SINGLE_MODE){
            resId = R.drawable.selector_mode_single;
        }

        Button button = findView(R.id.btn_audio_random);
        button.setBackgroundResource(resId);
    }

    private void playNext(){
        audioService.playNextAudio();
    }

    private void playPre(){
        audioService.playPreAudio();
    }

    /**
     * 设置时间
     * @param totleTime 总时长
     * @param curTime 播放时长
     */
    public void updateTimeText(int totleTime, int curTime){
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String time = sdf.format(curTime);
        time += "--" + sdf.format(totleTime);

        TextView tv_time = findView(R.id.tv_audio_time);
        tv_time.setText(time);

        setSeekBar(totleTime,curTime);
    }

    /**
     * 设置进度条的显示效果
     */
    private void setSeekBar(int totleTime, int curTime){
        sb_audio_progress.setMax(totleTime);
        sb_audio_progress.setProgress(curTime);
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            audioService.seekTo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}