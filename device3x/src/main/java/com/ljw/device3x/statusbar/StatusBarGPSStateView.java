package com.ljw.device3x.statusbar;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ljw.device3x.Activity.DeviceApplication;
import com.ljw.device3x.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ljw：Administrator on 2017/7/5 0005 15:52
 */
public class StatusBarGPSStateView  extends ImageView{
    private Context context;
    private RecStartTimerTask recStartTimerTask;
    private Timer recStartTimer;
    private Handler mHandler;
    private  int count = 0;//图片循环计数器
    public StatusBarGPSStateView(Context context) {
        this(context, null);
    }



    public StatusBarGPSStateView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        recStartTimer = new Timer(true);
        mHandler = new GPSHandler(this);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRecyleRecImg();
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(recStartTimer != null && recStartTimerTask != null)
            recStartTimerTask.cancel();
    }
    private void startRecyleRecImg() { //当行车记录仪打开时打开图片切换
        if(recStartTimer != null && recStartTimerTask != null)
            recStartTimerTask.cancel();
        recStartTimerTask = new RecStartTimerTask();
       recStartTimer.schedule(recStartTimerTask, 1000, 1000);
    }

    class RecStartTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(DeviceApplication.GPSState);
        }


    }


    private class GPSHandler extends Handler {
        WeakReference<StatusBarGPSStateView> mView;
        private RecStartTimerTask recStartTimerTask;
        public GPSHandler(StatusBarGPSStateView view) {
            mView = new WeakReference<StatusBarGPSStateView>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mView.get() == null)
                return;
            StatusBarGPSStateView view = mView.get();
            if (DeviceApplication.GPSState == 0){//GPS关闭

                view.setVisibility(View.GONE);
            }else if (DeviceApplication.GPSState == 1){//GPS打开，定位中
                view.setImageResource(R.mipmap.gps);
                if(count >= 2)
                    count = 0;
                if (count == 0)
                    view.setVisibility(View.VISIBLE);
                else if (count == 1)
                    view.setVisibility(View.INVISIBLE);
                count++;
            }else if (DeviceApplication.GPSState == 2){//GPS打开，已定位
                view.setVisibility(View.VISIBLE);
                view.setImageResource(R.mipmap.gps);
            }
        }
    }
}
