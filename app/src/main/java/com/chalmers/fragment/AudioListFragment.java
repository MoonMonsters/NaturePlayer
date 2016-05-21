package com.chalmers.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chalmers.activity.AudioPlayerActivity;
import com.chalmers.activity.R;
import com.chalmers.adapter.AudioItemAdapter;
import com.chalmers.bean.AudioItem;
import com.chalmers.interfaces.Keys;

import java.util.ArrayList;

/**
 * Created by Chalmers on 2016-05-08 22:13.
 * email:qxinhai@yeah.net
 */
public class AudioListFragment extends BaseFragment{

    private ListView listView = null;
    private AudioItemAdapter adapter = null;
    private ArrayList<AudioItem> audioItems = null;

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enterAudioPlayerActivity(position);
            }
        });
    }

    /**
     * 点击进入音乐播放界面中
     * @param position 进入界面后需要播放音乐的位置
     */
    private void enterAudioPlayerActivity(int position) {

        Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
        //传递所有列表过去，在那边需要切换歌曲
        intent.putExtra(Keys.AUDIO_LIST,audioItems);
        //当前点击的位置
        intent.putExtra(Keys.CURPOSITION,position);
        startActivity(intent);

    }

    private ArrayList<AudioItem> getListFromCursor(Cursor cursor){
        ArrayList<AudioItem> audioItems = new ArrayList<>();

        //避免错误，需要先移动到第一位去
        cursor.moveToFirst();
        do{
            //获得音乐列表
            audioItems.add(AudioItem.getAudioItem(cursor));
        }while(cursor.moveToNext());

        return audioItems;
    }


    @Override
    public void initView() {
        listView = findView(R.id.lv_media);
    }

    @Override
    public void initData() {
        //异步查询
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//                Utils.printCursor(cursor);
                //适配器对象
                //使用CursorAdapter的原因是，它使用了观察者模式，当有新的音乐进来时，它会自动刷新列表
                adapter = new AudioItemAdapter(getActivity(),cursor,false);
                audioItems = getListFromCursor(cursor);
                listView.setAdapter(adapter);
            }
        };

        int token = 0;  //相当于msg.what
        Object cookie = null;   //msg.obj
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        //查询标题，id，艺术家，路径
        String[] projection = {Media.TITLE, Media._ID, Media.ARTIST, Media.DATA};
        String selection = null;
        String[] selectionArgs = null;
        //按标题的升序排序
        String orderBy = Media.TITLE + " ASC";
        queryHandler.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_media_list;
    }

    @Override
    public void onClick(View view, int id) {

    }
}
