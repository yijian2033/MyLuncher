package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
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

public class StatusBarRecordView extends ImageView {
    public static final String RAYEE_RECORD_STATE = "DvrRecordState";
    private static final String DEV_REC_ON = "cn.conqueror.action.DVR_REC_ON";//行车记录仪打开
    private static final String DEV_REC_OFF = "cn.conqueror.action.DVR_REC_OFF";//行车记录仪关闭
    private static final int REC_OFF_IMG = R.mipmap.videocamera_off;
    private static final int[] REC_ON_IMG = {R.mipmap.videocamera_on, R.mipmap.videocamera_off};
    private static final Uri STATE_URI = Uri.parse("content://com.conqueror.dvr.contentprovider/recordState");
    private Context context;
    private  final String TAG = "StatusBarRecordView";

    private static final int REC_ON = 1;
    private static final int REC_OFF = 0;
    private int count = 0;//图片循环计数器
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

    private class RecHandler extends Handler {
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

            if (mView.get() == null)
                return;
            StatusBarRecordView view = mView.get();
            if (msg.what == REC_OFF) {
                if (recStartTimer != null && recStartTimerTask != null)
                    recStartTimerTask.cancel();
                view.setImageResource(REC_OFF_IMG);
            } else {
                if (count >= REC_ON_IMG.length)
                    count = 0;
                Log.i(TAG, "view.setImageResource"+count);
                view.setImageResource(REC_ON_IMG[count++]);
            }
        }

    }

    private void startRecyleRecImg() { //当行车记录仪打开时打开图片切换
        if (recHandler.recStartTimer != null && recHandler.recStartTimerTask != null)
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




    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
         ContentResolver resolver = context.getContentResolver();
        resolver.registerContentObserver(STATE_URI, true, observer);

        initState();
    }

    private void initState() {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(STATE_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            Log.i(TAG, "recordState change:"+state);
            if (state == 0){
                recHandler.sendEmptyMessage(REC_OFF);
            }else if (state == 1){
                startRecyleRecImg();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
       ContentResolver resolver = context.getContentResolver();
        resolver.unregisterContentObserver(observer);
    }


    private ContentObserver observer = new ContentObserver(new Handler()) {


        // 得到数据的变化通知，该方法只能粗略知道数据的改变，并不能判断是哪个业务操作进行的改变
        @Override
        public void onChange(boolean selfChange) {
            // select * from person order by id desc limit 1 // 取得最近插入的值（序号大——>小并取第一个）
            ContentResolver resolver = getContext().getContentResolver();
            Cursor cursor = resolver.query(STATE_URI, null, null, null, null);
            if (cursor.moveToFirst()) {
                int state = cursor.getInt(cursor.getColumnIndex("state"));
                Log.i(TAG, "recordState change:"+state);
                if (state == 0){
                    recHandler.sendEmptyMessage(REC_OFF);
                }else if (state == 1){
                    startRecyleRecImg();
                }
            }
        }

    };
}
