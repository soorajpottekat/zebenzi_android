package com.zebenzi.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class Worker extends User{


    // Constructor to convert JSON object into a Java class instance
    public Worker(JSONObject object){
        super();

        try {
            setDetails(object, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
