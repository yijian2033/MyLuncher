package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ljw.device3x.R;

import java.lang.ref.WeakReference;

/**
 * Created by lijianwen on 16/11/25.
 */

public class StatusHotpotView extends ImageView{

    private static final String WIFI_AP_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private static final int HOTPOT_ON = 0;
    private static final int HOTPOT_OFF = 1;
    private HotpotHandler hotpotHandler;


    public StatusHotpotView(Context context) {
        this(context, null);
    }

    public StatusHotpotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        hotpotHandler = new HotpotHandler(this);
    }

    private static class HotpotHandler extends Handler {
        WeakReference<StatusHotpotView> mView;

        public HotpotHandler(StatusHotpotView view) {
            mView = new WeakReference<StatusHotpotView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            StatusHotpotView view = mView.get();
            if(msg.what != HOTPOT_ON)
                view.setVisibility(View.GONE);
            else {
                view.setVisibility(View.VISIBLE);
                view.setImageResource(R.mipmap.statusbar_hotpot);
            }
        }
    }

    private BroadcastReceiver HotpotReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WIFI_AP_ACTION)) {
                int state = intent.getIntExtra("wifi_state",  0);
                if(state != 0) {
                    if(state == 10 || state == 11)
                        hotpotHandler.sendEmptyMessage(HOTPOT_OFF);
                    else if(state == 12 || state == 13)
                        hotpotHandler.sendEmptyMessage(HOTPOT_ON);
                }
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(WIFI_AP_ACTION);
        getContext().registerReceiver(HotpotReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(HotpotReceive);
    }
}
