package com.whty.qd.upay.phonefee;

import java.io.Serializable;

/**
 * 电话费充值面值Spinner适配器
 */
public class PhoneCardPrice implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String productId;
	private long purchasePrice; // 购买价格
	private long realPrice; // 面额

	public PhoneCardPrice() {

	}

	public long getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(long purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public long getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(long realPrice) {
		this.realPrice = realPrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return name + "," + productId + "," + purchasePrice + "," + realPrice;
	}

}
