
import com.zebenzi.utils.TimeFormat;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Vaugan.Nayagar on 2015/12/14.
 */
public class TimeFormatTest{

    @Test
    public void testGetPrettyDate(){

        System.out.println(TimeFormat.getPrettyDate("2015-12-11T15:48:00"));
        assertEquals(TimeFormat.getPrettyDate("2015-12-11T15:48:00"), "Fri, 11 Dec 2015");

   System.out.println(TimeFormat.getPrettyDate("2015-12-11T15:48:00"));

    }
}
