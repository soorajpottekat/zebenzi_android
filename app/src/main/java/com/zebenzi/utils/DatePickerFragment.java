package com.zebenzi.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import java.util.Calendar;


/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 */
public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
    }


    public void setCallBack(DatePickerDialog.OnDateSetListener mDateSetListener) {
        ondateSet = mDateSetListener;
    }
}