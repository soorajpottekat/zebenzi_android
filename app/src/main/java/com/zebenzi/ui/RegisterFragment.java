package com.zebenzi.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.zebenzi.network.HttpContentTypes;
import com.zebenzi.network.HttpPostTask;
import com.zebenzi.network.IAsyncTaskListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static com.zebenzi.ui.FragmentsLookup.LOGIN;

//TODO: Make this activity handle both Customer and worker registration since only diff is optional email

//TODO: Once registered, we must login the customer and save token.
/**
 * A login screen that offers login via email/password.
 */
public class RegisterFragment extends Fragment {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncTask<Object, String, String> mRegistrationTask;

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
    private ImageButton mProfileImageButton;
    private View mProgressView;
    private View mRegisterCustomerFormView;
    private FragmentListener fragmentListener;
    private static int RESULT_LOAD_IMAGE = 50;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        mMobileNumberView = (EditText) rootView.findViewById(R.id.register_mobile);
        mFirstnameView = (EditText) rootView.findViewById(R.id.register_firstname);
        mLastnameView = (EditText) rootView.findViewById(R.id.register_lastname);
        mEmailView = (EditText) rootView.findViewById(R.id.register_email);
        mPasswordView = (EditText) rootView.findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) rootView.findViewById(R.id.register_confirmPassword);
        mAddressLine1View = (EditText) rootView.findViewById(R.id.register_address_line1);
        mAddressLine2View = (EditText) rootView.findViewById(R.id.register_address_line2);
        mSuburbView = (EditText) rootView.findViewById(R.id.register_suburb);
        mSuburbCodeView = (EditText) rootView.findViewById(R.id.register_suburb_code);

        Button mEmailSignInButton = (Button) rootView.findViewById(R.id.register_customer_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCustomer();
            }
        });

        mProfileImageButton = (ImageButton) rootView.findViewById(R.id.register_profile_image);
        mProfileImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        mRegisterCustomerFormView = rootView.findViewById(R.id.register_customer_form);
        mProgressView = rootView.findViewById(R.id.login_progress);

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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void registerCustomer() {
        if (mRegistrationTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the registration attempt.
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
        String image = Base64.encodeToString(getBytesFromBitmap(((BitmapDrawable) mProfileImageButton.getDrawable()).getBitmap()),
                Base64.NO_WRAP);

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

            //Build url
            String url = MainActivity.getAppContext().getString(R.string.api_url_registration);

            //Build header
            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");

            //Build body
            JSONObject addressObject = new JSONObject();
            try {
                addressObject.put("AddressLine1", addressLine1);
                addressObject.put("AddressLine2", addressLine2);
                addressObject.put("Surburb", suburb);
                addressObject.put("code", code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject body = new JSONObject();
            try {
                body.put("FirstName", firstName);
                body.put("LastName", lastName);
                body.put("Email", email);
                body.put("Telephone", mobileNumber);
                body.put("Password", password);
                body.put("ConfirmPassword", confirmPassword);
                body.put("RoleName", "User");
                body.put("Adddress", addressObject);
                body.put("ImageExtention",".jpg");
                body.put("ImageData", image);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("Registration params="+body);

            showProgress(true);
            mRegistrationTask = new HttpPostTask(MainActivity.getAppContext(), new RegisterTaskCompleteListener()).execute(url, header, body, HttpContentTypes.RAW);
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
     * Shows the progress UI and hides the registration form.
     */
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegisterCustomerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public class RegisterTaskCompleteListener implements IAsyncTaskListener{
        @Override
        public void onAsyncTaskComplete(Object result, boolean networkError) {
            mRegistrationTask = null;
            showProgress(false);

            if (networkError){
                Toast.makeText(MainActivity.getAppContext(),
                        MainActivity.getAppContext().getString(R.string.check_your_network_connection),
                        Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    System.out.println("Register Result: " + result.toString());
                    JSONObject jsonResult = new JSONObject((String) result);
                    String userName = jsonResult.get("fullName").toString();

                    System.out.println("Registration succeeded for " + userName);
                    Toast.makeText(MainActivity.getAppContext(), "You are now registered! Please log in to continue.", Toast.LENGTH_LONG).show();

                    //If there is a valid name in the response, then the registration was successful.
                    fragmentListener.changeFragment(LOGIN);
                } catch (JSONException e) {
                    //Print error and keep user on this screen.
                    System.out.println("Registration failed: " + result.toString());
                    Toast.makeText(MainActivity.getAppContext(), "An error occurred during registration.", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onAsyncTaskCancelled() {
            showProgress(false);
            mRegistrationTask = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = MainActivity.getAppContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            mProfileImageButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}



