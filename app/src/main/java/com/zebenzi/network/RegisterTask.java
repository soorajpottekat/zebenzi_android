package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.zebenzi.R;

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
public class RegisterTask extends AsyncTask<JSONObject, String, String> {

    private Context ctx;
    private IAsyncTaskListener listener;
    String customerRegistrationAPIUrl = "http://www.zebenzi.com/api/accounts/create";

    public RegisterTask(Context ctx, IAsyncTaskListener<String> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        OutputStream os = null;
        BufferedInputStream in = null;
        HttpURLConnection conn = null;
        String resultToDisplay;
        JSONObject jsonResult;
        JSONObject jsonCustomerParams = params[0];;

        try {
            URL url = new URL(customerRegistrationAPIUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            //Write params to output string
            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(jsonCustomerParams.toString());
            writer.flush();

            //If successful connection, read input stream, else read error stream
            if (conn.getResponseCode() / 100 == 2) { // 2xx code means success
                in = new BufferedInputStream(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                String line = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                String result = sb.toString();

                jsonResult = new JSONObject(result);
                System.out.println("Registration Result = " + jsonResult.toString());
                System.out.println("Registration Result END ");
                resultToDisplay=jsonResult.toString();
            } else {

                in = new BufferedInputStream(conn.getErrorStream());
                StringBuilder sb = new StringBuilder();
                String line = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                String result = sb.toString();
                jsonResult = new JSONObject(result);

                System.out.println("Error = "+conn.getResponseCode());
                System.out.println("Error Stream = " + jsonResult.toString());

                resultToDisplay=jsonResult.toString();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();

        } finally {

            try {
                os.close();
                in.close();
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
        listener.onAsyncTaskComplete(result);
    }

    @Override
    protected void onCancelled() {
        listener.onAsyncTaskCancelled();
    }
}
