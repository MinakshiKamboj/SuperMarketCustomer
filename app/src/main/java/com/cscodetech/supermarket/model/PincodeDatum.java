
package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PincodeDatum {

    @SerializedName("d_charge")
    private String mDCharge;
    @SerializedName("id")
    private String mId;
    @SerializedName("pincode")
    private String mPincode;
    @SerializedName("status")
    private String mStatus;

    public String getDCharge() {
        return mDCharge;
    }

    public void setDCharge(String dCharge) {
        mDCharge = dCharge;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPincode() {
        return mPincode;
    }

    public void setPincode(String pincode) {
        mPincode = pincode;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
