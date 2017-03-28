package com.ljw.device3x.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ljw.device3x.Utils.Utils;

/**
 * Created by Administrator on 2017/1/13 0013.
 */

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("ljwtestboottest", action);
        if(action.equals("android.intent.action.ACTION_SHUTDOWN")) {
            Log.i("ljwtestboottest", "关机设置成功");
            Utils.setBoot(context, Utils.BOOT);
        } else if(action.equals("com.conqueror.action.jw.CompleteBoot")){
            Log.i("ljwtestboottest", "开机设置成功");
            Utils.setBoot(context, Utils.NOT_BOOT);
        } else if(action.equals("com.ljw.getbootvalue")) {
            Log.i("ljwtestboottest", "开机设置值是" + Utils.getBoot(context));
        }
    }
}
