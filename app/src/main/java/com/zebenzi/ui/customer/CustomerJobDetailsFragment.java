package com.zebenzi.ui.customer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zebenzi.job.JobStatusCodes;
import com.zebenzi.json.model.job.Job;
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.ui.FragmentListener;
import com.zebenzi.ui.FragmentsLookup;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;
import com.zebenzi.users.Customer;
import com.zebenzi.utils.TimeFormat;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.zebenzi.job.JobStatusCodes.*;


/**
 * Fragment to display a single job details with options for completing or cancelling etc.
 */
public class CustomerJobDetailsFragment extends Fragment {

    private FragmentListener fragmentListener;
    private static final String JOB_DETAILS_FRAGMENT_KEY = FragmentsLookup.JOB_DETAILS.getName();
    /**
     * Keep track of the spawned tasks to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mJobDetailsTask = null;
    private AsyncTask<Object, String, String> mSubmitJobRatingTask = null;

    // UI references.
    private View mProgressView;
    private View mLinearLayout;
    private View mRatingLayout;
    private View mButtonsLayout;

    //Job Details
    private TextView mServiceName;
    private TextView mUnits;
    private TextView mPrice;
    private TextView mStartDate;
    //    private TextView mCompleteDate;
    private TextView mJobNumber;
    private TextView mStatus;

    //Worker Details
    private TextView mWorkerFirstName;
    private TextView mWorkerLastName;
    private ImageView mWorkerImage;
    private TextView mWorkerRating;

    //Rating
    private RatingBar mJobRatingBar;
    private EditText mJobComment;


    private int mJobId = 0;
    private Job mJob;

    //This allows us to pass objects into the fragment
    //http://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static CustomerJobDetailsFragment newInstance(int jobId) {
        CustomerJobDetailsFragment fragment = new CustomerJobDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(JOB_DETAILS_FRAGMENT_KEY, jobId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View rootView = inflater.inflate(R.layout.fragment_customer_job_details, container, false);

        //Job Details
        mServiceName = (TextView) rootView.findViewById(R.id.job_details_service);
        mUnits = (TextView) rootView.findViewById(R.id.job_details_units);
        mPrice = (TextView) rootView.findViewById(R.id.job_details_price);
        mStartDate = (TextView) rootView.findViewById(R.id.job_details_date);
        mJobNumber = (TextView) rootView.findViewById(R.id.job_details_job_number);
        mStatus = (TextView) rootView.findViewById(R.id.job_details_job_status);

        //Worker Details
        mWorkerFirstName = (TextView) rootView.findViewById(R.id.job_details_worker_first_name);
        mWorkerLastName = (TextView) rootView.findViewById(R.id.job_details_worker_last_name);
        mWorkerRating = (TextView) rootView.findViewById(R.id.job_details_worker_rating);
        mWorkerImage = (ImageView) rootView.findViewById(R.id.job_details_worker_image);

        mProgressView = rootView.findViewById(R.id.job_details_progress);
        mLinearLayout = rootView.findViewById(R.id.linearLayout);
        mRatingLayout = rootView.findViewById(R.id.job_details_rating_layout);
        mButtonsLayout = rootView.findViewById(R.id.job_details_buttons_layout);

        mJobRatingBar = (RatingBar) rootView.findViewById(R.id.job_details_rating_bar);
        mJobComment = (EditText) rootView.findViewById(R.id.job_details_comment);
        ;


        //Button to cancel job
        Button buttonCancelJob = (Button) rootView.findViewById(R.id.job_details_cancel_job);
        buttonCancelJob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.getAppContext(), "This button not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        //Button to submit rating
        Button buttonSubmitRating = (Button) rootView.findViewById(R.id.job_details_submit_rating);
        buttonSubmitRating.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                submitRating(mJobId, mJobRatingBar.getRating(), mJobComment.getText());
//                Toast.makeText(MainActivity.getAppContext(), "This button not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        mJobId = (int) getArguments().getSerializable(JOB_DETAILS_FRAGMENT_KEY);

        getJob(mJobId);

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
     * Submit rating and comment after job is completed.
     *
     * @param jobId   ID of the job to be rated.
     * @param rating  customer rating for the job.
     * @param comment customer comment for the job.
     */
    public void submitRating(int jobId, float rating, Editable comment) {
        if (mSubmitJobRatingTask != null) {
            return;
        }
        showProgress(true);

        //Build url
        String url = MainActivity.getAppContext().getString(R.string.api_url_submit_job_rating);

        //Build header
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "bearer " + Customer.getInstance().getToken());

        //Build body
        JSONObject body = new JSONObject();
        try {
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_job_id), jobId);
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_job_comment), comment);
            body.put(MainActivity.getAppContext().getString(R.string.api_json_field_job_rating), rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSubmitJobRatingTask = new HttpPostTask(MainActivity.getAppContext(), new SubmitJobRatingTaskCompleteListener()).execute(url, header, body, HttpContentTypes.RAW);
    }

    /**
     * Attempts to connect to zebenzi server and get a quote based on the job request parameters.
     *
     * @param jobId ID of the job to be retrieved from server.
     */
    public void getJob(int jobId) {
        if ((jobId <= 0) || (mJobDetailsTask != null)) {
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
            mJobNumber.setText(Integer.toString(mJob.getJobId()));

            //TODO: Bug in server returning some null data for the Service and Work
            if (mJob.getQuote().getService() != null) {
                mServiceName.setText(mJob.getQuote().getService().getServiceName());
            }
            //TODO: Fix the completed date and job rating when available from server.
//            mCompleteDate.setText(mJob.getQuote().getWorkDate());

            mStatus.setText(mJob.getStatus().getStatusReason());

            JobStatusCodes jsc = findByCode(mJob.getStatus().getStatusCode());

            switch (jsc) {
                //Completed/Cancelled job. No further action to be taken.
//                case WORKER_COMPLETED:
                case WORKER_NO_REPLY:
                case WORKER_DECLINED:
                case CUSTOMER_CANCELLED:
                case DELETED:
                case WORKERS_NOT_AVAILABLE:
                    mRatingLayout.setVisibility(View.GONE);
                    mButtonsLayout.setVisibility(View.GONE);
                    break;

                //Completed but unrated. Customer should still be able to rate the job.
                case WORKER_COMPLETED:
                case WORKER_COMPLETED_UNRATED:
                    //TODO: This is a temporary workaround till server bug is fixed. Currently returns same code for Completed and Completed_Unrated.
                    if (mJob.getStatus().getStatusReason().equalsIgnoreCase("Completed_Unrated")) {
                        mRatingLayout.setVisibility(View.VISIBLE);
                        mButtonsLayout.setVisibility(View.GONE);
                    } else {
                        mRatingLayout.setVisibility(View.GONE);
                        mButtonsLayout.setVisibility(View.GONE);

                    }
                    break;

                //In progress job. Can go into various other states from here.
                case PENDING_ACCEPTANCE:
                case WORKER_ACCEPTED:
                case OTHER_WORKERS_WAITING_ACCEPTANCE:
                case ACCEPTED_SMS_SEND:
                    mRatingLayout.setVisibility(View.GONE);
                    mButtonsLayout.setVisibility(View.VISIBLE);
                    break;
            }

            //Worker Details
            mWorkerFirstName.setText(mJob.getWorker().getFirstName());
            mWorkerLastName.setText(mJob.getWorker().getLastName());
            mWorkerRating.setText(Float.toString(mJob.getWorker().getAverageRating()));
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
                } catch (Exception e) {
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

    public class SubmitJobRatingTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String ratingResult, boolean networkError) {
            mSubmitJobRatingTask = null;
            showProgress(false);

            if (networkError) {
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Job rating Response = " + ratingResult);

                //If rating successfully submitted, then refresh the screen
                getJob(mJobId);

            }

        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mSubmitJobRatingTask = null;
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



