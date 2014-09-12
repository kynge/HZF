package com.whty.qd.upay.finance.record;

import java.io.Serializable;

/**
 * 订单确认页
 */
public class SimpleKeyValue implements Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	private String value;

	public SimpleKeyValue() {
		
	}

	public SimpleKeyValue(String[] item) {
		super();
		this.key = item[0];
		this.value = item[1];
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
