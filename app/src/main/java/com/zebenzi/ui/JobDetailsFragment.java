package com.zebenzi.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zebenzi.json.model.job.Job;
import com.zebenzi.json.model.quote.Quote;
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;
import com.zebenzi.utils.TimeFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


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
    private AsyncTask<Object, String, String> mQuoteTask = null;

    // UI references.
    private View mProgressView;
    private ListView listView;

    //Job Details
    private TextView mServiceName;
    private TextView mUnits;
    private TextView mPrice;
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mCompleteDate;
    private TextView mJobRating;
    private TextView mStatus;

    //Worker Details
    private TextView mWorkerFirstName;
    private TextView mWorkerLastName;
    private TextView mWorkerMobile;
    private ImageView mWorkerImage;
    private TextView mWorkerRating;



    private Quote quote;
    private int mJobId;
    private Job mJob;

    //This allows us to pass objects into the fragment
    //http://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static JobDetailsFragment newInstance(Job job) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(JOB_DETAILS_FRAGMENT_KEY, job);
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
        mCompleteDate = (TextView) rootView.findViewById(R.id.job_details_complete_date);
        mJobRating = (TextView) rootView.findViewById(R.id.job_details_job_rating);
        mStatus = (TextView) rootView.findViewById(R.id.job_details_job_status);

        //Worker Details
        mWorkerFirstName = (TextView) rootView.findViewById(R.id.job_details_worker_first_name);
        mWorkerLastName = (TextView) rootView.findViewById(R.id.job_details_worker_last_name);
        mWorkerMobile = (TextView) rootView.findViewById(R.id.job_details_worker_mobile_number);
        mWorkerRating = (TextView) rootView.findViewById(R.id.job_details_worker_rating);
        mWorkerImage = (ImageView) rootView.findViewById(R.id.job_details_worker_image);

        mProgressView = rootView.findViewById(R.id.job_details_progress);

        mJob = (Job) getArguments().getSerializable(JOB_DETAILS_FRAGMENT_KEY);
//        System.out.println("Bundle=" + b.toString());

        refreshScreen();


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
        if (mJob != null) {
            mPrice.setText(Integer.toString(mJob.getQuote().getPrice()));
            mStartDate.setText(TimeFormat.getPrettyDate(mJob.getQuote().getWorkDate()));
            mStartTime.setText(TimeFormat.getPrettyDate(mJob.getQuote().getWorkDate()));

            //TODO: Bug in server returning some null data for the Service and Work
            if (mJob.getQuote().getService() != null) {
                mServiceName.setText(mJob.getQuote().getService().getServiceName());
//                mUnits.setText(mJob.getQuote().getService().getServiceUnit().getName());
            }
            //TODO: Fix the completed date and job rating when available from server.
            mCompleteDate.setText(mJob.getUpdatedDate());

//            mJobRating.setText("");

            mStatus.setText(mJob.getStatus().getStatusReason());

            //Worker Details
            mWorkerFirstName.setText(mJob.getWorker().getFirstName());
            mWorkerLastName.setText(mJob.getWorker().getLastName());
            mWorkerMobile.setText(mJob.getWorker().getUserName());
            mWorkerRating.setText("2.5");
            Picasso.with(MainActivity.getAppContext()).load(mJob.getWorker().getImageUrl()).into(mWorkerImage);
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


    /**
     * Shows the progress UI and hides the Search form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        listView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}



