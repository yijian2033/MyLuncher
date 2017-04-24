package com.ljw.device3x.Utils;

import android.view.View;
import android.widget.Toast;

import com.ljw.device3x.Activity.DeviceApplication;

/**
 * ljw：Administrator on 2017/4/24 0024 16:12
 */
public abstract class FiveClickListener implements View.OnClickListener {


    private  final String TAG = NoDoubleClickListener.class.getName();

    private  long lastTime;

    private long delay;

    private int has_click_times = 0;

    public abstract void singleClick(View view);

    public FiveClickListener(long delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick(v)) {
            has_click_times++;
            if (has_click_times == 4)
                singleClick(v);
            return;
        }else{
            has_click_times = 0;
        }


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
