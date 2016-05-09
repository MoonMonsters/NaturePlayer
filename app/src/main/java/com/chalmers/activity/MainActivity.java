package com.chalmers.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chalmers.adapter.MainAdapter;
import com.chalmers.fragment.AudioListFragment;
import com.chalmers.fragment.VideoListFragment;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private TextView tv_audio = null;
    private TextView tv_video = null;
    private View view_line = null;
    private ViewPager vp_listing = null;

    @Override
    public void initListener() {
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);
    }

    @Override
    public void initView() {
        Log.d("TAG","MainActivity-->initView");
        tv_audio = findView(R.id.id_tv_audio);
        tv_video = findView(R.id.id_tv_video);
        view_line = findView(R.id.id_view_line);
        vp_listing = findView(R.id.id_vp_listing);
    }

    @Override
    public void initData() {
        //默认选中tv_video
        setTitleChange(true);

        initViewPager();
    }

    private void initViewPager(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new AudioListFragment());
        fragments.add(new VideoListFragment());
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(),fragments);

        vp_listing.setAdapter(mainAdapter);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View view, int id) {
        switch (id){
            case R.id.id_tv_audio:
                setTitleChange(false);
                break;
            case R.id.id_tv_video:
                setTitleChange(true);
                break;
            default:
                break;
        }
    }

    /**
     * 判断Video和Audio是否被点击
     * @param isChecked 该TextView是否被点击 true:Video点击 false:Audio点击
     */
    private void setTitleChange(boolean isChecked){
        //如果Video传进来的是true，则表示tv_video被选中
        tv_video.setSelected(isChecked);
        //如果Audio传进来的是false，则表示tv_audio被选中
        tv_audio.setSelected(!isChecked);

        //动画设置为放大类型，如果选中，则放大1.2倍
        scaleTitle(isChecked ? 1.2f:1.0f,tv_video);
        scaleTitle(isChecked ? 1.2f : 1.0f, tv_audio);
    }

    private void scaleTitle(float scale, TextView textView){
        //动画
        ViewCompat.animate(textView).scaleX(scale).scaleY(scale);
    }
}
