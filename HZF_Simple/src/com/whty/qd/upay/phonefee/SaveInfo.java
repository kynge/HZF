package com.whty.qd.upay.phonefee;

import java.io.Serializable;

/**
 * 保存信息封装类
 */
public class SaveInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String num;
	private String id;

	public SaveInfo() {
		super();
	}

	public SaveInfo(String name, String num, String id) {
		super();
		this.name = name;
		this.num = num;
		this.id = id;
	}
	
	public SaveInfo(String num, String id) {
		super();
		this.num = num;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
