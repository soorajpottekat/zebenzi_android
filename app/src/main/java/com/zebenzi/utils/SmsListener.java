package com.zebenzi.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;

import com.zebenzi.ui.FragmentsLookup;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;

/**
 * Created by Vaugan.Nayagar on 2015/09/29.
 *
 * This already works as-is. If there are problems with onReceive, it is because of app permissions.
 */
public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;
    private int mId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        String msgBody=null;
        int jobId=0;

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                        System.out.println("SMS= " + msgBody);
                    }

                    jobId = parseSMS(msgBody);

                    if (jobId > 0) {
                        displayNotification(jobId);
                    }

                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    /**
     * Parse Zebenzi sms's and provide app notifications to user.
     * Eventually, this will be supplemented with Google Cloud Messaging as only worker phones will receive sms's.
     *
     * @param msgBody - the sms text which should contain a zebenzi header string.
     */
    private int parseSMS(String msgBody) {

        String header = "[zbzc-";

        if (msgBody.indexOf(header) != -1) {
            //Valid zebenzi customer SMS
            //TODO: Also handle worker SMS header - "[zbzw-"
            String newString = msgBody.substring(msgBody.indexOf(header)+header.length(), msgBody.indexOf("]"));
            System.out.println("New String = " + newString);

            return Integer.parseInt(newString);
        }
        return 0;
    }

    /**
     * Pop up zebenzi notification for customer.
     *
     * @param jobId - the job ID from the SMS, which will be used to retrieve and display job details.
     */

    private void displayNotification(int jobId) {

//        int jobId = parseSMS("test");

        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(MainActivity.getAppContext())
                .setSmallIcon(R.drawable.ic_launcher_zebenzi) // notification icon
                .setContentTitle("Zebenzi Job [" + jobId + "] update!") // title for notification
                        .setContentText("Touch to see the the job update") // message for notification
                        .setAutoCancel(true); // clear notification after click
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(MainActivity.getAppContext(), MainActivity.class);
        resultIntent.putExtra("fragment_to_launch", FragmentsLookup.JOB_DETAILS.getName());
        resultIntent.putExtra("fragment_data", Integer.toString(jobId));

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.getAppContext());
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) MainActivity.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }





}
