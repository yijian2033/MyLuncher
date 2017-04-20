package com.ljw.device3x.Utils;

import android.view.View;
import android.widget.Toast;

import com.ljw.device3x.Activity.DeviceApplication;


/**
 * 为了解决重复点击的问题
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {


    private static final String TAG = NoDoubleClickListener.class.getName();

    private static long lastTime;

    private long delay;

    public abstract void singleClick(View view);

    public NoDoubleClickListener(long delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick(v)) {
            Toast.makeText(DeviceApplication.getContext(),"点击间隔时间太短",Toast.LENGTH_SHORT).show();
            return;
        }
        singleClick(v);
    }

    public boolean onMoreClick(View view) {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }


}
