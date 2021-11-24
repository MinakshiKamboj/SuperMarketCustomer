
package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class OrderProductList {

    @SerializedName("Additional_Note")
    private String mAdditionalNote;
    @SerializedName("Coupon_Amount")
    private String mCouponAmount;
    @SerializedName("customer_address")
    private String mCustomerAddress;
    @SerializedName("Delivery_charge")
    private String mDeliveryCharge;
    @SerializedName("order_date")
    private String mOrderDate;
    @SerializedName("Order_Product_Data")
    private List<OrderProductDatum> mOrderProductData;
    @SerializedName("Order_Status")
    private String mOrderStatus;
    @SerializedName("Order_SubTotal")
    private String mOrderSubTotal;
    @SerializedName("Order_Total")
    private String mOrderTotal;
    @SerializedName("Order_Transaction_id")
    private String mOrderTransactionId;
    @SerializedName("p_method_name")
    private String mPMethodName;
    @SerializedName("Order_flow_id")
    private String orderflowid;

    @SerializedName("comment_reject")
    private String commentReject;

    @SerializedName("description")
    private String description;

    @SerializedName("customer_paddress")
    private String customerPaddress;

    @SerializedName("customer_daddress")
    private String customerDaddress;

    @SerializedName("customer_pmobile")
    private String customerPmobile;

    @SerializedName("customer_dmobile")
    private String customerdmobile;


    @SerializedName("photos")
    private List<String> photos;

    public String getAdditionalNote() {
        return mAdditionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        mAdditionalNote = additionalNote;
    }

    public String getCouponAmount() {
        return mCouponAmount;
    }

    public void setCouponAmount(String couponAmount) {
        mCouponAmount = couponAmount;
    }

    public String getCustomerAddress() {
        return mCustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        mCustomerAddress = customerAddress;
    }

    public String getDeliveryCharge() {
        return mDeliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        mDeliveryCharge = deliveryCharge;
    }

    public String getOrderDate() {
        return mOrderDate;
    }

    public void setOrderDate(String orderDate) {
        mOrderDate = orderDate;
    }

    public List<OrderProductDatum> getOrderProductData() {
        return mOrderProductData;
    }

    public void setOrderProductData(List<OrderProductDatum> orderProductData) {
        mOrderProductData = orderProductData;
    }

    public String getOrderStatus() {
        return mOrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        mOrderStatus = orderStatus;
    }

    public String getOrderSubTotal() {
        return mOrderSubTotal;
    }

    public void setOrderSubTotal(String orderSubTotal) {
        mOrderSubTotal = orderSubTotal;
    }

    public String getOrderTotal() {
        return mOrderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        mOrderTotal = orderTotal;
    }

    public String getOrderTransactionId() {
        return mOrderTransactionId;
    }

    public void setOrderTransactionId(String orderTransactionId) {
        mOrderTransactionId = orderTransactionId;
    }

    public String getPMethodName() {
        return mPMethodName;
    }

    public void setPMethodName(String pMethodName) {
        mPMethodName = pMethodName;
    }

    public String getOrderflowid() {
        return orderflowid;
    }

    public void setOrderflowid(String orderflowid) {
        this.orderflowid = orderflowid;
    }

    public String getCommentReject() {
        return commentReject;
    }

    public void setCommentReject(String commentReject) {
        this.commentReject = commentReject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerPaddress() {
        return customerPaddress;
    }

    public void setCustomerPaddress(String customerPaddress) {
        this.customerPaddress = customerPaddress;
    }

    public String getCustomerDaddress() {
        return customerDaddress;
    }

    public void setCustomerDaddress(String customerDaddress) {
        this.customerDaddress = customerDaddress;
    }

    public String getCustomerPmobile() {
        return customerPmobile;
    }

    public void setCustomerPmobile(String customerPmobile) {
        this.customerPmobile = customerPmobile;
    }

    public String getCustomerdmobile() {
        return customerdmobile;
    }

    public void setCustomerdmobile(String customerdmobile) {
        this.customerdmobile = customerdmobile;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
