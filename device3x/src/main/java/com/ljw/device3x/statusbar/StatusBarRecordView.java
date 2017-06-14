package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ljw.device3x.Activity.DeviceApplication;
import com.ljw.device3x.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/12/22 0022.
 */

public class StatusBarRecordView extends ImageView{
    public static final String RAYEE_RECORD_STATE = "DvrRecordState";
    private static final String DEV_REC_ON = "cn.conqueror.action.DVR_REC_ON";//行车记录仪打开
    private static final String DEV_REC_OFF = "cn.conqueror.action.DVR_REC_OFF";//行车记录仪关闭
    private static final int REC_OFF_IMG = R.mipmap.videocamera_off;
    private static final int[] REC_ON_IMG = {R.mipmap.videocamera_on, R.mipmap.videocamera_off};

    private Context context;

    private static final int REC_ON = 1;
    private static final int REC_OFF = 0;
    private  int count = 0;//图片循环计数器
    private boolean isStop = false;

    private RecHandler recHandler;

    public StatusBarRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        recHandler = new RecHandler(this);
        this.context = context;
        Log.i("gfwtest:", "StatusBarRecordView  构造函数");
    }

    public StatusBarRecordView(Context context) {
        this(context, null);
    }

    private  class RecHandler extends Handler {
        WeakReference<StatusBarRecordView> mView;
        private RecStartTimerTask recStartTimerTask;
        public Timer recStartTimer;
        public RecHandler(StatusBarRecordView view) {
            mView = new WeakReference<StatusBarRecordView>(view);
            recStartTimer = new Timer(true);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mView.get() == null)
                return;
            StatusBarRecordView view = mView.get();
            if(msg.what == REC_OFF) {
                if(recStartTimer != null && recStartTimerTask != null)
                    recStartTimerTask.cancel();
                view.setImageResource(REC_OFF_IMG);
            } else {
                if(count >= REC_ON_IMG.length)
                    count = 0;
                view.setImageResource(REC_ON_IMG[count++]);
            }
        }

    }

    private void startRecyleRecImg() { //当行车记录仪打开时打开图片切换
        if(recHandler.recStartTimer != null && recHandler.recStartTimerTask != null)
            recHandler.recStartTimerTask.cancel();
        recHandler.recStartTimerTask = new RecStartTimerTask();
        recHandler.recStartTimer.schedule(recHandler.recStartTimerTask, 1000, 1000);
    }

    class RecStartTimerTask extends TimerTask {
        @Override
        public void run() {
            recHandler.sendEmptyMessage(REC_ON);
        }
    }


    private BroadcastReceiver recReveive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(DEV_REC_OFF)) {
                recHandler.sendEmptyMessage(REC_OFF);
             //   Toast.makeText(context,"存储录像状态-关闭",Toast.LENGTH_SHORT).show();
            //    Settings.System.putInt(getContext().getContentResolver(),RAYEE_RECORD_STATE,REC_OFF);
            } else{
                startRecyleRecImg();
             //   Toast.makeText(context,"存储录像状态-打开",Toast.LENGTH_SHORT).show();
             //   Settings.System.putInt(getContext().getContentResolver(),RAYEE_RECORD_STATE,REC_ON);
            }


        }
    };



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter(DEV_REC_ON);
        intentFilter.addAction(DEV_REC_OFF);
        context.registerReceiver(recReveive, intentFilter);
        initState();
    }

    private void initState() {
        int state = Settings.System.getInt(getContext().getContentResolver(),RAYEE_RECORD_STATE,-1);
     //   Toast.makeText(context,"获取录像状态："+state,Toast.LENGTH_SHORT).show();
        if (state == 0) {
            recHandler.sendEmptyMessage(state);
        }else if (state == 1) {
            startRecyleRecImg();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(recReveive);
    }
}
