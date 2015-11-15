package com.zebenzi.json.model.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vaugan.Nayagar on 2015/11/09.
 */
public class Service {

    private int serviceId;
    private String serviceName;
    private ServiceUnit serviceUnit;
    private ServicePrice servicePrice;
    private ServiceDefaults[] serviceDefaults;
    private String addedDate;
    private String updatedDate;
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
