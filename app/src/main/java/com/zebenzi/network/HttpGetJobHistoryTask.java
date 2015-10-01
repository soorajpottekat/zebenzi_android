package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class HttpGetJobHistoryTask extends AsyncTask<String, String, JSONArray> {

    private String mToken;
    private String resultToDisplay = null;
    private Context ctx;
    private IAsyncTaskListener listener;
    String jobHistoryURL = "http://www.zebenzi.com/api/job/hired";
    private boolean networkError = false;


    public HttpGetJobHistoryTask(Context ctx, IAsyncTaskListener<JSONArray> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        JSONArray jsonresult = null;
        mToken = params[0];
        try {
            URL url = new URL(jobHistoryURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod(ctx.getString(R.string.api_rest_get));
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

                jsonresult = new JSONArray(sb.toString());
            } else {

                StringBuilder sb = new StringBuilder();
                String line = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                System.out.println("Error requesting job history: " + sb.toString());
            }

        } catch (Exception e) {
            networkError = true;
            System.out.println(e.getMessage());
            return null;
        }

        return jsonresult;
    }


    @Override
    protected void onPostExecute(final JSONArray result) {
        listener.onAsyncTaskComplete(result, networkError);
    }

    @Override
    protected void onCancelled() {
        listener.onAsyncTaskCancelled();
    }
}
