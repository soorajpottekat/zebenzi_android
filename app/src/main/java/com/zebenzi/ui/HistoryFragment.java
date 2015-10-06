package com.zebenzi.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zebenzi.job.Job;
import com.zebenzi.network.HttpGetJobHistoryTask;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.HttpPostHireWorkerTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A login screen that offers login via email/password.
 */
public class HistoryFragment extends Fragment {

    public static Context appContext;

    /**
     * Keep track of the history task to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mJobHistoryTask = null;
    private AsyncTask<String, String, String> mHireWorkerTask = null;

    // UI references.
    private View mProgressView;
    private JobHistoryAdapter jobHistoryResultsAdapter = null;
    private ListView listView;
    private ImageView imageView;
    private FragmentListener fragmentListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Construct the data source
        ArrayList<Job> arrayOfJobs = new ArrayList<Job>();

        // Create the adapter to convert the array to views
        jobHistoryResultsAdapter = new JobHistoryAdapter(MainActivity.getAppContext(), arrayOfJobs);


        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mProgressView = rootView.findViewById(R.id.history_progress);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.jobHistoryList);
        listView.setAdapter(jobHistoryResultsAdapter);
//        refreshScreen();

        if (Customer.getInstance().getToken() != null) {
            getJobHistory();
        } else {
            Toast.makeText(MainActivity.getAppContext(), "You need to be logged in to get your history", Toast.LENGTH_LONG).show();
            System.out.println("Cannot get history if not logged in.");
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
                        JSONArray jsonJobHistory = new JSONArray(history);
                        jobHistoryResultsAdapter.clear();
                        ArrayList<Job> jobList = Job.fromJson(jsonJobHistory);
                        jobHistoryResultsAdapter.addAll(jobList);
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
        if (jobHistoryResultsAdapter.isEmpty()) {
            listView.setVisibility(View.GONE);
//            imageView.setVisibility(View.VISIBLE);
        } else {
//            imageView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            jobHistoryResultsAdapter.notifyDataSetChanged();
        }
    }

    public class HireWorkerTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String hireResult, boolean networkError) {
            mHireWorkerTask = null;
            showProgress(false);

            if (networkError){
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            }
            else {
                System.out.println("Hire Response = " + hireResult);
                //TODO: refresh Job history screen?
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
            mHireWorkerTask = null;
        }
    }

    public class JobHistoryAdapter extends ArrayAdapter<Job> {
        public JobHistoryAdapter(Context context, ArrayList<Job> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Job job = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_job_history, parent, false);
            }
            // Lookup view for data population
            TextView tvWorkerName = (TextView) convertView.findViewById(R.id.jobWorkerName);
            TextView tvJobNumber = (TextView) convertView.findViewById(R.id.jobNumber);
            TextView tvJobStatus = (TextView) convertView.findViewById(R.id.jobStatus);
            TextView tvJobStartDate = (TextView) convertView.findViewById(R.id.jobStartDate);
            TextView tvJobCompletedDate = (TextView) convertView.findViewById(R.id.jobCompleteDate);
            TextView tvRating = (TextView) convertView.findViewById(R.id.jobRating);
            TextView tvWorkerMobileNumber = (TextView) convertView.findViewById(R.id.jobWorkerMobileNumber);
            TextView tvJobServiceName = (TextView) convertView.findViewById(R.id.jobServiceName);
            RatingBar rbJobRatingBar = (RatingBar) convertView.findViewById(R.id.jobRatingBar);
            // Populate the data into the template view using the data object

            try {
                tvWorkerName.setText(job.getWorkerName());
                tvJobNumber.setText(job.getJobId());
                tvJobStatus.setText(job.getJobStatus());
                tvJobStartDate.setText(job.getJobStartDate());
                tvJobCompletedDate.setText(job.getJobCompletedDate());
                tvRating.setText(job.getJobRating());
                tvWorkerMobileNumber.setText(job.getWorkerMobileNumber());
                tvJobServiceName.setText(job.getJobServiceName());
                rbJobRatingBar.setRating(Float.valueOf(job.getJobRating()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Set up Hire button, but don't allow hiring if a job is in progress
            //TODO: Rework this logic to actually check if a Worker is currently available or not, instead of checking job status
            Button hireButton = (Button) convertView.findViewById(R.id.hireButton);
            hireButton.setTag(position);

            if (job.isJobInProgress()) {
                hireButton.setVisibility(View.GONE);
            } else {
                hireButton.setVisibility(View.VISIBLE);
            }
            hireButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    int position = (Integer) arg0.getTag();
                    Job job = getItem(position);
                    System.out.println("Trying to hire worker: " + job.getWorkerName());
                    try {
                        hireWorker(job.getWorkerId(), job.getJobServiceId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            // Return the completed view to render on screen
            return convertView;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        listView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void hireWorker(String workerId, String serviceId) {

        mHireWorkerTask = new HttpPostHireWorkerTask(MainActivity.getAppContext(), new HireWorkerTaskCompleteListener()).execute(Customer.getInstance().getToken(), serviceId, workerId);
    }
}



