package com.ljw.device3x.customview;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;
import com.ljw.device3x.common.CommonBroacastName;
import com.ljw.device3x.common.CommonCtrl;

/**
 * Created by Administrator on 2016/5/4 0004.
 */
public class WifiStateView extends LinearLayout{
    public static final String TAG = "WifiStateView";
    private static final int LEVEL_DGREE = 4;
    //定义wifi信号等级
    private static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_NONE = -1;

    private ImageView wifiImage;
    private TextView wifiName;
    WifiManager mWifiManager;
    ConnectivityManager mCM;
    Context context;
    MobileStateView mobileStateView;


    private BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //WifiManager.WIFI_STATE_CHANGED_ACTION
            Log.i(TAG, "onReceive " + action);
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                //如果wifi没有连接成功，则显示wifi图标无连接的状�??
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    Log.i("ljwtest:", "wifi已关闭");

//                    mobileStateView.openDataNetworkIfWifiIsClose();
                }
                refreshButton();


//                if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
//                    Toast.makeText(getContext(), "已开启wifi", Toast.LENGTH_SHORT).show();
//                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
//                    if(wifiInfo != null)
//                        wifiName.setText(wifiInfo.getSSID());
//                }

//
//            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//
//                NetworkInfo netInfo = mCM.getActiveNetworkInfo();
//                //当前网络无连接，当前网络不是wifi连接,当前网络是wifi但是没有连接，wifi图标都显示无连接
//                if (netInfo == null || netInfo.getType() != ConnectivityManager.TYPE_WIFI) {
//                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
//                } else if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && !netInfo.isConnected()) {
//                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
//
//                }
//
//            } else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
//                //当信号的rssi值发生变化时，在这里处理
//                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
//                    mWifiHandler.sendEmptyMessage(LEVEL_NONE);
//                    return;
//                }
//                WifiInfo info = mWifiManager.getConnectionInfo();
//                //计算wifi的信号等�?
//                int level = WifiManager.calculateSignalLevel(info.getRssi(), LEVEL_DGREE);
//                Log.i(TAG, "wifi rssi " + info.getRssi());
//                mWifiHandler.sendEmptyMessage(level);
                changeWifiDisplay();
            }

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                changeWifiDisplay();
            }
        }
    };

    private void changeWifiDisplay() {
        WifiManager wifimanager = (WifiManager)getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifimanager.getConnectionInfo();
        if(wifiinfo != null) {
            String name = deleteDoubleQuotation(wifiinfo.getSSID());
            wifiName.setGravity(Gravity.CENTER);
            Log.e("ljwtest:", "网络状态改变后的wifi名" + name);
            wifiName.setText((!TextUtils.isEmpty(name) && !"0x".equals(name)) ? name : "WLAN");
//                    wifiName.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    private String deleteDoubleQuotation(String s) {
        if(!TextUtils.isEmpty(s)) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < s.length(); ++i) {
                if(s.charAt(i) != '"')
                    sb.append(s.charAt(i));
            }
            return sb.toString();
        }
        return null;
    }


    public WifiStateView(Context context) {
        this(context, null);
    }

    public WifiStateView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public WifiStateView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wifistateview, this);
        wifiImage = (ImageView) findViewById(R.id.wifiimage);
        wifiName = (TextView) findViewById(R.id.wifiname);
        wifiName.setTextColor(context.getResources().getColor(R.color.dark_text));
        mWifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mobileStateView = new MobileStateView(context);

        wifiImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWifiEnabled(mWifiManager.isWifiEnabled());
            }
        });

        wifiImage.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(CommonBroacastName.SYSTEM_WIFIWETTING);
                intent.putExtra("jumptype", "wifi");
                context.startActivity(intent);
                return false;
            }
        });
        refreshButton();
    }

    /**
     * 更新按钮状态
     */
    private void refreshButton() {
        wifiImage.setImageResource(mWifiManager.isWifiEnabled() ? R.mipmap.wifi_on : R.mipmap.wifi_off);
        wifiName.setTextColor(mWifiManager.isWifiEnabled() ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.dark_text));
    }

    private void setWifiEnabled(boolean enabled) {
        if(CommonCtrl.isWifiApEnabled(context))
            CommonCtrl.setWifiApEnabled(context, false);
        mWifiManager.setWifiEnabled(enabled ? false : true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter mFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //注册广播接收�?
        getContext().registerReceiver(mWifiStateReceiver, mFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //注销广播接收�?
        getContext().unregisterReceiver(mWifiStateReceiver);
    }
}
