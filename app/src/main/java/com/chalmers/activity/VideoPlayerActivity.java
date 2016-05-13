package com.chalmers.activity;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.VideoView;

import com.chalmers.bean.VideoItem;
import com.chalmers.interfaces.Keys;

import java.util.ArrayList;

public class VideoPlayerActivity extends BaseActivity {

    private ArrayList<VideoItem> videoItems = null;
    private int curPosition = 0;
    private VideoView videoView = null;

    @Override
    public void initListener() {
        videoView.setOnPreparedListener(onPreparedListener);
    }

    @Override
    public void initView() {
        videoView = findView(R.id.id_video_view);
    }

    @Override
    public void initData() {
        videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra(Keys.VIDEO_LIST);
        curPosition = getIntent().getIntExtra(Keys.CURPOSITION,-1);

        //获得点击位置的视频信息
        VideoItem videoItem = videoItems.get(curPosition);

        //准备视频资源
        videoView.setVideoPath(videoItem.getPath());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void onClick(View view, int id) {

    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //当准备完成后，再播放视频
            videoView.start();
        }
    };
}
