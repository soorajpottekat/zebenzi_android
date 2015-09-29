package com.zebenzi.ui;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zebenzi.Service.Services;
import com.zebenzi.users.Customer;
import com.zebenzi.users.Worker;

import java.util.ArrayList;


//TODO: Revisit how the token is saved and managed via variables

/**
 * A login screen that offers login via mobile number and password.
 */
public class NewJobFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_new_job, container, false);

        Spinner spinnerService = (Spinner) rootView.findViewById(R.id.new_job_service_name);
        ArrayList<String> spinnerArray= new ArrayList<String>() {
            {
                add(Services.GARDENING.getName());
                add(Services.TILING.getName());
                add(Services.PAINTING.getName());
                add(Services.CLADDING.getName());
                add(Services.PAVING.getName());
                add(Services.PLASTERING.getName());
            }};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerService.setAdapter(spinnerArrayAdapter);

        Button buttonGetQuote = (Button)  rootView.findViewById(R.id.new_job_get_quote);
        buttonGetQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.getAppContext(), "You have requested a quote!", Toast.LENGTH_LONG).show();
            }
        });


        return rootView;
    }
}



