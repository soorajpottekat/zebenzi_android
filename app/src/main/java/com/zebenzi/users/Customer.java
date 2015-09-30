package com.zebenzi.users;

import android.content.Context;
import android.content.SharedPreferences;

import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class Customer {
    public static final String PREFS_NAME = "ZebenziPrefsFile";

    private static String name = "";
    private static String email = "";
    private static String mobileNumber = "";
    private static String address = "";
    private static String id = "";
    private static Context ctx = null;
    private static SharedPreferences settings = null;
    private static Customer instance = null;

    // Constructor to convert JSON object into a Java class instance
    private Customer() {

        ctx = MainActivity.getAppContext();
        settings = ctx.getSharedPreferences(PREFS_NAME, 0);

        //Read saved customer details from preferences.
        getCustomerDetails();
    }

    public static Customer getInstance() {
        if (instance == null) {
            instance = new Customer();
        }
        return instance;
    }

    //Update this singleton details and save to preferences.
    public static void setCustomerDetails(JSONObject customerDetails, String token) throws JSONException {

        name = customerDetails.getString("fullName");
        email = customerDetails.getString("email");
        mobileNumber = customerDetails.getString("userName");
        address = customerDetails.getString("email");
        id = customerDetails.getString("id");

        saveToken(token);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("customer_name", name);
        editor.putString("customer_email", email);
        editor.putString("customer_address", address);
        editor.putString("customer_mobile_number", mobileNumber);
        editor.putString("customer_id", id);

        // Commit the edits!
        editor.commit();

    }
    //Get from preferences and update this singleton details.
    public static void getCustomerDetails() {
        //Read saved customer details from preferences.
        name = settings.getString("customer_name", "");
        email = settings.getString("customer_email", "");
        mobileNumber = settings.getString("customer_mobile_number", "");
        address = settings.getString("customer_address", "");
        id = settings.getString("customer_id", "");
    }

    //Delete from preferences and update this singleton
    public static void signOut() {

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        getCustomerDetails();
    }

    public static String getCustomerName() {
        return name;
    }

    public static String getCustomerEmail() {
        return email;
    }

    public static String getCustomerMobileNumber() {
        return mobileNumber;
    }

    public static String getCustomerAddress() {
        return address;
    }

    public static String getCustomerID() {
        return id;
    }

    private static void saveToken(String token) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ctx.getString(R.string.api_rest_access_token), token);
        editor.commit();
    }

    public static String getToken() {
        return settings.getString(ctx.getString(R.string.api_rest_access_token), null);
    }

}
