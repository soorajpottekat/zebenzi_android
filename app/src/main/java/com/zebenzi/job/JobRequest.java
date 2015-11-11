package com.zebenzi.job;

import com.zebenzi.service.ServicesHardcoded;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 *
 * Right now, we just need this to calculate price, but we might want to save quotes in future.
 */
public class JobRequest {
    ServicesHardcoded svc;
    int units;
    GregorianCalendar date;
    GregorianCalendar time;

    public JobRequest(ServicesHardcoded svc, int units, GregorianCalendar date, GregorianCalendar time) {
        this.svc = svc;
        this.units = units;
        this.date = date;
        this.time = time;
    }

    public String getPrice() {
        return String.format ("%.02f", this.units * this.svc.getUnitPrice());
    }

    public String getServiceName() {
        return svc.getName();
    }

    public int getUnits() {
        return units;
    }

    public String getDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        return sdf.format(date.getTime());
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(time.getTime());
    }
}
