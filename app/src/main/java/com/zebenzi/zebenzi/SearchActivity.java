package com.zebenzi.zebenzi;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.network.SearchTask;
import com.zebenzi.users.Worker;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class SearchActivity extends ActionBarActivity {

    public static final int LOGIN_REQUEST = 1;
    public static final String apiURL = "http://www.zebenzi.com/oauth/token";
    public static final String user = "0846676467";
    public static final String password = "dolphin";
    public static Context appContext;

    private String mSearchString;

    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */
    private AsyncTask<String, String, JSONArray> mSearchTask = null;

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
        getSupportActionBar().setTitle(R.string.app_name);

        appContext = getApplicationContext();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_account:
                return true;
            case R.id.action_register:
                Intent intent = new Intent(this, RegisterCustomerActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_login:
                intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String user=data.getStringExtra("Username");
                getSupportActionBar().setTitle(user);
            }
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
        mSearchView.setError(null);

        // Store values at the time of the login attempt.
        mSearchString = mSearchView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mSearchString)) {
            mSearchView.setError(getString(R.string.empty_search_string));
            focusView = mSearchView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            mSearchTask = new SearchTask(this, new SearchTaskCompleteListener()).execute(mSearchString);
        }
    }

    public class SearchTaskCompleteListener implements IAsyncTaskListener<JSONArray>{
        @Override
        public void onAsyncTaskComplete(JSONArray jsonSearchResults) {
            mSearchTask = null;
//            showProgress(false);

            if (jsonSearchResults != null) {
                searchResultsAdapter.clear();
                ArrayList<Worker> newWorkers = Worker.fromJson(jsonSearchResults);
                searchResultsAdapter.addAll(newWorkers);
            } else {
                mSearchView.requestFocus();
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
            mSearchTask = null;
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


    public static Context getAppContext(){
        return appContext;
    }

}



