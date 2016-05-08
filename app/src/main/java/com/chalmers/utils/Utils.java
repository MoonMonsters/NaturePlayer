package com.chalmers.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Chalmers on 2016-05-08 19:10.
 * email:qxinhai@yeah.net
 */
public class Utils {

    /**
     * 查找Button并且设置监听事件
     */
    public static void findButtonSetOnClickListener(View view, View.OnClickListener listener){
        //如果是布局
        if(view instanceof ViewGroup){
            //找到该布局下的所有的Button
            ViewGroup viewGroup = (ViewGroup) view;
            for(int i=0; i<viewGroup.getChildCount(); i++){
                //继续遍历
                findButtonSetOnClickListener(viewGroup.getChildAt(i),listener);
            }
            //如果是Button或者ImageButton，则为它添加监听事件
        }else if(view instanceof Button || view instanceof ImageButton){
            view.setOnClickListener(listener);
        }
    }
}
