package com.zebenzi.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class Worker {
//    public String name;
//    public String contact;
//    public String address;
//    public String id;

    private JSONObject worker;
    private JSONArray services;

    // Constructor to convert JSON object into a Java class instance
    public Worker(JSONObject object){
        this.worker = object;
//            this.name = object.getString("name");
//            this.contact = object.getString("contactNumber");
//            this.address = object.getString("address");
//            this.id = object.getString("userId");
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<Worker> fromJson(JSONArray jsonObjects) {
        ArrayList<Worker> workers = new ArrayList<Worker>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                workers.add(new Worker(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return workers;
    }

    public String getName()
    {
        String name = null;
        try {
            name = worker.get("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getId()
    {
        String Id = null;
        try {
            Id = worker.get("userId").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Id;
    }

    public String getMobileNumber()
    {
        String mobileNumber = null;
        try {
            mobileNumber = worker.get("contactNumber").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mobileNumber;
    }
    public String getAddress()
    {
        String address = null;
        try {
            address = worker.get("address").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return address;
    }
    public JSONArray getServices()
    {
        JSONArray servicesList = null;
        try {
            servicesList = worker.getJSONArray("servicesList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return servicesList;
    }

    public String getServiceIdFromName(String serviceName)
    {
        try {
            JSONArray servicesList = worker.getJSONArray("servicesList");
            JSONObject service;

            for (int i = 0; i < servicesList.length(); i++) {
                service = (JSONObject)servicesList.get(i);
                if (service.getString("serviceName").equalsIgnoreCase(serviceName))
                    return service.getString("serviceId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0";
    }

}
