package com.ljw.device3x.gpscontrol;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


public class MyGpsHardware {
    private static final String TAG = "MyGpsHardware";
    private MyGpsListener listener = null;
    private LocationManager locationManager;

    /**
     * 开始监听GPS(不使用时一定要调用close()停止监听，否监听一直存在即使应用程序已经退出)
     */
    public boolean open(Context context,MyGpsListener listener){
        if((context==null)||(null!=this.listener)||(null==listener))
            return false;

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS是否正常启动
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 返回开启GPS导航设置界面
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivityForResult(intent, 0);
            //locationManager = null;
        	//return false; // 这里不要返回false，这种情况下应该做成当用户打开gps后就自动监听gps，而不要重新打开
//        	Toast.makeText(context, "请先开启gps", Toast.LENGTH_LONG).show();
        }

        this.listener = listener;
        // 监听
//        if(!locationManager.addGpsStatusListener(gpsListener)) {// 不要多次加入
//            Log.w(TAG,"gps打开失败，请检查后台定位控制设置是否打开");
//            Edog.toast("gps 打开失败");
//            return false;
//        }
        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新最短时间，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息，单位米 (参数2和3成与相关)
        // 参数4，监听器
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener, Looper.getMainLooper()); // SecurityException
        }catch(Exception e){
            Log.w(TAG,"open fail: SecurityException");
            return false;
        }
        return true;
    }

    /**
     * 停止监听，回收资源
     */
    public void close(){
        if(locationManager!=null){	// 不用时一定要移除监听器
//            locationManager.removeGpsStatusListener(gpsListener);
            locationManager.removeUpdates(locationListener);

            listener = null;
            Log.i(TAG, "Close");
        }
    }

    private LocationListener locationListener=new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            MyLocation myLocation = new MyLocation();
            myLocation.lat    = location.getLatitude();
            myLocation.lon    = location.getLongitude();
            myLocation.alt    = (float)location.getAltitude();
            myLocation.speed  = location.getSpeed()*3.6f;
            myLocation.course = (short)location.getBearing();
            myLocation.time   = location.getTime();

            if(null!=listener){
                listener.onLocationChanged(myLocation);
            }else
                Log.d(TAG,"listener is null");
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            switch (status) {
//                //GPS状态为可见时
//                case LocationProvider.AVAILABLE:
//                    Log.i("GpsHardware", "当前GPS状态为可见状态");
//                    break;
//                //GPS状态为服务区外时
//                case LocationProvider.OUT_OF_SERVICE:
//                    Log.i("GpsHardware", "当前GPS状态为服务区外状态");
//                    break;
//                //GPS状态为暂停服务时
//                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    Log.i("GpsHardware", "当前GPS状态为暂停服务状态");
//                    break;
//            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
//            Location location = locationManager.getLastKnownLocation(provider);
//            if(location==null) // 测试发现刚打开GPS的时候有可能为null
//                return;
//            Log.i("GpsHardware", provider+" lastPoint| 时间："+location.getTime()+"经度："+location.getLongitude()
//                    +"纬度："+location.getLatitude()+"速度："+location.getSpeed()
//                    +"方向："+location.getBearing()+"海拔："+location.getAltitude()
//                    +"精度："+location.getAccuracy());
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }
    };

//    private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
//        public void onGpsStatusChanged(int event) {
//            switch (event) {
//                case GpsStatus.GPS_EVENT_FIRST_FIX://第一次定位(如果使用gps时gps已经定位了就不会发送此条消息)
//                    Log.i("GpsHardware", "第一次定位");
//                    break;
//                case GpsStatus.GPS_EVENT_SATELLITE_STATUS://卫星状态改变
//                    //获取当前状态
//                    GpsStatus gpsStatus=locationManager.getGpsStatus(null);
//                    //获取卫星颗数的默认最大值
//                    int maxSatellites = gpsStatus.getMaxSatellites();
//                    //创建一个迭代器保存所有卫星
//                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
//                    int count = 0;
//                    while (iters.hasNext() && count <= maxSatellites) {
//                        iters.next();
//                        count++;
//                    }
//                    //Log.i("GPS", "搜索到："+count+"颗卫星");
//                    break;
//                case GpsStatus.GPS_EVENT_STARTED://定位启动
//                    break;
//                case GpsStatus.GPS_EVENT_STOPPED://定位结束
//                    Log.i(TAG, "定位结束");
//                    break;
//            }
//        }
//    };


}
