package com.chalmers.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by Chalmers on 2016-05-13 10:11.
 * email:qxinhai@yeah.net
 */
public class VideoItem implements Serializable{
    //主题
    private String title;
    //路径
    private String path;
    //大小
    private long size;
    //时长
    private long duration;

    public VideoItem(String title, String path, long size, long duration) {
        this.title = title;
        this.path = path;
        this.size = size;
        this.duration = duration;
    }

    public VideoItem(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 从游标中获得数据并返回VideoItem对象
     * @param cursor 游标
     * @return VideoItem对象
     */
    public static VideoItem getItemFromCursor(Cursor cursor){
        VideoItem videoItem = null;

        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        videoItem = new VideoItem(title,path,size,duration);

        return videoItem;
    }
}
