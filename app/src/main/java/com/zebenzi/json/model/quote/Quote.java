package com.zebenzi.json.model.quote;

import com.google.gson.annotations.SerializedName;
import com.zebenzi.json.model.service.Service;
import com.zebenzi.json.model.user.User;

/**
 * Created by Vaugan.Nayagar on 2015/11/11.
 */
public class Quote {
    @SerializedName("QuoteId")
    private int quoteId;
    @SerializedName("Price")
    private int price;
    @SerializedName("WorkDate")
    private String workDate;
    @SerializedName("Work")
    private Work work;
    @SerializedName("Service")
    private Service service;
    @SerializedName("AvailableWorkers")
    private User[] users;

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

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }
}
