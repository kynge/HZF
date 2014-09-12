package com.whty.qd.upay.mybankcard;

import java.io.Serializable;


/**
 * 银行卡信息实体类
 * @author Administrator
 *
 */
public class BankCardNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5116777556214123984L;

	/*
	 * 持卡人
	 */
	private String holder;
	
	/*
	 * 银行卡号
	 */
	private String bankCardNum;
	
	/*
	 * 所属银行
	 */
	private String belongBank;
	

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getBankCardNum() {
		return bankCardNum;
	}

	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
	}

	public String getBelongBank() {
		return belongBank;
	}

	public void setBelongBank(String belongBank) {
		this.belongBank = belongBank;
	}
	
	
	
	
}
