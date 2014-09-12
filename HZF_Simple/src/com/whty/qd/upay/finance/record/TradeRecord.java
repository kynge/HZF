package com.whty.qd.upay.finance.record;

import java.io.Serializable;

public class TradeRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private String orderId;// 订单号
	private String payOrderId;// 插件支付订单号
	private String transId;// 交易类型
	private String title;
	private String date;
	private String money;
	private String state;
	private String stateCode;
	private String[] dataList;
	private String appId;
	private String phoneNo;

	public TradeRecord() {
	}

	public TradeRecord(String title, String date, String money, String state) {
		super();
		this.title = title;
		this.date = date;
		this.money = money;
		this.state = state;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String[] getDataList() {
		return dataList;
	}

	public void setDataList(String[] dataList) {
		this.dataList = dataList;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getPayOrderId() {
		return payOrderId;
	}

	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
