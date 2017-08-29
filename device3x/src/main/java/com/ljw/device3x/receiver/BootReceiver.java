package com.ljw.device3x.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ljw.device3x.Activity.WindowsActivity;
import com.ljw.device3x.Utils.Utils;

/**
 * Created by Administrator on 2017/1/13 0013.
 */

public class BootReceiver extends BroadcastReceiver{
    int dd = 0xff;
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("ljwtestboottest", action);
        if(action.equals("android.intent.action.ACTION_SHUTDOWN")) {
            Log.i("ljwtestboottest", "关机设置成功"+dd);
            Utils.setBoot(context, Utils.BOOT);
        } else if(action.equals("com.conqueror.action.jw.CompleteBoot")){
            Log.i("ljwtestboottest", "开机设置成功");
            Utils.setBoot(context, Utils.NOT_BOOT);
            final AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            int ALARM_curvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            int MUSIC_curvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i("ljwtestboottest", "ALARM_curvolume "+ALARM_curvolume+"  MUSIC_curvolume "+MUSIC_curvolume);
            if (ALARM_curvolume != MUSIC_curvolume && MUSIC_curvolume != 0){
                Log.i("testSetVolume","setStreamVolume3: "+MUSIC_curvolume);
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,MUSIC_curvolume,0);
            }

            Handler mha = new Handler();
            mha.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int ALARM_curvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                    int MUSIC_curvolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    Log.i("ljwtestboottest", "ALARM_curvolume "+ALARM_curvolume+"  MUSIC_curvolume "+MUSIC_curvolume);
                    if (ALARM_curvolume != MUSIC_curvolume && MUSIC_curvolume != 0){
                        Log.i("testSetVolume","setStreamVolume4: "+MUSIC_curvolume);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,MUSIC_curvolume,0);
                    }
                }
            },3000);
        } else if(action.equals("com.ljw.getbootvalue")) {
            Log.i("ljwtestboottest", "开机设置值是" + Utils.getBoot(context));
        }else if (action.equals(Intent.ACTION_BOOT_COMPLETED)){

        }
    }
}
