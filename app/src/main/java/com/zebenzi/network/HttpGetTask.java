package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.ui.R;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class HttpGetTask extends AsyncTask<Object, String, String> {

    private String mUrl;
    private Header mHeader;
    private JSONObject mBody;
    private String resultToDisplay = null;
    private Context ctx;
    private IAsyncTaskListener listener;
    String userDetailsURL = "http://www.zebenzi.com/api/accounts/user/current";
    private boolean networkError = false;
    private String mSearchString;


    public HttpGetTask(Context ctx, IAsyncTaskListener<String> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... params) {
        mUrl = (String)params[0];
        mHeader = (Header)params[1];
        mBody = (JSONObject)params[2];

            try {

                URL url = new URL(mUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod(ctx.getString(R.string.api_rest_get));

                if (mHeader != null) {
//                    conn.setRequestProperty("Content-Type", "application/json");
//                    conn.setRequestProperty("Authorization", "bearer " + mToken);
                }

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
                networkError = true;
                System.out.println(e.getMessage());
                return null;
            }

            return resultToDisplay;
    }


    @Override
    protected void onPostExecute(final String result) {
        listener.onAsyncTaskComplete(result, networkError);
    }

    @Override
    protected void onCancelled() {
        listener.onAsyncTaskCancelled();
    }
}
