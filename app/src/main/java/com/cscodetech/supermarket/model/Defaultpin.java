package com.cscodetech.supermarket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Defaultpin {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("d_charge")
    @Expose
    private String dCharge;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_primary")
    @Expose
    private String isPrimary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDCharge() {
        return dCharge;
    }

    public void setDCharge(String dCharge) {
        this.dCharge = dCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

}