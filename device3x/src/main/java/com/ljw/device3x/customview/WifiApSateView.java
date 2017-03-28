package com.ljw.device3x.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;
import com.ljw.device3x.common.CommonCtrl;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/8/22 0022.
 */
public class WifiApSateView extends LinearLayout{

    private ImageView imageView;
    private TextView textView;
    private IntentFilter intentFilter;
    private WifiManager wifiManager;
    private static final String SYSTEM_WIFIWETTING = "acyion_my_wifisetting";

    private static final String WIFI_AP_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    public WifiApSateView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wifiapstateview, this);
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        imageView = (ImageView) findViewById(R.id.wifiapname);
        textView = (TextView) findViewById(R.id.wifiaptext);
        imageView.setImageResource(CommonCtrl.isWifiApEnabled(context) ? R.mipmap.wifiap_on : R.mipmap.wifiap_off);
        textView.setTextColor(CommonCtrl.isWifiApEnabled(context) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.dark_text));
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWifiApEnabled(CommonCtrl.isWifiApEnabled(context) ? false : true);
            }
        });


        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(SYSTEM_WIFIWETTING);
                intent.putExtra("jumptype", "hotspot");
                context.startActivity(intent);
                return false;
            }
        });
    }


//    public boolean isWifiApEnabled() {
//        WifiManager mWifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
//        try {
//            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
//            method.setAccessible(true);
//            return (Boolean) method.invoke(mWifiManager);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return false;
//    }

    // wifi热点开关
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    private BroadcastReceiver wifiApReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WIFI_AP_ACTION)) {
                int state = intent.getIntExtra("wifi_state",  0);
                if(state != 0) {
                    if(state == 10 || state == 11) {
                        imageView.setImageResource(R.mipmap.wifiap_off);
                        textView.setTextColor(context.getResources().getColor(R.color.dark_text));
                    } else if(state == 12 || state == 13) {
                        imageView.setImageResource(R.mipmap.wifiap_on);
                        textView.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        intentFilter = new IntentFilter(WIFI_AP_ACTION);
        getContext().registerReceiver(wifiApReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(wifiApReceive);
    }
}
