package com.zebenzi.ui.customer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zebenzi.json.model.job.Job;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.ui.FragmentListener;
import com.zebenzi.ui.FragmentsLookup;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;
import com.zebenzi.ui.RecyclerItemClickListener;
import com.zebenzi.users.Customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Fragment for customer to view history of zebenzi jobs.
 *
 * */
public class CustomerHistoryFragment extends Fragment {

    /**
     * Keep track of the history task to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mJobHistoryTask = null;

    // UI references.
    private View mProgressView;
    private CustomerHistoryAdapter jobHistoryResultsAdapter = null;
    private ListView listView;
    private FragmentListener fragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Construct the data source
        ArrayList<Job> arrayOfJobs = new ArrayList<Job>();

        // Create the adapter to convert the array to views
//        jobHistoryResultsAdapter = new JobHistoryAdapter(MainActivity.getAppContext(), arrayOfJobs);

        View rootView = inflater.inflate(R.layout.fragment_customer_history, container, false);
        mProgressView = rootView.findViewById(R.id.history_progress);

        // Attach the adapter to a ListView
//        listView = (ListView) rootView.findViewById(R.id.history_list);
//        listView.setAdapter(jobHistoryResultsAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Job job = jobHistoryResultsAdapter.getItem(position);
//                System.out.println("Job ID =" + job.getJobId() + " Job Quote=" + job.getQuote() + " Worker=" + job.getWorker().getFirstName());
//                Toast.makeText(MainActivity.getAppContext(), "Job Id = " + job.getJobId(), Toast.LENGTH_LONG).show();
//                fragmentListener.changeFragment(JOB_DETAILS, Integer.toString(job.getJobId()));
//
//            }
//        });

        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.history_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.getAppContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        // specify an adapter (see also next example)
        jobHistoryResultsAdapter = new CustomerHistoryAdapter(arrayOfJobs);
        recList.setAdapter(jobHistoryResultsAdapter);
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.getAppContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Job job = jobHistoryResultsAdapter.getJobFromPosition(position);
                        System.out.println("Job ID =" + job.getJobId() + " Job Quote=" + job.getQuote() + " Worker=" + job.getWorker().getFirstName());
                        Toast.makeText(MainActivity.getAppContext(), "Job Id = " + job.getJobId(), Toast.LENGTH_LONG).show();
                        fragmentListener.changeFragment(FragmentsLookup.JOB_DETAILS, Integer.toString(job.getJobId()));
                    }
                })
        );

        if (Customer.getInstance().getToken() != null) {
            getJobHistory();
        } else {
            Toast.makeText(MainActivity.getAppContext(), "You need to be logged in to get your history", Toast.LENGTH_LONG).show();
            System.out.println("Cannot get history if not logged in.");
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Activity activity = (Activity) context;
            fragmentListener = (FragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    /**
     * Attempts to connect to zebenzi server and obtain job history
     */
    public void getJobHistory() {

        if (mJobHistoryTask == null) {
            //Build url
            String url = MainActivity.getAppContext().getString(R.string.api_url_job_history);

            //Build header
            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");
            header.put("Authorization", "bearer " + Customer.getInstance().getToken());

            showProgress(true);
            mJobHistoryTask = new HttpGetTask(MainActivity.getAppContext(), new JobHistoryTaskCompleteListener()).execute(url, header, null);
        }
    }

    public class JobHistoryTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String history, boolean networkError) {
            mJobHistoryTask = null;
            showProgress(false);

            if (networkError){
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            }
            else {
                if (history != null) {
                    try {
                        //TODO: Handle invalid request returned here. User is presented with blank screen.
                        jobHistoryResultsAdapter.clear();
                        Gson gson = new Gson();
                        Job[] jobHistory = gson.fromJson(history, Job[].class);

                        //Remove bad entries. ie. user, worker or quote is null. This happened
                        // during development, and we need to avoid displaying incorrect data.
                        ArrayList<Job> jobList = new ArrayList<>(Arrays.asList(jobHistory));
                        jobHistoryResultsAdapter.addAll(jobList);
                        refreshScreen();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.getAppContext(), MainActivity.getAppContext().getString(R.string.no_history_found), Toast.LENGTH_LONG).show();
                    //TODO: What to do if no job history?
                }
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mJobHistoryTask = null;
        }
    }



    private void refreshScreen() {
//        if (jobHistoryResultsAdapter.isEmpty()) {
//            listView.setVisibility(View.GONE);
//        } else {
//            listView.setVisibility(View.VISIBLE);
//            jobHistoryResultsAdapter.notifyDataSetChanged();
//        }

        jobHistoryResultsAdapter.notifyDataSetChanged();
    }

//    public class JobHistoryAdapter extends ArrayAdapter<Job> {
//        public JobHistoryAdapter(Context context, ArrayList<Job> users) {
//            super(context, 0, users);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // Get the data item for this position
//            Job job = getItem(position);
//            // Check if an existing view is being reused, otherwise inflate the view
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_customer_job_history, parent, false);
//            }
//            // Lookup view for data population
//            TextView tvJobPrice = (TextView) convertView.findViewById(R.id.list_row_job_history_price);
//            TextView tvWorkerFirstName = (TextView) convertView.findViewById(R.id.list_row_job_history_worker_first_name);
//            TextView tvWorkerLastName = (TextView) convertView.findViewById(R.id.list_row_job_history_worker_last_name);
//            TextView tvJobNumber = (TextView) convertView.findViewById(R.id.list_row_job_history_job_number);
//            TextView tvJobStatus = (TextView) convertView.findViewById(R.id.list_row_job_history_job_status);
//            TextView tvJobDate = (TextView) convertView.findViewById(R.id.list_row_job_history_job_date);
//            TextView tvJobTime = (TextView) convertView.findViewById(R.id.list_row_job_history_job_time);
//            TextView tvRating = (TextView) convertView.findViewById(R.id.list_row_job_history_job_rating);
//            TextView tvWorkerMobileNumber = (TextView) convertView.findViewById(R.id.list_row_job_history_worker_mobile);
//            TextView tvJobServiceName = (TextView) convertView.findViewById(R.id.list_row_job_history_service_name);
//            RatingBar rbJobRatingBar = (RatingBar) convertView.findViewById(R.id.list_row_job_history_job_rating_bar);
//            ImageView img = (ImageView) convertView.findViewById(R.id.list_row_worker_image);
//
//            //TODO: Should we handle errors in the results? Eg. Null data. Or should the server worry about his?
//
//            try {
//                tvJobPrice.setText("R" + Integer.toString(job.getQuote().getPrice()));
//                tvWorkerFirstName.setText(job.getWorker().getFirstName());
//                tvWorkerLastName.setText(job.getWorker().getLastName());
//                tvJobNumber.setText(Integer.toString(job.getJobId()));
//                if (job.getStatus() != null) {
//                    tvJobStatus.setText(job.getStatus().getStatusReason());
//                }
//                tvJobDate.setText(TimeFormat.getPrettyDate(job.getQuote().getWorkDate()));
//                tvJobTime.setText(TimeFormat.getPrettyTime(job.getQuote().getWorkDate()));
////                tvRating.setText(Float.valueOf(job.getUser().);
//                tvWorkerMobileNumber.setText(job.getWorker().getUserName());
//
//                if (job.getQuote().getService() != null) {
//                    tvJobServiceName.setText(job.getQuote().getService().getServiceName());
//                }
//                float rating= (float) 3.5;
//                rbJobRatingBar.setRating(rating);
//                Picasso.with(MainActivity.getAppContext()).load(job.getWorker().getImageUrl()).into(img);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Return the completed view to render on screen
//            return convertView;
//        }
//    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//        listView.setVisibility(show ? View.GONE : View.VISIBLE);
    }



}