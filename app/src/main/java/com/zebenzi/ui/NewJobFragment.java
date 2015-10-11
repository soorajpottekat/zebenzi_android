package com.zebenzi.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.zebenzi.service.Services;
import com.zebenzi.job.Quote;
import com.zebenzi.users.Customer;
import com.zebenzi.utils.DatePickerFragment;
import com.zebenzi.utils.TimePickerFragment;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    private Spinner spinnerService;
    private Spinner spinnerUnits;
    ArrayList<String> unitsSpinnerArray = new ArrayList<String>();
    ArrayAdapter<String> unitsSpinnerArrayAdapter;
    private TextView mUnitsLabel;
    private TextView mDateLabel;

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
        spinnerService = (Spinner) rootView.findViewById(R.id.new_job_service_name);
        ArrayList<String> spinnerArray = new ArrayList<String>() {
            {
                for (Services svc : Services.values()) {
                    add(svc.getName());
                }
            }
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerService.setAdapter(spinnerArrayAdapter);
        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spinnerUnits = (Spinner) rootView.findViewById(R.id.new_job_units_spinner);
        updateUnits();


        Button buttonGetQuote = (Button) rootView.findViewById(R.id.new_job_get_quote);
        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String item = spinnerService.getSelectedItem().toString();
                Services quoteSvc = Services.of(item);
                int quoteUnits = Integer.parseInt(spinnerUnits.getSelectedItem().toString());
                Quote q = new Quote(quoteSvc, quoteUnits, mDate, mTime);
                System.out.println("Quote price = " + q.getPrice());
                Customer.getInstance().setCurrentQuote(q);
                fragmentListener.changeFragment(SEARCH);
            }
        });


        return rootView;
    }

    private void updateUnits() {
        unitsSpinnerArray.clear();
        Services svc = Services.of(spinnerService.getSelectedItem().toString());
        mUnitsLabel.setText(svc.getUnit());

        int increment;
        int units = 0;
        increment = svc.getIncrement();

        //TODO: Move the logic to server
        for (int i = 1; i <= svc.getMaxUnits(); i++) {
            units = (i * increment);
            if (units < svc.getMaxUnits()) {
                unitsSpinnerArray.add(Integer.toString(units));
            } else {
                break;
            }
        }
        unitsSpinnerArrayAdapter = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_item, unitsSpinnerArray);
        unitsSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUnits.setAdapter(unitsSpinnerArrayAdapter);
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
     * For the spinner, we can't just display a linear increment.
     * So we have a sliding scale.
     * 1-20 = x1
     * 21-30 = x5
     * 31-50 = x10
     * 51-100 = x20
     *
     * @param spinnerCounter
     * @return
     */
    public int getUnit(int spinnerCounter, int increment) {

        if (spinnerCounter <= 20) {
            return spinnerCounter * increment * spinnerCounter;
        } else if (spinnerCounter <= 30) {
            return spinnerCounter * increment * spinnerCounter * 5;
        } else if (spinnerCounter <= 50) {
            return spinnerCounter * increment * spinnerCounter * 10;
        } else if (spinnerCounter <= 100) {
            return spinnerCounter * increment * spinnerCounter * 20;
        }

        return 0;
    }


}



