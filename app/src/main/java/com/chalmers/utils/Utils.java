package com.chalmers.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.chalmers.interfaces.Config;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Chalmers on 2016-05-08 19:10.
 * email:qxinhai@yeah.net
 */
public class Utils {

    /**
     * 查找Button并且设置监听事件
     */
    public static void findButtonSetOnClickListener(View view, View.OnClickListener listener) {
        //如果是布局
        if (view instanceof ViewGroup) {
            //找到该布局下的所有的Button
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                //继续遍历
                findButtonSetOnClickListener(viewGroup.getChildAt(i), listener);
            }
            //如果是Button或者ImageButton，则为它添加监听事件
        } else if (view instanceof Button || view instanceof ImageButton) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 获得屏幕的宽度
     *
     * @param context 上下文对象
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();

        return screenWidth;
    }

    /**
     * 获得屏幕的高度
     *
     * @param context 上下文对象
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getWidth();

        return height;
    }

    /**
     * 打印输出Cursor数据
     * @param cursor 游标对象
     */
    public static void printCursor(Cursor cursor) {

        //没有数据
        if(cursor == null){
            return;
        }

        /*
                Media.TITLE,        //标题
                Media._ID,          //ID
                Media.DATA,         //路径
                Media.SIZE,         //大小
                Media.DURATION      //时长
         */
        Logger.d(Utils.class,""+cursor.getCount());
        while(cursor.moveToNext()){
            Logger.d(Utils.class,"----------------------");
            Logger.d(Utils.class,cursor.getString(0));
            Logger.d(Utils.class,cursor.getString(2));
//            Logger.d(Utils.class,cursor.getLong(3) + "");
//            Logger.d(Utils.class,cursor.getLong(4) + "");
        }
    }

    public static String formatTime(long duration){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.add(Calendar.MILLISECOND,(int)duration);
        String pattern = duration > Config.HOUR ? "hh:mm:ss" : "mm:ss";

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        return sdf.format(calendar.getTime());
    }
}
