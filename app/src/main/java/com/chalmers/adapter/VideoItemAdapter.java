package com.chalmers.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.chalmers.activity.R;
import com.chalmers.bean.VideoItem;
import com.chalmers.utils.Utils;

/**
 * Created by Chalmers on 2016-05-13 10:08.
 * email:qxinhai@yeah.net
 */
public class VideoItemAdapter extends CursorAdapter {

    public VideoItemAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //需要显示的布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_video,null);
        //创建ViewHolder对象
        ViewHolder viewHolder = new ViewHolder(view);
        //加入标志中
        view.setTag(viewHolder);

        //返回
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //获得VideoItem数据
        VideoItem videoItem = VideoItem.getItemFromCursor(cursor);
        //获得标志
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        //绑定显示数据
        viewHolder.tv_title.setText(videoItem.getTitle());
        viewHolder.tv_size.setText(Formatter.formatFileSize(context,videoItem.getSize()));
        viewHolder.tv_duration.setText(Utils.formatTime(videoItem.getDuration()));
    }

    class ViewHolder{
        private TextView tv_title;
        private TextView tv_size;
        private TextView tv_duration;

        /**
         * 初始化控件
         * @param view View对象
         */
        public ViewHolder(View view){
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        }
    }
}
