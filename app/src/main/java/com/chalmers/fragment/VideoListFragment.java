package com.chalmers.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chalmers.activity.R;
import com.chalmers.activity.VideoPlayerActivity;
import com.chalmers.adapter.VideoItemAdapter;
import com.chalmers.bean.VideoItem;
import com.chalmers.interfaces.Keys;

import java.util.ArrayList;

/**
 * Created by Chalmers on 2016-05-08 22:13.
 * email:qxinhai@yeah.net
 */
public class VideoListFragment extends BaseFragment{
    private ListView lv_video = null;

    @Override
    public void initListener() {
        lv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //点击之后，可以获得Cursor对象
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ArrayList<VideoItem> videoItems = getAllVideoItem(cursor);

                enterVideoPalyerActivity(videoItems,position);
            }
        });
    }

    private void enterVideoPalyerActivity(ArrayList<VideoItem> videoItems, int position) {
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra(Keys.VIDEO_LIST,videoItems);
        intent.putExtra(Keys.CURPOSITION,position);
        startActivity(intent);
    }

    private ArrayList<VideoItem> getAllVideoItem(Cursor cursor){
        //如果为空，则返回空
        if(cursor == null){
            return null;
        }

        ArrayList<VideoItem> videoItems = new ArrayList<>();
        cursor.moveToFirst();
        //通过循环，获得所有的VideoItem对象数据
        do{
            videoItems.add(VideoItem.getItemFromCursor(cursor));
        }while(cursor.moveToNext());

        return videoItems;
    }

    @Override
    public void initView() {
        lv_video = (ListView) rootView.findViewById(R.id.lv_media);
//        lv_video = findView(R.id.lv_video);
    }

    @Override
    public void initData() {
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//                Utils.printCursor(cursor);
                VideoItemAdapter videoItemAdapter = new VideoItemAdapter(getActivity(),cursor,false);
                lv_video.setAdapter(videoItemAdapter);
            }
        };

        int token = 0;  //相当于Message中的what
        Object cookie = null;  //相当于Message中obj
        Uri uri = Media.EXTERNAL_CONTENT_URI;  //查询存储器中的Video数据
        String[] projection = new String[]{
                Media.TITLE,        //标题
                Media._ID,          //ID
                Media.DATA,         //路径
                Media.SIZE,         //大小
                Media.DURATION      //时长
        };    //查询项
        String selection = null;       //查询条件
        String[] selectionArgs = null; //具体数据
        String orderBy = Media.TITLE + " ASC";         //排序方式
        //使用此方法，会在子线程中进行查询操作
        asyncQueryHandler.startQuery(token,cookie,uri,projection,selection,selectionArgs,orderBy);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_media_list;
    }

    @Override
    public void onClick(View view, int id) {

    }
}
