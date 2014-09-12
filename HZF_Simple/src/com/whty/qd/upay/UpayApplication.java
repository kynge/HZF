package com.whty.qd.upay;

import android.app.Application;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.JsonOperation;
import com.whty.qd.pay.common.ResourceUtils;

/**
 * 应用程序Application，初始化数据
 */
public class UpayApplication extends Application {
	public void onCreate() {
		super.onCreate();
		ResourceUtils.setPackageName("com.whty.qd.upay");
		Log.init(true);
		Thread.currentThread().setName("QPay");
		JsonOperation.isStaticKey = true;
	}
	
	public static String ACCOUNT_ID = "681";
}
