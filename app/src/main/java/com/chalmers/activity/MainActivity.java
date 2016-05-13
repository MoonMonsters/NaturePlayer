package com.chalmers.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.chalmers.adapter.MainAdapter;
import com.chalmers.fragment.AudioListFragment;
import com.chalmers.fragment.VideoListFragment;
import com.chalmers.utils.Logger;
import com.chalmers.utils.Utils;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    //音乐
    private TextView tv_audio = null;
    //视频
    private TextView tv_video = null;
    //显示条
    private View view_indicator = null;
    //ViewPager
    private ViewPager vp_listing = null;

    private int pageSize = 0;

    @Override
    public void initListener() {
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);

        //ViewPager监听器
        vp_listing.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //ViewPager正在滚动
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scrollIndecator(position, positionOffset);
            }

            //ViewPager被选择
            @Override
            public void onPageSelected(int position) {
                //判断当前position是否为0，如果为0则表示是在Video位置处
                setTitleChange(position==0);
                Logger.d(MainActivity.class,"index-->"+String.valueOf(vp_listing.getCurrentItem()));
            }

            //状态已经发生改变
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //指示条跟随发生改变
    private void scrollIndecator(int position, float positionOffset) {
        //获得指示条当前位置
        float translationX = view_indicator.getWidth() * (position+positionOffset);
        //ViewHelper是nineoldandroidslibrary.jar中的类
        ViewHelper.setTranslationX(view_indicator,translationX);
    }

    /**
     * 初始化控件信息
     */
    @Override
    public void initView() {
        tv_audio = findView(R.id.id_tv_audio);
        tv_video = findView(R.id.id_tv_video);
        view_indicator = findView(R.id.id_view_indicator);
        vp_listing = findView(R.id.id_vp_listing);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        //默认选中tv_video
        setTitleChange(true);

        initViewPager();
        initPagerWidth();
    }

    /**
     * 初始化分割线长度
     */
    private void initPagerWidth() {
        //获得屏幕宽度
        int screenWidth = Utils.getScreenWidth(MainActivity.this);
        //设置长度时，需要除以pageSize
        view_indicator.getLayoutParams().width = screenWidth / pageSize;
        //刷新界面
        view_indicator.requestLayout();
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new VideoListFragment());
        fragments.add(new AudioListFragment());
        //获得Fragment的数量
        pageSize = fragments.size();
        //设置适配器（自定义适配器），也可以使用系统的FragmentPagerAdapter
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(),fragments);
        vp_listing.setAdapter(mainAdapter);
        vp_listing.setCurrentItem(0);

        Logger.d(MainActivity.class,"0-->"+fragments.get(0).toString());
        Logger.d(MainActivity.class,"1-->"+fragments.get(1).toString());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View view, int id) {
        switch (id){
            case R.id.id_tv_audio:
                //当点击了audio的TextView时，将ViewPager设置为第一个
                //当ViewPager发生改变时，会调用scollIndecator方法，指示条随即也会发生改变
                vp_listing.setCurrentItem(1);
                break;
            case R.id.id_tv_video:
                vp_listing.setCurrentItem(0);
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
