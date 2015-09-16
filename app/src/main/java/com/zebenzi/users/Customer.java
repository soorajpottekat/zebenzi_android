package com.zebenzi.users;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class Customer {
    public static String name;
    public static String contact;
    public static String address;
    public static String id;

    private static Customer instance = null;
    // Constructor to convert JSON object into a Java class instance
    private Customer(){

            name = "name";
            contact = "contactNumber";
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

    public static void setCustomerDetails(JSONObject customerDetails) throws JSONException {

        name = customerDetails.getString("fullName");
        contact = customerDetails.getString("userName");
        address = customerDetails.getString("email");
        id = customerDetails.getString("id");

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

    public String getCustomerName(){
        return name;
    }
    public String getCustomerContact(){
        return contact;
    }
    public String getCustomerAddress(){
        return address;
    }
    public String getCustomerID(){
        return id;
    }

}
