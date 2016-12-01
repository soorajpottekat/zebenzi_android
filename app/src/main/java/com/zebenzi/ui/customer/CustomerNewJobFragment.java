package com.zebenzi.ui.customer;

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
import com.zebenzi.json.model.service.Service;
import com.zebenzi.json.model.service.ServiceDefaults;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.job.JobRequest;
import com.zebenzi.ui.FragmentListener;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;
import com.zebenzi.users.Customer;
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
public class CustomerNewJobFragment extends Fragment {

    private FragmentListener fragmentListener;
    private View mProgressView;
    private Button jobDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMin;
    private Button jobTime;
    GregorianCalendar mJobDateTime;
    private Spinner serviceSpinner;
    private Spinner unitsSpinner;
    ArrayList<String> unitsSpinnerArray = new ArrayList<String>();
    ArrayAdapter<String> unitsSpinnerArrayAdapter;
    private TextView mUnitsLabel;
    private TextView mDateLabel;
    private AsyncTask<Object, String, String> mGetServicesTask;

    ArrayAdapter<String> serviceSpinnerArrayAdapter;
    ArrayList<String> serviceSpinnerArray;
    Service[] services;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialise variables for date time
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        mHour = 8;
        mMin = 0;

        View rootView = inflater.inflate(R.layout.fragment_new_job, container, false);

        mProgressView = rootView.findViewById(R.id.new_job_progress);

        mUnitsLabel = (TextView) rootView.findViewById(R.id.new_job_units_label);
        mDateLabel =  (TextView) rootView.findViewById(R.id.new_job_date_label);

        //Select date via datepicker fragment
        jobDate = (Button) rootView.findViewById(R.id.new_job_date);
        jobTime = (Button) rootView.findViewById(R.id.new_job_time);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        mJobDateTime = new GregorianCalendar();
        String date = df.format(mJobDateTime.getTime());
        jobDate.setText(date);
        jobDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        df = new SimpleDateFormat("HH:mm");
        String time = df.format(mJobDateTime.getTime());
        jobTime.setText(time);
        jobTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        //Spinner for list of services
        serviceSpinner = (Spinner) rootView.findViewById(R.id.new_job_service_name);
        serviceSpinnerArray = new ArrayList<String>() {
            {
                    add(MainActivity.getAppContext().getString(R.string.no_connection));
            }
        };
        serviceSpinnerArrayAdapter = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_item, serviceSpinnerArray);
        serviceSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        serviceSpinner.setAdapter(serviceSpinnerArrayAdapter);
        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnits();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //Spinner for number of units
        unitsSpinner = (Spinner) rootView.findViewById(R.id.new_job_units_spinner);
        unitsSpinner.setEnabled(false);

        //Button to save the job request and change to Quote fragment to request quote
        Button buttonGetQuote = (Button) rootView.findViewById(R.id.new_job_get_quote);
        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                try {
                    String service = serviceSpinner.getSelectedItem().toString();
                    int units = Integer.parseInt(unitsSpinner.getSelectedItem().toString());

                    JobRequest request = new JobRequest(getServiceId(service), getDefaultId(service, units), mJobDateTime);

                    //Save the latest job request.
//                    Customer.getInstance().setCurrentJobRequest(request);

                    //Change to Quote fragment to get and display quote
                    fragmentListener.changeFragment(QUOTE, request);
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.getAppContext(), "An error occurred with this job request. Please try again later.", Toast.LENGTH_SHORT).show();
                    System.out.println("An error occurred during the job request. Check the stack trace.");
                    e.printStackTrace();
                }
            }
        });
        getServices();

        return rootView;
    }

    private void updateUnits() {
        unitsSpinnerArray.clear();

        if (services != null) {
            for (int i=0;i<services.length;i++)
            {
                if (services[i].getServiceName().equalsIgnoreCase(serviceSpinner.getSelectedItem().toString())){
                    ServiceDefaults[] units = services[i].getServiceDefaults();
                    for (int j=0;j<units.length;j++) {
                        unitsSpinnerArray.add(Integer.toString(units[j].getDefaultValue()));
                    }
                    break;
                }
            }
        }

        unitsSpinnerArrayAdapter = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_item, unitsSpinnerArray);
        unitsSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        unitsSpinner.setAdapter(unitsSpinnerArrayAdapter);


        try {
            String serviceName = serviceSpinner.getSelectedItem().toString();
            mUnitsLabel.setText(getService(serviceName).getServiceUnit().getName());
        }catch (Exception e)
        {
            System.out.println("No services retrieved yet, so cannot update units label");
        }
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
     * Handle the selection of date via dialog
     */
    private void showDatePicker() {
        DatePickerFragment mDatePicker = new DatePickerFragment();
        mDatePicker.setCallBack(mDateSetListener);
        mDatePicker.show(getFragmentManager(), "datePicker");
    }

    /**
     * Handle the selection of date via dialog
     */
    private void showTimePicker() {
        TimePickerFragment mTimePicker = new TimePickerFragment();
        mTimePicker.setCallBack(mTimeSetListener);
        mTimePicker.show(getFragmentManager(), "timePicker");

    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDateTime();
//            updateDate();
        }
    };

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour,
                              int min) {

            if (min>0){
                Toast.makeText(MainActivity.getAppContext(), "Sorry, you can only schedule on the hour", Toast.LENGTH_LONG).show();
            }
            mHour = hour;
            mMin = 0;
            updateDateTime();
            //            updateTime();
        }
    };

    private void updateDateTime() {
        //Update datetime"

        mJobDateTime = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        jobDate.setText(sdf.format(mJobDateTime.getTime()));

        //Update label for "In x days"
        GregorianCalendar currentDay = new GregorianCalendar();
        Long millisecs = mJobDateTime.getTimeInMillis() + TimeUnit.HOURS.toMillis(mJobDateTime.HOUR_OF_DAY) - currentDay.getTimeInMillis();
        int daysRemainingTillJob = 0;
        if (TimeUnit.MILLISECONDS.toDays(millisecs) > 0){
            daysRemainingTillJob = (int)TimeUnit.MILLISECONDS.toDays(millisecs);
        }
        mDateLabel.setText("Date (In " + TimeUnit.MILLISECONDS.toDays(millisecs) + " days)");

        sdf = new SimpleDateFormat("HH:mm");
        jobTime.setText(sdf.format(mJobDateTime.getTime()));

    }

    private void updateDate() {
        //Update datetime"
        mJobDateTime = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        jobDate.setText(sdf.format(mJobDateTime.getTime()));

        //Update label for "In x days"
        GregorianCalendar currentDay = new GregorianCalendar();
        Long millisecs = mJobDateTime.getTimeInMillis() + TimeUnit.HOURS.toMillis(mJobDateTime.HOUR_OF_DAY) - currentDay.getTimeInMillis();
        int daysRemainingTillJob = 0;
        if (TimeUnit.MILLISECONDS.toDays(millisecs) > 0){
            daysRemainingTillJob = (int)TimeUnit.MILLISECONDS.toDays(millisecs);
        }
        mDateLabel.setText("Date (In " + TimeUnit.MILLISECONDS.toDays(millisecs) + " days)");
    }
    private void updateTime() {

        //Update datetime"
        mJobDateTime = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        jobTime.setText(sdf.format(mJobDateTime.getTime()));

        //Update label for "In x days"
        GregorianCalendar currentDay = new GregorianCalendar();
        Long millisecs = mJobDateTime.getTimeInMillis() + TimeUnit.HOURS.toMillis(mJobDateTime.HOUR_OF_DAY) - currentDay.getTimeInMillis();
        mDateLabel.setText("Date (In " + TimeUnit.MILLISECONDS.toDays(millisecs) + " days)");
    }

    /**
     * Get list of available services from zebenzi server
     */
    private void getServices() {
        if (mGetServicesTask == null) {
            //Build url
            String url = MainActivity.getAppContext().getString(R.string.api_url_all_services);

            //Build header
            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            showProgress(true);
            mGetServicesTask = new HttpGetTask(MainActivity.getAppContext(), new GetServicesTaskCompleteListener()).execute(url, header, null);
        }
    }

    /**
     * Shows the progress UI
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Once the http request for available Services is complete, handle the returned data.
     */
    public class GetServicesTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String result, boolean networkError) {
            mGetServicesTask = null;
            showProgress(false);

            if (networkError) {
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            } else {
                if (result != null) {
                    try {
                        //Parse json into java objects
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        services = gson.fromJson(result, Service[].class);

                        //update spinners
                        serviceSpinnerArray.clear();
                        for (Service svc : services) {
                            serviceSpinnerArray.add(svc.getServiceName());
                        }
                        serviceSpinnerArrayAdapter.notifyDataSetChanged();
                        updateUnits();
                        unitsSpinner.setEnabled(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.getAppContext(), MainActivity.getAppContext().getString(R.string.no_response_from_server), Toast.LENGTH_LONG).show();
                }

        }
        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mGetServicesTask = null;
        }
    }

    private Service getService(String serviceName){
        for (Service svc: services){
            if (svc.getServiceName().equalsIgnoreCase(serviceName)){
                return svc;
            }
        }
        throw new ZebenziException("Invalid service name");
    }

    private int getServiceId(String serviceName){
        for (Service svc: services){
            if (svc.getServiceName().equalsIgnoreCase(serviceName)){
                return svc.getServiceId();
            }
        }
        throw new ZebenziException("Invalid service ID");
    }

    private int getDefaultId(String serviceName, int defaultValue){
        for (Service svc: services){
            if (svc.getServiceName().equalsIgnoreCase(serviceName)){
                ServiceDefaults[] defaults = svc.getServiceDefaults();
                for (ServiceDefaults sd: defaults) {
                    if (sd.getDefaultValue() == defaultValue) {
                        return sd.getServiceDefaultId();
                    }
                }
            }
        }
        throw new ZebenziException("Invalid serviceDefault ID");
    }
}



