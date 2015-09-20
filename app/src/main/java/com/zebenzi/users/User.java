package com.zebenzi.users;

import android.content.Context;
import android.content.SharedPreferences;

import com.zebenzi.zebenzi.R;
import com.zebenzi.zebenzi.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class User {
    public static final String PREFS_NAME = "ZebenziPreferencesFile";
    // Store values at the time of the login attempt.

    private static String firstName;
    private static String lastName;
    private static String mobileNumber;
    private static String email;
    private static String address;
    private static String id;
    private static Context ctx = null;
    private static SharedPreferences settings = null;

    // Constructor to convert JSON object into a Java class instance
    public User(){

            ctx = SearchActivity.getAppContext();
            settings = ctx.getSharedPreferences(PREFS_NAME, 0);

            firstName = "name";
            lastName = "lastName";
            mobileNumber = "mobileNumber";
            email = "email";
            address = "address";
            id = "id";

    }

    //User details should be updated every successful login.
    public static void setDetails(JSONObject customerDetails, String token) throws JSONException {

        //TODO: Customer and Worker GET must have same fields - ie. firstName, LastName etc. etc.
        
        firstName = customerDetails.getString("name");
        mobileNumber = customerDetails.getString("userName");
        address = customerDetails.getString("email");
        id = customerDetails.getString("id");
//        lastName = customerDetails.getString("lastName");
//        email = customerDetails.getString("email");

        saveToken(token);
    }


    public static String getLastName(){
        return lastName;
    }
    public static String getEmail(){
        return email;
    }
    public static String getFirstName(){
        return firstName;
    }
    public static String getMobileNumber(){
        return mobileNumber;
    }
    public static String getAddress(){
        return address;
    }
    public static String getId(){
        return id;
    }

    private static void saveToken(String token) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ctx.getString(R.string.api_access_token), token);

        // Commit the edits!
        editor.commit();
    }

    public static String getToken() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        return settings.getString(ctx.getString(R.string.api_access_token), null);

    }
}
