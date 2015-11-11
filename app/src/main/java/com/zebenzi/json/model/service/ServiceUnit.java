package com.zebenzi.json.model.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vaugan.Nayagar on 2015/11/09.
 */
public class ServiceUnit {

    @SerializedName("UnitId")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("UnitCounter")
    private int unitCounter;
    @SerializedName("AddedDate")
    private String addedDate;
    @SerializedName("UpdatedDate")
    private String updatedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitCounter() {
        return unitCounter;
    }

    public void setUnitCounter(int unitCounter) {
        this.unitCounter = unitCounter;
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
