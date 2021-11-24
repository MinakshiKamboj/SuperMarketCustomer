package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

public class PackageBannerItem{

	@SerializedName("img")
	private String img;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	public String getImg(){
		return img;
	}

	public String getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}
}