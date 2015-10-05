package com.zebenzi.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;


/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 */
public class TimePickerFragment extends DialogFragment {
    TimePickerDialog.OnTimeSetListener onTimeSet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), onTimeSet, hour, min, true);
    }


    public void setCallBack(TimePickerDialog.OnTimeSetListener mDateSetListener) {
        onTimeSet = mDateSetListener;
    }
}