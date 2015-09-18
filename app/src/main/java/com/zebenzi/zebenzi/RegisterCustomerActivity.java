package com.zebenzi.zebenzi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.zebenzi.network.IAsyncTaskListener;
import com.zebenzi.network.RegisterTask;
import org.json.JSONException;
import org.json.JSONObject;

//TODO: Make this activity handle both Customer and worker registration since only diff is optional email

//TODO: Once registered, we must login the customer and save token.
/**
 * A login screen that offers login via email/password.
 */
public class RegisterCustomerActivity extends ActionBarActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncTask<JSONObject, String, String> mRegistrationTask;

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
        if (mRegistrationTask != null) {
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

        JSONObject jsonCustomerParams = new JSONObject();
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
            mRegistrationTask = new RegisterTask(this, new RegisterTaskCompleteListener()).execute(jsonCustomerParams);
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

    public class RegisterTaskCompleteListener implements IAsyncTaskListener{
        @Override
        public void onAsyncTaskComplete(Object result) {
            String userName = "";
            mRegistrationTask = null;
            showProgress(false);
            System.out.println("Register Result: " + result.toString());

            try {
                JSONObject jsonResult = new JSONObject((String)result);
                userName = jsonResult.get("fullName").toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Username", userName);
                setResult(RESULT_OK, resultIntent);
                // Eventually, we should save the token and display the logged-in user's name in the app.
                finish();
            }
            catch (JSONException e)
            {
                //Print error and keep user on this screen.
                System.out.println("Registration failed: "+result.toString());
            }

        }

        @Override
        public void onAsyncTaskCancelled() {
            mRegistrationTask = null;
            showProgress(false);
        }
    }
}



