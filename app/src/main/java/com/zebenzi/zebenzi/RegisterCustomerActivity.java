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

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
public class RegisterCustomerActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    public final static String customerRegistrationAPIUrl = "http://www.zebenzi.com/api/accounts/create";
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
    private RegisterCustomerTask mAuthTask = null;

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
    private TestRegisterCustomerTask mTestRegistrationTask;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 14) {
            // Use ContactsContract.Profile (API 14+)
            getLoaderManager().initLoader(0, null, this);
        } else if (VERSION.SDK_INT >= 8) {
            // Use AccountManager (API 8+)
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegisterCustomer() {
        if (mAuthTask != null) {
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



        List<NameValuePair> customer_address = new ArrayList<NameValuePair>();
        customer_address.add(new BasicNameValuePair("AddressLine1", addressLine1));
        customer_address.add(new BasicNameValuePair("AddressLine2", addressLine2));
        customer_address.add(new BasicNameValuePair("Suburb", suburb));
        customer_address.add(new BasicNameValuePair("code", code));

        customer_register_params = new ArrayList<>();
        customer_register_params.add(new BasicNameValuePair("Firstname", firstName));
        customer_register_params.add(new BasicNameValuePair("Lastname", lastName));
        customer_register_params.add(new BasicNameValuePair("Email", email));
        customer_register_params.add(new BasicNameValuePair("Telephone", mobileNumber));
        customer_register_params.add(new BasicNameValuePair("Password", password));
        customer_register_params.add(new BasicNameValuePair("ConfirmPassword", confirmPassword));
        customer_register_params.add(new BasicNameValuePair("RoleName", "User"));
        customer_register_params.add(new BasicNameValuePair("Address", "Dummy Address"));


        JSONObject addressObject = new JSONObject();
        try {
            addressObject.put("AddressLine1", addressLine1);
            addressObject.put("AddressLine2", addressLine2);
            addressObject.put("Suburb", suburb);
            addressObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonCustomerParams = new JSONObject();
        try {
            jsonCustomerParams.put("Firstname", firstName);
            jsonCustomerParams.put("Lastname", lastName);
            jsonCustomerParams.put("Email", email);
            jsonCustomerParams.put("Telephone", mobileNumber);
            jsonCustomerParams.put("Password", password);
            jsonCustomerParams.put("ConfirmPassword", confirmPassword);
            jsonCustomerParams.put("RoleName", "User");
            jsonCustomerParams.put("Address", addressObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsonParams="+jsonCustomerParams);
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
            mAuthTask = new RegisterCustomerTask(email, password);
            mAuthTask.execute((String) null);
        }
    }



    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true; //email.contains("@");0846676467
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
     * the email text field with results on the main UI thread.
     */
    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            // Get all emails from the user's contacts and copy them to a list.
            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

        @Override
        protected void onPostExecute(List<String> emailAddressCollection) {
            addEmailsToAutoComplete(emailAddressCollection);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegisterCustomerActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

//        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegisterCustomerTask extends AsyncTask<String, ArrayList<String>, String> {

        private final String mEmail;
        private final String mPassword;
        String resultToDisplay = null;

        RegisterCustomerTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

//            String paramString = "username="+user+"&password="+password+"&grant_type=password";
//            String urlString =  URLEncoder.encode(paramString, "UTF-8");
//
//            System.out.println(urlString);


//String urlString = addLoginParamsToUrl(apiURL);
//            String urlString = "http://zebenzi.com/api/search/services/painter";


            try {
//                System.out.println(urlString);
                /*-------------------*/
                URL url = new URL(customerRegistrationAPIUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

//                List<NameValuePair> local_params = new ArrayList<NameValuePair>();
//                local_params.add(new BasicNameValuePair("username", user));
//                local_params.add(new BasicNameValuePair("password", password));
//                local_params.add(new BasicNameValuePair("grant_type", "password"));

                //for hiring or profile changes etc. use token:
//                local_params.add(new BasicNameValuePair("Authorization", "bearer ey........"));



                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(customer_register_params));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                /*-------------------*/
//                url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

                StringBuilder sb = new StringBuilder();

                String line = "";

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=reader.readLine())!=null){
                    sb.append(line);
                }
                reader.close();
                String result = sb.toString();

                JSONObject jsonResult = new JSONObject(result);
//            int id = jsonResult.getInt("ID");
                resultToDisplay = jsonResult.getString("name");

//            txtId.setText(id);
//            txtName.setText(name);

            } catch (Exception e) {
                System.out.println(e.getMessage());
//                return e.getMessage();

            }

            return resultToDisplay;
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(final String resultToDisplay) {
            mAuthTask = null;
            showProgress(false);

            if (resultToDisplay != null) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    protected String addLoginParamsToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair("username", "0846676467"));
        params.add(new BasicNameValuePair("password", "dolphin"));
        params.add(new BasicNameValuePair("grant_type", "password"));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }

    private void testJsonRegistration()
    {
        // Store values at the time of the login attempt.
        String mobileNumber = "0333333333";
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

        mTestRegistrationTask = new TestRegisterCustomerTask("test", "registration");
        mTestRegistrationTask.execute((String) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class TestRegisterCustomerTask extends AsyncTask<String, ArrayList<String>, String> {

        private final String mEmail;
        private final String mPassword;
        String resultToDisplay = null;

        TestRegisterCustomerTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            OutputStream os = null;
            BufferedInputStream in = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(customerRegistrationAPIUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches (false);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();


                os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os));
                System.out.println("json writer string = "+jsonCustomerParams.toString());
                writer.write(jsonCustomerParams.toString());
                writer.flush();


//                osWriter.write(URLEncoder.encode(jsonCustomerParams.toString(), "UTF-8"));
//                osWriter.flush();
//                osWriter.close();
//                os.close();

                if (conn.getResponseCode() / 100 == 2) { // 2xx code means success
                    in = new BufferedInputStream(conn.getInputStream());
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    String result = sb.toString();

                    JSONObject jsonResult = new JSONObject(result);
                    System.out.println("Registration Result = " + jsonResult.toString());
                    System.out.println("Registration Result END ");
                } else {

                    in = new BufferedInputStream(conn.getErrorStream());

                    String result = in.toString();
                    System.out.println("Error != 2xx" + result);
                    System.out.println("Error = "+conn.getResponseCode());
                }




            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();

            } finally {

                try {
                    os.close();
                    in.close();
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return resultToDisplay;
        }

    }

    private String testGetQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}



