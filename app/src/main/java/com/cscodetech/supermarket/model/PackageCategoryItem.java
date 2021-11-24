package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

public class PackageCategoryItem{

	@SerializedName("cat_status")
	private String catStatus;

	@SerializedName("cat_img")
	private String catImg;

	@SerializedName("cat_name")
	private String catName;

	@SerializedName("id")
	private String id;

	public String getCatStatus(){
		return catStatus;
	}

	public String getCatImg(){
		return catImg;
	}

	public String getCatName(){
		return catName;
	}

	public String getId(){
		return id;
	}
}