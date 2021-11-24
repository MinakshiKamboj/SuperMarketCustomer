package com.cscodetech.supermarket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeDatum {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("product_data")
    @Expose
    private List<Medicine> productData = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Medicine> getProductData() {
        return productData;
    }

    public void setProductData(List<Medicine> productData) {
        this.productData = productData;
    }

}