package com.zebenzi.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.zebenzi.json.model.quote.Quote;
import com.zebenzi.json.model.user.User;
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.zebenzi.ui.FragmentsLookup.HISTORY;


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
    private FragmentListener fragmentListener;
    /**
     * Keep track of the search task to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mSearchTask = null;
    private AsyncTask<Object, String, String> mHireWorkerTask = null;
    private AsyncTask<Object, String, String> mQuoteTask = null;


    // UI references.
    private EditText mSearchView;
    private View mProgressView;
    private AvailableWorkersAdapter availableWorkersAdapter = null;
    private ListView listView;
    private TextView mQuoteService;
    private TextView mQuoteUnits;
    private TextView mQuotePrice;
    private TextView mQuoteDate;
    private TextView mQuoteTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Construct the data source
        ArrayList<User> arrayOfAvailableWorkers = new ArrayList<User>();
        // Create the adapter to convert the array to views
        availableWorkersAdapter = new AvailableWorkersAdapter(MainActivity.getAppContext(), arrayOfAvailableWorkers);

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mQuoteService = (TextView) rootView.findViewById(R.id.search_results_quote_service);
        mQuoteUnits = (TextView) rootView.findViewById(R.id.search_results_quote_units);
        mQuotePrice = (TextView) rootView.findViewById(R.id.search_results_quote_price);
        mQuoteDate = (TextView) rootView.findViewById(R.id.search_results_quote_date);
        mQuoteTime = (TextView) rootView.findViewById(R.id.search_results_quote_time);
        mProgressView = rootView.findViewById(R.id.search_progress);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.searchResultsList);
        listView.setAdapter(availableWorkersAdapter);
        refreshScreen();

        if (getArguments() != null) {
            String quoteServiceName = getArguments().getString("service");
            String quoteServiceUnits = getArguments().getString("units");
            String quoteServicePrice = getArguments().getString("price");

            String quoteServiceDate = getArguments().getString("date");
            String quoteServiceTime = getArguments().getString("time");


            mQuoteService.setText("Service " + quoteServiceName);
            mQuoteUnits.setText("SQM " + quoteServiceUnits);
            mQuotePrice.setText("R"+quoteServicePrice);
            mQuoteDate.setText("Date " + quoteServiceDate);
            mQuoteTime.setText("Time " + quoteServiceTime);

            getQuote(quoteServiceName);
        }


        return rootView;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            fragmentListener = (FragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentListener");
        }
    }

    /**
     * Attempts to connect to zebenzi server and obtain search results
     */
    public void getQuote(String searchString) {
        if (mQuoteTask != null) {
            return;
        }
        showProgress(true);

        //Build url
        String url = MainActivity.getAppContext().getString(R.string.api_url_quote);

        //Build header
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        //Build body
        JSONObject body = new JSONObject();
        try {
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_id), "1");
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_default_id), "2");
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_work_start_date), "2015-11-02T00:00:00+02:00");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mQuoteTask = new HttpPostTask(MainActivity.getAppContext(), new GetQuoteTaskCompleteListener()).execute(url, header, body, HttpContentTypes.RAW);
    }

    private void refreshScreen() {
        if (availableWorkersAdapter.isEmpty()) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            availableWorkersAdapter.notifyDataSetChanged();
        }
    }

    public class GetQuoteTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String quoteResult, boolean networkError) {
            mQuoteTask = null;
            showProgress(false);

            if (networkError) {
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Job request Response = " + quoteResult);
                Gson gson = new Gson();
                Quote quote = gson.fromJson(quoteResult, Quote.class);

                System.out.println("Quote="+quote);
                Customer.getInstance().setLastQuote(quote);

                //TODO: Update search results list with available workers in quote.
                availableWorkersAdapter.clear();

                ArrayList<User> newWorkers = new ArrayList<User>(Arrays.asList(quote.getAvailableWorkers()));
                availableWorkersAdapter.addAll(newWorkers);
                refreshScreen();
            }

        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mQuoteTask = null;
        }
    }

    public class HireWorkerTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String hireResult, boolean networkError) {
            mHireWorkerTask = null;
            showProgress(false);

            if (networkError) {
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Hire Response = " + hireResult);
                //TODO: Clear search results and take the user to Job history screen.
                fragmentListener.changeFragment(HISTORY);
            }

        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mHireWorkerTask = null;
        }
    }

    public class AvailableWorkersAdapter extends ArrayAdapter<User> {
        public AvailableWorkersAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            User user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_search_results, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.workerName);
            TextView tvContact = (TextView) convertView.findViewById(R.id.workerContactNumber);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.workerAddress);
            TextView tvID = (TextView) convertView.findViewById(R.id.workerID);
            // Populate the data into the template view using the data object
            tvName.setText(user.getFirstName());
            tvContact.setText(user.getUserName());
            tvAddress.setText(user.getUserAddress().toString());
            tvID.setText(user.getId());

            Button hireButton = (Button) convertView.findViewById(R.id.hireButton);
            hireButton.setTag(position);
            hireButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    int position = (Integer) arg0.getTag();
                    User user = getItem(position);
                    System.out.println("Trying to hire: " + user.getFirstName() + " ID=" + user.getId());

                    if (Customer.getInstance().getToken() != null) {
                        hireWorker(Customer.getInstance().getLastQuote().getService().getServiceId(), user.getId());
                    } else {
                        Toast.makeText(MainActivity.getAppContext(), "You need to be logged in to hire a worker", Toast.LENGTH_LONG).show();
                        System.out.println("Cannot hire worker if not logged in.");
                    }
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }

    public void hireWorker(int serviceId, String workerId) {
        JSONObject jsonHireParams = new JSONObject();
        //Build url
        String url = MainActivity.getAppContext().getString(R.string.api_url_hire_worker);

        //Build header
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "bearer " + Customer.getInstance().getToken());

        //Build body
        try {
            jsonHireParams.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_id), serviceId);
            jsonHireParams.put(MainActivity.getAppContext().getString(R.string.api_json_field_worker_id), workerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgress(true);
        mHireWorkerTask = new HttpPostTask(MainActivity.getAppContext(), new HireWorkerTaskCompleteListener()).execute(url, header, jsonHireParams, HttpContentTypes.RAW);
    }


    /**
     * Shows the progress UI and hides the Search form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        listView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}



