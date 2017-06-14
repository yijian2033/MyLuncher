package com.ljw.device3x.Activity;

import android.app.Application;
import android.content.Context;
import android.hardware.camera2.params.Face;
import android.util.Log;
import android.widget.Toast;

//import common.DumpUtils;

/**
 * Created by Administrator on 2016/8/14 0014.
 */
public class DeviceApplication extends Application{

    private static Context mContext;
    public static String city = "";
    public static boolean isNetworkAvailable;
    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Unknown Error");
        }
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        isNetworkAvailable = false;
//
//        DumpUtils.getInstance().initialize(mContext);
//        DumpUtils.getInstance().setEnabledCacheLog(true);
//        DumpUtils.getInstance().setEnabledSendLog(true);
    }
}
