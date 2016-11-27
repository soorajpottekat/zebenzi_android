package com.zebenzi.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zebenzi.job.JobRequest;
import com.zebenzi.json.model.service.Service;
import com.zebenzi.json.model.service.ServiceDefaults;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.utils.DatePickerFragment;
import com.zebenzi.utils.TimePickerFragment;
import com.zebenzi.utils.ZebenziException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.zebenzi.ui.FragmentsLookup.QUOTE;

//TODO: Display network error if user is not connected to network and allow refreshing of page when reconnected.

/**
 * Fragment for customer to select parameters for a new job.
 * serviceSpinner and serviceSpinner values must be obtained from the zebenzi server first.
 *
 * */
public class HomeFragmentWorker extends Fragment {

    private FragmentListener fragmentListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_supplier_home, container, false);


        //Button to go to Job Requests screen.
        Button buttonSupplierJobRequests = (Button) rootView.findViewById(R.id.supplier_job_requests);
        buttonSupplierJobRequests.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                try {
                    //Change to supplier job requests screen
//                    fragmentListener.changeFragment();
                    Toast.makeText(MainActivity.getAppContext(), "Change to supplier job requests screen", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.getAppContext(), "An error occurred with supplier job requests screen.", Toast.LENGTH_SHORT).show();
                    System.out.println("An error occurred with supplier job requests screen.");
                    e.printStackTrace();
                }
            }
        });


        //Button to go to Job Requests screen.
        Button buttonSupplierJobsInProgress = (Button) rootView.findViewById(R.id.supplier_jobs_in_progress);
        buttonSupplierJobsInProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                try {
                    //Change to supplier job requests screen
//                    fragmentListener.changeFragment();
                    Toast.makeText(MainActivity.getAppContext(), "Change to supplier jobs in progress screen", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.getAppContext(), "An error occurred with jobs in progress screen.", Toast.LENGTH_SHORT).show();
                    System.out.println("An error occurred with jobs in progress screen.");
                    e.printStackTrace();
                }
            }
        });

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

}



