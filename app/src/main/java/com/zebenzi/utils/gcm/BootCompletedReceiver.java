package com.zebenzi.utils.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Vaugan.Nayagar on 2016/01/26.
 *
 * On bootup, register with GCM, so that device still get zebenzi push notifications
 * even if the user does not launch the app.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onReceive(Context context, Intent arg1) {
        System.out.println("Received boot notification so register zebenzi with GCM");
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(context, RegistrationIntentService.class);
        context.startService(intent);
    }

}