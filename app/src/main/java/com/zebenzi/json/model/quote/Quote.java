package com.zebenzi.json.model.quote;

import com.google.gson.annotations.SerializedName;
import com.zebenzi.json.model.service.Service;
import com.zebenzi.json.model.user.User;

/**
 * Created by Vaugan.Nayagar on 2015/11/11.
 */
public class Quote {
    private int quoteId;
    private int price;
    private String workDate;
    private Work work;
    private Service service;
    private User[] availableWorkers;

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User[] getAvailableWorkers() {
        return availableWorkers;
    }

    public void setAvailableWorkers(User[] availableWorkers) {
        this.availableWorkers = availableWorkers;
    }
}
