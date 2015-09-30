package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous search request to zebenzi.com
 */
public class HttpPostSearchTask extends AsyncTask<String, String, JSONArray> {

    private IAsyncTaskListener<JSONArray> listener;
    private Context ctx;
    private boolean networkError = false;

    public HttpPostSearchTask(Context ctx, IAsyncTaskListener<JSONArray> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        JSONArray jsonResult=null;
        HttpURLConnection conn = null;
        String searchURL = ctx.getString(R.string.api_url_search_services);
        String urlString = searchURL + params[0];
        System.out.println("params = " + params[0]);

        try {
            System.out.println(urlString);
//            url = new URL(urlString);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            URL url = new URL(searchURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
//            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            StringBuilder sb = new StringBuilder();
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            String result = sb.toString();

            jsonResult = new JSONArray(result);
        } catch (Exception e) {
            networkError = true;
            System.out.println(e.getMessage());
        } finally
        {
            try {
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonResult;
    }

    @Override
    protected void onPostExecute(final JSONArray jsonSearchResults) {

        listener.onAsyncTaskComplete(jsonSearchResults, networkError);
    }

    @Override
    protected void onCancelled() {
        listener.onAsyncTaskCancelled();
    }

}