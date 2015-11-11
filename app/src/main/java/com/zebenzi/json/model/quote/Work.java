package com.zebenzi.json.model.quote;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vaugan.Nayagar on 2015/11/11.
 */
public class Work {
    @SerializedName("ServiceDefaultId")
    private int serviceDefaultId;
    @SerializedName("DefaultValue")
    private int defaultValue;
    @SerializedName("AddedDateTime")
    private String addedDateTime;
    @SerializedName("WorkDate")
    private String updatedDateTime;

    public int getServiceDefaultId() {
        return serviceDefaultId;
    }

    public void setServiceDefaultId(int serviceDefaultId) {
        this.serviceDefaultId = serviceDefaultId;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAddedDateTime() {
        return addedDateTime;
    }

    public void setAddedDateTime(String addedDateTime) {
        this.addedDateTime = addedDateTime;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
