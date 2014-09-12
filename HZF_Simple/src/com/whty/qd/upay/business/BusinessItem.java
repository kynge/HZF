package com.whty.qd.upay.business;

import java.io.Serializable;

/**
 * 业务相关封装类
 * @author Cai
 */
public class BusinessItem implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String JSON_BUSINESSID = "businessid";
	public static final String JSON_BUSINESSNAME = "businessname";
	public static final String JSON_CLIENTINVOKE = "clientinvoke";
	public static final String JSON_BUSINESSICON = "businessicon";
	public static final String JSON_CLIENTURL = "clientdownurl";
	public static final String JSON_CLIENTVER = "clientversion";
	public static final String JSON_CLIENTAID = "clientaid";
	public static final String JSON_METHOD = "method"; 
	public static final String JSON_BUSINESSMINICON = "businessminicon"; 
	public static final String JSON_PAYMETHOD = "paymethod"; 
	private String mBusinessId;
	private String mName;
	private String mPackageName;
	private String mClassName;
	private String mAction;
	private String mIcon;
	private String mDownUrl;
	private String mVersion;
	private int mAid;
	private String mMethod;
	private String mCode;
	private boolean isShow  =false;
	private String businessminicon;
	private String paymethod; //支付支持介质. 0为支持，1为不支持
	
	public String getBusinessminicon() {
		return businessminicon;
	}

	public void setBusinessminicon(String businessminicon) {
		this.businessminicon = businessminicon;
	}
    
	


	public String getPaymethod() {
		return paymethod;
	}

	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}

	/**
	 * @param mBusinessId the mBusinessId to set
	 */
	public void setBusinessId(String businessId) {
		this.mBusinessId = businessId;
	}

	/**
	 * @return the mBusinessId
	 */
	public String getBusinessId() {
		return mBusinessId;
	}

	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}
	
	
	public String getmCode() {
		return mCode;
	}

	public void setmCode(String mCode) {
		this.mCode = mCode;
	}

	/**
	 * @param mName the mName to set
	 */
	public void setName(String name) {
		this.mName = name;
	}
	
	/**
	 * @param mPackageName the mPackageName to set
	 */
	public void setPackageName(String packageName) {
		this.mPackageName = packageName;
	}
	
	/**
	 * @return the mPackageName
	 */
	public String getPackageName() {
		return mPackageName;
	}
	
	/**
	 * @param mAction the mAction to set
	 */
	public void setAction(String action) {
		this.mAction = action;
	}
	
	/**
	 * @return the mAction
	 */
	public String getAction() {
		return mAction;
	}
	/**
	 * @param mClassName the mClassName to set
	 */
	public void setClassName(String className) {
		this.mClassName = className;
	}
	/**
	 * @return the mClassName
	 */
	public String getClassName() {
		return mClassName;
	}
	/**
	 * @param mIcon the mIcon to set
	 */
	public void setIcon(String icon) {
		this.mIcon = icon;
	}
	
	/**
	 * @return the mIcon
	 */
	public String getIcon() {
		return mIcon;
	}

	public String getDownUrl() {
		return mDownUrl;
	}

	public void setDownUrl(String mDownUrl) {
		this.mDownUrl = mDownUrl;
	}

	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String mVersion) {
		this.mVersion = mVersion;
	}
	
	public int getAid() {
		return mAid;
	}

	public void setAid(int mAid) {
		this.mAid = mAid;
	}

	public String getMethod() {
		return mMethod;
	}

	public void setMethod(String mMethod) {
		this.mMethod = mMethod;
	}

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
	
}
