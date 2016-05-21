package com.chalmers.interfaces;

import com.chalmers.bean.AudioItem;

/**
 * Created by Chalmers on 2016-05-18 21:42.
 * email:qxinhai@yeah.net
 */
public interface UIActivity {
    void updateUI(AudioItem audioItem);

    void updateTimeText(int totleTime, int curTime);
}
