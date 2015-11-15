package com.zebenzi.json.model.user;

/**
 * Created by Vaugan.Nayagar on 2015/11/11.
 */
public class UserAddress {
    private String url;
    private int id;
    private String addressLine1;
    private String addressLine2;
    private String addressSurburb;
    private String code;
    private String addressProvince;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressSuburb() {
        return addressSurburb;
    }

    public void setAddressSuburb(String addressSuburb) {
        this.addressSurburb = addressSuburb;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    @Override
    public String toString(){

        return addressLine1 + "\n" +
               addressLine2 + "\n" +
               addressSurburb + "\n" +
               code + "\n" +
               addressProvince;
    }
}
