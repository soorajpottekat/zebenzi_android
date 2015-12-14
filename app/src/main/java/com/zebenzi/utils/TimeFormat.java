package com.zebenzi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class to convert between server time and app time
 *
 * Created by Vaugan.Nayagar on 2015/12/14.
 */
public final class TimeFormat {

    public static String getPrettyDate(String serverDate) {
        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date d = serverFormat.parse(serverDate);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy");
            return format.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPrettyTime(String serverDate) {
        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date d = serverFormat.parse(serverDate);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
