package com.zebenzi.ui;

import android.app.Activity;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zebenzi.json.model.user.User;
import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpGetTask;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.users.Customer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zebenzi.ui.FragmentsLookup.NEW_JOB;


//TODO: Revisit how the token is saved and managed via variables

/**
 * A login screen that offers login via mobile number and password.
 */
public class LoginFragment extends Fragment {

    // Keep track of the async tasks to ensure we can cancel it if requested.
    private AsyncTask<Object, String, String>  mLoginTask = null;
    private AsyncTask<Object, String, String>  mUserDetailsTask = null;

    // UI references.
    private EditText mMobileNumberView;
    private EditText mPasswordView;
    private TextView mLoginTokenView;
    private View mProgressView;
    private View mLoginFormView;
    private String oAuthToken;
    private FragmentListener fragmentListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Set up the login form.
        mMobileNumberView = (EditText) rootView.findViewById(R.id.mobile_number);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });

        mLoginTokenView = (TextView) rootView.findViewById(R.id.login_token);

        Button mEmailSignInButton = (Button) rootView.findViewById(R.id.customer_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        TextView mForgotPasswordText = (TextView) rootView.findViewById(R.id.forgot_password);
        mForgotPasswordText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        mLoginFormView = rootView.findViewById(R.id.login_form);
        mProgressView = rootView.findViewById(R.id.login_progress);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.my_awesome_toolbar);
        login(Customer.getInstance().getToken());

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

    private void forgotPassword() {
        Toast.makeText(MainActivity.getAppContext(), "We will sms you a new password", Toast.LENGTH_LONG).show();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void login() {
        if (mLoginTask != null) {
            return;
        }

        // Reset errors.
        mMobileNumberView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobileNumber = mMobileNumberView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid mobile number.
        if (TextUtils.isEmpty(mobileNumber)) {
            mMobileNumberView.setError(getString(R.string.error_field_required));
            focusView = mMobileNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            //Build url
            String url = MainActivity.getAppContext().getString(R.string.api_url_login);

            //Build body
            List<NameValuePair> body = new ArrayList<NameValuePair>();
            body.add(new BasicNameValuePair(MainActivity.getAppContext().getString(R.string.api_json_field_username), mobileNumber));
            body.add(new BasicNameValuePair(MainActivity.getAppContext().getString(R.string.api_json_field_password), password));
            body.add(new BasicNameValuePair(MainActivity.getAppContext().getString(R.string.api_json_field_grant_type), MainActivity.getAppContext().getString(R.string.api_json_field_password)));

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mLoginTask = new HttpPostTask(MainActivity.getAppContext(), new LoginTaskCompleteListener()).execute(url, null, body, HttpContentTypes.X_WWW_FORM_URLENCODED);
        }
    }

    /**
     * Attempts to sign in using stored oAuth token
     */
    private void login(String token) {

        if (token != null) {
            oAuthToken = token;
            mLoginTokenView.setText(oAuthToken);

            //Get the User details and display
            if (mUserDetailsTask == null) {
                //Build url
                String url = MainActivity.getAppContext().getString(R.string.api_url_user_details);

                //Build header
                HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("Authorization", "bearer " + token);

                showProgress(true);
                mUserDetailsTask = new HttpGetTask(MainActivity.getAppContext(), new UserDetailsTaskCompleteListener()).execute(url, header, null);
            }
        }
        else
        {
            //Do nothing, but wait for login
            System.out.println("The oAuth token is null!");
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Once the login task completes successfully, we should have a valid token and can
     * obtain the user details.
     * If unsuccessful, place focus on input text field.
     */
    public class LoginTaskCompleteListener implements IAsyncTaskListener<String> {
        @Override
        public void onAsyncTaskComplete(String result, boolean networkError) {
            mLoginTask = null;
            showProgress(false);

            if (networkError){
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    oAuthToken = (String) jsonResult.get(getString(R.string.api_rest_access_token));

                    if (oAuthToken != null) {
                        mLoginTokenView.setText(oAuthToken);
                        login(oAuthToken);
                    }
                    else {
                        System.out.println("Error occurred with login: " + jsonResult.toString());
                        mMobileNumberView.setError(getString(R.string.error_incorrect_mobile_or_password));
                        mMobileNumberView.requestFocus();
                        mLoginTokenView.setText(jsonResult.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mLoginTask = null;
        }
    }

    /**
     * Once user details task completes successfully, we should have a JSONObject with all the user
     * data from the server.
     * If unsuccessful, place focus on input field.
     */
    public class UserDetailsTaskCompleteListener implements  IAsyncTaskListener<String>
    {
        @Override
        public void onAsyncTaskComplete(String result, boolean networkError) {
            mUserDetailsTask = null;
            showProgress(false);

            if (networkError){
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            }else {
                if (result != null) {
                    try {
                        //Parse json into java objects
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        User user = gson.fromJson(result, User.class);

                        Customer.getInstance().setCustomerDetails(user, oAuthToken);

                        if (user != null) {
                            mLoginTokenView.setText(user.getUserName());
                            fragmentListener.changeFragment(NEW_JOB, null);
                        } else {
                            System.out.println("Error occurred with login: " + result);
                            mMobileNumberView.setError(getString(R.string.error_incorrect_mobile_or_password));
                            mMobileNumberView.requestFocus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    System.out.println("User Details task returned null data");
                }
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
            mUserDetailsTask = null;
            showProgress(false);
        }
    }

}



