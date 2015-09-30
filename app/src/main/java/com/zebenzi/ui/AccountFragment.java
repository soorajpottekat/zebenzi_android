package com.zebenzi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.zebenzi.users.Customer;


/**
 * An Account screen that user's details.
 */
public class AccountFragment extends Fragment {

    private FragmentListener fragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

//        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        TextView tvName = (TextView) rootView.findViewById(R.id.account_name);
        TextView tvMobileNumber = (TextView) rootView.findViewById(R.id.account_mobile_number);
        TextView tvAddress = (TextView) rootView.findViewById(R.id.account_address);
        TextView tvJobsComplete = (TextView) rootView.findViewById(R.id.account_jobs_done);
        TextView tvJobsInProgress = (TextView) rootView.findViewById(R.id.account_jobs_in_progress);

        Button myJobsButton = (Button) rootView.findViewById(R.id.accounts_job_history_button);
        myJobsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentListener.changeFragment(R.id.action_history);
            }
        });

        Button changePasswordButton = (Button) rootView.findViewById(R.id.accounts_change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.getAppContext(), MainActivity.getAppContext().getString(R.string.implementation_coming_soon), Toast.LENGTH_LONG).show();
            }
        });

//        imageView.setImageDrawable(R.drawable.orange_circle);
        tvName.setText(Customer.getInstance().getCustomerName());
        tvMobileNumber.setText(Customer.getInstance().getCustomerMobileNumber());
        tvAddress.setText(Customer.getInstance().getCustomerAddress());
        tvJobsComplete.setText("Jobs complete: 5");
        tvJobsInProgress.setText("Jobs in progress: 1");

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
}



