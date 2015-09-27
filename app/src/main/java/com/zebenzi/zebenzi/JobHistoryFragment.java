package com.zebenzi.zebenzi;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zebenzi.network.HttpGetJobHistoryTask;
import com.zebenzi.network.HttpPostHireWorkerTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;
import com.zebenzi.users.Job;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * A login screen that offers login via email/password.
 */
public class JobHistoryFragment extends Fragment {

    public static Context appContext;

    /**
     * Keep track of the history task to ensure we can cancel it if requested.
     */
    private AsyncTask<String, String, JSONArray> mJobHistoryTask = null;
    private AsyncTask<String, String, String> mHireWorkerTask = null;

    // UI references.
    private View mProgressView;
    private JobHistoryAdapter jobHistoryResultsAdapter = null;
    private ListView listView;
    private ImageView imageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Construct the data source
        ArrayList<Job> arrayOfJobs = new ArrayList<Job>();

        // Create the adapter to convert the array to views
        jobHistoryResultsAdapter = new JobHistoryAdapter(MainActivity.getAppContext(), arrayOfJobs);


        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.jobHistoryList);
        listView.setAdapter(jobHistoryResultsAdapter);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        refreshScreen();

        if (Customer.getInstance().getToken() != null) {
            getJobHistory();
        } else {
            System.out.println("Cannot get history if not logged in.");
        }

        return rootView;

    }




    /**
     * Attempts to connect to zebenzi server and obtain job history
     */
    public void getJobHistory() {
        if (mJobHistoryTask != null) {
            return;
        }

//            showProgress(true);
//        mSearchString = searchString;
        mJobHistoryTask = new HttpGetJobHistoryTask(MainActivity.getAppContext(), new JobHistoryTaskCompleteListener()).execute(Customer.getToken());
    }

    public class JobHistoryTaskCompleteListener implements IAsyncTaskListener<JSONArray>{
        @Override
        public void onAsyncTaskComplete(JSONArray jsonJobHistory) {
            mJobHistoryTask = null;
//            showProgress(false);

            if (jsonJobHistory != null) {
                jobHistoryResultsAdapter.clear();
                ArrayList<Job> jobList = Job.fromJson(jsonJobHistory);
                jobHistoryResultsAdapter.addAll(jobList);
            } else {
            //TODO: What to do if no job history?
            }
            refreshScreen();
        }

        @Override
        public void onAsyncTaskCancelled() {
            mJobHistoryTask = null;
        }
    }

    private void refreshScreen() {
        if (jobHistoryResultsAdapter.isEmpty()){
            listView.setVisibility(View.GONE);
//            imageView.setVisibility(View.VISIBLE);
        }
        else
        {
//            imageView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            jobHistoryResultsAdapter.notifyDataSetChanged();
        }
    }

    public class HireWorkerTaskCompleteListener implements IAsyncTaskListener<String>{
        @Override
        public void onAsyncTaskComplete(String hireResult) {
            mHireWorkerTask = null;
//            showProgress(false);
            System.out.println("Hire Response = " + hireResult);
            //TODO: refresh Job history screen?
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
            Button hireButton = (Button)  convertView.findViewById(R.id.hireButton);
            hireButton.setTag(position);

            if (job.isJobInProgress()) {
                hireButton.setVisibility(View.GONE);
            } else {
                hireButton.setVisibility(View.VISIBLE);
            }
            hireButton.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    int position=(Integer)arg0.getTag();
                    Job job = getItem(position);
                    System.out.println("Trying to hire worker: " + job.getWorkerName());
                    try{
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

    public void hireWorker(String workerId, String serviceId) {

        mHireWorkerTask = new HttpPostHireWorkerTask(MainActivity.getAppContext(), new HireWorkerTaskCompleteListener()).execute(Customer.getInstance().getToken(), serviceId, workerId);
    }
}



