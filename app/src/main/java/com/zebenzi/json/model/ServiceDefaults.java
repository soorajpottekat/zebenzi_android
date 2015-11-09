package com.zebenzi.json.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vaugan.Nayagar on 2015/11/09.
 */
public class ServiceDefaults {


    @SerializedName("ServiceDefaultId")
    private int id;
    @SerializedName("DefaultValue")
    private int value;
    @SerializedName("AddedDateTime")
    private String addedDate;
    @SerializedName("UpdatedDateTime")
    private String updatedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
