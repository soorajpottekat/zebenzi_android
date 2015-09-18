package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.zebenzi.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class HireWorkerTask extends AsyncTask<String, String, String> {

    private String mToken;
    private String resultToDisplay = null;
    private Context ctx;
    private IAsyncTaskListener listener;
    String HireWorkerURL = "http://www.zebenzi.com/api/job/hire/";


    public HireWorkerTask(Context ctx, IAsyncTaskListener<String> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
            mToken = params[0];
        String mWorkerID = params[1];
            try {
                System.out.println("Worker ID=" + mWorkerID);
                URL url = new URL(HireWorkerURL+mWorkerID);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(ctx.getString(R.string.api_get));
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "bearer " + mToken);


                conn.connect();

                if (conn.getResponseCode() / 100 == 2) { // 2xx code means success
                    //Read data from input stream
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    resultToDisplay = sb.toString();
                }
                else
                {

                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    resultToDisplay = sb.toString();
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }

            return resultToDisplay;
    }


    @Override
    protected void onPostExecute(final String result) {
        listener.onAsyncTaskComplete(result);
    }

    @Override
    protected void onCancelled() {
        listener.onAsyncTaskCancelled();
    }
}
