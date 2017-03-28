package com.ljw.device3x.Utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
/*
 * tel.getNetworkOperator()
 3G中国是460固定的,
 中国移动的是 46000
 中国联通的是 46001
 中国电信的是 46003
 *获取国别
 tel.getSimCountryIso()
 */

public class My3GInfo {
    private WifiManager mWifi;
    private Context mContext;
    private Handler mHandler;
    private ImageView img;
    private String STRNetworkOperator[] = { "46000", "46001", "46003" };
    private int mark = -1;
    private int position;
    private int img3g[] = {};
    private boolean is3Ghave = false;
    private TelephonyManager tel;

    public My3GInfo(Context context, ImageView img) {
        mContext = context;
        this.img = img;
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
   /* 可 参考一下相关的方法，得到自己想要的参数来处理自己的ui
    public void initListValues(){
            tel.getDeviceId());//获取设备编号
            tel.getSimCountryIso());//获取SIM卡国别
            tel.getSimSerialNumber());//获取SIM卡序列号
            (simState[tm.getSimState()]);//获取SIM卡状态
            (tel.getDeviceSoftwareVersion()!=null?tm.getDeviceSoftwareVersion():"未知"));    //获取软件版本
            tel.getNetworkOperator());//获取网络运营商代号
            tel.getNetworkOperatorName());//获取网络运营商名称
            (phoneType[tm.getPhoneType()]);//获取手机制式
            tel.getCellLocation().toString());//获取设备当前位置
       }*/
        tel = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        firstView();
        getmark();
        //设置监听事件，监听信号强度的改变和状态的改变
        tel.listen(new PhoneStateMonitor(),
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                        | PhoneStateListener.LISTEN_SERVICE_STATE);
    }
    private void getmark()//得到当前电话卡的归属运营商
    {
        String strNetworkOperator = tel.getNetworkOperator();
        if (strNetworkOperator != null) {
            for (int i = 0; i < 3; i++) {
                if (strNetworkOperator.equals(STRNetworkOperator[i])) {
                    mark = i;
                    Log.v(TAG, "mark==" + i);
                    break;
                }
            }
        } else {
            mark = -1;
        }
    }
    private void firstView() {//第一次自动检测并设置初始状态

        int state = tel.getSimState();
        switch (state) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                is3Ghave = false;
                break;
            case TelephonyManager.SIM_STATE_READY:
                is3Ghave = true;
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                is3Ghave = false;
                break;
            default:
                break;
        }
    }

    private String TAG = "WHLOG";
    int signal;
    class PhoneStateMonitor extends PhoneStateListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {//3g信号强度的改变
            super.onSignalStrengthsChanged(signalStrength);
    /*
     * signalStrength.isGsm() 是否GSM信号 2G or 3G
     * signalStrength.getCdmaDbm(); 联通3G 信号强度
     * signalStrength.getCdmaEcio(); 联通3G 载干比
     * signalStrength.getEvdoDbm(); 电信3G 信号强度
     * signalStrength.getEvdoEcio(); 电信3G 载干比
     * signalStrength.getEvdoSnr(); 电信3G 信噪比
     * signalStrength.getGsmSignalStrength(); 2G 信号强度
     * signalStrength.getGsmBitErrorRate(); 2G 误码率
     * 载干比 ，它是指空中模拟电波中的信号与噪声的比值
     */
            Log.v(TAG, "change signal");
            if(mark<0)
            {
                getmark();
            }
            if (mark == 0) {
                signal = signalStrength.getGsmSignalStrength();
            } else if (mark == 1) {
                signal = signalStrength.getCdmaDbm();
            } else if (mark == 2) {
                signal = signalStrength.getEvdoDbm();
            }
            if(is3Ghave==true){
                getLevel();
                show3GDetail(position);
            }
        }

        private void getLevel() {
            // TODO Auto-generated method stub
            if (mark == 2) {//电信3g信号强度的分类，可以按照ui自行划分等级
                if (signal >= -65)
                    position = 5;
                else if (signal >= -75)
                    position = 4;
                else if (signal >= -85)
                    position = 3;
                else if (signal >= -95)
                    position = 2;
                else if (signal >= -105)
                    position = 1;
                else
                    position = 0;
            }
            if (mark == 1) {//联通3g信号划分
                if (signal >= -75)
                    position = 5;
                else if (signal >= -80)
                    position = 4;
                else if (signal >= -85)
                    position = 3;
                else if (signal >= -95)
                    position = 2;
                else if (signal >= -100)
                    position = 1;
                else
                    position = 0;
            }
            if (mark == 0) {//移动信号的划分，这个不是很确定是2g还是3g
                if (signal <= 2 || signal == 99)
                    position = 0;
                else if (signal >= 12)
                    position = 5;
                else if (signal >= 10)
                    position = 4;
                else if (signal >= 8)
                    position = 3;
                else if (signal >= 5)
                    position = 2;
                else
                    position = 1;
            }
        }

        public void onServiceStateChanged(ServiceState serviceState) {//3g状态的改变
            super.onServiceStateChanged(serviceState);
    /*
     * ServiceState.STATE_EMERGENCY_ONLY 仅限紧急呼叫
     * ServiceState.STATE_IN_SERVICE 信号正常
     * ServiceState.STATE_OUT_OF_SERVICE 不在服务区
     * ServiceState.STATE_POWER_OFF 断电
     */
            int pos = serviceState.getState();
            Log.v(TAG, "state change===" + pos);

            switch (pos) {
                case ServiceState.STATE_EMERGENCY_ONLY:
                    is3Ghave=true;
                    show3GDetail(0);
                    break;
                case ServiceState.STATE_IN_SERVICE:
                    is3Ghave=true;
                    show3GDetail(3);
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    is3Ghave=false;
                    show3GDetail(6);
                    break;
                case ServiceState.STATE_POWER_OFF:
                    is3Ghave=false;
                    show3GDetail(6);
                    break;
                default:
                    break;
            }
        }
    }

    private void show3GDetail(int level) {//设置显示3g的图标
        // TODO Auto-generated method stub
        if(is3Ghave==false)
        {
            img.setBackgroundResource(img3g[6]);
        }else{
            img.setBackgroundResource(img3g[level]);
        }
    }
}
