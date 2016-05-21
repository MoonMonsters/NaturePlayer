package com.chalmers.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.chalmers.bean.VideoItem;
import com.chalmers.interfaces.Keys;
import com.chalmers.utils.Logger;
import com.chalmers.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class VideoPlayerActivity extends BaseActivity {

    private ArrayList<VideoItem> videoItems = null;
    private int curPosition = 0;
    private VideoView videoView = null;

    //第一层
    private TextView tv_title = null;
    private ImageView iv_battery = null;
    private TextView tv_systemTime = null;

    //第二层
    private Button btn_voice = null;
    private SeekBar sb_voice = null;

    //第三层
    private TextView tv_curTime = null;
    private SeekBar sb_progress = null;
    private TextView tv_totleTime = null;

    //第四层
    private Button btn_exit = null;
    private Button btn_pre = null;
    private Button btn_play = null;
    private Button btn_next = null;
    private Button btn_full = null;

    private LinearLayout linear_top = null;
    private LinearLayout linear_bottom = null;

    //当前音量值
    int curVolume = 0;
    //最大音量值
    int streamMaxVolume = 0;
    AudioManager audioManager = null;

    //显示对话框面板
    private LinearLayout linear_loading = null;

    @Override
    public void supportFinishAfterTransition() {
        super.supportFinishAfterTransition();
    }

    //手势
    private GestureDetector gestureDetector = null;
    //最大音量值和屏幕高度的比例
    float maxVolumeScreenHeightScale = 0.0f;
    //最大亮度和高度的比例
    float maxBrightness = 0.0f;
    //屏幕当前亮度
    float curBrightnessValue = 0.0f;

    private boolean isFullScreen = false;
    private boolean isPanelShow = false;

    private View view_video = null;

    public static int UPDATE_SYSTEM_TIME = 0;
    public static int UPDATE_CURRENT_TIME = 1;
    public static int HIDE_PANEL_MESSAGE = 2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //如果为更新时间指令
            if(msg.what == UPDATE_SYSTEM_TIME){
                //更新显示时间
                updateSystemTime();
            }else if(msg.what == UPDATE_CURRENT_TIME){
                updateCurPlayingTime();
            } else if (msg.what == HIDE_PANEL_MESSAGE){
                hideLayoutPanel();
            }
        }
    };

    @Override
    public void initListener() {
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.setOnCompletionListener(onCompletionListener);
        sb_voice.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sb_progress.setOnSeekBarChangeListener(onSeekBarChangeListener);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    public void initView() {
        videoView = findView(R.id.id_video_view);

        //第一层
        tv_title = findView(R.id.id_tv_title);
        iv_battery = findView(R.id.id_iv_battery);
        tv_systemTime = findView(R.id.id_tv_system_time);

        //第二层
        btn_voice = findView(R.id.id_btn_voice);
        sb_voice = findView(R.id.id_seekBar_voice);

        //第三层
        tv_curTime = findView(R.id.id_tv_cur_time);
        sb_progress = findView(R.id.id_seekBar_progress);
        tv_totleTime = findView(R.id.id_tv_totle_time);

        //第四层
        btn_exit = findView(R.id.id_btn_exit);
        btn_pre = findView(R.id.id_btn_pre);
        btn_play = findView(R.id.id_btn_play);
        btn_next = findView(R.id.id_btn_next);
        btn_full = findView(R.id.id_btn_full);

        //手势
        gestureDetector = new GestureDetector(VideoPlayerActivity.this,onGestureListener);

        view_video = findView(R.id.view_video);
        view_video.setVisibility(View.VISIBLE);
        //设置初始化时屏幕亮度
        ViewHelper.setAlpha(view_video,0.0f);

        linear_top = findView(R.id.linear_top);
        linear_bottom = findView(R.id.linear_bottom);

        linear_loading = findView(R.id.linear_loading);

        hideLayoutPanel();
    }

    @Override
    public void initData() {
        videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra(Keys.VIDEO_LIST);
        curPosition = getIntent().getIntExtra(Keys.CURPOSITION,-1);

        //开始播放
        playVideo();

        //注册电量广播
        registerBatteryBroadCast();
        //更新时间
        updateSystemTime();
        //初始化音量信息
        initVolume();

        //得到音量与屏幕高的比例
        maxVolumeScreenHeightScale = (float) (streamMaxVolume * 1.0 / Utils.getScreenHeight(VideoPlayerActivity.this));
        //得到亮度与高度的比例
        maxBrightness = 1.0f / Utils.getScreenHeight(VideoPlayerActivity.this);
    }

    private void playVideo() {

        showLoadingDialog();

        //接收uri
        Uri data = getIntent().getData();
        //如果是从外部打开的视频
        if(data != null){
            videoView.setVideoURI(data);
            tv_title.setText(data.getPath());

            btn_next.setEnabled(false);
            btn_pre.setEnabled(false);
        }else{
            //获得点击位置的视频信息
            VideoItem videoItem = videoItems.get(curPosition);
            //准备视频资源
            videoView.setVideoPath(videoItem.getPath());
            //显示视频名称
            tv_title.setText(videoItem.getTitle());

            //如果是第一个视频，则不可点击
            btn_pre.setEnabled(curPosition != 0);
            //如果是最后一个视频，则不可点击
            btn_next.setEnabled(curPosition != videoItems.size() - 1);
        }

        isFullScreen = false;
        fullScreen();
    }

    /**
     * 注册电量改变广播
     */
    private void registerBatteryBroadCast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(onBatteryChanged,intentFilter);
    }

    /**
     * 根据电量改变，设置不同图片
     */
    private void batteryChanged(int level){
        int resId = 0;

        if(level == 0){
            resId = R.drawable.ic_battery_0;
        }else if(level <= 10){
            resId = R.drawable.ic_battery_10;
        }else if(level <= 20){
            resId = R.drawable.ic_battery_20;
        }else if(level <= 40){
            resId = R.drawable.ic_battery_40;
        }else if(level <= 60){
            resId = R.drawable.ic_battery_60;
        }else if(level <= 80){
            resId = R.drawable.ic_battery_80;
        }else{
            resId = R.drawable.ic_battery_100;
        }

        iv_battery.setImageResource(resId);
    }

    /**
     * 更新时间
     */
    private void updateSystemTime(){
        tv_systemTime.setText(DateFormat.format("kk:mm:ss",System.currentTimeMillis()));
        //发送延迟消息，每隔一秒发送一次
        handler.sendEmptyMessageDelayed(UPDATE_SYSTEM_TIME,1000);
    }

    /**
     * 初始化电量信息
     */
    private void initVolume(){
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //获得最大音量值
        streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获得当前音量值
        curVolume = getStreamVolume();

        //设置进SeekBar中
        sb_voice.setProgress(curVolume);
        sb_voice.setMax(streamMaxVolume);
    }

    /**
     * 返回当前系统音量值
     * @return int 音量值
     */
    private int getStreamVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置音量值
     * @param volume int
     */
    private void setCurVolume(int volume){
        int streamType = AudioManager.STREAM_MUSIC;
        //0:标志在调音量时，不显示系统音量 1，表示显示
        int flags = 0;
        audioManager.setStreamVolume(streamType,volume,flags);
    }

    /**
     * 设置静音与非静音
     */
    private void tagglePlaying(){
        //如果当前音量是0，那么则表示为静音模式
        //其实，如果是音量为0时静音也不会有影响，只是不静音时不为0而已
        if(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0){
            setCurVolume(curVolume);
        }else{  //如果要静音，则把音量设置为0
            setCurVolume(0);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_video_player;
    }

    /**
     * 移动改变音量值
     * @param disY 手指滑动距离
     */
    private void moveVolumeValue(int disY){
        //移动距离*比例可以得出应该改变多少音量
        //音量值为整型
        int moveVolume = (int) (disY * maxVolumeScreenHeightScale);
        //当前音量值为改变音量值加上原有值
        int result = moveVolume + curVolume;
        //判断现在的值是否大于了最大值
        result = result > streamMaxVolume ? streamMaxVolume : result;
        //判断当前音量值是否小于0
        result = result < 0 ? 0 : result;

        //设置值
        setCurVolume(result);
        //改变滑动条
        sb_voice.setProgress(result);
    }

    private void setBrightnessValue(int disY){
        float moveBrightnessValue = (-disY * maxBrightness);
        float result = moveBrightnessValue + curBrightnessValue;
        result = result > 1.0f ? 1.0f : result;
        result = result < 0.0f ? 0.0f : result;

        ViewHelper.setAlpha(view_video,result);
    }

    /**
     * 更新播放时间
     */
    public void updateCurPlayingTime(){
        int duration = videoView.getCurrentPosition();
        tv_curTime.setText(Utils.formatTime(duration));
        sb_progress.setProgress(duration);
        //之所以是延迟300毫秒，而不是1000毫秒，是因为避免线程间的相互干扰
        handler.sendEmptyMessageDelayed(UPDATE_CURRENT_TIME,300);
    }

    /**
     * 控制视频的播放
     */
    private void controlPlaying() {
        //如果点击按钮时，视频正在播放
        if(videoView.isPlaying()){
            //暂停
            videoView.pause();
        }else{
            //播放
            videoView.start();
        }

        controlBtnBg();
    }

    /**
     * 改变播放按钮的背景图片
     */
    private void controlBtnBg(){
        int resId;
        if(videoView.isPlaying()){
            resId = R.drawable.selector_btn_video_pause;
        }else{
            resId = R.drawable.selector_btn_video_play;
        }

        btn_play.setBackgroundResource(resId);
    }

    /**
     * 播放上一个视频
     */
    private void preVideo() {
//        curPosition = --curPosition < 0 ? 0 : curPosition;
//        playVideo();
        if(curPosition - 1 >= 0){
            curPosition--;
            playVideo();
        }
    }

    /**
     * 播放下一个视频
     */
    private void nextVideo(){
//        curPosition = ++curPosition > videoItems.size()-1 ? videoItems.size()-1 : curPosition;
//        playVideo();
        if(curPosition + 1 <= videoItems.size() - 1){
            curPosition++;
            playVideo();
        }
    }

    /**
     * 设置全屏
     */
    private void fullScreen(){
        if(isFullScreen){
            videoView.getLayoutParams().width = Utils.getScreenWidth(VideoPlayerActivity.this);
            videoView.getLayoutParams().height = Utils.getScreenHeight(VideoPlayerActivity.this);
        }else{
            videoView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            videoView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    /**
     * 隐藏面板
     */
    private void hideLayoutPanel(){
        //提供0,0参数，表示让系统测量控件的宽和高
        linear_top.measure(0,0);
        //得到高
        int topHeight = linear_top.getMeasuredHeight();
        linear_bottom.measure(0,0);
        int bottomHeight = linear_bottom.getMeasuredHeight();

        Logger.d(VideoPlayerActivity.this,"topHeight-->"+topHeight);
        Logger.d(VideoPlayerActivity.this,"bottomHeight-->"+bottomHeight);

        //隐藏
        ViewPropertyAnimator.animate(linear_top).translationY(-topHeight);
        ViewPropertyAnimator.animate(linear_bottom).translationY(bottomHeight);
    }

    /**
     * 显示面板
     */
    private void showLayoutPanel(){
        ViewPropertyAnimator.animate(linear_top).translationY(0f);
        ViewPropertyAnimator.animate(linear_bottom).translationY(0f);


    }

    /**
     * 发送消息
     */
    private void sendHideMessage(){
        //5秒后隐藏面板
        handler.sendEmptyMessageDelayed(HIDE_PANEL_MESSAGE,5000);
    }

    /**
     * 移除消息
     */
    private void removeHideMessage(){
        handler.removeMessages(HIDE_PANEL_MESSAGE);
    }

    /**
     * 控制面板的显示与隐藏
     */
    private void ctrlPanel(){
        if(isPanelShow){
            showLayoutPanel();
        }else{
            hideLayoutPanel();
        }
    }

    /**
     * 隐藏对话框
     */
    private void hideLodingDialog(){
        ViewPropertyAnimator.animate(linear_loading).alpha(0.0f)
                .setDuration(1500)
                .setListener(animatorListener);
    }

    /**
     * 显示对话框
     */
    private void showLoadingDialog(){
        linear_loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view, int id) {
        removeHideMessage();
        switch (id){
            case R.id.id_btn_voice:
                //切换静音与非静音模式
                tagglePlaying();
                break;
            case R.id.id_btn_exit:
                //退出
                this.finish();
                break;
            case R.id.id_btn_full:
                isFullScreen = !isFullScreen;
                fullScreen();
                break;
            case R.id.id_btn_next:
                nextVideo();
                break;
            case R.id.id_btn_play:
                controlPlaying();
                break;
            case R.id.id_btn_pre:
                preVideo();
                break;
        }
        sendHideMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //当不可见时，将更新消息停掉
        handler.removeMessages(UPDATE_SYSTEM_TIME);
        handler.removeMessages(UPDATE_CURRENT_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(onBatteryChanged);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把手势传给GestureDetector，因为此时的手势是给的Activity，GetstureDetector接收不到
        gestureDetector.onTouchEvent(event);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                removeHideMessage();
                break;
            case MotionEvent.ACTION_UP:
                sendHideMessage();
                break;
            default:
                break;
        }
        return true;
    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //当准备完成后，再播放视频
            videoView.start();
            //设置总时长
            tv_totleTime.setText(Utils.formatTime(videoView.getDuration()));
            //设置进度条的最大值
            //需要放在videoView.start()之后，VideoView把资源准备完成之后才能获取得到大小
            sb_progress.setMax(videoView.getDuration());
            //更新播放时间
            updateCurPlayingTime();
            //当开始播放时就改变背景图片
            controlBtnBg();

            hideLodingDialog();
        }
    };

    BroadcastReceiver onBatteryChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);
            //改变电量背景图
            batteryChanged(level);
        }
    };

    //SeekBar的监听器
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar == sb_voice){
                //将当前音量保存
                curVolume = progress;
                //设置音量
                setCurVolume(curVolume);
            }else if(seekBar == sb_progress){
                //要是用户改变才调用
                if(fromUser){
                    //改变播放位置
                    videoView.seekTo(progress);
                }

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeHideMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideMessage();
        }
    };

    //手势监听器
    GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
        //判断是在左边屏幕滑动还是在右边
        boolean isLeftDown = false;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isPanelShow = !isPanelShow;
            ctrlPanel();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        //手指按下
        @Override
        public boolean onDown(MotionEvent e) {
            //保存当前的音量值
            curVolume = getStreamVolume();
            //按下时获得当前屏幕亮度
            curBrightnessValue = ViewHelper.getAlpha(view_video);

            isLeftDown = e.getX() < Utils.getScreenWidth(VideoPlayerActivity.this) / 2;

            return super.onDown(e);
        }

        //手指在屏幕中滑动
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //e1表示第一个点，e2表示第二个点
            //e1与e2的差表示滑动的距离
            int disY = (int) (e1.getY() - e2.getY());
            if(isLeftDown){
                //如果在左边滑动，则调节屏幕亮度
                setBrightnessValue(disY);
            }else {
                //右边则调节音量
                moveVolumeValue(disY);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
    };

    //当视频播放完成后调用
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //当播放完成后，时间归0
            videoView.seekTo(0);
            tv_curTime.setText(Utils.formatTime(0));
            sb_progress.setProgress(0);
        }
    };

    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            Logger.d(VideoPlayerActivity.this,"onAnimationEnd");
            //动画结束时，将对话框面板隐藏
            linear_loading.setVisibility(View.GONE);
            //重新设置成全部显示模式
            ViewHelper.setAlpha(linear_loading,1.0f);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            int secondaryProgress = (int)((percent*1.0/100) * mp.getDuration());
            sb_progress.setSecondaryProgress(secondaryProgress);
        }
    };
}