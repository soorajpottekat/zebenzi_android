package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/09/17.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.zebenzi.users.Worker;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Represents an asynchronous search request to zebenzi.com
 */
public class SearchTask extends AsyncTask<String, String, JSONArray> {

    private String mSearchString;
    String resultToDisplay = null;
    private ISearchTaskListener<JSONArray> listener;
    private Context ctx;


    public SearchTask(Context ctx, ISearchTaskListener<JSONArray> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        // TODO: attempt authentication against a network service.
        JSONArray jsonResult=null;
        String searchURL = "http://zebenzi.com/api/search/services/";
        String urlString = searchURL + params[0];

        System.out.println("params = " + params[0]);

        URL url = null;
        try {
            System.out.println(urlString);
            url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

            StringBuilder sb = new StringBuilder();

            String line = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            String result = sb.toString();

            jsonResult = new JSONArray(result);
//            int id = jsonResult.getInt("ID");
//                    resultToDisplay = jsonResult.getString("name");

//            txtId.setText(id);
//            txtName.setText(name);

        } catch (Exception e) {
            System.out.println(e.getMessage());
//                return e.getMessage();

        }

        return jsonResult;
    }

    @Override
    protected void onPostExecute(final JSONArray jsonSearchResults) {

        listener.onSearchTaskComplete(jsonSearchResults);
    }

    @Override
    protected void onCancelled() {
        listener.onSearchTaskCancelled();
    }

}