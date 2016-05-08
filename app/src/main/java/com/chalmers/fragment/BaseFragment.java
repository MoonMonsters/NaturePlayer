package com.chalmers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chalmers.interfaces.UiOperations;
import com.chalmers.utils.Utils;

/**
 * Created by Chalmers on 2016-05-08 19:07.
 * email:qxinhai@yeah.net
 */
public abstract class BaseFragment extends Fragment implements UiOperations,View.OnClickListener{

    View rootView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(getLayoutResId(), null);

        View view = rootView.findViewById(android.R.id.content);
        Utils.findButtonSetOnClickListener(view,this);

        initView();
        initData();
        initListener();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 根据id初始化控件信息，使用泛型避免强制转换的麻烦
     * @param id 控件id
     * @param <T> 泛型
     * @return 控件
     */
    public <T> T findView(int id){
        T view = (T)getActivity().findViewById(id);

        return view;
    }

    /**
     * 给继承了该Activity的类的公共Button设置监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //如果不是已经设定了点击事件的Button，那么便使用自定义的点击事件
            default:
                onClick(v,v.getId());
                break;
        }
    }

    /**
     * 在屏幕中间显示Toast
     * @param text 显示信息
     */
    public void showToast(String text){
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
