package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ljw.device3x.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/11/23 0023.
 */

public class NetworkTypeView extends ImageView{
    private static final int NETWORN_2G = 0;
    private static final int NETWORN_3G = 1;
    private static final int NETWORN_4G = 2;

    private Context context;

    private NetworkTypeHandler networkTypeHandler;

    public NetworkTypeView(Context context) {
        this(context, null);
    }

    public NetworkTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        networkTypeHandler = new NetworkTypeHandler(this);
        this.context = context;
    }

    private static class NetworkTypeHandler extends Handler {
        WeakReference<NetworkTypeView> mView;

        public NetworkTypeHandler(NetworkTypeView view) {
            mView = new WeakReference<NetworkTypeView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            NetworkTypeView view = mView.get();
            switch (msg.what) {
                case NETWORN_2G: {
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.mipmap.stat_sys_network_type_e);
                }
                break;
                case NETWORN_3G: {
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.mipmap.stat_sys_network_type_3g);
                }
                break;
                case NETWORN_4G: {
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.mipmap.stat_sys_network_type_4g);
                }
                break;
                case -1:
                    view.setVisibility(INVISIBLE);

            }
        }
    }

    private BroadcastReceiver networkTypeReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("com.private.datanetwork.change".equals(action)) {
//                Toast.makeText(context, "网络状态已改变", Toast.LENGTH_SHORT).show();
                int type = intent.getIntExtra("datanettype", -1);
                networkTypeHandler.sendEmptyMessage(getDataNetworkType(type));
//                setNetworkTypeIcon(type);
            } else if("com.launcher.hidenettype".equals(action))
                networkTypeHandler.sendEmptyMessage(-1);//在sim卡拔出后隐藏网络类型图标
        }
    };

    private int getDataNetworkType(int sysDataType) {
//        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        //获取当前网络类型，如果为空，返回无网络
//        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
//        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
//            return -1;
//        }

//        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if (null != networkInfo) {
//            NetworkInfo.State state = networkInfo.getState();
//            String strSubTypeName = networkInfo.getSubtypeName();
//            if (null != state) {
                //                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                switch (sysDataType) {
                    //如果是2g类型
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                    case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                    case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORN_2G;
                    //如果是3g类型
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORN_3G;
                    //如果是4g类型
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORN_4G;
//                        default:
//                            //中国移动 联通 电信 三种3G制式
//                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
//                                return NETWORN_3G;
//                            } else {
//                                return NETWORN_MOBILE;
//                            }
                }
//                }
//            }

//        }
        return -1;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction("com.private.datanetwork.change");
        intentFilter.addAction("com.launcher.hidenettype");
        getContext().registerReceiver(networkTypeReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(networkTypeReceive);
    }
}
