
package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class OrderProductDatum {

    @SerializedName("Product_discount")
    private String mProductDiscount;
    @SerializedName("Product_image")
    private String mProductImage;
    @SerializedName("Product_name")
    private String mProductName;
    @SerializedName("Product_price")
    private String mProductPrice;
    @SerializedName("Product_quantity")
    private String mProductQuantity;
    @SerializedName("Product_total")
    private String mProductTotal;
    @SerializedName("Product_variation")
    private String mProductVariation;

    public String getProductDiscount() {
        return mProductDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        mProductDiscount = productDiscount;
    }

    public String getProductImage() {
        return mProductImage;
    }

    public void setProductImage(String productImage) {
        mProductImage = productImage;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public void setProductPrice(String productPrice) {
        mProductPrice = productPrice;
    }

    public String getProductQuantity() {
        return mProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        mProductQuantity = productQuantity;
    }

    public String getProductTotal() {
        return mProductTotal;
    }

    public void setProductTotal(String productTotal) {
        mProductTotal = productTotal;
    }

    public String getProductVariation() {
        return mProductVariation;
    }

    public void setProductVariation(String productVariation) {
        mProductVariation = productVariation;
    }

}
