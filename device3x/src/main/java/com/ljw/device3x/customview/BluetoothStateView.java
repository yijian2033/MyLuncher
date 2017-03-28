package com.ljw.device3x.customview;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;
import com.ljw.device3x.Utils.Utils;
import com.ljw.device3x.common.AppPackageName;
import com.ljw.device3x.common.CommonBroacastName;

/**
 * Created by Administrator on 2016/5/6 0006.
 */
public class BluetoothStateView extends LinearLayout {

    private ImageView imageView;
    Context context;
    TextView textView;
    private int bluetoothStatus = 1;
    private static final int BLUETOOTH_ON = 1;
    private static final int BLUETOOTH_OFF = 0;
    public BluetoothStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bluetoothstateview, this);
        imageView = (ImageView) findViewById(R.id.bluetoothimage);
        textView = (TextView) findViewById(R.id.bluetooth_text);
        imageView.setImageResource(R.mipmap.bluetooth_on);
        textView.setTextColor(context.getResources().getColor(R.color.white));
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothStatus == BLUETOOTH_ON)
                    closeBluetooth();
                else
                    openBluetooth();
            }
        });
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(Utils.getInstance().isInstalled(AppPackageName.BLUETOOTH_APP))
                    Utils.getInstance().openApplication(AppPackageName.BLUETOOTH_APP);
                return false;
            }
        });
    }

    private void openBluetooth() {
        Intent intent = new Intent(CommonBroacastName.BLUETOOTH_ON);
        context.sendBroadcast(intent);
        bluetoothStatus = BLUETOOTH_ON;
    }

    private void closeBluetooth() {
        Intent intent =  new Intent(CommonBroacastName.BLUETOOTH_OFF);
        context.sendBroadcast(intent);
        bluetoothStatus = BLUETOOTH_OFF;
    }


    private BroadcastReceiver bluetoothReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CommonBroacastName.BLUETOOTH_STATUSON.equals(action)) {
                Log.i("ljwtest:", "蓝牙打开广播");
                imageView.setImageResource(R.mipmap.bluetooth_on);
                textView.setTextColor(context.getResources().getColor(R.color.white));
                bluetoothStatus = BLUETOOTH_ON;
            } else if(CommonBroacastName.BLUETOOTH_STATUSOFF.equals(action)) {
                Log.i("ljwtest:", "蓝牙关闭广播");
                imageView.setImageResource(R.mipmap.bluetooth_off);
                textView.setTextColor(context.getResources().getColor(R.color.dark_text));
                bluetoothStatus = BLUETOOTH_OFF;
            }
        }
    };


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonBroacastName.BLUETOOTH_STATUSON);
        intentFilter.addAction(CommonBroacastName.BLUETOOTH_STATUSOFF);
        getContext().registerReceiver(bluetoothReceive, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(bluetoothReceive);
    }
}
