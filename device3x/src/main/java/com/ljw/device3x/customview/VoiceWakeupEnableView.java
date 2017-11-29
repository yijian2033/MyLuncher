package com.ljw.device3x.customview;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;
import com.ljw.device3x.common.CommonCtrl;

/**
 * ljw：Administrator on 2017/11/21 0021 13:54
 */
public class VoiceWakeupEnableView extends LinearLayout {
    public static final String RAYEE_VOICE_WAKEUP_STATE = "VoiceWakeupEnabledState";
    private ImageView imageView;
    private TextView textView;
    private Context context;
   public VoiceWakeupEnableView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.voicewakeupenableview, this);
        this.context = context;
        imageView = (ImageView) findViewById(R.id.wakeupname);
        textView = (TextView) findViewById(R.id.wakeuptext);
        imageView.setImageResource(CommonCtrl.isWifiApEnabled(context) ? R.mipmap.wakeup_on : R.mipmap.wakeup_off);
        textView.setTextColor(CommonCtrl.isWifiApEnabled(context) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.dark_text));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = Settings.System.getInt(context.getContentResolver(),"VoiceWakeupEnabledState",1);
                Intent intent = new Intent("com.conqueror.Action.setVoiceWakeupEnabled");
                //Log.i("VoiceWakeupEnabledState","VoiceWakeupEnabledState:"+state);
                if (state == 1 ){
                    intent.putExtra("VoiceWakeupEnabledState",0);
                //    Settings.System.putInt(context.getContentResolver(),"VoiceWakeupEnabledState",0);
                //    imageView.setImageResource(R.mipmap.wakeup_off);
                 //   textView.setTextColor(context.getResources().getColor(R.color.dark_text));
                }else {
                    intent.putExtra("VoiceWakeupEnabledState",1);
                //    Settings.System.putInt(context.getContentResolver(),"VoiceWakeupEnabledState",1);
                //   imageView.setImageResource(R.mipmap.wakeup_on);
                //    textView.setTextColor(context.getResources().getColor(R.color.white));
                }
                context.sendBroadcast(intent);
            }
        });

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initState();
        ContentResolver resolver = context.getContentResolver();
        resolver.registerContentObserver(Settings.System.getUriFor(RAYEE_VOICE_WAKEUP_STATE), true, observer);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ContentResolver resolver = context.getContentResolver();
        resolver.unregisterContentObserver(observer);
    }
    private void initState() {
        int state = Settings.System.getInt(getContext().getContentResolver(),"VoiceWakeupEnabledState",1);
        if(state == 1) {
            imageView.setImageResource(R.mipmap.wakeup_on);
            textView.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            imageView.setImageResource(R.mipmap.wakeup_off);
            textView.setTextColor(context.getResources().getColor(R.color.dark_text));
        }
    }

    private ContentObserver observer = new ContentObserver(new Handler()) {


        // 得到数据的变化通知，该方法只能粗略知道数据的改变，并不能判断是哪个业务操作进行的改变
        @Override
        public void onChange(boolean selfChange) {
            initState();
        }
    };
}
