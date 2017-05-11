package com.ljw.device3x.customview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.sip.SipSession;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.example.showweather.model.db.entities.minimalist.IGetWeatherService;
import com.example.showweather.model.db.entities.minimalist.WeatherLive;
import com.ljw.device3x.Activity.DeviceApplication;
import com.ljw.device3x.R;
import com.ljw.device3x.Utils.AMapCommonUtils;
import com.ljw.device3x.Utils.Utils;
import com.ljw.device3x.Utils.WeatherUtils;
import com.ljw.device3x.common.AppPackageName;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class WindowsWeaDatePlugin extends LinearLayout implements AMapLocationListener {
    private ImageView weather;
    private TextView tmp;
    private TextView cityName;
    private TextView date;

    private Context context;

    AMapCommonUtils aMapCommonUtils;
    private WeatherUtils weatherUtils;

    private Intent alarmIntent = null;
    private PendingIntent alarmPi = null;
    private AlarmManager alarm = null;

    private static final int WEATHER_CHANGE = 1;
    private static final int TMP_CHANGE = 2;
    private static final int CITY_CHANGE = 3;
    private static final int DATE_CHANGE = 4;
    private static final String ACTION_TIMEZONE_CHANGED = Intent.ACTION_TIME_TICK;//监听时区变化的广播

    private IGetWeatherService aidlServerService;
    ServiceConnection connection=new ServiceConnection() {

        String content;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlServerService = IGetWeatherService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlServerService=null;
        }
    };
    public WindowsWeaDatePlugin(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        aMapCommonUtils = new AMapCommonUtils(context, this);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_date_plugin, this);

        weather = (ImageView) findViewById(R.id.plugin_weather);
        tmp = (TextView) findViewById(R.id.plugin_tmp);
        cityName = (TextView) findViewById(R.id.plugin_city);
        date = (TextView) findViewById(R.id.plugin_date);
        initLocationAlarm();
        getAndDisplayDate();
        weatherUtils = new WeatherUtils(weather, tmp);
        aMapCommonUtils.startLocation();
        Intent intent=new Intent();
        intent.setClassName("com.example.showweather","com.example.showweather.model.db.entities.minimalist.MyService");
        context.bindService(intent,connection,context.BIND_AUTO_CREATE);
        UpdateWeather();
      //  findViewById(R.id.weather_plugin).setOnClickListener(listener);
        findViewById(R.id.plugin_location).setOnClickListener(listener);
        findViewById(R.id.plugin_weather_container).setOnClickListener(listener);
    }

    private void UpdateWeather() {
        if (Utils.isNetworkConnected(context)){
            weatherUtils.updateWeatherInfo(DeviceApplication.city);
        }else {
            try {
                if ( aidlServerService != null ){
                    WeatherLive weatherlive = aidlServerService.getWeatherLive(weatherUtils.getCityCode(DeviceApplication.city));
                    if (weatherlive != null)
                        weatherUtils.updateWeatherInfo(weatherlive);
                    //   else  Toast.makeText(context,"weatherLive == null",Toast.LENGTH_SHORT).show();

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }catch (Exception e){
                //   Toast.makeText(context,"weatherLive "+e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.plugin_location :
                  //  Toast.makeText(context,"更新地址",Toast.LENGTH_SHORT).show();
                    aMapCommonUtils.startLocation();
                   /* Intent in = new Intent("android.settings.APPLICATION_DEVELOPMENT_SETTINGS");
                    context.startActivity(in);*/
                    break;
                case R.id.plugin_weather_container :
                 //   Toast.makeText(context,"更新天气",Toast.LENGTH_SHORT).show();
                    UpdateWeather();

                    if(Utils.getInstance().isInstalled(AppPackageName.WEATHER_APP)) {
                        Utils.getInstance().openApplication(AppPackageName.WEATHER_APP);
                    }
                    else
                        Toast.makeText(context,"请安装指定的应用！",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.weather_plugin :
                 //   Toast.makeText(context,"打开天气",Toast.LENGTH_SHORT).show();
                    if(Utils.getInstance().isInstalled(AppPackageName.WEATHER_APP)) {
                        Utils.getInstance().openApplication(AppPackageName.WEATHER_APP);
                    }
                    else
                        Toast.makeText(context,"请安装指定的应用！",Toast.LENGTH_SHORT).show();

                    break;


            }
        }
    };
    public WindowsWeaDatePlugin(Context context) {
        this(context, null);
    }

    private void initLocationAlarm() {
        // 创建Intent对象，action为LOCATION
        alarmIntent = new Intent();
        alarmIntent.setAction("LOCATION");
        // 定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        // 也就是发送了action 为"LOCATION"的intent
        alarmPi = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        // AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("LOCATION")){
                Log.i("ljwtestamap", "定时调用定位函数");
                aMapCommonUtils.startLocation();
            }if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){

                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeInfo = manager.getActiveNetworkInfo();

                if(null!=activeInfo && activeInfo.isConnected()){
                    aMapCommonUtils.startLocation();
                }


            } else if(action.equals("android.intent.action.DATE_CHANGED") || "android.intent.action.TIME_SET".equals(action)) {
                getAndDisplayDate();
            } else if(action.equals(ACTION_TIMEZONE_CHANGED)) {
                UpdateWeather();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == CITY_CHANGE) {
                AMapLocation loc = (AMapLocation) msg.obj;
                String result = cutLocationCityName(loc.getCity());
                DeviceApplication.city = result;
                Log.i("ljwtestamap", result);
                if(cityName != null && !TextUtils.isEmpty(result))
                    cityName.setText(result);
            }
        }
    };

    private void getAndDisplayDate() {
        int mon, day, week;
        Calendar calendar = Calendar.getInstance();
        mon = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.DAY_OF_WEEK);
        if(date != null)
            date.setText(mon + "月" + day + "日" + " " + getStringWeek(week));
    }

    /**
     * 根据数字返回字符串形式的星期数
     */
    private String getStringWeek(int weeks) {
        switch (weeks) {
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return null;
    }

    private String cutLocationCityName(String cityOriginName) { //去掉城市的“市”字
        if(!TextUtils.isEmpty(cityOriginName)) {
            int length = cityOriginName.length();
            String cutName = cityOriginName.substring(0, length - 1);
            return cutName;
        }
        return "";
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            Message msg = handler.obtainMessage();
            msg.obj = aMapLocation;
            msg.what = CITY_CHANGE;
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        filter.addAction("android.intent.action.DATE_CHANGED");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction(ACTION_TIMEZONE_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(alarmReceiver, filter);

        if(null != alarm){
            //设置一个闹钟，2秒之后每隔一段时间执行启动一次定位程序
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2*1000,
                    5 * 1000, alarmPi);
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //停止定位的时候取消闹钟
        if(null != alarm){
            alarm.cancel(alarmPi);
        }
        context.unregisterReceiver(alarmReceiver);
        aMapCommonUtils.stopLocation();
    }
}
