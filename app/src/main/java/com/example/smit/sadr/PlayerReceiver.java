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
        if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
           MPlayer.INSTANCE.onInputCall();
        }
        else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK )){
            MPlayer.INSTANCE.onOutputCall();
        }
        else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            MPlayer.INSTANCE.onEndCall();
        }
    }
}
