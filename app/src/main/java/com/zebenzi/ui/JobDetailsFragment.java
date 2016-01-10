package com.zebenzi.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;
import com.zebenzi.utils.TimeFormat;

import java.util.HashMap;

import static com.zebenzi.ui.FragmentsLookup.QUOTE;


/**
 * Fragment to display a single job details with options for completing or cancelling etc.
 *
 * */
public class JobDetailsFragment extends Fragment {

    private FragmentListener fragmentListener;
    private static final String JOB_DETAILS_FRAGMENT_KEY = FragmentsLookup.JOB_DETAILS.getName();
    /**
     * Keep track of the spawned tasks to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mJobDetailsTask = null;

    // UI references.
    private View mProgressView;
    private View mLinearLayout;

    //Job Details
    private TextView mServiceName;
    private TextView mUnits;
    private TextView mPrice;
    private TextView mStartDate;
    private TextView mStartTime;
//    private TextView mCompleteDate;
    private TextView mJobRating;
    private TextView mJobNumber;
    private TextView mStatus;

    //Worker Details
    private TextView mWorkerFirstName;
    private TextView mWorkerLastName;
    private TextView mWorkerMobile;
    private ImageView mWorkerImage;
    private TextView mWorkerRating;



    private int mJobId = 0;
    private Job mJob;

    //This allows us to pass objects into the fragment
    //http://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static JobDetailsFragment newInstance(int jobId) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(JOB_DETAILS_FRAGMENT_KEY, jobId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View rootView = inflater.inflate(R.layout.fragment_job_details, container, false);

        //Job Details
        mServiceName = (TextView) rootView.findViewById(R.id.job_details_service);
        mUnits = (TextView) rootView.findViewById(R.id.job_details_units);
        mPrice = (TextView) rootView.findViewById(R.id.job_details_price);
        mStartDate = (TextView) rootView.findViewById(R.id.job_details_date);
        mStartTime = (TextView) rootView.findViewById(R.id.job_details_time);
//        mCompleteDate = (TextView) rootView.findViewById(R.id.job_details_complete_date);
//        mJobRating = (TextView) rootView.findViewById(R.id.job_details_job_rating);
        mJobNumber = (TextView) rootView.findViewById(R.id.job_details_job_number);
        mStatus = (TextView) rootView.findViewById(R.id.job_details_job_status);

        //Worker Details
        mWorkerFirstName = (TextView) rootView.findViewById(R.id.job_details_worker_first_name);
        mWorkerLastName = (TextView) rootView.findViewById(R.id.job_details_worker_last_name);
        mWorkerMobile = (TextView) rootView.findViewById(R.id.job_details_worker_mobile_number);
        mWorkerRating = (TextView) rootView.findViewById(R.id.job_details_worker_rating);
        mWorkerImage = (ImageView) rootView.findViewById(R.id.job_details_worker_image);

        mProgressView = rootView.findViewById(R.id.job_details_progress);
        mLinearLayout = rootView.findViewById(R.id.linearLayout);

        //Button to cancel job
        Button buttonCancelJob = (Button) rootView.findViewById(R.id.job_details_cancel_job);
        buttonCancelJob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.getAppContext(), "This button not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        //Button to complete job
        Button buttonCompleteJob = (Button) rootView.findViewById(R.id.job_details_complete_job);
        buttonCompleteJob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.getAppContext(), "This button not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        mJobId = (int) getArguments().getSerializable(JOB_DETAILS_FRAGMENT_KEY);

        if (mJobId > 0){
            getJob(mJobId);
        }

//        refreshScreen();


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
     * Attempts to connect to zebenzi server and get a quote based on the job request parameters.
     * @param jobId ID of the job to be retrieved from server.
     *
     */
    public void getJob(int jobId) {
        if (mJobDetailsTask != null) {
            return;
        }
        showProgress(true);

        //Build url
        String url = MainActivity.getAppContext().getString(R.string.api_url_job_by_id) + Integer.toString(jobId);

        //Build header
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "bearer " + Customer.getInstance().getToken());

        //Build body
//        JSONObject body = new JSONObject();
//        try {
//            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_id), serviceId);
//            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_service_default_id), serviceDefaultId);
//            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_work_start_date), dateTime);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        mJobDetailsTask = new HttpGetTask(MainActivity.getAppContext(), new GetJobDetailsTaskCompleteListener()).execute(url, header, null, HttpContentTypes.RAW);
    }

    private void refreshScreen() {
        if (mJob != null) {
            mPrice.setText("R" + Integer.toString(mJob.getQuote().getPrice()));
            mStartDate.setText(TimeFormat.getPrettyDate(mJob.getQuote().getWorkDate()));
            mStartTime.setText(TimeFormat.getPrettyTime(mJob.getQuote().getWorkDate()));
            mJobNumber.setText(Integer.toString(mJob.getJobId()));

            //TODO: Bug in server returning some null data for the Service and Work
            if (mJob.getQuote().getService() != null) {
                mServiceName.setText(mJob.getQuote().getService().getServiceName());
            }
            //TODO: Fix the completed date and job rating when available from server.
//            mCompleteDate.setText(mJob.getQuote().getWorkDate());

//            mJobRating.setText("");

            mStatus.setText(mJob.getStatus().getStatusReason());

            //Worker Details
            mWorkerFirstName.setText(mJob.getWorker().getFirstName());
            mWorkerLastName.setText(mJob.getWorker().getLastName());
            mWorkerMobile.setText(mJob.getWorker().getUserName());
            mWorkerRating.setText("3.5");
            Picasso.with(MainActivity.getAppContext()).load(mJob.getWorker().getImageUrl()).into(mWorkerImage);
        }
    }

    public class GetJobDetailsTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String jobDetailsResult, boolean networkError) {
            mJobDetailsTask = null;
            showProgress(false);

            if (networkError) {
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Job request Response = " + jobDetailsResult);
                Gson gson = new Gson();
                mJob = gson.fromJson(jobDetailsResult, Job.class);

                try {
                    System.out.println("mJob=" + mJob);

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
            mJobDetailsTask = null;
        }
    }


    /**
     * Shows the progress UI and hides the Search form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);


    }
}



