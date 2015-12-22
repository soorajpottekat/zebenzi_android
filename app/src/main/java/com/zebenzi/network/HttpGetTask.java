package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.ui.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class HttpGetTask extends AsyncTask<Object, String, String> {

    private String mUrl;
    private HashMap<String, String> mHeader = null;
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
        mHeader = (HashMap)params[1];
        mBody = (JSONObject)params[2];

            try {

                //Url should be built by the caller
                URL url = new URL(mUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(ctx.getString(R.string.api_rest_get));

                //All headers should be passed in as a hashmap
                if (mHeader != null) {
                    Iterator<String> it = mHeader.keySet().iterator();
                    while(it.hasNext()){
                        String key = it.next();
                        conn.setRequestProperty(key, mHeader.get(key));
                    }
                }

                //TODO: Body

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

            }
            catch (SocketTimeoutException e) {
                e.printStackTrace();
                return e.getMessage();
            }
            catch (Exception e) {
                networkError = true;
                System.out.println(e.getMessage());
                return e.getMessage();
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
