package com.chalmers.fragment;

import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.ListView;

import com.chalmers.activity.R;
import com.chalmers.adapter.AudioItemAdapter;
import com.chalmers.bean.AudioItem;

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

    }

    @Override
    public void initView() {
        listView = findView(R.id.lv_media);
        audioItems = new ArrayList<>();

    }

    @Override
    public void initData() {
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//                Utils.printCursor(cursor);
                adapter = new AudioItemAdapter(getActivity(),cursor,false);
                listView.setAdapter(adapter);
            }
        };

        int token = 0;
        Object cookie = null;
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] projection = {Media.TITLE, Media._ID, Media.ARTIST, Media.DATA};
        String selection = null;
        String[] selectionArgs = null;
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
