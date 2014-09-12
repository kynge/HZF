package com.whty.qd.upay.finance.record;

import java.io.Serializable;

public class NewTradeRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountSeq;// 账户编号
	private String amount;// 交易金额
	private String changeamount;// 结算金额
	private String createTime;// 交易时间
	private String fee;// 交易手续费
	private String id;// 序号
	private String status;// 交易状态
	private String transId;// 交易编号
	private String transType;// 交易类型
	private String phoneNo;//手机号
	
	public String getAccountSeq() {
		return accountSeq;
	}
	public void setAccountSeq(String accountSeq) {
		this.accountSeq = accountSeq;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getChangeamount() {
		return changeamount;
	}
	public void setChangeamount(String changeamount) {
		this.changeamount = changeamount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
}
