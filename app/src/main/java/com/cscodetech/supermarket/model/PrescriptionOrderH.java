
package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class PrescriptionOrderH {

    @SerializedName("PrescriptionOrderProductList")
    private List<PrescriptionOrderProductList> mPrescriptionOrderProductList;
    @SerializedName("ResponseCode")
    private String mResponseCode;
    @SerializedName("ResponseMsg")
    private String mResponseMsg;
    @SerializedName("Result")
    private String mResult;

    public List<PrescriptionOrderProductList> getPrescriptionOrderProductList() {
        return mPrescriptionOrderProductList;
    }

    public void setPrescriptionOrderProductList(List<PrescriptionOrderProductList> prescriptionOrderProductList) {
        mPrescriptionOrderProductList = prescriptionOrderProductList;
    }

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public String getResponseMsg() {
        return mResponseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        mResponseMsg = responseMsg;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

}
