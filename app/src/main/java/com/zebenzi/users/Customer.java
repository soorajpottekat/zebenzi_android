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

    private static String name="name";
    private static String email="email";
    private static String mobileNumber;
    private static String address;
    private static String id;
    private static Context ctx = null;
    private static SharedPreferences settings = null;
    private static Customer instance = null;

    // Constructor to convert JSON object into a Java class instance
    private Customer(){

            ctx = MainActivity.getAppContext();
            settings = ctx.getSharedPreferences(PREFS_NAME, 0);

            name = settings.getString("customer_name","no name");
            email = settings.getString("customer_email","no email");
            mobileNumber = "contactNumber";
            address = "address";
            id = "userId";

    }

    public static Customer getInstance()
    {
        if (instance == null)
        {
            instance = new Customer();
        }
        return instance;
    }

    //Customer details should be updated every successful login.
    public static void setCustomerDetails(JSONObject customerDetails, String token) throws JSONException {

        name = customerDetails.getString("fullName");
        email = customerDetails.getString("email");
        mobileNumber = customerDetails.getString("userName");
        address = customerDetails.getString("email");
        id = customerDetails.getString("id");

        //TODO: Save token here
        saveToken(token);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("customer_name", name);
        editor.putString("customer_email", email);

        // Commit the edits!
        editor.commit();

    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
//    public static ArrayList<Customer> fromJson(JSONArray jsonObjects) {
//        ArrayList<Customer> workers = new ArrayList<Customer>();
//        for (int i = 0; i < jsonObjects.length(); i++) {
//            try {
//                workers.add(new Customer(jsonObjects.getJSONObject(i)));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return workers;
//    }

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
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ctx.getString(R.string.api_rest_access_token), token);

        // Commit the edits!
        editor.commit();
    }

    public static String getToken() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        return settings.getString(ctx.getString(R.string.api_rest_access_token), null);

    }
}
