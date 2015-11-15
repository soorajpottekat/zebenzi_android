package com.zebenzi.job;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 *
 * Class to hold the parameters for a job request.
 * These parameters will be used to request a Quote from the server.
 */
public class JobRequest {
    int serviceId;
    int serviceDefaultId; //We don't use the actual number of units, but instead the ID of the "ServiceDefault"
    GregorianCalendar date;
    GregorianCalendar time;

    public JobRequest(int serviceId, int serviceDefaultId, GregorianCalendar date, GregorianCalendar time) {
        this.serviceId = serviceId;
        this.serviceDefaultId = serviceDefaultId;
        this.date = date;
        this.time = time;
    }

    public int getServiceDefaultId() {
        return serviceDefaultId;
    }

    public String getDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        return sdf.format(date.getTime());
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(time.getTime());
    }
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }


}
