package com.ljw.device3x.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;
import com.ljw.device3x.Utils.Utils;
import com.ljw.device3x.common.AppPackageName;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public class FMStateView extends LinearLayout{
    ImageView imageView;
    TextView textView;
    IntentFilter intentFilter;
    private Context context;
    int flag = 0;
    /**
     * 远程打开FM广播
     */
    String OPEN_FM = "RemoteOpenFMRadio";

    /**
     * 远程关闭FM广播
     */
    String CLOSE_FM = "RemoteCloseFMRadio";

    /**
     * 发送打开FM广播
     */
    String FM_STATE_IS_ON = "OpenFMBroadcast";

    /**
     * 发送关闭FM广播
     */
    String FM_STATE_IS_OFF = "CloseFMBroadcast";

    public FMStateView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        intentFilter = new IntentFilter(FM_STATE_IS_ON);
        intentFilter.addAction(FM_STATE_IS_OFF);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fmstateview, this);
        imageView = (ImageView) findViewById(R.id.fmimage);

        textView = (TextView) findViewById(R.id.fmtext);
        imageView.setImageResource(R.mipmap.fm_off);
        textView.setTextColor(context.getResources().getColor(R.color.dark_text));
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0) {
                    Intent intent = new Intent(OPEN_FM);
                    context.sendBroadcast(intent);
                    flag = 1;
                }else{
                    Intent intent = new Intent(CLOSE_FM);
                    context.sendBroadcast(intent);
                    flag = 0;
                }
            }
        });

        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Utils.getInstance().isInstalled(AppPackageName.FM_APP))
                    Utils.getInstance().openApplication(AppPackageName.FM_APP);
                return false;
            }
        });
    }

    private BroadcastReceiver changeFMState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(FM_STATE_IS_ON.equals(action)) {
                imageView.setImageResource(R.mipmap.fm_on);
                textView.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                imageView.setImageResource(R.mipmap.fm_off);
                textView.setTextColor(context.getResources().getColor(R.color.dark_text));
            }

        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        context.registerReceiver(changeFMState, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(changeFMState);
    }
}
