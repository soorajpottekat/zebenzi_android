package com.zebenzi.json.model.user;

import android.graphics.Path;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zebenzi.json.model.job.Job;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Vaugan.Nayagar on 2015/12/17.
 */
public class UserAddressTest {

    UserAddress ua;

    String path = "C:\\Software\\dev\\zebenzi_android\\app\\src\\test\\data\\UserAddress.json";
    File file = new File(path);
    String jsonAddress = getContents(file);


    @Before
    public void setUp() throws Exception {

        Gson gson = new Gson();
        ua = gson.fromJson(jsonAddress, UserAddress.class);
    }

    @After
    public void tearDown() throws Exception {
        ua = null;
    }

    @Test
    public void testGetUrl() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(4, ua.getId());
    }

    @Test
    public void testGetAddressLine1() throws Exception {
        assertEquals("Unit C1, 216 Main Avenue", ua.getAddressLine1());
    }

    @Test
    public void testGetAddressLine2() throws Exception {
        assertEquals("Ferndale", ua.getAddressLine2());

    }

    @Test
    public void testGetAddressSuburb() throws Exception {
        assertEquals("Randburg", ua.getAddressSuburb());

    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals("2194", ua.getCode());

    }

    @Test
    public void testGetAddressProvince() throws Exception {
        assertEquals("Gauteng", ua.getAddressProvince());

    }

    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     *
     * @param aFile is a file which already exists and can be read.
     */
    static public String getContents(File aFile) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
        /*
        * readLine is a bit quirky :
        * it returns the content of a line MINUS the newline.
        * it returns null only for the END of the stream.
        * it returns an empty String if two newlines appear in a row.
        */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString().replace("\r\n", "");
    }
}