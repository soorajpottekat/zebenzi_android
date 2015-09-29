package com.zebenzi.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zebenzi.network.HttpGetUserDetailsTask;
import com.zebenzi.network.HttpPostLoginTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;

import org.json.JSONException;
import org.json.JSONObject;



//TODO: Revisit how the token is saved and managed via variables

/**
 * A login screen that offers login via mobile number and password.
 */
public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

//        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        TextView tvName = (TextView) rootView.findViewById(R.id.account_name);
        TextView tvMobileNumber = (TextView) rootView.findViewById(R.id.account_mobile_number);
        TextView tvAddress = (TextView) rootView.findViewById(R.id.account_address);
        TextView tvJobsComplete = (TextView) rootView.findViewById(R.id.account_jobs_done);
        TextView tvJobsInProgress = (TextView) rootView.findViewById(R.id.account_jobs_in_progress);

//        imageView.setImageDrawable(R.drawable.orange_circle);
        tvName.setText(Customer.getInstance().getCustomerName());
        tvMobileNumber.setText(Customer.getInstance().getCustomerMobileNumber());
        tvAddress.setText(Customer.getInstance().getCustomerAddress());
        tvJobsComplete.setText("5");
        tvJobsInProgress.setText("1");

        return rootView;
    }
}



