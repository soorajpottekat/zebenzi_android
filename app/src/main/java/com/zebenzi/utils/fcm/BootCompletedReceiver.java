package com.zebenzi.utils.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Vaugan.Nayagar on 2016/01/26.
 *
 * On bootup, register with FCM, so that device still get zebenzi push notifications
 * even if the user does not launch the app.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent arg1) {
        System.out.println("Received boot notification so register zebenzi with FCM");
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(context, RegistrationIntentService.class);
        context.startService(intent);
    }

}