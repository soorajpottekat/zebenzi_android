package com.zebenzi.zebenzi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.network.LoginTask;
import com.zebenzi.network.RegisterTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class RegisterCustomerActivity extends ActionBarActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

//    String customerRegistrationAPIUrl = this.getString(R.string.api_url_registration);
    public final static String user = "0846676467";
    public final static String password = "dolphin";

    List<NameValuePair> customer_register_params;
    JSONObject jsonCustomerParams;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private RegisterCustomerTask mAuthTask = null;

    // UI references.
    private EditText mMobileNumberView;
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mAddressLine1View;
    private EditText mAddressLine2View;
    private EditText mSuburbView;
    private EditText mSuburbCodeView;


    private View mProgressView;
    private View mRegisterCustomerFormView;
    private TextView mTestRegistrationTextView;
    private AsyncTask<JSONObject, String, String> mTestRegistrationTask;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_account:
                return true;
            case R.id.action_register:
                return true;
            case R.id.action_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);


        mMobileNumberView = (EditText) findViewById(R.id.register_mobile);
        mFirstnameView = (EditText) findViewById(R.id.register_firstname);
        mLastnameView = (EditText) findViewById(R.id.register_lastname);
        mEmailView = (EditText) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirmPassword);
        mAddressLine1View = (EditText) findViewById(R.id.register_address_line1);
        mAddressLine2View = (EditText) findViewById(R.id.register_address_line2);
        mSuburbView = (EditText) findViewById(R.id.register_suburb);
        mSuburbCodeView = (EditText) findViewById(R.id.register_suburb_code);

//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptRegisterCustomer();
//                    return true;
//                }
//                return false;
//            }
//        });

        Button mEmailSignInButton = (Button) findViewById(R.id.register_customer_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegisterCustomer();
            }
        });


        mRegisterCustomerFormView = findViewById(R.id.register_customer_form);
        mProgressView = findViewById(R.id.login_progress);


        mTestRegistrationTextView = (TextView) findViewById(R.id.test_registration_textView);
        Button mTestRegistrationButton = (Button) findViewById(R.id.test_registration_button);
        mTestRegistrationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                testJsonRegistration();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegisterCustomer() {
        if (mTestRegistrationTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobileNumber = mMobileNumberView.getText().toString();
        String firstName = mFirstnameView.getText().toString();
        String lastName = mLastnameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String addressLine1 = mAddressLine1View.getText().toString();
        String addressLine2 = mAddressLine2View.getText().toString();
        String suburb = mSuburbView.getText().toString();
        String code = mSuburbCodeView.getText().toString();

        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("AddressLine1", addressLine1);
            addressObject.put("AddressLine2", addressLine2);
            addressObject.put("Surburb", suburb);
            addressObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonCustomerParams = new JSONObject();
        try {
            jsonCustomerParams.put("FirstName", firstName);
            jsonCustomerParams.put("LastName", lastName);
            jsonCustomerParams.put("Email", email);
            jsonCustomerParams.put("Telephone", mobileNumber);
            jsonCustomerParams.put("Password", password);
            jsonCustomerParams.put("ConfirmPassword", confirmPassword);
            jsonCustomerParams.put("RoleName", "User");
            jsonCustomerParams.put("Adddress", addressObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Registration jsonParams="+jsonCustomerParams);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check passwords match.
        if (!TextUtils.equals(password, confirmPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new RegisterCustomerTask(email, password);
//            mAuthTask.execute((String) null);
        }
    }



    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this zebenzi server logic for password strength
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterCustomerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterCustomerFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterCustomerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterCustomerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void testJsonRegistration()
    {
        // Store values at the time of the login attempt.
        String mobileNumber = "0333333334";
        String firstName = "Dhivanee";
        String lastName = "Nayagar";
        String email = "dhivanee.nayagar@gmail.com";
        String password = "dolphin";
        String confirmPassword = "dolphin";
        String addressLine1 = "Unit C1";
        String addressLine2 = "216 Main Avenue";
        String suburb = "Randburg";
        String code = "2194";



        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("AddressLine1", addressLine1);
            addressObject.put("AddressLine2", addressLine2);
            addressObject.put("Surburb", suburb);
            addressObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonCustomerParams = new JSONObject();
        try {
            jsonCustomerParams.put("FirstName", firstName);
            jsonCustomerParams.put("LastName", lastName);
            jsonCustomerParams.put("Email", email);
            jsonCustomerParams.put("Telephone", mobileNumber);
            jsonCustomerParams.put("Password", password);
            jsonCustomerParams.put("ConfirmPassword", confirmPassword);
            jsonCustomerParams.put("RoleName", "User");
            jsonCustomerParams.put("Adddress", addressObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Registration jsonParams="+jsonCustomerParams);

//        mTestRegistrationTask = new TestRegisterCustomerTask("test", "registration");
//        mTestRegistrationTask.execute((String) null);
//

        mTestRegistrationTask = new RegisterTask(this, new RegisterTaskCompleteListener()).execute(jsonCustomerParams);

    }

    public class RegisterTaskCompleteListener implements IAsyncTaskListener{
        @Override
        public void onAsyncTaskComplete(Object result) {
            System.out.println("Register Result: " + result.toString());
        }

        @Override
        public void onAsyncTaskCancelled() {

        }
    }
}



