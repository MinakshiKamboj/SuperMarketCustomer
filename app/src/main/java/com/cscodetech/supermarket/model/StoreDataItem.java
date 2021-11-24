package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

public class StoreDataItem{

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("address")
	private String address;

	@SerializedName("IS_OPEN")
	private String isOpen;

	@SerializedName("mobile")
	private String mobile;


	@SerializedName("store_image")
	private String vImg;

	@SerializedName("Total_Items")
	private String totalitem;

	@SerializedName("star")
	private String star;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getvImg() {
		return vImg;
	}

	public void setvImg(String vImg) {
		this.vImg = vImg;
	}

	public String getTotalitem() {
		return totalitem;
	}

	public void setTotalitem(String totalitem) {
		this.totalitem = totalitem;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}
}