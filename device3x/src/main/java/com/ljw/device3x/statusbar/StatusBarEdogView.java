package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ljw.device3x.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/10/9 0009.
 */
public class StatusBarEdogView extends ImageView {
    public static final String RAYEE_EDOG_STATE = "rayee_edog_state";

    private static final String CTRL_EDOG = "ctrlEdog";
    private static final String CTRL_RD = "ctrlRd";
    private static final Uri CTRL_URI = Uri.parse("content://tk.huayu.edog.WriteProvider/Control");

    private static final String EDOG_STATUS_ON = "com.wanma.action.EDOG_STATUS_ON";
    private static final String EDOG_STATUS_OFF = "com.wanma.action.EDOG_STATUS_OFF";

    private static final int EDOG_ON = 1;
    private static final int EDOG_OFF = 0;
    private EdogHandler edogHandler;

    public StatusBarEdogView(Context context) {
        this(context, null);
    }

    public StatusBarEdogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        edogHandler = new EdogHandler(this);
    }


    private static class EdogHandler extends Handler {
        WeakReference<StatusBarEdogView> mView;

        public EdogHandler(StatusBarEdogView view) {
            mView = new WeakReference<StatusBarEdogView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            StatusBarEdogView view = mView.get();
            if(msg.what != EDOG_ON)
                view.setVisibility(View.GONE);
            else {
                view.setVisibility(View.VISIBLE);
                view.setImageResource(R.mipmap.statusbar_edog);
            }

        //    Settings.System.putInt(mView.get().getContext().getContentResolver(),RAYEE_EDOG_STATE,msg.what);
        }
    }

    private BroadcastReceiver edogReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(EDOG_STATUS_ON.equals(action)) {
                Log.i("ljwtest:", "电子狗已开启");
                edogHandler.sendEmptyMessage(EDOG_ON);
            }
            else {
                Log.i("ljwtest:", "电子狗已关闭");
                edogHandler.sendEmptyMessage(EDOG_OFF);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(EDOG_STATUS_ON);
        intentFilter.addAction(EDOG_STATUS_OFF);
        getContext().registerReceiver(edogReceive, intentFilter);
        initState();
    }

    private void initState() {
        int state = 0;
        String[] columns = new String[]{CTRL_EDOG, CTRL_RD};
        Cursor cursor = getContext().getContentResolver().query(CTRL_URI, columns, null, null, null);
        if ((null != cursor) && (cursor.moveToFirst())) {
            state = cursor.getInt(cursor.getColumnIndex(CTRL_EDOG)) ;
            cursor.close();
        }
      //  int state = Settings.System.getInt(getContext().getContentResolver(),RAYEE_EDOG_STATE,0);
        if(state != EDOG_ON)
            setVisibility(View.GONE);
        else {
            setVisibility(View.VISIBLE);
            setImageResource(R.mipmap.statusbar_edog);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(edogReceive);
    }
}
