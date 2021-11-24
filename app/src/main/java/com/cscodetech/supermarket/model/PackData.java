package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

public class PackData{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("ResultData")
	private PackItem resultData;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public PackItem getResultData(){
		return resultData;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}