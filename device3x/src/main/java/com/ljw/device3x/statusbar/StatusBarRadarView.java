package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ljw.device3x.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/10/9 0009.
 */
public class StatusBarRadarView extends ImageView{
    private static final String RADAR_STATUS_ON = "com.wanma.action.RADAR_STATUS_ON";
    private static final String RADAR_STATUS_OFF = "com.wanma.action.RADAR_STATUS_OFF";

    private static final int RADAR_ON = 1;
    private static final int RADAR_OFF = 0;

    private RadarHandler radarHandler;

    public StatusBarRadarView(Context context) {
        this(context, null);
    }

    public StatusBarRadarView(Context context, AttributeSet attrs) {
        super(context,attrs);
        radarHandler = new RadarHandler(this);
    }

    private static class RadarHandler extends Handler {
        WeakReference<StatusBarRadarView> mView;

        public RadarHandler(StatusBarRadarView view) {
            mView = new WeakReference<StatusBarRadarView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            StatusBarRadarView view = mView.get();
            if(msg.what != RADAR_ON)
                view.setVisibility(View.GONE);
            else {
                view.setVisibility(View.VISIBLE);
                view.setImageResource(R.mipmap.statusbar_radar);
            }
        }
    }

    private BroadcastReceiver radarReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(RADAR_STATUS_ON)) {
                Log.i("ljwtest:", "雷达已开启");
                radarHandler.sendEmptyMessage(RADAR_ON);
            } else if(action.equals(RADAR_STATUS_OFF)) {
                Log.i("ljwtest:", "雷达已关闭");
                radarHandler.sendEmptyMessage(RADAR_OFF);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(RADAR_STATUS_ON);
        intentFilter.addAction(RADAR_STATUS_OFF);
        getContext().registerReceiver(radarReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(radarReceive);
    }
}
