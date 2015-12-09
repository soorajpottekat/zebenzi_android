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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zebenzi.job.JobRequest;
import com.zebenzi.json.model.job.Job;
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
import java.util.Iterator;

import static com.zebenzi.ui.FragmentsLookup.HISTORY;
import static com.zebenzi.ui.FragmentsLookup.JOB_DETAILS;


/**
 * Fragment for customer to see quote from server and list of available workers.
 * Customer can view details of the workers and hire via button click.
 *
 * */
public class QuoteFragment extends Fragment {
    private static final String QUOTE_FRAGMENT_KEY = FragmentsLookup.QUOTE.getName();

    private FragmentListener fragmentListener;
    /**
     * Keep track of the spawned tasks to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mQuoteTask = null;
    private AsyncTask<Object, String, String> mHireWorkerTask = null;

    // UI references.
    private View mProgressView;
    private AvailableWorkersAdapter availableWorkersAdapter = null;
    private ListView listView;
    private TextView mQuoteService;
    private TextView mQuoteUnits;
    private TextView mQuotePrice;
    private TextView mQuoteDate;
    private TextView mQuoteTime;
    private Quote quote;

    //This allows us to pass objects into the fragment
    //http://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static QuoteFragment newInstance(JobRequest request) {
        QuoteFragment fragment = new QuoteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(QUOTE_FRAGMENT_KEY, request);
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quote, container, false);
        mQuoteService = (TextView) rootView.findViewById(R.id.search_results_quote_service);
        mQuoteUnits = (TextView) rootView.findViewById(R.id.search_results_quote_units);
        mQuotePrice = (TextView) rootView.findViewById(R.id.search_results_quote_price);
        mQuoteDate = (TextView) rootView.findViewById(R.id.search_results_quote_date);
        mQuoteTime = (TextView) rootView.findViewById(R.id.search_results_quote_time);
        mProgressView = rootView.findViewById(R.id.search_progress);

        // Set up the available workers list view
        ArrayList<User> arrayOfAvailableWorkers = new ArrayList<User>();
        availableWorkersAdapter = new AvailableWorkersAdapter(MainActivity.getAppContext(), arrayOfAvailableWorkers);
        listView = (ListView) rootView.findViewById(R.id.availableWorkersList);
        listView.setAdapter(availableWorkersAdapter);
        refreshScreen();

        //If there is a valid job request stored, update the ui
//        JobRequest jr = Customer.getInstance().getCurrentJobRequest();

        JobRequest jr = (JobRequest) getArguments().getSerializable(QUOTE_FRAGMENT_KEY);

        if (jr != null) {
            int id = jr.getServiceId();
            int units = jr.getServiceDefaultId();
            String date = jr.getDateTime();

            getQuote(id, units, date);
        }
        else
        {
            Toast.makeText(MainActivity.getAppContext(), "No job request to quote", Toast.LENGTH_SHORT).show();
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
     * Attempts to connect to zebenzi server and get a quote based on the job request parameters.
     * @param serviceId ID of the job request.
     * @param serviceDefaultId See ServiceDefault class for explanation.
     * @param dateTime Date and time of job request.
     *
     */
    public void getQuote(int serviceId, int serviceDefaultId, String dateTime ) {
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
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_id), serviceId);
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_default_id), serviceDefaultId);
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_work_start_date), dateTime);
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

        if (quote != null) {
            mQuoteService.setText(quote.getService().getServiceName());
            mQuoteUnits.setText(quote.getWork().getDefaultValue() + " " + quote.getService().getServiceUnit().getName());
            mQuotePrice.setText("R" + quote.getPrice());
            mQuoteDate.setText("Date " + quote.getPrettyDate());
            mQuoteTime.setText("Time " + quote.getPrettyTime());
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
                quote = gson.fromJson(quoteResult, Quote.class);

                try {
                    System.out.println("Quote=" + quote);
                    Customer.getInstance().setLastQuote(quote);

                    availableWorkersAdapter.clear();
                    ArrayList<User> newWorkers = new ArrayList<User>(Arrays.asList(quote.getAvailableWorkers()));
                    availableWorkersAdapter.addAll(removeDirtyWorkers(newWorkers));
                    refreshScreen();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
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

                Gson gson = new Gson();
                Job j = gson.fromJson(hireResult, Job.class);


                fragmentListener.changeFragment(HISTORY, null);

//                fragmentListener.changeFragment(JOB_DETAILS, j);
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
            TextView tvFirstName = (TextView) convertView.findViewById(R.id.workerFirstName);
            TextView tvLastName = (TextView) convertView.findViewById(R.id.workerLastName);
            TextView tvContact = (TextView) convertView.findViewById(R.id.workerContactNumber);
            TextView tvAddress = (TextView) convertView.findViewById(R.id.workerAddress);
            TextView tvID = (TextView) convertView.findViewById(R.id.workerID);
            ImageView img = (ImageView) convertView.findViewById(R.id.workerImage);

            try {
                // Populate the data into the template view using the data object
                tvFirstName.setText(user.getFirstName());
                tvLastName.setText(user.getLastName());
                tvContact.setText(user.getUserName());
                tvAddress.setText(user.getUserAddress().toString());
                tvID.setText(user.getId());
                Picasso.with(MainActivity.getAppContext()).load(user.getImageUrl()).into(img);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            Button hireButton = (Button) convertView.findViewById(R.id.hireButton);
            hireButton.setTag(position);
            hireButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    int position = (Integer) arg0.getTag();
                    User user = getItem(position);
                    System.out.println("Trying to hire: " + user.getFirstName() + " ID=" + user.getId());

                    if (Customer.getInstance().getToken() != null) {
                        hireWorker(Customer.getInstance().getLastQuote().getQuoteId(), user.getId());
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

    public void hireWorker(int quoteId, String workerId) {
        //Build url
        String url = MainActivity.getAppContext().getString(R.string.api_url_hire_worker);

        //Build header
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "bearer " + Customer.getInstance().getToken());

        //Build body
        JSONObject body = new JSONObject();
        try {
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_quote_id), quoteId);
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_worker_id), workerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgress(true);
        mHireWorkerTask = new HttpPostTask(MainActivity.getAppContext(), new HireWorkerTaskCompleteListener()).execute(url, header, body, HttpContentTypes.RAW);
    }


    /**
     * Shows the progress UI and hides the Search form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        listView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    //Handle bad worker data coming from server
    private ArrayList<User> removeDirtyWorkers(ArrayList<User>  availableWorkersList) {

        for (Iterator<User> iterator = availableWorkersList.iterator(); iterator.hasNext();) {
            User worker = iterator.next();
            if (worker == null){
                //delete this worker from the display as it has corrupted data.
                iterator.remove();
            }
        }
        return availableWorkersList;
    }
}



