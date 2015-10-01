package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class HttpPostHireWorkerTask extends AsyncTask<String, String, String> {

    private Context ctx;
    private IAsyncTaskListener listener;
    private boolean networkError = false;


    public HttpPostHireWorkerTask(Context ctx, IAsyncTaskListener<String> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        OutputStream os = null;
        BufferedInputStream in = null;
        HttpURLConnection conn = null;
        JSONObject jsonHireParams = new JSONObject();
        String resultToDisplay;
        String token = params[0];
        int serviceId = Integer.parseInt(params[1]);
        String workerId = params[2];

        try {
            jsonHireParams.put(ctx.getString(R.string.api_json_field_service_id), serviceId);
            jsonHireParams.put(ctx.getString(R.string.api_json_field_worker_id), workerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(ctx.getString(R.string.api_url_hire_worker));
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod(ctx.getString(R.string.api_rest_post));
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "bearer " + token);
            conn.connect();

            //Write params to output string
            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(jsonHireParams.toString());
            writer.flush();

            //If successful connection, read input stream, else read error stream
            if (conn.getResponseCode() / 100 == 2) { // 2xx code means success
//                in = new BufferedInputStream(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                String line = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                resultToDisplay = sb.toString();

            } else {

//                in = new BufferedInputStream(conn.getErrorStream());
                StringBuilder sb = new StringBuilder();
                String line = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                resultToDisplay = sb.toString();

                System.out.println("Error = "+conn.getResponseCode());
                System.out.println("Error Stream = " + resultToDisplay);
            }
        } catch (Exception e) {
            networkError = true;
            System.out.println(e.getMessage());
            return e.getMessage();

        } finally {

            try {
                os.close();
//                in.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //This will be either input stream or error stream
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
