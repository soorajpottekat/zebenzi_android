package com.zebenzi.network;

/**
 * Created by Vaugan.Nayagar on 2015/10/06.
 */
public enum HttpContentTypes {
    FORM_DATA("form-data"),
    X_WWW_FORM_URLENCODED("x-www-form-urlencoded"),
    RAW("raw"),
    BINARY("binary");

    private final String contentType;

    HttpContentTypes(String name) {
        this.contentType = name;
    }

    public String getString() {
        return contentType;
    }

}
