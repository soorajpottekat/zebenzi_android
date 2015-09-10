package com.zebenzi.zebenzi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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
import java.util.LinkedList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class SearchActivity extends ActionBarActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    public final static String apiURL = "http://www.zebenzi.com/oauth/token";
    public final static String user = "0846676467";
    public final static String password = "dolphin";

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */
    private SearchTask mSearchTask = null;

    // UI references.
    private EditText mSearchView;
    private View mProgressView;
    private SearchResultsAdapter searchResultsAdapter = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // Construct the data source
        ArrayList<Worker> arrayOfUsers = new ArrayList<Worker>();
        // Create the adapter to convert the array to views
        searchResultsAdapter = new SearchResultsAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.searchResultsList);
        listView.setAdapter(searchResultsAdapter);



        mSearchView = (EditText) findViewById(R.id.searchText);

        Button mSearchButton = (Button) findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
//                composeMessage();
                return true;
            case R.id.action_account:
//                composeMessage();
                return true;
            case R.id.action_register:
                Intent intent = new Intent(this, RegisterCustomerActivity.class);
//                EditText editText = (EditText) findViewById(R.id.edit_message);
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                return true;
            case R.id.action_login:
                intent = new Intent(this, LoginActivity.class);
//                EditText editText = (EditText) findViewById(R.id.edit_message);
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Attempts to connect to zebenzi server and obtain search results
     */
    public void doSearch() {
        if (mSearchTask != null) {
            return;
        }

        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);
        mSearchView.setError(null);

        // Store values at the time of the login attempt.
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
        String searchText = mSearchView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(searchText)) {
            mSearchView.setError(getString(R.string.empty_search_string));
            focusView = mSearchView;
            cancel = true;
        }

        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            mSearchTask = new SearchTask(searchText);
            mSearchTask.execute((String) null);
        }
    }

    /**
     * Represents an asynchronous search request to zebenzi.com
     */
    public class SearchTask extends AsyncTask<String, String, JSONArray> {

        private final String mSearchString;
        String resultToDisplay = null;

        SearchTask(String searchString) {
            mSearchString = searchString;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            JSONArray jsonResult=null;
            String searchURL = "http://zebenzi.com/api/search/services/";
            String urlString = searchURL + mSearchString;

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
            mSearchTask = null;
//            showProgress(false);

            if (jsonSearchResults != null) {
                System.out.println("============= SEARCH RESULTS FOR: "+ mSearchString + " =====================");
                System.out.println("jsonResult = "+jsonSearchResults.toString());
                System.out.println("============= SEARCH RESULTS FOR: "+ mSearchString + " =====================");

//                JSONArray jsonArray = ...;
                searchResultsAdapter.clear();
                ArrayList<Worker> newWorkers = Worker.fromJson(jsonSearchResults);
                searchResultsAdapter.addAll(newWorkers);

//                finish();
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mSearchView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
//            showProgress(false);
        }

    }

    public class SearchResultsAdapter extends ArrayAdapter<Worker> {
        public SearchResultsAdapter(Context context, ArrayList<Worker> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Worker worker = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_results_row, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.workerName);
            TextView tvContact = (TextView) convertView.findViewById(R.id.workerContactNumber);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.workerAddress);
            TextView tvID = (TextView) convertView.findViewById(R.id.workerID);
            // Populate the data into the template view using the data object
            tvName.setText(worker.name);
            tvContact.setText(worker.contact);
            tvAddress.setText(worker.address);
            tvID.setText(worker.id);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    protected String addLoginParamsToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair("username", "0846676467"));
        params.add(new BasicNameValuePair("password", "dolphin"));
        params.add(new BasicNameValuePair("grant_type", "password"));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }
}



