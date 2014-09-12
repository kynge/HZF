package com.whty.qd.upay;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.InstallUtils;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.devices.receiver.SdcardStatusReceiver;
import com.whty.qd.pay.info.AccountInfo;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.dialog.CommonDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.home.HomeUtils;
import com.whty.qd.upay.home.MainTabActivity;
import com.whty.qd.upay.reciever.MySmsService;

/**
 * loading页面
 */
public class LoadingActivity extends BaseActivity {
	private static final int LOGIN_ERROR = 0;
	private static final int LOGIN_SUCCESS = 1;
	private Context context;
	private CommonDialog commonDialog;
	private boolean isFromSetting = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_layout);
		context = this;
		checkNet();
		
		Intent intent = new Intent(LoadingActivity.this,MySmsService.class);
		LoadingActivity.this.startService(intent);
	}

	private void checkNet() {
		if (!InstallUtils.isNetworkOk(context)) {
			commonDialog = new CommonDialog(context);
			commonDialog.setCancelable(false);
			commonDialog.showDialog(context.getString(R.string.network_wrong),
					context.getString(R.string.text_confirm),
					new OnClickListener() {

						public void onClick(View v) {
							Intent intent = new Intent(Settings.ACTION_SETTINGS);
							startActivity(intent);
							isFromSetting = true;
						}
					}, new OnClickListener() {

						public void onClick(View v) {
							Process.killProcess(Process.myPid());
						}
					});
		} else {
			loadThread.start();
		}
	}

	private Thread loadThread = new Thread() {

		@Override
		public void run() {
			Thread.currentThread();
			try {
				sleep(50 * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isAutoLoginNeeded();
		}
	};

	/**
	 * @Title: redirect
	 * @Description: 跳到主页
	 * @param intent
	 */
	private void redirect(Intent intent) {
		if (null == intent) {
			intent = new Intent();
		}
		intent.setClass(context, MainTabActivity.class);
		startActivity(intent);
		finish();
	}

	private void redirect() {
		redirect(null);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (isFromSetting) {
			isFromSetting = false;
			if (InstallUtils.isNetworkOk(context)) {
				commonDialog.dismissDialog();
				isAutoLoginNeeded();
			}
		}
	}

	/**
	 * @Title: isAutoLoginNeeded
	 * @Description: 是否自动登录
	 */
	private void isAutoLoginNeeded() {
		if (AccountPref.get(context).isAutoLogin()) {
			String phoneNo = AccountPref.get(context).getPhoneNo();
			String loginPwd = AccountPref.get(context).getLoginPwd();
			Autologin(phoneNo, loginPwd);//loginPwd已经用md5加密
		} else {
			redirect();
		}
	}
	
	/**
	 * 自动登录
	 */
	private void Autologin(final String phoneNo, final String loginPwd) {
		// 1 phoneNo 手机号码 C 11 手机号码
		// 2 loginPwd 登录密码 C 32 采用固MD5算法加密。
		// 3 para 参数 C 固定值：mobileLogon
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("loginPwd", loginPwd));
		params.add(new BasicNameValuePair("para", "mobileLogon"));
		
		Looper.prepare();
		// 发送网络请求
		HttpUtil.HttpsPost(context, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				ApplicationConfig.isLogon = false;
				handler.sendEmptyMessage(LOGIN_ERROR);
			}

			@Override
			public void data(byte[] data, int length, String url,
					int requestId, int actionId) {
				try {
					Log.e("loading auto login", "data:" + new String(data));
					JSONObject jsonObject = new JSONObject(new String(data));
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					if (RetCode.OK.equals(retCode)) {

						String userName = jsonObject.optString("userName");
						String accountId = jsonObject.optString("accountId");
						double balance = jsonObject.optDouble("balance", 0);
						String userId = jsonObject.optString("userId");

						AccountPref account = AccountPref.get(context);
						account.setUserName(userName);
						account.setAccountId(accountId);
						account.setBalance(balance);
						account.setUserId(userId);

						account.setPhoneNo(phoneNo);
						// 此处采用广播的方式发出登录成功通知，因为首页采用的是TabActivity，
						// 无法拦截onActivityResult,接收次通知的广播注册在BaseActivity
						// 中，需要接收此广播继承实现onReceiveLoginSuccess()方法即可
						if (ApplicationConfig.isLogon == false) {
							ApplicationConfig.accountInfo = new AccountInfo();
							ApplicationConfig.accountInfo.setPhone(phoneNo);
							ApplicationConfig.accountInfo.setRealName(userName);
							ApplicationConfig.accountInfo.setUserId(userId);
						}
						ApplicationConfig.isLogon = true;
//						HomeUtils.userLoginPwdCache = Constant.md5(loginPwd);
						
						HomeUtils.userLoginPwdCache = loginPwd;

						Intent intent = new Intent();
						intent.putExtra("userName", userName);
						intent.putExtra("phoneNo", phoneNo);
						intent.putExtra("balance", balance);
						handler.obtainMessage(LOGIN_SUCCESS, intent)
								.sendToTarget();
					} else {
						handler.sendEmptyMessage(LOGIN_ERROR);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, true);
		
		Looper.loop();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGIN_ERROR:
				redirect();
			case LOGIN_SUCCESS:
				Intent intent = (Intent) msg.obj;
				redirect(intent);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	
	
}
