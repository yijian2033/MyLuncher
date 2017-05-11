package com.ljw.device3x.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.ljw.device3x.Activity.DeviceApplication;
import com.ljw.device3x.common.CommonBroacastName;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/27 0027.
 */
public class Utils {

    //若本地没有定位过，则默认返回清华大学的经纬度
    public static final String QINGHUA_LON = "116.332979";
    public static final String QINGHUA_LAT = "40.009612";
    public static final String DEFAULT_CITY_CODE = "101010100";
    private static Utils mUtil;
    private final String ACTION_LAUNCHER_NOTIFY = "action_launcher_notify";//changed home icon from launcher
    private final String ACTION_LAUNCHER_BACKGROUND = "action.ljw.launcher.background";

    public static final int NOT_BOOT = 1;
    public static final int BOOT = 2;
    private static final String ISFIRST_BOOT = "isfirstboot";

    private Context mContext;

    public Utils() {
        this.mContext = DeviceApplication.getContext();
    }

    public static synchronized Utils getInstance() {

        if (mUtil == null) {

            mUtil = new Utils();
        }
        return mUtil;
    }

    /**
     * 设置定位数据
     *
     * @parm 定位得到的经纬度
     */
    public static void setLocationCoordinate(Context context, String lon, String lat) {
        SharedPreferences sp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("longitude", lon);
        editor.putString("latitude", lat);
        editor.commit();
    }

    /**
     * 取得定位数据
     */
    public static String getLongitude(Context context) {
        SharedPreferences sp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
        return sp.getString("longitude", "");
    }

    public static String getLatitude(Context context) {
        SharedPreferences sp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
        return sp.getString("latitude", "");
    }

    public static void setBoot(final Context context, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISFIRST_BOOT, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(ISFIRST_BOOT, value);
        ed.commit();
    }

    public static int getBoot(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISFIRST_BOOT, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ISFIRST_BOOT, BOOT);
    }

    /**
     * 设置定位到的城市
     */
    public static void setCity(Context context, String city) {
        SharedPreferences sp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("city", city);
        editor.commit();
    }

    /**
     * 取得定位到的城市
     */
    public static String getCity(Context context) {
        SharedPreferences sp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
        String cityName = sp.getString("city", "");
        return cityName == null ? "shenzhen" : cityName;
    }

    /**
     * 设置默认打开的地图
     */
    public static void setLocalMapType(String whichMap, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mapsetting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maptype", whichMap);
        editor.commit();
    }

    /**
     * 获取默认地图
     */
    public static String getLocalMapType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mapsetting", Context.MODE_PRIVATE);
        String currentMap = sharedPreferences.getString("maptype", "default");
        return currentMap;
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 取出返回的目的地和出发地信息，返回数组
     */
    public static String[] getNaviArray(String info) {
        String[] data = new String[]{};
        if (info != null && !"".equals(info)) {
            data = info.split(",");
            return data;
        }
        return data;
    }

    /**
     * 获得下一天的日期
     */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 格式化时间：x年x月形式
     */
    public static String formatDate(String date) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sf = new SimpleDateFormat("M" + "月" + "d" + "日");
        Date formatdate = null;
        try {
            formatdate = sf1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sf.format(formatdate);
    }

    /**
     * 取得星期数
     */
    public static String getWeekDays(String date) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date formatdate = null;
        try {
            formatdate = sf1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatdate);
        return getStringWeek(calendar.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * 根据数字返回字符串形式的星期数
     */
    private static String getStringWeek(int weeks) {
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

    /**
     * 把十六进制Unicode编码字符串转换为中文字符串
     */
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    /**
     * 去除bom报头
     */
    public static String formatJsonString(String s) {
        if (s != null) {
            s = s.replaceAll("\ufeff", "");
        }
        return s;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 通过包名检测APP是否安装
     *
     * @param packageName 包名
     * @return true or false
     */
    public boolean isInstalled(String packageName) {
        boolean isInstalled = false;

        if (!TextUtils.isEmpty(packageName)) {
            PackageInfo packageInfo;
            try {
                packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                packageInfo = null;
                e.printStackTrace();
            }

            if (packageInfo == null) {
                isInstalled = false;
            } else {
                isInstalled = true;
            }
        }
        return isInstalled;
    }

    /**
     * 通过包名打开应用 返回true:成功 返回false:失败
     */
    public boolean openApplication(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }

        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (intent == null) {
            return false;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        UITimer.getInstance().executeAppTask(new UITimerTask(), UITimer.DELAY_MIDDLE);
        return true;
    }

    //获取移动数据开关状态
    public boolean getMobileDataStatus() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        String methodName = "getMobileDataEnabled";
        Class cmClass = mConnectivityManager.getClass();
        Boolean isOpen = null;

        try {
//            Method method = cmClass.getMethod(methodName, null);
            Method method = cmClass.getMethod(methodName);

//            isOpen = (Boolean) method.invoke(mConnectivityManager, null);
            isOpen = (Boolean) method.invoke(mConnectivityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    /**
     * 换页时通知导航栏home键切换图片
     */
    public void notifyHomeChangedIcon(int position) {
        Intent intent = new Intent(ACTION_LAUNCHER_NOTIFY);
        intent.putExtra("fromLuancher", position);
        Log.i("ljwtest:", "发出广播,附加值是" + position);
        mContext.sendBroadcast(intent);
    }

    /**
     * 通知launcher已经进入后台
     */
    public void notifyLauncherIsBackground() {
        Intent intent = new Intent(ACTION_LAUNCHER_BACKGROUND);
        mContext.sendBroadcast(intent);
    }

    /***
     * 语音播报
     *
     * @param text
     */
    public void ttsSpeak(String text) {
        Intent tts = new Intent(CommonBroacastName.AIOS_TTS_SPEAK);
        tts.putExtra("text", text);
        mContext.sendBroadcast(tts);
    }

    /**
     * 判断网络连接是否可用
     */
    public static Boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            if (networkinfo != null && networkinfo.isConnected()  && networkinfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
