package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ljw.device3x.R;
import com.ljw.device3x.Utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class StatusBarGPRSStateView extends ImageView {
    private TelephonyManager mManger;
    //    private MyPhoneStateListener myPhoneStateListener;
    private GPRSHandler mhandler;
    private boolean isSimCardExist;
    private int mark = -1;
    private String STRNetworkOperator[] = {"46000", "46001", "46003"};
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private Context context;

    public StatusBarGPRSStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusBarGPRSStateView(Context context) {
        super(context);
    }

    public StatusBarGPRSStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mManger = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//        myPhoneStateListener = new MyPhoneStateListener();
//        mManger.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);
        mhandler = new GPRSHandler(this);
        initGPRS();
    }

    /**
     * 初始化sim卡状态
     */
    private void initGPRS() {
        mark = -1;
        checkSimCardExist();
        Log.i("ljwtest:", "isSimCardExist" + isSimCardExist);
        if (isSimCardExist)
            getMark();
    }

    private void checkSimCardExist() {
        int state = mManger.getSimState();
        switch (state) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                isSimCardExist = false;
                break;
            case TelephonyManager.SIM_STATE_READY:
                isSimCardExist = true;
            //    new Utils().ttsSpeak("SIM卡已插入");
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                isSimCardExist = false;
            //    new Utils().ttsSpeak("SIM卡已拔出");
                break;
            default:
                break;
        }
        if (!isSimCardExist) {
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mhandler.sendEmptyMessage(-200);
                    context.sendBroadcast(new Intent("com.launcher.hidenettype"));
                    context.sendBroadcast(new Intent("com.launcher.changeiofromstrength"));
                }
            },200);

         }

    }

    private void getMark()//得到当前电话卡的归属运营商
    {
        String strNetworkOperator = mManger.getNetworkOperator();
        Log.i("ljwtest:", "运营商是" + strNetworkOperator);
        if (strNetworkOperator != null) {
            for (int i = 0; i < 3; i++) {
                if (strNetworkOperator.equals(STRNetworkOperator[i])) {
                    mark = i;
                    break;
                }
            }
        } else {
            mark = -1;
        }
        Log.i("ljwtest:", "mark==" + mark);
    }

//    private class MyPhoneStateListener extends PhoneStateListener {
//        @Override
//        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//            super.onSignalStrengthsChanged(signalStrength);
//            if (mark == 0) {
//                mhandler.sendEmptyMessage(signalStrength.getGsmSignalStrength());
//                signal = signalStrength.getGsmSignalStrength();
//            } else if (mark == 1) {
//                mhandler.sendEmptyMessage(signalStrength.getCdmaDbm());
//                signal = signalStrength.getCdmaDbm();
//            } else if (mark == 2) {
//                mhandler.sendEmptyMessage(signalStrength.getEvdoDbm());
//                signal = signalStrength.getEvdoDbm();
//            } else
//                mhandler.sendEmptyMessage(getDbm(signalStrength));
//            Log.i("ljwtest:", "现在信号强度是" + getDbm(signalStrength));
//            Log.i("ljwtest:", "mark:" + mark);
//        }
//    }

    private class GPRSHandler extends android.os.Handler {
        WeakReference<StatusBarGPRSStateView> mView;

        public GPRSHandler(StatusBarGPRSStateView view) {
            mView = new WeakReference<StatusBarGPRSStateView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mView.get() == null)
                return;
            StatusBarGPRSStateView view = mView.get();
            if (isSimCardExist) {
                if (msg.what >= -96)
                    view.setImageResource(R.mipmap.gprs_4);
                else if (msg.what >= -106)
                    view.setImageResource(R.mipmap.gprs_3);
                else if (msg.what >= -116)
                    view.setImageResource(R.mipmap.gprs_2);
                else if (msg.what >= -999)
                    view.setImageResource(R.mipmap.gprs_1);
            } else
                view.setImageResource(R.mipmap.gprs_none);

//            if (mark == 2) {//电信3g信号强度的分类，可以按照ui自行划分等级
//                if (msg.what >= -75)
//                    view.setImageResource(R.mipmap.gprs_4);
//                    position = 4;
//                else if (msg.what >= -85)
//                    view.setImageResource(R.mipmap.gprs_3);
//                    position = 3;
//                else if (msg.what >= -95)
//                    view.setImageResource(R.mipmap.gprs_2);
//                    position = 2;
//                else if (msg.what >= -105)
//                    view.setImageResource(R.mipmap.gprs_1);
//                    position = 1;
//                else
//                    view.setImageResource(R.mipmap.gprs_none);
//                    position = 0;
//            }
//            if (mark == 1) {
//                Log.i("ljwtest:", "现在的信号强度是" + msg.what);
//                //联通3g信号划分
//                if (msg.what >= -80)
//                    view.setImageResource(R.mipmap.gprs_4);
//                    position = 4;
//                else if (msg.what >= -85)
//                    view.setImageResource(R.mipmap.gprs_3);
//                    position = 3;
//                else if (msg.what >= -95)
//                    view.setImageResource(R.mipmap.gprs_2);
//                    position = 2;
//                else if (msg.what >= -100)
//                    view.setImageResource(R.mipmap.gprs_1);
//                    position = 1;
//                else
//                    view.setImageResource(R.mipmap.gprs_none);
//                    position = 0;
//            }
//            if (mark == 0) {//移动信号的划分，这个不是很确定是2g还是3g
//                if (msg.what <= 2 || msg.what == 99)
//                    view.setImageResource(R.mipmap.gprs_none);
//                    position = 0;
//                else if (msg.what >= 10)
//                    view.setImageResource(R.mipmap.gprs_4);
//                    position = 4;
//                else if (msg.what >= 8)
//                    view.setImageResource(R.mipmap.gprs_3);
//                    position = 3;
//                else if (msg.what >= 5)
//                    view.setImageResource(R.mipmap.gprs_2);
//                    position = 2;
//                else
//                    view.setImageResource(R.mipmap.gprs_1);
//            }
        }
    }

    private BroadcastReceiver GPRSStateReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_SIM_STATE_CHANGED)) {
                initGPRS();
                Log.i("ljwtest:", "sim卡状态有改变");
            } else if (action.equals("com.launcher.signalupdate")) {
                int signalStrength = intent.getIntExtra("signalstrength", -1);
                Log.i("ljwtest:", "收到的信号强度是:" + signalStrength);
                if (signalStrength != -1) {
                    Log.i("ljwtest:", "收到的信号强度是:" + signalStrength);
                    mhandler.sendEmptyMessage(signalStrength);
                    context.sendBroadcast(new Intent("com.launcher.changeiofromstrength"));
                }
            }
        }
    };

    public int getDbm(SignalStrength signalStrength) {

        int cdmaDbm = signalStrength.getCdmaDbm();
        int evdoDbm = signalStrength.getEvdoDbm();

        return (evdoDbm == -120) ? cdmaDbm : ((cdmaDbm == -120) ? evdoDbm
                : (cdmaDbm < evdoDbm ? cdmaDbm : evdoDbm));

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(ACTION_SIM_STATE_CHANGED);
        intentFilter.addAction("com.launcher.signalupdate");
        getContext().registerReceiver(GPRSStateReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(GPRSStateReceive);
        mhandler.removeCallbacksAndMessages(null);
    }
}
