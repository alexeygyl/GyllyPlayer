package com.example.smit.sadr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PlayerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING) && MainActivity.status == MainActivity.ON) {
            MainActivity.mediaPlayer.pause();
            MainActivity.status = MainActivity.PAUSE;
            MainActivity.PHONE_STATUS = MainActivity.ON;
        }
        else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK )&& MainActivity.status == MainActivity.ON){
            MainActivity.mediaPlayer.pause();
            MainActivity.status = MainActivity.PAUSE;
            MainActivity.PHONE_STATUS = MainActivity.ON;
        }
        else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE) && MainActivity.status == MainActivity.PAUSE){
            if( MainActivity.PHONE_STATUS == MainActivity.ON) {
                MainActivity.mediaPlayer.start();
                MainActivity.status = MainActivity.ON;
                MainActivity.PHONE_STATUS = MainActivity.OFF;
            }
        }

    }
}
