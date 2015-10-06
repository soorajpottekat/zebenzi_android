package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.ui.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an asynchronous http post task
 *
 */
public class HttpPostTask extends AsyncTask<Object, String, String> {

    private Context ctx;
    private IAsyncTaskListener listener;
    private boolean networkError = false;
    private String mUrl;
    private HashMap<String, String> mHeader = null;


    public HttpPostTask(Context ctx, IAsyncTaskListener<String> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... params) {
        OutputStream os = null;
        HttpURLConnection conn = null;
        String resultToDisplay;
        String outputString;
        BufferedWriter writer;

        mUrl = (String)params[0];
        mHeader = (HashMap)params[1];
        HttpContentTypes type = (HttpContentTypes)params[3];

        try {
            URL url = new URL(mUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod(ctx.getString(R.string.api_rest_post));
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            //All headers should be passed in as a hashmap
            if (mHeader != null) {
                Iterator<String> it = mHeader.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next();
                    conn.setRequestProperty(key, mHeader.get(key));
                }
            }
            conn.connect();

            os = conn.getOutputStream();
            //Build output string
            switch(type){
                case X_WWW_FORM_URLENCODED:
                    writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    List<NameValuePair> listBody = (List<NameValuePair>)params[2];
                    outputString = getQuery(listBody);
                    break;
                case RAW:
                    writer = new BufferedWriter(new OutputStreamWriter(os));
                    JSONObject jsonBody = (JSONObject)params[2];
                    outputString = jsonBody.toString();
                    break;

                //Unsupported content types, so return null.
                case BINARY:
                case FORM_DATA:
                default:
                    System.out.println("Unsupported content type: "+type);
                    return null;
            }

            //Write params to output string
            writer.write(outputString);
            writer.flush();

            //If successful connection, read input stream, else read error stream
            if (conn.getResponseCode() / 100 == 2) { // 2xx code means success
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

    //Encode the login params in UTF-8
    private String getQuery(List<NameValuePair> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        try {
            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), ctx.getString(R.string.api_rest_utf8)));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), ctx.getString(R.string.api_rest_utf8)));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
