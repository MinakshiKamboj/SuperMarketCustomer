
package com.cscodetech.supermarket.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PrescriptionOrderProductList {

    @SerializedName("customer_address")
    private String mCustomerAddress;
    @SerializedName("order_date")
    private String mOrderDate;
    @SerializedName("Order_Status")
    private String mOrderStatus;
    @SerializedName("Prescription_image_list")
    private List<String> mPrescriptionImageList;

    public String getCustomerAddress() {
        return mCustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        mCustomerAddress = customerAddress;
    }

    public String getOrderDate() {
        return mOrderDate;
    }

    public void setOrderDate(String orderDate) {
        mOrderDate = orderDate;
    }

    public String getOrderStatus() {
        return mOrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        mOrderStatus = orderStatus;
    }

    public List<String> getPrescriptionImageList() {
        return mPrescriptionImageList;
    }

    public void setPrescriptionImageList(List<String> prescriptionImageList) {
        mPrescriptionImageList = prescriptionImageList;
    }

}
