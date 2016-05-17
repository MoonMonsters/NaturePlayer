package com.chalmers.bean;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by Chalmers on 2016-05-16 22:34.
 * email:qxinhai@yeah.net
 */
public class AudioItem {
    private String title;
    private String artist;
    private String path;

    public AudioItem(String title, String artist, String path) {
        this.title = title;
        this.artist = artist;
        this.path = path;
    }

    public AudioItem(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static AudioItem getAudioItem(Cursor cursor){
        AudioItem audioItem = null;

        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

        audioItem = new AudioItem(title,artist,path);

        return audioItem;
    }
}
