package com.zebenzi.utils;

import com.zebenzi.utils.TimeFormat;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Vaugan.Nayagar on 2015/12/14.
 *
 * Unit tests for TimeFormat class.
 */
public class TimeFormatTest{

    @Test
    public void testGetPrettyDate(){
        assertEquals("Fri, 11 Dec 2015", TimeFormat.getPrettyDate("2015-12-11T15:48:00"));
    }

    @Test
    public void testGetPrettyTime(){
        assertEquals("15:48", TimeFormat.getPrettyTime("2015-12-11T15:48:00"));
    }
}
