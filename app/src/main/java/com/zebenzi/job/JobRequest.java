package com.zebenzi.job;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 *
 * Class to hold the parameters for a job request.
 * These parameters will be used to request a Quote from the server.
 */
public class JobRequest implements Serializable{
    int serviceId;
    int serviceDefaultId; //We don't use the actual number of units, but instead the ID of the "ServiceDefault"
    GregorianCalendar dateTime;

    public JobRequest(int serviceId, int serviceDefaultId, GregorianCalendar dateTime) {
        this.serviceId = serviceId;
        this.serviceDefaultId = serviceDefaultId;
        this.dateTime = dateTime;
    }

    public int getServiceDefaultId() {
        return serviceDefaultId;
    }

    public String getDateTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        return sdf.format(dateTime.getTime());
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(dateTime.getTime());
    }
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }


}
