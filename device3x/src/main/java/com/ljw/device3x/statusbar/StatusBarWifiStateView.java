package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.ljw.device3x.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/5/4 0004.
 */
public class StatusBarWifiStateView extends ImageView {
    public static final String TAG = "WifiStateView";
    private static final int LEVEL_DGREE = 4;

    private static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_NONE = -1;

    WifiManager mWifiManager;
    WifiHandler mWifiHandler;
    ConnectivityManager mCM;


    private static class WifiHandler extends Handler {
        WeakReference<StatusBarWifiStateView> mView;

        public WifiHandler(StatusBarWifiStateView view) {
            mView = new WeakReference<StatusBarWifiStateView>(view);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mView.get() == null) {
                return;
            }
            StatusBarWifiStateView view = mView.get();
            Log.i(TAG, "handleMessage level " + msg.what);

            if(msg.what == LEVEL_NONE)
                view.setVisibility(View.GONE);
            else {
                if(view.getVisibility() == View.GONE)
                    view.setVisibility(View.VISIBLE);
                switch (msg.what) {
                case LEVEL_0:
                    view.setImageResource(R.mipmap.wifi0);
                    break;
                case LEVEL_1:
                    view.setImageResource(R.mipmap.wifi1);
                    break;
                case LEVEL_2:
                    view.setImageResource(R.mipmap.wifi2);
                    break;

                case LEVEL_3:
                    view.setImageResource(R.mipmap.wifi3);
                    break;
            }
            }
        }

    }


    private BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //WifiManager.WIFI_STATE_CHANGED_ACTION
            Log.i(TAG, "onReceive " + action);
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                Log.e("ljwtest:", "WIFI_STATE_CHANGED_ACTION");

                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
                    Log.e("ljwtest:", "WIFI_STATE_CHANGED_ACTION  LEVEL_NONE");
                    return;
                }

            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.e("ljwtest:", "CONNECTIVITY_ACTION");

                NetworkInfo netInfo = mCM.getActiveNetworkInfo();

                if (netInfo == null || netInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
                } else if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && !netInfo.isConnected()) {
                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
                }
                Log.e("ljwtest:", "CONNECTIVITY_ACTION  LEVEL_NONE");

            } else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Log.e("ljwtest:", "RSSI_CHANGED_ACTION");
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
                    Log.e("ljwtest:", "RSSI_CHANGED_ACTION  LEVEL_NONE");
                    return;
                }
                WifiInfo info = mWifiManager.getConnectionInfo();
//                NetworkInfo networkInfo = mCM.getActiveNetworkInfo();
                int level = WifiManager.calculateSignalLevel(info.getRssi(), LEVEL_DGREE);
                String wifiName = info.getSSID();
                Log.e("ljwtest:", "wifi rssi " + info.getRssi() + "wifi名是" + info.getSSID());
//                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                if(!TextUtils.isEmpty(wifiName) || !wifiName.contains("0x"))
                    mWifiHandler.sendEmptyMessage(level);
            }
        }
    };

    public StatusBarWifiStateView(Context context) {
        this(context, null);
    }

    public StatusBarWifiStateView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public StatusBarWifiStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiHandler = new WifiHandler(this);
//        this.setImageResource(R.mipmap.wifinone);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter mFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        getContext().registerReceiver(mWifiStateReceiver, mFilter);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mWifiHandler.removeCallbacksAndMessages(null);

        getContext().unregisterReceiver(mWifiStateReceiver);
    }
}
