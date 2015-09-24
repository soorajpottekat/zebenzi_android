package com.zebenzi.zebenzi;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.network.SearchTask;
import com.zebenzi.network.HireWorkerTask;
import com.zebenzi.users.Customer;
import com.zebenzi.users.Worker;
import org.json.JSONArray;
import java.util.ArrayList;


/**
 * A login screen that offers login via email/password.
 */
public class SearchResultsFragment extends Fragment {

    public static final int LOGIN_REQUEST = 1;
    public static final int REGISTER_REQUEST = 2;
    public static final String apiURL = "http://www.zebenzi.com/oauth/token";
    public static final String user = "0846676467";
    public static final String password = "dolphin";
    public static Context appContext;

    private String mSearchString;

    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */
    private AsyncTask<String, String, JSONArray> mSearchTask = null;
    private AsyncTask<String, String, String> mHireWorkerTask = null;

    // UI references.
    private EditText mSearchView;
    private View mProgressView;
    private SearchResultsAdapter searchResultsAdapter = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Construct the data source
        ArrayList<Worker> arrayOfUsers = new ArrayList<Worker>();
        // Create the adapter to convert the array to views
        searchResultsAdapter = new SearchResultsAdapter(MainActivity.getAppContext(), arrayOfUsers);

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        // Attach the adapter to a ListView
        ListView listView = (ListView) rootView.findViewById(R.id.searchResultsList);
        listView.setAdapter(searchResultsAdapter);

        return rootView;

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
            mSearchTask = new SearchTask(MainActivity.getAppContext(), new SearchTaskCompleteListener()).execute(mSearchString);
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

    public class HireWorkerTaskCompleteListener implements IAsyncTaskListener<String>{
        @Override
        public void onAsyncTaskComplete(String hireResult) {
            mHireWorkerTask = null;
//            showProgress(false);
            System.out.println("Hire Response = " + hireResult);

            Toast.makeText(appContext, "Worker Hired!!!", Toast.LENGTH_SHORT);
        }

        @Override
        public void onAsyncTaskCancelled() {
            mHireWorkerTask = null;
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

            Button hireButton = (Button)  convertView.findViewById(R.id.hireButton);
            hireButton.setTag(position);
            hireButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    int position=(Integer)arg0.getTag();
                    Worker worker = getItem(position);
                    System.out.println("Trying to hire: " + worker.name + " ID=" + worker.id);
                    hireWorker(worker.id);
                }
                });

            // Return the completed view to render on screen
            return convertView;
        }
    }

    public void hireWorker(String id) {

        mHireWorkerTask = new HireWorkerTask(MainActivity.getAppContext(), new HireWorkerTaskCompleteListener()).execute(Customer.getInstance().getToken(), id);
    }


    public static Context getAppContext(){
        return appContext;
    }

}



