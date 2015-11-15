package com.zebenzi.utils;

/**
 * Created by Vaugan.Nayagar on 2015/11/15.
 */
public class ZebenziException extends RuntimeException{

    public String message;

    public ZebenziException(String message){
        this.message = message;
    }

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }

}