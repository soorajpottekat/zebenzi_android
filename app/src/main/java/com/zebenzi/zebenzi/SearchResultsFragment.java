package com.zebenzi.zebenzi;

import android.media.Image;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
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
    public static final String SEARCH_STRING = "Search";
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
    private ListView listView;
    private ImageView imageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Construct the data source
        ArrayList<Worker> arrayOfUsers = new ArrayList<Worker>();
        // Create the adapter to convert the array to views
        searchResultsAdapter = new SearchResultsAdapter(MainActivity.getAppContext(), arrayOfUsers);



        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.searchResultsList);
        listView.setAdapter(searchResultsAdapter);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        refreshScreen();

        if (getArguments() != null){
        String searchString = getArguments().getString(SearchResultsFragment.SEARCH_STRING);
        doSearch(searchString);}

        return rootView;

    }




    /**
     * Attempts to connect to zebenzi server and obtain search results
     */
    public void doSearch(String searchString) {
        if (mSearchTask != null) {
            return;
        }

//            showProgress(true);
        mSearchString = searchString;
        mSearchTask = new SearchTask(MainActivity.getAppContext(), new SearchTaskCompleteListener()).execute(mSearchString);
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
            refreshScreen();
        }

        @Override
        public void onAsyncTaskCancelled() {
            mSearchTask = null;
        }
    }

    private void refreshScreen() {
        if (searchResultsAdapter.isEmpty()){
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
        else
        {
            imageView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            searchResultsAdapter.notifyDataSetChanged();
        }
    }

    public class HireWorkerTaskCompleteListener implements IAsyncTaskListener<String>{
        @Override
        public void onAsyncTaskComplete(String hireResult) {
            mHireWorkerTask = null;
//            showProgress(false);
            System.out.println("Hire Response = " + hireResult);
            //TODO: Clear search results and take the user to Job history screen.
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
            tvName.setText(worker.getName());
            tvContact.setText(worker.getMobileNumber());
            tvAddress.setText(worker.getAddress());
            tvID.setText(worker.getId());

            Button hireButton = (Button)  convertView.findViewById(R.id.hireButton);
            hireButton.setTag(position);
            hireButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    int position=(Integer)arg0.getTag();
                    Worker worker = getItem(position);
                    System.out.println("Trying to hire: " + worker.getName() + " ID=" + worker.getId());
                    hireWorker(worker.getId(), worker.getServiceIdFromName(mSearchString));
                }
                });

            // Return the completed view to render on screen
            return convertView;
        }
    }

    public void hireWorker(String workerId, String serviceId) {

        mHireWorkerTask = new HireWorkerTask(MainActivity.getAppContext(), new HireWorkerTaskCompleteListener()).execute(Customer.getInstance().getToken(), serviceId, workerId);
    }


    public static Context getAppContext(){
        return appContext;
    }



}



