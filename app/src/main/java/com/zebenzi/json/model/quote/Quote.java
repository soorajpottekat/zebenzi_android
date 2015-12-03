package com.zebenzi.json.model.quote;

import com.google.gson.annotations.SerializedName;
import com.zebenzi.json.model.service.Service;
import com.zebenzi.json.model.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    /**
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601#Combined_date_and_time_representations">Combined Date and Time Representations</a>
     */
    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";

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

    //TODO: We need a date util class to convert between server and Calendar formats.
    public String getPrettyDate() {
        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date d = serverFormat.parse(workDate);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy");
            return format.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPrettyTime() {
        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date d = serverFormat.parse(workDate);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
