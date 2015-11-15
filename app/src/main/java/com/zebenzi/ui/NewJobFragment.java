package com.zebenzi.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.zebenzi.service.ServicesHardcoded;
import com.zebenzi.job.JobRequest;
import com.zebenzi.users.Customer;
import com.zebenzi.utils.DatePickerFragment;
import com.zebenzi.utils.TimePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.zebenzi.ui.FragmentsLookup.SEARCH;


//TODO: Revisit how the token is saved and managed via variables

/**
 * A login screen that offers login via mobile number and password.
 */
public class NewJobFragment extends Fragment {

    private FragmentListener fragmentListener;
    private Button jobDate;
    private int mYear;
    public int mMonth;
    private int mDay;
    private int mHour;
    private int mMin;
    private Button jobTime;
    GregorianCalendar mDate;
    GregorianCalendar mTime;
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

        View rootView = inflater.inflate(R.layout.fragment_new_job, container, false);

        mUnitsLabel = (TextView) rootView.findViewById(R.id.new_job_units_label);
        mDateLabel =  (TextView) rootView.findViewById(R.id.new_job_date_label);

        //Select date via datepicker fragment
        jobDate = (Button) rootView.findViewById(R.id.new_job_date);
        jobTime = (Button) rootView.findViewById(R.id.new_job_time);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        mDate = new GregorianCalendar();
        String date = df.format(mDate.getTime());
        jobDate.setText(date);
        jobDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        df = new SimpleDateFormat("HH:mm");
        mTime = new GregorianCalendar();
        mTime.set(Calendar.HOUR_OF_DAY, 8);
        mTime.set(Calendar.MINUTE, 0);
        String time = df.format(mTime.getTime());
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
                for (ServicesHardcoded svc : ServicesHardcoded.values()) {
                    add(svc.getName());
                }
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
//        updateUnits();


        Button buttonGetQuote = (Button) rootView.findViewById(R.id.new_job_get_quote);
        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String item = serviceSpinner.getSelectedItem().toString();
                ServicesHardcoded quoteSvc = ServicesHardcoded.of(item);
                int quoteUnits = Integer.parseInt(unitsSpinner.getSelectedItem().toString());
                JobRequest q = new JobRequest(quoteSvc, quoteUnits, mDate, mTime);
                System.out.println("Estimated price = " + q.getPrice());
                Customer.getInstance().setCurrentJobRequest(q);
                fragmentListener.changeFragment(SEARCH);
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
                }
            }
        }

        unitsSpinnerArrayAdapter = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_item, unitsSpinnerArray);
        unitsSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        unitsSpinner.setAdapter(unitsSpinnerArrayAdapter);
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
     * Handle the selection of date via dialog
     */
    private void showDatePicker() {
        //To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerFragment mDatePicker = new DatePickerFragment();
        mDatePicker.setCallBack(mDateSetListener);
        mDatePicker.show(getFragmentManager(), "datePicker");
    }

    /**
     * Handle the selection of date via dialog
     */
    private void showTimePicker() {
        //To show current time in the timepicker
        Calendar mcurrentDate = Calendar.getInstance();
        mHour = mcurrentDate.get(Calendar.HOUR);
        mMin = mcurrentDate.get(Calendar.MINUTE);

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
            updateDate();
        }
    };

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour,
                              int min) {
            mHour = hour;
            mMin = min;
            updateTime();
        }
    };

    private void updateDate() {
        mDate = new GregorianCalendar(mYear, mMonth, mDay);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        jobDate.setText(sdf.format(mDate.getTime()));

        GregorianCalendar currentDay = new GregorianCalendar();

        Long millisecs = mDate.getTimeInMillis() + TimeUnit.HOURS.toMillis(mTime.HOUR_OF_DAY) - currentDay.getTimeInMillis();
        mDateLabel.setText("Date (In " + TimeUnit.MILLISECONDS.toDays(millisecs) + " days)");

    }

    private void updateTime() {
        mTime = new GregorianCalendar(mHour, mMonth, mDay, mHour, mMin);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        jobTime.setText(sdf.format(mTime.getTime()));
    }

    /**
     * Attempts to sign in using stored oAuth token
     */
    private void getServices() {
        if (mGetServicesTask == null) {
            //Build url
            String url = MainActivity.getAppContext().getString(R.string.api_url_all_services);

            //Build header
            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

//            showProgress(true);
            mGetServicesTask = new HttpGetTask(MainActivity.getAppContext(), new GetServicesTaskCompleteListener()).execute(url, header, null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
//    public void showProgress(final boolean show) {
//        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//    }

    /**
     * Once the login task completes successfully, we should have a valid token and can
     * obtain the user details.
     * If unsuccessful, place focus on input text field.
     */
    public class GetServicesTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String result, boolean networkError) {
            mGetServicesTask = null;
//            showProgress(false);

            if (networkError){
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    //Parse json into java objects
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    services = gson.fromJson(result, Service[].class);

                    //update spinners
                    serviceSpinnerArray.clear();
                    for (int i=0;i<services.length;i++) {
                        String serviceName = services[i].getServiceName();
                        serviceSpinnerArray.add(serviceName);
                    }
                    serviceSpinnerArrayAdapter.notifyDataSetChanged();
                    updateUnits();
                    unitsSpinner.setEnabled(true);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
//            showProgress(false);
            mGetServicesTask = null;
        }
    }

}



