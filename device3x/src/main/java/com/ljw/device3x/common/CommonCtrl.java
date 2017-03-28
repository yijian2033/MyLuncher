package com.ljw.device3x.common;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/11/1 0001.
 */
public class CommonCtrl {
    public static boolean isBackFobidden = true;
    public static boolean isFirstSystemTimeSet = false;//第一次开机利用GPS时间设置为系统时间
    public static long tmpSystemTime = 0;//缓存系统时间

    public static boolean isWifiApEnabled(Context context) {
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    // wifi热点开关
    public static boolean setWifiApEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

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
}
