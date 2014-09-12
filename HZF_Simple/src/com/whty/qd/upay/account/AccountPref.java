package com.whty.qd.upay.account;

import com.whty.qd.pay.common.SecurityUtil;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountPref {
	private static AccountPref account;
	private static SharedPreferences pref;
	private static Context context;
	
	
	public void setSignMsg(String singMsg){
		set("signMsg", singMsg);
	}
	public String getSignMsg(){
		return get("signMsg");
	}
	public AccountPref(Context ctx) {
		context = ctx;
	}

	public static AccountPref get(Context ctx) {
		if (null == account) {
			account = new AccountPref(ctx);
		}
		return account;
	}

	public static SharedPreferences getPref() {
		if (null == pref) {
			pref = context
					.getSharedPreferences("account", Context.MODE_PRIVATE);
		}
		return pref;
	}

	public void setUserName(String userName) {
		set("userName", userName);

	}

	public String getUserName() {
		return get("userName");
	}

	public void setAccountId(String accountId) {
		set("accountId", accountId);
	}

	public String getAccountId() {
		return get("accountId");
	}

	public void setPhoneNo(String phoneNo) {
		set("phoneNo", SecurityUtil.encode(phoneNo));
	}

	public void setLoginPwd(String password) {
		set("loginPwd", SecurityUtil.encode(password));
	}

	public void setBalance(double balance) {
		set("balance", String.valueOf(balance));
	}

	public double getBalance() {
		try {
			return Double.parseDouble(get("balance"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getPhoneNo() {
		try {
			return SecurityUtil.decode(get("phoneNo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getLoginPwd() {
		try {
			return SecurityUtil.decode(get("loginPwd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setUserId(String userId) {
		set("userId", userId);
	}

	public String getUserId() {
		return get("userId");
	}

	public boolean isAutoLogin() {
		return get("autoLogin", false);
	}

	public void setAutoLogin(boolean value) {
		set("autoLogin", value);
	}

	private void set(String key, String value) {
		getPref().edit().putString(key, value).commit();
	}

	private void set(String key, boolean value) {
		getPref().edit().putBoolean(key, value).commit();
	}

	private String get(String key) {
		return getPref().getString(key, "");
	}

	private boolean get(String key, boolean defValue) {
		return getPref().getBoolean(key, defValue);
	}

	public void removeLoginPwd() {
		remove("loginPwd");
	}

	private void remove(String key) {
		getPref().edit().remove(key).commit();
	}

}
