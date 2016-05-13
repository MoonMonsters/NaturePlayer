package com.chalmers.utils;

/**
 * Created by Chalmers on 2016-05-11 22:48.
 * email:qxinhai@yeah.net
 */

import android.util.Log;

/**
 * 打印类
 * 万能打印
 */
public class Logger {

    private static boolean isShowLog = true;

    /**
     * 自定义打印方法
     * @param objLog 输出TAG
     * @param msg 输出内容
     */
    public static void d(Object objLog, String msg){

        //如果不需要打印log，则直接返回
        if(!isShowLog){
            return ;
        }

        String tag = null;
        //如果objLog是String类型，则直接打印输出
        //如果objLog是Class或者Object类型，则提取类名进行打印
        if(objLog instanceof String){
            tag = (String) objLog;
        }else if(objLog instanceof Class){
            tag = ((Class) objLog).getSimpleName();
        }else{
            tag = objLog.getClass().getSimpleName();
        }

        //打印输出
        Log.d(tag,msg);

    }
}
