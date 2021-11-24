package com.cscodetech.supermarket.model;

import com.google.gson.annotations.SerializedName;

public class FaqDataItem{

	@SerializedName("question")
	private String question;

	@SerializedName("answer")
	private String answer;

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	public String getQuestion(){
		return question;
	}

	public String getAnswer(){
		return answer;
	}

	public String getId(){
		return id;
	}

	public String getStatus(){
		return status;
	}
}