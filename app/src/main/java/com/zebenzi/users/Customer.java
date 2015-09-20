package com.zebenzi.users;


/**
 * Created by vaugan.nayagar on 2015/09/06.
 */
public class Customer extends User {

    private static Customer instance;
    // Constructor to convert JSON object into a Java class instance
    private Customer(){
        super();
   }

    public static Customer getInstance()
    {
        if (instance == null)
        {
            instance = new Customer();
        }
        return instance;
    }


    public static String getCustomerFirstName(){
        return getFirstName();
    }
    public static String getCustomerLastName(){
        return getLastName();
    }
    public static String getCustomerMobileNumber(){
        return getMobileNumber();
    }
    public static String getCustomerEmail(){
        return getEmail();
    }
    public static String getCustomerAddress(){
        return getAddress();
    }
    public static String getCustomerId(){
        return getId();
    }

}
