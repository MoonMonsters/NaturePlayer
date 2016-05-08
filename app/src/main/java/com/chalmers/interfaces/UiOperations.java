package com.chalmers.interfaces;

import android.view.View;

/**
 * Created by Chalmers on 2016-05-08 18:47.
 * email:qxinhai@yeah.net
 */
public interface UiOperations {

    /**
     * 初始化监听事件
     */
    void initListener();

    /**
     * 初始化控件信息
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 返回布局id，用来设置Activity布局
     * @return int 布局id
     */
    int getLayoutResId();

    /**
     * 点击事件
     * @param view Button
     * @param id Button的id
     */
    void onClick(View view, int id);
}
