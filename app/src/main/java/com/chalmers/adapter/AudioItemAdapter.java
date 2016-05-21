package com.chalmers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.chalmers.activity.R;
import com.chalmers.bean.AudioItem;

/**
 * Created by Chalmers on 2016-05-16 22:32.
 * email:qxinhai@yeah.net
 */
public class AudioItemAdapter extends CursorAdapter {

    public AudioItemAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_audio,null);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        AudioItem audioItem = AudioItem.getAudioItem(cursor);
        viewHolder.tv_title.setText(audioItem.getTitle());
        viewHolder.tv_artist.setText(audioItem.getArtist());
    }

    class ViewHolder{
        private TextView tv_title;
        private TextView tv_artist;

        public ViewHolder(View view){
            tv_artist = (TextView)view.findViewById(R.id.tv_audio_artist);
            tv_title = (TextView) view.findViewById(R.id.tv_audio_title);
        }
    }
}
