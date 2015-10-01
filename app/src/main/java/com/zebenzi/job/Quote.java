package com.zebenzi.job;

import com.zebenzi.Service.Services;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 *
 * Right now, we just need this to calculate price, but we might want to save quotes in future.
 */
public class Quote {
    Services svc;
    int units;
    Date date;
    Time time;

    public Quote(Services svc, int units, Date date, Time time) {
        this.svc = svc;
        this.units = units;
        this.date = date;
        this.time = time;
    }
    public Quote(Services svc, int units) {
        this.svc = svc;
        this.units = units;
    }
    public float getPrice() {
        return this.units * this.svc.getUnitPrice();
    }

    public String getServiceName() {
        return svc.getName();
    }

    public int getUnits() {
        return units;
    }
}
