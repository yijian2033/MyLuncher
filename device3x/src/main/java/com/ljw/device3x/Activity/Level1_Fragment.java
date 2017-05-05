package com.ljw.device3x.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljw.device3x.R;
import com.ljw.device3x.Utils.EdogBrocastIntent;
import com.ljw.device3x.Utils.FiveClickListener;
import com.ljw.device3x.Utils.Utils;
import com.ljw.device3x.adapter.ButtonViewAdapter;
import com.ljw.device3x.adapter.pagerAdapter;
import com.ljw.device3x.common.AppPackageName;
import com.ljw.device3x.common.CommonCtrl;
import com.ljw.device3x.customview.CubeOutTransformer;
import com.ljw.device3x.customview.MyViewPager;
import com.ljw.device3x.gpscontrol.MyGpsHardware;
import com.ljw.device3x.gpscontrol.MyGpsListener;
import com.ljw.device3x.gpscontrol.MyLocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/27 0027.
 */
public class Level1_Fragment extends Fragment implements MyGpsListener{
    private static final int SECOND_WIDTH = 1430;
    private static final int FIRST_WIDTH = 823;
    private Context context;
    private List<GridView> gridViewList;//装载滑动的GridView数据
    private LinearLayout pagerLayout, carLayout, limitlayout, btnLayout;//ViewPager
    private MyViewPager mViewPager;
    private pagerAdapter pa;//滑动的ViewPager
    private ImageView imageLeft, imageRight, naviPoint;//公路两侧
    private TextView directionText;//方向
    private TextView speed;//GPS速度
    private TextView limitSpeed;//限速数字
    private FrameLayout weatherFrame;
    private MyGpsHardware myGpsHardware;
    private ImageView openSecondPage,openFirstPage;



    public boolean isLevel1Visible = false;//一级菜单第二页是否显示


    private int pageNum = 0;//记录页数
    private static final int totalPage = 3;//一级菜单一共三页
    private static int FOOT_LEFT = 0;//公路左侧图片切换计数器
    private static int FOOT_RIGHT = 0;//公路右侧图片切换计数器
    private static volatile boolean isRunning = false;//线程停止标志

    private SensorManager sm;//系统重力加速度传感器

    private static DisplayMetrics dm = new DisplayMetrics();//获取屏幕的分辨率

    private MsgRecieve mRecieve;//广播监听

    private String test1 = "";


    /**
     * 主界面的公路左侧
     */
    private int[] image_left = {R.mipmap.leftroad_2, R.mipmap.leftroad_1,
            R.mipmap.leftroad_3};

    /**
     * 主界面的公路右侧
     */
    private int[] image_right = {R.mipmap.rightroad_2, R.mipmap.rightroad_1,
            R.mipmap.rightroad_3};

    /**
     * 一级菜单第一页图标
     */
    private int[] level_1_first_image = {R.mipmap.btn_navi, R.mipmap.btn_record, R.mipmap.btn_warn};

    /**
     * 一级菜单第一页文字图标
     */
//    private int[] level_1_first_text = {R.mipmap.text_navi, R.mipmap.text_record, R.mipmap.text_warn};
    private int[] level_1_first_text = {R.string.level_1_navi, R.string.level_1_record, R.string.level_1_warn};

    /**
     * 一级菜单第二页图标
     */
    private int[] level_1_second_image = {R.mipmap.btn_fm, R.mipmap.btn_music, R.mipmap.btn_call};

    /**
     * 一级菜单第二页文字图标
     */
//    private int[] level_1_second_text = {R.mipmap.text_wechat, R.mipmap.text_music, R.mipmap.text_call, R.mipmap.text_weather, R.mipmap.text_setting};
    private int[] level_1_second_text = {R.string.level_2_fm, R.string.level_2_music, R.string.level_2_phone};

    /**
     * 一级菜单第三页图标
     */
    private int[] level_1_third_image = { R.mipmap.btn_filemanager, R.mipmap.btn_weather, R.mipmap.btn_setting};

    /**
     * 一级菜单第三页文字图标
     */
//    private int[] level_1_second_text = {R.mipmap.text_wechat, R.mipmap.text_music, R.mipmap.text_call, R.mipmap.text_weather, R.mipmap.text_setting};
    private int[] level_1_third_text = {R.string.level2_filemanager, R.string.level_2_weather, R.string.level_2_setting};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        log_i("onCreateView" + "线程状态" + changeRoadImage.isAlive());
        context = getActivity().getApplicationContext();
        View rootView = inflater.inflate(R.layout.level1_menu, null);
        initData(rootView);
        regisReceive();
        myGpsHardware = new MyGpsHardware();
        myGpsHardware.open(context, this);
        return rootView;
    }


    private void regisReceive() {
        mRecieve = new MsgRecieve();
        IntentFilter edogFilter = new IntentFilter();
        edogFilter.addAction(EdogBrocastIntent.ALARM_POINT);
        edogFilter.addAction(EdogBrocastIntent.ALARM_RADAR);
        edogFilter.addAction(EdogBrocastIntent.COLECTION_WARNING);
        getActivity().registerReceiver(mRecieve, edogFilter);

    }

    @Override
    public void onLocationChanged(MyLocation location) {
//        toast("Gps回调");
        long currentTime = location.time;
        if(!CommonCtrl.isFirstSystemTimeSet) {
            if(!DeviceApplication.isNetworkAvailable && currentTime / 1000 < Integer.MAX_VALUE) {
//                SystemClock.setCurrentTimeMillis(currentTime);
                sendGpsMillsToSettings(currentTime);
                Log.d("ljwtestgps", "GPS时间设置到系统成功");
                CommonCtrl.isFirstSystemTimeSet = true;//设置成功后以后就不用再设置了
            }
        }
        displayDirectionAndSpeed(location.course, location.speed);
    }

    private void sendGpsMillsToSettings(long mills) {
        Intent intent = new Intent("com.launcher.setsystemmills");
        intent.putExtra("currentmills", mills);
        context.sendBroadcast(intent);
    }

    /**
     * 接收广播
     */
    public class MsgRecieve extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            log_i("Action:" + action);
            if (EdogBrocastIntent.ALARM_POINT.equals(action)) {
                log_i("警示点信息:" + intent.getStringExtra("img") + ","
                        + intent.getIntExtra("limit", 1)
                        + "," + intent.getStringExtra("text"));
                controVISIBLElLimitLayout(intent);
            } else if (EdogBrocastIntent.ALARM_RADAR.equals(action)) {
                log_i("雷达信息:" + intent.getStringExtra("band") + ","
                        + intent.getStringExtra("level"));
            }
        }
    }

    /**
     * 控制限速布局的可见或不可见
     */
    private void controVISIBLElLimitLayout(Intent intent) {
        if(intent.hasExtra("img")) {
            if("".equals(intent.getStringExtra("img")) && limitlayout.getVisibility() == View.VISIBLE)
                limitlayout.setVisibility(View.INVISIBLE);
            else {
                if(limitlayout.getVisibility() == View.INVISIBLE)
                    limitlayout.setVisibility(View.VISIBLE);
            }
        }
        changeLimitSpeed(intent.getIntExtra("limit", 1));
    }

    /**
     * 更改限速图片显示
     */
    private void changeLimitSpeed(int limit) {
        if(limit == 1)
            return;
        else if(limit == 0 && limitlayout.getVisibility() == View.VISIBLE)
            limitlayout.setVisibility(View.INVISIBLE);
        else {
            if(limitlayout.getVisibility() == View.VISIBLE)
                limitSpeed.setText("" + limit);
        }
    }

    /**
     * 更改雷达强度显示
     */
    private void changeRadarStrength(String band, String level) {
        if("K".equals(band)) {

        } else if("Ka".equals(band)) {

        } else if("Ku".equals(band)) {

        } else if("X".equals(band)) {

        } else if("Sk".equals(band)) {

        } else {

        }

        if("low".equals(level)) {

        } else if("mid".equals(level)) {

        } else {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isRunning)
            isRunning = false;
//        log_i("onStart" + "线程状态" + changeRoadImage.isAlive());
    }

    @Override
    public void onResume() {
        super.onResume();
//        log_i("onResume" + "线程状态" + changeRoadImage.isAlive());
        if(isRunning)
            isRunning = false;
//        sm.registerListener(mySensorEventListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        sm.registerListener(mySensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        calculateOrientation();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStop() {
        super.onStop();
//        log_i("onStop" + "线程状态" + changeRoadImage.isAlive());
    }

    @Override
    public void onPause() {
        super.onPause();
//        log_i("onPause" + "线程状态" + changeRoadImage.isAlive());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mRecieve);
//        log_i("onDestroyView" + "线程状态" + changeRoadImage.isAlive());
    }

    /**
     * 初始化控件
     */
    private void initData(View view) {
        carLayout = (LinearLayout) view.findViewById(R.id.carandroad);
        imageLeft = (ImageView) view.findViewById(R.id.roadleft);
        imageRight = (ImageView) view.findViewById(R.id.roadright);
        pagerLayout = (LinearLayout) view.findViewById(R.id.viewpagerparent);
        directionText = (TextView) view.findViewById(R.id.directiontext);
        limitSpeed = (TextView) view.findViewById(R.id.limitdigital);
        limitlayout = (LinearLayout) view.findViewById(R.id.includelimit);
        limitlayout.setVisibility(View.INVISIBLE);
        btnLayout = (LinearLayout) view.findViewById(R.id.viewpagerparent);
        speed = (TextView) view.findViewById(R.id.speed);
        naviPoint = (ImageView) view.findViewById(R.id.navi_point);
        openFirstPage = (ImageView) view.findViewById(R.id.manaul_toone);
        openSecondPage = (ImageView) view.findViewById(R.id.manaul_totwo);
        openFirstPage.setVisibility(View.GONE);
        openSecondPage.setVisibility(View.GONE);
        openFirstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                openFirstPage.setVisibility(View.GONE);
                openSecondPage.setVisibility(View.GONE);
            }
        });

        openSecondPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
                openFirstPage.setVisibility(View.GONE);
                openSecondPage.setVisibility(View.GONE);
            }
        });
       /* carLayout.setOnClickListener(new FiveClickListener(1000) {
            @Override
            public void singleClick(View view) {
                Toast.makeText(context,"打开测试软件",Toast.LENGTH_LONG).show();
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.rayee.factorytest","com.rayee.factorytest.MainActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context,"打开测试软件"+e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });*/
        initGridView();
        try {
            initViewPager();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
//        initSensorManager();
        startChangeRoadImage();
    }

    /**
     * 开始公路图片切换
     */
    private void startChangeRoadImage() {
        isRunning = false;
      changeRoadImage.start();
    }

    /**
     * 公路图片轮播线程
     */
   Thread changeRoadImage = new Thread(new Runnable() {

        @Override
        public void run() {
            while (!isRunning) {
               try {
                    Thread.sleep(250);
                   mhandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (FOOT_LEFT == image_left.length - 1
                                    && FOOT_RIGHT == image_left.length - 1)
                                FOOT_LEFT = FOOT_RIGHT = 0;
                            imageLeft.setImageResource(image_left[FOOT_LEFT++]);
                            imageRight.setImageResource(image_right[FOOT_RIGHT++]);
                        }
                   });
                } catch (InterruptedException e) {
                    e.printStackTrace();
               }
            }
        }
    });

    private android.os.Handler mhandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 初始化每页的gridview数据
     */
    private void initGridView() {
        gridViewList = new ArrayList<GridView>();
        for (int i = 0; i < totalPage; ++i) {
            GridView gv = new GridView(Level1_Fragment.this.getActivity());
            if(i ==2){ //第三页
                gv.setAdapter(new ButtonViewAdapter(Level1_Fragment.this.getActivity(), level_1_third_image, level_1_third_text, i));
                gv.setNumColumns(3);
            }else if (i== 1) { //第二页
                gv.setAdapter(new ButtonViewAdapter(Level1_Fragment.this.getActivity(), level_1_second_image, level_1_second_text, i));
                gv.setNumColumns(3);
            } else if (i== 0){ //第一页
                gv.setAdapter(new ButtonViewAdapter(Level1_Fragment.this.getActivity(), level_1_first_image, level_1_first_text, i));
                gv.setNumColumns(3);
            }
            gv.setGravity(Gravity.CENTER);
            gv.setClickable(true);
            gv.setFocusable(true);
            gv.setBackgroundColor(Color.parseColor("#000000"));
            gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (pageNum) {
                        case 0:
                            level1MenuListener(position);
                            break;
                        case 1:
                            level2MenuListener(position);
                            break;
                        case 2:
                            level3MenuListener(position);
                            break;
                    }
                }
            });
            gridViewList.add(gv);
        }
    }

    /**
     * 监听一级菜单第一页
     */
    private void level1MenuListener(int position) {
        switch (position) {
            case 0: {
                String currentMap = Utils.getLocalMapType(getActivity());
                if(currentMap.equals("baidu"))
                    openAppAndNotifyHomeChanged(AppPackageName.BAIDUMAP_APP);
                else if(currentMap.equals("gaode"))
                    openAppAndNotifyHomeChanged(AppPackageName.GAODEMAP_APP);
                else
                    openAppAndNotifyHomeChanged(AppPackageName.GAODEMAP_APP);
            }
//                toast("导航");
                break;
            case 1:
                openAppAndNotifyHomeChanged(AppPackageName.AUTORECORD_APP);
//                toast("行车记录");
                break;
            case 2:
//                toast("征仔预警");
                openAppAndNotifyHomeChanged(AppPackageName.EDOG_APP);
                break;
        }
    }

    /**
     * 监听一级菜单第二页
     */
    private void level2MenuListener(int position) {
        switch (position) {
            case 0:
//                toast("微信");
                openAppAndNotifyHomeChanged(AppPackageName.FM_APP);
                break;
            case 1:
//                toast("音乐");
                openAppAndNotifyHomeChanged(AppPackageName.KUWO_APP);
                break;
            case 2:
//                toast("蓝牙电话");
                openAppAndNotifyHomeChanged(AppPackageName.BLUETOOTH_APP);
                break;

        }
    }

    /**
     * 监听一级菜单第三页
     */
    private void level3MenuListener(int position) {
        switch (position) {
            case 0:
                openAppAndNotifyHomeChanged(AppPackageName.FILE_MANAGER_APP);
//                toast("导航");
            break;
            case 1:
                openAppAndNotifyHomeChanged(AppPackageName.WEATHER_APP);
//                toast("天气");
                break;
            case 2:
//                toast("设置");
                goToSetting();
                break;
        }
    }
    /**
     * 打开应用并且通知导航栏按钮变换图片
     */
    private void openAppAndNotifyHomeChanged(String appName) {
        if(Utils.getInstance().isInstalled(appName)) {
            Utils.getInstance().openApplication(appName);
//            Utils.getInstance().notifyHomeChangedIcon(2);
//            log_i("从二级菜单发出广播,附加值是" + 2);
        }
        else
            toast("请安装指定的应用！");
    }

    /**
     * 跳转到天气界面
     */
//    private void goToWeatherView() {
//        Intent intent = new Intent(getActivity(), WeatherAcivity.class);
//        getActivity().startActivity(intent);
//    }

    /**
     * 跳转到设置界面
     */
    private void goToSetting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        getActivity().startActivity(intent);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() throws IllegalAccessException, java.lang.InstantiationException {

        mViewPager = new MyViewPager(Level1_Fragment.this.getActivity());
//        mViewPager.setPageTransformer(false, new TransformerItem(CubeOutTransformer.class).clazz.newInstance());
        mViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mViewPager.setOnPageChangeListener(new MyOnPageChanger());
        pa = new pagerAdapter(this, gridViewList);
        mViewPager.setAdapter(pa);
        pagerLayout.addView(mViewPager);
        setBtnLayoutWidth(false);
    }

    /**
     * 初始化天气界面
     */
    private void initWeatherView() {
    }

    /**
     * ViewPager页面选项卡进行切换时候的监听器处理
     *
     * @author lijianwen
     */
    class MyOnPageChanger implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            pageNum = arg0;
            if (arg0 == 0) {
              //  carLayout.setVisibility(View.VISIBLE);
                openFirstPage.setVisibility(View.GONE);
                openSecondPage.setVisibility(View.GONE);
                isLevel1Visible = false;
                setBtnLayoutWidth(false);
                naviPoint.setImageResource(R.mipmap.circle_one);
                Utils.getInstance().notifyHomeChangedIcon(0);
            }else if(arg0 == 1) {
              //  carLayout.setVisibility(View.GONE);
                openFirstPage.setVisibility(View.GONE);
                openSecondPage.setVisibility(View.GONE);
                isLevel1Visible = true;
                setBtnLayoutWidth(false);
                naviPoint.setImageResource(R.mipmap.circle_two);
                Utils.getInstance().notifyHomeChangedIcon(1);

            }else if(arg0 == 2){
                openFirstPage.setVisibility(View.GONE);
                openSecondPage.setVisibility(View.GONE);
                isLevel1Visible = true;
                setBtnLayoutWidth(false);
                naviPoint.setImageResource(R.mipmap.circle_third);
                Utils.getInstance().notifyHomeChangedIcon(1);
            }

        }

    }

    /**
     * 翻页后改变按钮布局的宽度
     */
    private void setBtnLayoutWidth(boolean flag) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(flag ? SECOND_WIDTH : FIRST_WIDTH, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        btnLayout.setLayoutParams(lp);
    }

    /**
     * 如果点击导航栏时一级菜单第二页在显示的时候跳转到第一页
     */
    public void jumpIfLevel1SecondPageVisible() {
        if(isLevel1Visible)
            mViewPager.setCurrentItem(0);
    }

    /**
     * 初始化传感器
     */
//    private void initSensorManager() {
//        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
//        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//    }

    /**
     * 注册传感器监听事件
     */
//    private final class MySensorEventListener implements SensorEventListener {
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//                magneticFieldValues = event.values;
//            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//                accelerometerValues = event.values;
//
//            calculateOrientation();
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    }
    /**
     * 提供外部接口给外部类调用
     */
    public void displayDirectionAndSpeed(short bearing, float speedValue) {
        calculateOrientation(bearing);
        displayGpsSpeed(speedValue);
    }

    /**
     * 根据GPS信息判断方向
     */
    private void calculateOrientation(short bearing) {

//        float[] values = new float[3];
//
//        float[] R = new float[9];

//        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
//
//        SensorManager.getOrientation(R, values);

        // 要经过一次数据格式的转换，转换为度

//        values[0] = (float) Math.toDegrees(values[0]);

        if ((bearing > 355.0 && bearing <= 359.0) || (bearing > 0 && bearing <= 5)) {
//            Log.i("正西", "ss");
//            derecLayout.setBackgroundResource(com.rayee.administrator.buttonview.R.mipmap.direction_west);
//            derecLayout.setGravity(Gravity.CENTER);
//            directionText.setText(context.getResources().getString(R.string.north));
            displayDirection(context.getResources().getString(R.string.north));
        }

        else if((bearing > 5 && bearing < 85)) {
//            directionText.setText(context.getResources().getString(R.string.northeast));
            displayDirection(context.getResources().getString(R.string.northeast));
        }

        else if (bearing > 85 && bearing <= 95) {
//            log_i("正东");
//            derecLayout.setBackgroundResource(com.rayee.administrator.buttonview.R.mipmap.direction_south);
//            derecLayout.setGravity(Gravity.CENTER);
//            directionText.setText(context.getResources().getString(R.string.east));
            displayDirection(context.getResources().getString(R.string.east));
        }

        else if(bearing > 95 && bearing <= 175 ) {
//            directionText.setText(context.getResources().getString(R.string.southeast));
            displayDirection(context.getResources().getString(R.string.southeast));
        }

        else if (bearing > 175 && bearing <= 185) {
//            log_i("正南");
//            derecLayout.setBackgroundResource(com.rayee.administrator.buttonview.R.mipmap.direction_east);
//            derecLayout.setGravity(Gravity.CENTER);
//            directionText.setText(context.getResources().getString(R.string.south));
            displayDirection(context.getResources().getString(R.string.south));
        }

        else if(bearing > 185 && bearing <= 265) {
//            directionText.setText(context.getResources().getString(R.string.southwest));
            displayDirection(context.getResources().getString(R.string.southwest));
//            log_i("西南");
        }

        else if (bearing > 265 && bearing <= 275) {
//            log_i("正西");
//            derecLayout.setBackgroundResource(com.rayee.administrator.buttonview.R.mipmap.direction_north);
//            derecLayout.setGravity(Gravity.CENTER);
//            directionText.setText(context.getResources().getString(R.string.west));
            displayDirection(context.getResources().getString(R.string.west));
        }

        else if(bearing > 275 && bearing <= 355) {
//            directionText.setText(context.getResources().getString(R.string.northwest));
            displayDirection(context.getResources().getString(R.string.northwest));
//            log_i("西北");
        }

    }

    /**
     * 根据GPS信息显示速度
     */
    private void displayDirection(String s) {
        try {
            if(directionText != null)
                directionText.setText(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据GPS信息显示速度
     */
    private void displayGpsSpeed(float speedValue) {
        Log.e("ljwtest:", "全薪版本刷新的速度值" + speedValue);
        try {
            if(speed != null)
                speed.setText(String.format("%.2f", speedValue));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ljwtest", "GPS刷新异常" + e.toString());
        }
    }

    /**
     * 取得外部类
     */
    private static final class TransformerItem {

        final String title;
        final Class<? extends ViewPager.PageTransformer> clazz;

        public TransformerItem(Class<? extends ViewPager.PageTransformer> clazz) {
            this.clazz = clazz;
            title = clazz.getSimpleName();
        }

        @Override
        public String toString() {
            return title;
        }

    }

    private void toast(String content) {
        Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }

    private void log_i(String s) {
        Log.i("ljwtest:", s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log_i("onDestroy");
        isRunning = true;
        myGpsHardware.close();
//        changeRoadImage = null;
    }
}
