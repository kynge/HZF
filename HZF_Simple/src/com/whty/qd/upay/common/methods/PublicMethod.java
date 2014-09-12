package com.whty.qd.upay.common.methods;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import com.whty.qd.pay.common.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.whty.qd.pay.cardmanage.ReadDovilaCardService;
import com.whty.qd.pay.cardmanage.ReadSDCardService;
import com.whty.qd.pay.common.ApplicationConfig;

import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.devices.BatteryElectricUtils;
import com.whty.qd.pay.devices.impl.hardware.sdcard.CupMobileAdapter;
import com.whty.qd.pay.info.AccountInfo;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.dialog.CommonDialog;
import com.whty.qd.upay.home.HomeUtils;

public class PublicMethod {

	private static final String TAG = "PublicMethod";

	public static List<Activity> list;

	public static String LocationCity = "";

	public static String RiskSrialNumber = "";

	/**
	 * 吐出内容
	 * 
	 * @param mContext
	 * @param text
	 *            内容
	 */
	public static void makeToastText(Context mContext, String text) {
		Toast.makeText(
				mContext,
				mContext.getString(ResourceUtils.getResourceId(
						ResourceUtils.packageNameWithR, "string", text)),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 主程序退出
	 */
	public static void appExit(final Context mContext) {
		final CommonDialog commonDialog = new CommonDialog(mContext);
		commonDialog.showDialog(mContext.getString(R.string.text_app_exit),
				mContext.getString(R.string.text_confirm),
				new OnClickListener() {

					public void onClick(View v) {
						commonDialog.dismissDialog();
						try {
//							CupMobileAdapter.getInstance().freeSDMem();
//							ApplicationConfig.dcl = null;
//							mContext.stopService(new Intent(mContext,
//									ReadDovilaCardService.class));
//							mContext.stopService(new Intent(mContext,
//									ReadSDCardService.class));
//							BatteryElectricUtils.stopService(mContext);
							Log.e("", "start to exit and close session");
							if(HomeUtils.isUserLogin()){
								HttpUtil.closeSession(0);
							}else{
								Process.killProcess(Process.myPid());
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
//							Process.killProcess(Process.myPid());
						}
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();
					}
				});
	}

	public static void addActivity(Activity activity) {
		list = new ArrayList<Activity>();
		list.add(activity);
	}

	public static void removeActivity() {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).finish();
			}
		}
	}

	/**
	 * 获取手机CPU信息
	 * 
	 * @return 手机CPU信息
	 */
	public static String getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		Log.e(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);
		return cpuInfo[0] + " " + cpuInfo[1];
	}

	/**
	 * 登录后保存AccountInfo
	 * 
	 * @param encodeInfoStr
	 * @return
	 * @throws JSONException
	 */
	public static void saveAccountInfo(String encodeInfoStr)
			throws JSONException {
		JSONObject jsonData = new JSONObject(encodeInfoStr);

		ApplicationConfig.isLogon = true;
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setQdoneId(jsonData.optString("QD_ID"));
		accountInfo.setPhone(jsonData.optString("PHONE"));
		accountInfo.setLoginAccount(jsonData.optString("LOGIN_ACCOUNT"));
		accountInfo.setCredit(jsonData.optString("CREDIT"));
		accountInfo.setNickName(jsonData.optString("USER_NICKNAME"));
		accountInfo.setLastTime(jsonData.optString("LAST_LOGIN_TIME"));
		accountInfo.setUserId(jsonData.optString("USER_ID"));
		accountInfo.setRealName(jsonData.optString("REAL_NAME"));
		accountInfo.setWelcome(jsonData.optString("WELCOME"));
		accountInfo.setCfNum(jsonData.optString("CERTIFICATE_NUM"));
		accountInfo.setGrade(jsonData.optString("GRADE"));
		accountInfo.setSessionID(jsonData.optString("SESSION_ID"));
		accountInfo.setNo_read_count(jsonData.optString("NO_READ_COUNT"));
		accountInfo.setImage_path(jsonData.optString("IMAGE_PATH"));
		accountInfo.setSex(jsonData.optString("SEX"));
		accountInfo.setAddress(jsonData.optString("ADDRESS"));
		accountInfo.setEmail(jsonData.optString("EMAIL"));
		accountInfo.setBirthday(jsonData.optString("BIRTHDAY"));
		ApplicationConfig.accountInfo = accountInfo;
	}
}
