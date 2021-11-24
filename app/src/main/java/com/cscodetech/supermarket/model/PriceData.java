package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

public class PriceData{

	@SerializedName("utprice")
	private String utprice;

	@SerializedName("ukms")
	private String ukms;

	@SerializedName("afprice")
	private String afprice;

	public String getUtprice(){
		return utprice;
	}

	public String getUkms(){
		return ukms;
	}

	public String getAfprice(){
		return afprice;
	}
}