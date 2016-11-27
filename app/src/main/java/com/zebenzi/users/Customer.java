package com.zebenzi.users;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zebenzi.job.JobRequest;
import com.zebenzi.json.model.quote.Quote;
import com.zebenzi.json.model.user.User;
import com.zebenzi.ui.MainActivity;
import com.zebenzi.ui.R;

import org.json.JSONException;

/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class Customer {
    public static final String PREFS_NAME = "ZebenziUser";
    private Context ctx = null;
    private SharedPreferences settings = null;
    private static Customer instance = null;
    private User currentUser;
    private Quote lastQuote;

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
    public void setCustomerDetails(User user, String token) throws JSONException {

        saveToken(token);

        currentUser = user;
        Gson gson = new Gson();
        String userString = gson.toJson(currentUser);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("current_user", userString);
        editor.commit();

    }
    //Get from preferences and update this singleton details.
    public void getCustomerDetails() {
        //Read saved customer details from preferences.
        Gson gson = new Gson();
        String json = settings.getString("current_user", "");
        currentUser = gson.fromJson(json, User.class);
    }

    //Delete from preferences and update this singleton
    public void signOut() {
        currentUser = null;

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public String getCustomerFirstName() {
        if (currentUser != null) {
            return currentUser.getFirstName();
        }
        return "";
    }

    public String getCustomerLastName() {
        if (currentUser != null) {
            return currentUser.getLastName();
        }
        return "";
    }

    public String getCustomerEmail() {
        if (currentUser != null) {
            return currentUser.getEmail();
        }
        return "";
    }

    public String getCustomerMobileNumber() {
        if (currentUser != null) {
            return currentUser.getUserName();
        }
        return "";
    }

    public String getCustomerAddress() {
        if (currentUser.getUserAddress() != null) {
            return currentUser.getUserAddress().toString();
        }
        return "";
    }

    public String getCustomerID() {
        if (currentUser != null) {
            return currentUser.getId();
        }
        return "";
    }

    public String getCustomerImageUrl() {
        if (currentUser != null) {
            return currentUser.getImageUrl();
        }
        return "";
    }

    private void saveToken(String token) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ctx.getString(R.string.api_rest_access_token), token);
        editor.commit();
    }

    public String getToken() {
        return settings.getString(ctx.getString(R.string.api_rest_access_token), null);
    }

    public boolean isLoggedIn() {
        return (settings.getString(ctx.getString(R.string.api_rest_access_token), null) != null);
    }

    public Quote getLastQuote() {
        return lastQuote;
    }

    public void setLastQuote(Quote lastQuote) {
        this.lastQuote = lastQuote;
    }
}
