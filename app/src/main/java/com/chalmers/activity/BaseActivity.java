package com.chalmers.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.chalmers.interfaces.UiOperations;
import com.chalmers.utils.Utils;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity
        implements View.OnClickListener,UiOperations{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        View rootView = findViewById(android.R.id.content);
        Utils.findButtonSetOnClickListener(rootView,this);

        initView();
        initData();
        initListener();
    }

    /**
     * 根据id初始化控件信息，使用泛型避免强制转换的麻烦
     * @param id 控件id
     * @param <T> 泛型
     * @return 控件
     */
    public <T> T findView(int id){
        T view = (T)super.findViewById(id);

        return view;
    }

    /**
     * 给继承了该Activity的类的公共Button设置监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //如果不是已经设定了点击事件的Button，那么便使用自定义的点击事件
            default:
                onClick(v,v.getId());
                break;
        }
    }

    /**
     * 在屏幕中间显示Toast
     * @param text 显示信息
     */
    public void showToast(String text){
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
