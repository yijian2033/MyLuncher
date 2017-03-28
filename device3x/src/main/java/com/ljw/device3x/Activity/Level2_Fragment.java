package com.ljw.device3x.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ljw.device3x.R;
import com.ljw.device3x.Utils.Utils;
import com.ljw.device3x.adapter.ButtonViewLevel2Adapter;
import com.ljw.device3x.common.AppPackageName;
import com.ljw.device3x.common.CommonBroacastName;

/**
 * Created by Administrator on 2016/5/28 0028.
 */
public class Level2_Fragment extends Fragment {

    private GridView level2GridView;

    /**
     * 二级菜单图标
     */
    private int[] level_2_image = {R.mipmap.btn_level2_navi, R.mipmap.btn_level2_record, R.mipmap.btn_level2_call, R.mipmap.btn_level2_music, R.mipmap.btn_level2_fm,
            R.mipmap.btn_level2_internet, R.mipmap.btn_level2_aboutdevice, R.mipmap.btn_level2_setting, R.mipmap.btn_level2_update, R.mipmap.btn_level2_weather, R.mipmap.btn_level2_filemanager, R.mipmap.btn_level2_warn};

    /**
     * 去掉的没用的图标
     * R.mipmap.btn_level2_notify,R.mipmap.text_level2_notify,
     R.mipmap.btn_level2_asistant,R.mipmap.text_level2_asistant
     */

    /**
     * 二级菜单文字图标
     */
//    private int[] level_2_text = {R.mipmap.text_level2_navi, R.mipmap.text_level2_record, R.mipmap.text_level2_call, R.mipmap.text_level2_music, R.mipmap.text_level2_fm,
//            R.mipmap.text_level2_internet, R.mipmap.text_level2_aboutdevice, R.mipmap.text_level2_setting, R.mipmap.text_level2_update, R.mipmap.text_level2_weather,
//            R.mipmap.text_level2_asistant};
    private int[] level_2_text = {R.string.level2_navi, R.string.level2_record, R.string.level2_phone, R.string.level2_music, R.string.level2_fm, R.string.level2_internet,
            R.string.level2_about, R.string.level2_setting, R.string.level2_update, R.string.level2_weather, R.string.level2_filemanager, R.string.level2_warn};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.level2_menu, null);
        initData(rootView);
        return rootView;
    }


    /**
     * 初始化控件数据
     */
    private void initData(View view) {
        level2GridView = (GridView) view.findViewById(R.id.level2gridview);
        level2GridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        level2GridView.setVerticalSpacing(10);
        initLevel2Menu();
    }

    /**
     * 初始化二级菜单GridView的数据
     */
    private void initLevel2Menu() {
        level2GridView.setAdapter(new ButtonViewLevel2Adapter(Level2_Fragment.this.getActivity(), level_2_image, level_2_text));
        level2GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case (0): {
                        String currentMap = Utils.getLocalMapType(getActivity());
                        if(currentMap.equals("baidu"))
                            openAppAndNotifyHomeChanged(AppPackageName.BAIDUMAP_APP);
                        else if(currentMap.equals("gaode"))
                            openAppAndNotifyHomeChanged(AppPackageName.GAODEMAP_APP);
                        else
                            openAppAndNotifyHomeChanged(AppPackageName.GAODEMAP_APP);
                    }
//                        toast("导航");
                        break;
                    case 1:
                        openAppAndNotifyHomeChanged(AppPackageName.AUTORECORD_APP);
//                        toast("行车记录");
                        break;
                    case 2:
//                        toast("蓝牙电话");
                        openAppAndNotifyHomeChanged(AppPackageName.BLUETOOTH_APP);
                        break;
                    case 3:
//                        toast("音乐");
                        openAppAndNotifyHomeChanged(AppPackageName.KUWO_APP);
                        break;
                    case 4:
//                        toast("FM");
                        openAppAndNotifyHomeChanged(AppPackageName.FM_APP);
                        break;
                    case 5: {
                        Intent intent=new Intent(CommonBroacastName.SYSTEM_WIFIWETTING);
                        intent.putExtra("jumptype", "wifi");
                        getActivity().startActivity(intent);
                    }
//                        toast("网络");
                        break;
                    case 6: {
                        Intent intent=new Intent(CommonBroacastName.JUMP_TO_SYSTEM_DEVICE_INFO);
                        getActivity().sendBroadcast(intent);
                    }
//                        toast("关于设备");
//                        openWifi();
                        break;
                    case 7:
//                        toast("设置");
//                        changeSystemScreenBright(true);
                        goToSetting();
                        break;
                    case 8:
                        openAppAndNotifyHomeChanged(AppPackageName.UPDATE_SYSTEM_APP);
//                        toast("系统升级");
                        break;
                    case 9:
//                        toast("天气");
                        openAppAndNotifyHomeChanged(AppPackageName.WEATHER_APP);
                        break;
                    case 10:
                        openAppAndNotifyHomeChanged(AppPackageName.FILE_MANAGER_APP);
                        break;
                    case 11:
                        openAppAndNotifyHomeChanged(AppPackageName.EDOG_APP);
                }
            }
        });
    }

    /**
     * 打开应用并且通知导航栏按钮变换图片
     */
    private void openAppAndNotifyHomeChanged(String appName) {
        if(Utils.getInstance().isInstalled(appName)) {
            Utils.getInstance().openApplication(appName);
//            Utils.getInstance().notifyHomeChangedIcon(2);
//            log_i("从一级菜单发出广播,附加值是" + 2);
        }
        else
            toast("请安装指定的应用！");
    }

    private void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void log_i(String s) {
        Log.i("ljwtest:", s);
    }

    /**
     * 跳转到设置界面
     */
    private void goToSetting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log_i("onDestroy");
    }
}
