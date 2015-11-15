package com.zebenzi.json.model.service;

/**
 * Created by Vaugan.Nayagar on 2015/11/09.
 *
 * A service default is a value that will be displayed in the drop down list for job size.
 * This was done so the server can send a predefined list of values to display.
 * Eg. for painting job size, we can have in the drop down: "10, 20, 50, 100, 500, 1000, 1500".
 * The server will send us the list of "Service Default" IDs and values.
 * When requesting a quote, we must send the Service ID and the ServiceDefault ID.
 */
public class ServiceDefaults {


    private int serviceDefaultId;
    private int defaultValue;
    private String addedDateTime;
    private String updatedDateTime;

    public int getServiceDefaultId() {
        return serviceDefaultId;
    }

    public void setServiceDefaultId(int serviceDefaultId) {
        this.serviceDefaultId = serviceDefaultId;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAddedDateTime() {
        return addedDateTime;
    }

    public void setAddedDateTime(String addedDateTime) {
        this.addedDateTime = addedDateTime;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
