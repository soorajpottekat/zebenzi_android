package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.zebenzi.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<String, String, String> {

    private String mMobileNumber;
    private String mPassword;
    private String resultToDisplay = null;
    private Context ctx;
    private ILoginTaskListener listener;

    public LoginTask(Context ctx, ILoginTaskListener<String> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            mMobileNumber = params[0];
            mPassword = params[1];

            URL url = new URL(ctx.getString(R.string.api_url_login_token));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(ctx.getString(R.string.api_post));
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> local_params = new ArrayList<NameValuePair>();
            local_params.add(new BasicNameValuePair(ctx.getString(R.string.api_username), mMobileNumber));
            local_params.add(new BasicNameValuePair(ctx.getString(R.string.api_password), mPassword));
            local_params.add(new BasicNameValuePair(ctx.getString(R.string.api_grant_type), ctx.getString(R.string.api_password)));


            //Send params via output stream
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, ctx.getString(R.string.api_utf8)));
            writer.write(getQuery(local_params));
            writer.flush();
            writer.close();
            os.close();

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
            } else {

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

    //Encode the login params in UTF-8
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), ctx.getString(R.string.api_utf8)));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), ctx.getString(R.string.api_utf8)));
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(final String result) {
        listener.onLoginTaskComplete(result);
    }

    @Override
    protected void onCancelled() {
        listener.onLoginTaskCancelled();
    }
}
