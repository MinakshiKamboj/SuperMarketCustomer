package com.cscodetech.supermarket.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Store{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("StoreData")
	private List<StoreDataItem> storeData;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public List<StoreDataItem> getStoreData(){
		return storeData;
	}

	public String getResult(){
		return result;
	}
}