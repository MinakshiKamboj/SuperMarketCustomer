package com.cscodetech.supermarket.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PackItem {

	@SerializedName("PriceData")
	private PriceData priceData;

	@SerializedName("Package_Category")
	private List<PackageCategoryItem> packageCategory;

	@SerializedName("Package_Banner")
	private List<Banner> packageBanner;

	public PriceData getPriceData(){
		return priceData;
	}

	public List<PackageCategoryItem> getPackageCategory(){
		return packageCategory;
	}

	public List<Banner> getPackageBanner(){
		return packageBanner;
	}
}