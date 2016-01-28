/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zebenzi.utils.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.ui.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static final int ANDROID_DEVICE = 1;

    private AsyncTask<Object, String, String> mSendDeviceTokenTask = null;


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(this.getString(R.string.api_gcm_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            sharedPreferences.edit().putString(QuickstartPreferences.DEVICE_TOKEN, token).apply();

            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            sharedPreferences.edit().putString(QuickstartPreferences.DEVICE_TOKEN, "").apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    /**
     * Send device registration token to Zebenzi server.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        SharedPreferences settings = null;
        settings = this.getSharedPreferences("ZebenziUser", 0);
        String userAccessToken = settings.getString(this.getString(R.string.api_rest_access_token), null);

        if ((mSendDeviceTokenTask != null) || (userAccessToken == null)) {
            return;
        }

        Log.i(TAG, "User access token: " + userAccessToken);


        //Build url
        String url = this.getString(R.string.api_url_send_device_token_to_server);

        //Build header
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "bearer " + userAccessToken);

        //Build body
        JSONObject body = new JSONObject();
        try {
            body.put(this.getString(R.string.api_json_field_device_type), ANDROID_DEVICE);
            body.put(this.getString(R.string.api_json_field_device_key), token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSendDeviceTokenTask = new HttpPostTask(this, new SendDeviceTokenTaskCompleteListener()).execute(url, header, body, HttpContentTypes.RAW);
    }

    public class SendDeviceTokenTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String sendTokenResult, boolean networkError) {
            mSendDeviceTokenTask = null;

            if (networkError) {
                System.out.println("Network Error sending device token to zebenzi server.");
            } else {
                System.out.println("Send Device Token Response = " + sendTokenResult);
            }

        }

        @Override
        public void onAsyncTaskCancelled() {
            mSendDeviceTokenTask = null;
        }
    }
}
