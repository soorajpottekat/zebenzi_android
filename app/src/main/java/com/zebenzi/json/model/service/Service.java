package com.zebenzi.json.model.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vaugan.Nayagar on 2015/11/09.
 */
public class Service {

    @SerializedName("ServiceId")
    private int serviceId;
    @SerializedName("ServiceName")
    private String serviceName;

    @SerializedName("ServiceUnit")
    private ServiceUnit serviceUnit;
    @SerializedName("ServicePrice")
    private ServicePrice servicePrice;
    @SerializedName("ServiceDefaults")
    private ServiceDefaults[] serviceDefaults;

    @SerializedName("AddedDate")
    private String addedDate;
    @SerializedName("UpdatedDate")
    private String updatedDate;
    @SerializedName("HoursPerUnit")
    private int hoursPerUnit;


    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceUnit getServiceUnit() {
        return serviceUnit;
    }

    public void setServiceUnit(ServiceUnit serviceUnit) {
        this.serviceUnit = serviceUnit;
    }

    public ServicePrice getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(ServicePrice servicePrice) {
        this.servicePrice = servicePrice;
    }

    public ServiceDefaults[] getServiceDefaults() {
        return serviceDefaults;
    }

    public void setServiceDefaults(ServiceDefaults[] serviceDefaults) {
        this.serviceDefaults = serviceDefaults;
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

    public int getHoursPerUnit() {
        return hoursPerUnit;
    }

    public void setHoursPerUnit(int hoursPerUnit) {
        this.hoursPerUnit = hoursPerUnit;
    }
}
