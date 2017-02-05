package com.zebenzi.ui.supplier;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zebenzi.json.model.job.JobRating;
import com.zebenzi.json.model.user.User;
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.ui.FragmentListener;
import com.zebenzi.ui.FragmentsLookup;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;
import com.zebenzi.users.Customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Fragment for customer to see worker history and reviews.
 */
public class SupplierAccountFragment extends Fragment {
    private static final String WORKER_PROFILE_FRAGMENT_KEY = FragmentsLookup.WORKER_PROFILE.getName();

    private FragmentListener fragmentListener;
    /**
     * Keep track of the spawned tasks to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mWorkerRatingsTask = null;

    private User mWorker;

    // UI references.
    private View mProgressView;
    private SupplierAccountAdapter workerJobRatingsAdapter = null;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mRating;
    private ImageView mImage;
    private JobRating[] jobRatings;

    //This allows us to pass objects into the fragment
    //http://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static SupplierAccountFragment newInstance(User worker) {
        SupplierAccountFragment fragment = new SupplierAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(WORKER_PROFILE_FRAGMENT_KEY, worker);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_supplier_account, container, false);
        mFirstName = (TextView) rootView.findViewById(R.id.worker_profile_first_name);
        mLastName = (TextView) rootView.findViewById(R.id.worker_profile_last_name);
        mRating = (TextView) rootView.findViewById(R.id.worker_profile_rating);
        mImage = (ImageView) rootView.findViewById(R.id.worker_profile_image);
        mProgressView = rootView.findViewById(R.id.search_progress);

        // Set up the worker reviews list view
        ArrayList<JobRating> arrayOfJobRatings = new ArrayList<JobRating>();

        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.worker_profile_comments_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.getAppContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        // specify an adapter (see also next example)
        workerJobRatingsAdapter = new SupplierAccountAdapter(arrayOfJobRatings, SupplierAccountFragment.this);
        recList.setAdapter(workerJobRatingsAdapter);
        refreshScreen();

        mWorker = (User) getArguments().getSerializable(WORKER_PROFILE_FRAGMENT_KEY);

        if (mWorker != null) {
            mFirstName.setText(mWorker.getFirstName());
            mLastName.setText(mWorker.getLastName());
            mRating.setText(Float.toString(mWorker.getAverageRating()));
            Picasso.with(MainActivity.getAppContext()).load(mWorker.getImageUrl()).into(mImage);

            getJobRatings(mWorker.getId());
        } else {
            Toast.makeText(MainActivity.getAppContext(), "No worker id to obtain ratings", Toast.LENGTH_SHORT).show();
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
     * Attempts to connect to zebenzi server and get a job ratings based on the worker id.
     *
     * @param workerId ID of the worker for whom to retrieve ratings.
     */
    public void getJobRatings(String workerId) {
        if (mWorkerRatingsTask != null) {
            return;
        }
        showProgress(true);

        //Build url
        String url = MainActivity.getAppContext().getString(R.string.api_url_job_ratings_by_supplier_id) + workerId;

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

        mWorkerRatingsTask = new HttpGetTask(MainActivity.getAppContext(), new GetJobRatingsTaskCompleteListener()).execute(url, header, null, HttpContentTypes.RAW);
    }

    private void refreshScreen() {

        workerJobRatingsAdapter.notifyDataSetChanged();

        if (mWorker != null) {
            mFirstName.setText(mWorker.getFirstName());
            mLastName.setText(mWorker.getLastName());
            mRating.setText(Float.toString(mWorker.getAverageRating()));
            Picasso.with(MainActivity.getAppContext()).load(mWorker.getImageUrl()).into(mImage);

        }
    }

    public class GetJobRatingsTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String jobRatingsResult, boolean networkError) {
            mWorkerRatingsTask = null;
            showProgress(false);

            if (networkError) {
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            } else {
                System.out.println("Job Ratings request Response = " + jobRatingsResult);
                Gson gson = new Gson();
                jobRatings = gson.fromJson(jobRatingsResult, JobRating[].class);

                try {
                    System.out.println("Job Ratings=" + jobRatings);

                    if (jobRatings != null) {
                        workerJobRatingsAdapter.clear();
                        ArrayList<JobRating> ratingList = new ArrayList<>(Arrays.asList(jobRatings));
                        workerJobRatingsAdapter.addAll(ratingList);
                        refreshScreen();
                    } else {
                        //If no ratings available, only show profile
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mWorkerRatingsTask = null;
        }
    }



    /**
     * Shows the progress UI and hides the Search form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//        listView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    //Handle bad worker data coming from server
    private ArrayList<User> removeDirtyWorkers(ArrayList<User> availableWorkersList) {

        for (Iterator<User> iterator = availableWorkersList.iterator(); iterator.hasNext(); ) {
            User worker = iterator.next();
            if (worker == null) {
                //delete this worker from the display as it has corrupted data.
                iterator.remove();
            }
        }
        return availableWorkersList;
    }
}



