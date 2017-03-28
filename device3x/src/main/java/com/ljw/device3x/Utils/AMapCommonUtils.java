package com.ljw.device3x.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class AMapCommonUtils {
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private static String LOCATIONTIME = "600000";

    private Context context;
    private AMapLocationListener aMapLocationListener;

    private static AMapCommonUtils aMapCommonUtils;

    public AMapCommonUtils(Context context, AMapLocationListener aMapLocationListener) {
        this.context = context;
        this.aMapLocationListener = aMapLocationListener;
        initLocation();
        initOption();
    }

//    public static synchronized AMapCommonUtils getInstance() {
//
//        if (aMapCommonUtils == null) {
//
//            aMapCommonUtils = new AMapCommonUtils();
//        }
//        return aMapCommonUtils;
//    }

    // 根据控件的选择，重新设置定位参数
    private void initOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        String strInterval = "60000";
        if (!TextUtils.isEmpty(strInterval)) {
            // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
            locationOption.setInterval(Long.valueOf(strInterval));
        }

    }

    //开始定位
    public void startLocation() {
        if(locationClient == null || locationOption == null) {
            initLocation();
            initOption();
        }
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    private void initLocation() {
        locationClient = new AMapLocationClient(context);
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(aMapLocationListener);
    }

    //结束定位
    public void stopLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
