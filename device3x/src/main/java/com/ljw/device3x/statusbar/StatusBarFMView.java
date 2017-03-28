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
 * Created by Administrator on 2016/10/9 0009.
 */
public class StatusBarFMView extends ImageView{

    /**
     * 发送打开FM广播
     */
    String FM_STATE_IS_ON = "OpenFMBroadcast";

    /**
     * 发送关闭FM广播
     */
    String FM_STATE_IS_OFF = "CloseFMBroadcast";

    private static final int FM_ON = 1;
    private static final int FM_OFF = 0;

    private FMHandler fmHandler;

    public StatusBarFMView(Context context) {
        this(context, null);
    }

    public StatusBarFMView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fmHandler = new FMHandler(this);
    }

    private static class FMHandler extends Handler {
        WeakReference<StatusBarFMView> mView;

        public FMHandler(StatusBarFMView view) {
            mView = new WeakReference<StatusBarFMView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            StatusBarFMView view = mView.get();
            if(msg.what != FM_ON)
                view.setVisibility(View.GONE);
            else {
                view.setVisibility(View.VISIBLE);
                view.setImageResource(R.mipmap.statusbar_fm);
            }
        }
    }

    private BroadcastReceiver FMReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(FM_STATE_IS_ON))
                fmHandler.sendEmptyMessage(FM_ON);
            else if(action.equals(FM_STATE_IS_OFF))
                fmHandler.sendEmptyMessage(FM_OFF);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(FM_STATE_IS_ON);
        intentFilter.addAction(FM_STATE_IS_OFF);
        getContext().registerReceiver(FMReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(FMReceive);
    }
}
