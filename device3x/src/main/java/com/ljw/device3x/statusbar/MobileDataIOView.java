package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ljw.device3x.R;
import com.ljw.device3x.Utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/12/17 0017.
 */

public class MobileDataIOView extends ImageView{

    private static final int DATA_ACTIVITY_NONE = R.mipmap.data_none;
    private static final int DATA_ACTIVITY_IN = R.mipmap.data_in;
    private static final int DATA_ACTIVITY_OUT = R.mipmap.data_out;
    private static final int DATA_ACTIVITY_INOUT = R.mipmap.data_inout;

    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    private static final String NOTIFY_LAUNCHER_CHANGEDATAIO = "com.rayee.notifylauncherchangedataio";
    private static final String SET_LAUNCHER_CHANGEDATAIO_VISIBLE = "com.rayee.setlauncherchangedataiovisible";

    private static final int IN = 1;
    private static final int OUT = 2;
    private static final int INOUT = 3;

    private static final int IMAGE_GONE = 10;
    private static final int IMAGE_VISIBLE = 11;

    private Context context;
    private MobileDataIOViewHandler mobileDataIOViewHandler;
    private TelephonyManager telephonyManager;
    public WifiManager mWifiManager;

    public MobileDataIOView(Context context) {
        this(context, null);
    }

    public MobileDataIOView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mobileDataIOViewHandler = new MobileDataIOViewHandler(this);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private static class MobileDataIOViewHandler extends Handler {
        WeakReference<MobileDataIOView> mView;

        public MobileDataIOViewHandler(MobileDataIOView view) {
            mView = new WeakReference<MobileDataIOView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            MobileDataIOView view = mView.get();
            if(msg.what >= IMAGE_GONE)
                view.setVisibility(GONE);
            else {
                if(view.getVisibility() == GONE)
                    view.setVisibility(View.VISIBLE);
                view.setImageResource(getIOImage(msg.what));
            }
        }

        private int getIOImage(int type) {
            int icon = DATA_ACTIVITY_NONE;
            switch(type) {
                case IN:
                    icon = DATA_ACTIVITY_IN;
                    break;
                case OUT:
                    icon = DATA_ACTIVITY_OUT;
                    break;
                case INOUT:
                    icon = DATA_ACTIVITY_INOUT;
                    break;
                default:
                    break;
            }
            return icon;
        }

    }

    private BroadcastReceiver MobileDataIOReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(NOTIFY_LAUNCHER_CHANGEDATAIO.equals(action)) {
                int changeType = intent.getIntExtra("changetype", -1);
                mobileDataIOViewHandler.sendEmptyMessage(changeType);
            } else if(SET_LAUNCHER_CHANGEDATAIO_VISIBLE.equals(action)) {
                int visible = intent.getIntExtra("visible", -1);
                mobileDataIOViewHandler.sendEmptyMessage(visible);
            }
//            else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
//
//            }
            else if("com.launcher.changeiofromstrength".equals(action) || ConnectivityManager.CONNECTIVITY_ACTION.equals(action) || WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED && Utils.getInstance().getMobileDataStatus()) {
                    Log.e("ljwtest:", "mWifiManager.getWifiState()" + mWifiManager.getWifiState());
                    mobileDataIOViewHandler.sendEmptyMessage(telephonyManager.getDataActivity());
                    Log.e("ljwtest:", "telephonyManager.getDataActivity():" + telephonyManager.getDataActivity());
                }
                else
                    mobileDataIOViewHandler.sendEmptyMessage(IMAGE_VISIBLE);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(NOTIFY_LAUNCHER_CHANGEDATAIO);
        intentFilter.addAction(SET_LAUNCHER_CHANGEDATAIO_VISIBLE);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("com.launcher.changeiofromstrength");
        context.registerReceiver(MobileDataIOReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(MobileDataIOReceive);
    }
}
