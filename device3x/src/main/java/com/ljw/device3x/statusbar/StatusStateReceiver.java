package com.ljw.device3x.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.ljw.device3x.common.CommonBroacastName;

import static com.ljw.device3x.statusbar.StatusBarBlueToothView.RAYEE_BT_STATE;
import static com.ljw.device3x.statusbar.StatusBarEdogView.RAYEE_EDOG_STATE;
import static com.ljw.device3x.statusbar.StatusBarFMView.RAYEE_FM_STATE;
import static com.ljw.device3x.statusbar.StatusBarRadarView.RAYEE_RADAR_STATE;
import static com.ljw.device3x.statusbar.StatusBarRecordView.RAYEE_RECORD_STATE;

public class StatusStateReceiver extends BroadcastReceiver {
    private static final String EDOG_STATUS_ON = "com.wanma.action.EDOG_STATUS_ON";
    private static final String EDOG_STATUS_OFF = "com.wanma.action.EDOG_STATUS_OFF";
    /**
     * 打开FM广播
     */
    String FM_STATE_IS_ON = "OpenFMBroadcast";

    /**
     * 关闭FM广播
     */
    String FM_STATE_IS_OFF = "CloseFMBroadcast";
    private static final String RADAR_STATUS_ON = "com.wanma.action.RADAR_STATUS_ON";
    private static final String RADAR_STATUS_OFF = "com.wanma.action.RADAR_STATUS_OFF";
    private static final String DEV_REC_ON = "cn.conqueror.action.DVR_REC_ON";//行车记录仪打开
    private static final String DEV_REC_OFF = "cn.conqueror.action.DVR_REC_OFF";//行车记录仪关闭
    public StatusStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equals(CommonBroacastName.BLUETOOTH_STATUSON)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_BT_STATE, 1);
        } else if (action.equals(CommonBroacastName.BLUETOOTH_STATUSOFF)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_BT_STATE, 0);
        }else if (action.equals(EDOG_STATUS_ON)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_EDOG_STATE, 1);
        } else if (action.equals(EDOG_STATUS_OFF)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_EDOG_STATE, 0);
        }else if (action.equals(FM_STATE_IS_ON)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_FM_STATE, 1);
        } else if (action.equals(FM_STATE_IS_OFF)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_FM_STATE, 0);
        }else if (action.equals(RADAR_STATUS_ON)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_RADAR_STATE, 1);
        } else if (action.equals(RADAR_STATUS_OFF)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_RADAR_STATE, 0);
        }else if (action.equals(DEV_REC_ON)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_RECORD_STATE, 1);
        } else if (action.equals(DEV_REC_OFF)) {
            Settings.System.putInt(context.getContentResolver(), RAYEE_RECORD_STATE, 0);
        }
    }
}
