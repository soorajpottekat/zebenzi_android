package com.zebenzi.job;

import org.junit.Test;
import com.zebenzi.job.JobRequest;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Vaugan.Nayagar on 2015/10/01.
 *
 * Unit tests for JobRequest class.
 */
public class JobRequestTest{
    JobRequest j;
    GregorianCalendar g;

    public JobRequestTest() {
        g = new GregorianCalendar();
        j = new JobRequest(1, 1, g);
    }

    @Test
    public void getServiceIdTest() {
        assertEquals(1, j.getServiceId());
    }

    @Test
    public void getServiceDefaultIdTest() {
        assertEquals(1, j.getServiceDefaultId());
    }

    @Test
    public void getDateTimeTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        assertEquals(sdf.format(g.getTime()), j.getDateTime());
    }

    @Test
    public void getTimeTest() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        assertEquals(sdf.format(g.getTime()), j.getTime());
    }

    @Test
    public void setServiceIdTest() {
        int newServiceId = j.getServiceId()+1;
        j.setServiceId(newServiceId);
        assertEquals(newServiceId, j.getServiceId());
    }


}
