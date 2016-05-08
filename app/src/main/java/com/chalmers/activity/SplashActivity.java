package com.chalmers.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class SplashActivity extends BaseActivity {

    private Handler handler = null;

    @Override
    public void initListener() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        delayEnterHome();
    }

    /**
     * 延迟进入主界面
     */
    private void delayEnterHome() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHome();
            }
        }, 3000);
    }

    /**
     * 进入主界面
     */
    private void enterHome(){
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);

        //关闭界面
        SplashActivity.this.finish();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onClick(View view, int id) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            //当点击界面时，就直接进入主界面
            case MotionEvent.ACTION_DOWN:
                //取消延迟
                handler.removeCallbacksAndMessages(null);
                enterHome();
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }
}