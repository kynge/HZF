package com.whty.qd.upay.account;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.whty.qd.pay.common.Log;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.ApplicationUrl;
import com.whty.qd.pay.common.JsonOperation;
import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.SecurityUtil;
import com.whty.qd.pay.common.errorreport.ErrorReporter;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.common.net.ResourceLoader;
import com.whty.qd.pay.common.net.WebSettings;
import com.whty.qd.pay.info.AccountInfo;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.MobileNumberUtils;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.PublicMethod;
import com.whty.qd.upay.common.StringUtil;
/**
 * 找回密码
 * @author 吴非
 *
 */
public class FindPasswordActivity extends BaseActivity implements OnClickListener{
	private Context mContext;
	private RelativeLayout titleLayout;
	private TextView titleTv;
	private LinearLayout left_linear;
	private LinearLayout right_linear;
	private Button okBtn;
	private Button backBtn;
	private Button getAuthCodeBtn, limitTimeBtn;
	private EditText phoneNuEdt;
	private EditText authCodeEdt;
	private EditText newPsdEdt;
	private EditText confirmNewPsdEdt;
	private CommonProgressDialog dialog;
	private int timeInt;
	private Timer timer;
//	private ErrorReporter er;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password_layout);
		mContext = this;
		
		init();
	}
	
	private void init() {
//		er = ErrorReporter.getInstance(mContext);
		timer = new Timer();
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		titleTv = (TextView) titleLayout.findViewById(R.id.text_main_title);
		titleTv.setText(R.string.find_password);
		left_linear = (LinearLayout) findViewById(R.id.left_linear);
		right_linear = (LinearLayout) findViewById(R.id.right_linear);
		left_linear.setOnClickListener(this);
		right_linear.setOnClickListener(this);
		backBtn = (Button) titleLayout.findViewById(R.id.button_title_back);
		okBtn = (Button) titleLayout.findViewById(R.id.button_title_ok);
		okBtn.setVisibility(View.VISIBLE);
		getAuthCodeBtn = (Button) findViewById(R.id.getAuthCode);
		getAuthCodeBtn.setOnClickListener(this);
		phoneNuEdt = (EditText) findViewById(R.id.phoneNu);
		authCodeEdt = (EditText) findViewById(R.id.auth_code);
		newPsdEdt = (EditText) findViewById(R.id.newPsd);
		newPsdEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String temp = s.toString();
				if (temp.length()-1 < 0) {
					return;
				}
		        String tem = temp.substring(temp.length()-1, temp.length());
		        char[] temC = tem.toCharArray();
		        int mid = temC[0];
		        if (mid == 32) {
		        	s.delete(temp.length()-1, temp.length());
		        }
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
		confirmNewPsdEdt = (EditText) findViewById(R.id.confirmNewPsd);
		confirmNewPsdEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String temp = s.toString();
				if (temp.length()-1 < 0) {
					return;
				}
		        String tem = temp.substring(temp.length()-1, temp.length());
		        char[] temC = tem.toCharArray();
		        int mid = temC[0];
		        if (mid == 32) {
		        	s.delete(temp.length()-1, temp.length());
		        }
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
		limitTimeBtn = (Button) findViewById(R.id.limitTime);
		limitTimeBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final String phoneNumStr = phoneNuEdt.getText().toString().trim();
		final String authCodeStr = authCodeEdt.getText().toString().trim();
		final String newPsdStr = newPsdEdt.getText().toString();
		final String confirmNewPsdStr = confirmNewPsdEdt.getText().toString();
		
		switch (v.getId()) {
		case R.id.left_linear://返回按钮
			finish();
			break;
		case R.id.right_linear://完成按钮	
			//校验手机号码
			if (phoneNumStr.trim().equals("")) {
				Toast.makeText(mContext, R.string.phoneNum_null, Toast.LENGTH_SHORT).show();
				return;
			} else if (!StringUtil.isPhoneValid(phoneNumStr)) {
				Toast.makeText(mContext, R.string.mobile_number_error, Toast.LENGTH_SHORT).show();
				return;
			} else if (!MobileNumberUtils.isMobileNumberSegment(mContext, phoneNumStr)) {
				Toast.makeText(mContext, R.string.mobile_segment_error, Toast.LENGTH_SHORT).show();
				return;
			}
			
			//校验验证码
			if (authCodeStr.equals("")) {
				Toast.makeText(mContext, R.string.auth_code_null, Toast.LENGTH_SHORT).show();
				return;
			}
			if (authCodeStr.length() != 6) {
				Toast.makeText(mContext, R.string.auth_code_segment_error, Toast.LENGTH_SHORT).show();
				return;
			}
			
			//校验新密码
			if (newPsdStr.equals("") || newPsdStr.length()<6 || newPsdStr.length()>20) {
				Toast.makeText(mContext, R.string.input_psd, Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (confirmNewPsdStr.equals("")) {
				Toast.makeText(mContext, R.string.input_confirm_psd, Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (!newPsdStr.equals(confirmNewPsdStr)) {
				Toast.makeText(mContext, R.string.confirm_psd_error, Toast.LENGTH_SHORT).show();
				return;
			}
			
			//调用找回密码接口
			try {
				dialog = new CommonProgressDialog(mContext);
				dialog.showDialog(mContext.getString(ResourceUtils.getResourceId(
						ResourceUtils.packageNameWithR, "string", "text_get_code_back")));
				
				JSONObject data1 = new JSONObject();
				data1.put("TRANS_ID", "00");
				data1.put("LOGIN_ACCOUNT", phoneNumStr);
				data1.put("CODE", authCodeStr);
				data1.put("PASSWORD", SecurityUtil.encryptMD5(newPsdStr));
				JSONObject root1 = JsonOperation.buildJSON("ACTION_INFO", data1, this,
						null, true);
				root1.put("ACTION_NAME", "USER_FORGETPWD");
				Log.v("silen", "data == " + data1.toString());
				
				ResourceLoader.getInstance(this)
				.startLoadingResource(
						ApplicationUrl.UserInfoURL, "POST",
						null, null, (root1.toString()).getBytes(), 0,
						WebSettings.LOAD_NO_CACHE, false, false,
						findPsdListener, 1);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			break;
		case R.id.getAuthCode://获取验证码
			//校验手机号码
			timeInt = 120;
			if (phoneNumStr.trim().equals("")) {
				Toast.makeText(mContext, R.string.phoneNum_null, Toast.LENGTH_SHORT).show();
				return;
			} else if (!StringUtil.isPhoneValid(phoneNumStr)) {
				Toast.makeText(mContext, R.string.mobile_number_error, Toast.LENGTH_SHORT).show();
				return;
			} else if (!MobileNumberUtils.isMobileNumberSegment(mContext, phoneNumStr)) {
				Toast.makeText(mContext, R.string.mobile_segment_error, Toast.LENGTH_SHORT).show();
				return;
			}
			
			//调用获取验证码接口
			try {
				dialog = new CommonProgressDialog(mContext);
				dialog.showDialog(mContext.getString(ResourceUtils.getResourceId(
						ResourceUtils.packageNameWithR, "string", "get_auth_code")));
				
				JSONObject data = new JSONObject();
				data.put("TRANS_ID", "00");
				data.put("LOGIN_ACCOUNT", phoneNumStr);
				data.put("TYPE", "2");
				JSONObject root = JsonOperation.buildJSON("ACTION_INFO", data, this, null, true);
				root.put("ACTION_NAME", "USER_VERIFY");
				Log.v("silen", "data == " + data.toString());
				ResourceLoader.getInstance(this).startLoadingResource(
						ApplicationUrl.UserInfoURL, "POST",
						null, null, (root.toString()).getBytes(), 0,
						WebSettings.LOAD_NO_CACHE, false, false,
						getAuthCodeListener, 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 获取验证码
	 * 数据解析/处理
	 */
	private OnLoadListener getAuthCodeListener = new OnLoadListener() {
		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			dialog.dialogDismiss();
			PublicMethod.makeToastText(mContext, "connection_fail");
//			StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
//			String errorloc = "CLASS:" + ste.getClassName() + " METHOD:"
//					+ ste.getMethodName() + " LINE:" + ste.getLineNumber();
//			er.report("Http Error", "USER_VERIFY", "999", errorloc,
//					mContext.getString(R.string.network_requet_error));
			return;
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			dialog.dialogDismiss();
			String json = new String(data);
			Log.v("silen", "json == " + json.toString());
			try {
				JSONObject object = new JSONObject(json);
				String code = object.optString("ACTION_RETURN_CODE");
				if (code.equals("000000")) {
					timer = new Timer();
					getAuthCodeBtn.setVisibility(View.GONE);
					String timeStr = mContext.getResources().getString(R.string.limit_time,timeInt);
					limitTimeBtn.setText(timeStr);
					limitTimeBtn.setVisibility(View.VISIBLE);
					TimerTask timerTask = new TimerTask() {
						@Override
						public void run() {
							Message msg = new Message();
							if (timeInt == 1) {
								msg.what = 20;
							} else {
								msg.what = 10;
								timeInt--;
							}
							handler.sendMessage(msg);
						}
					};
					timer.schedule(timerTask, 1000, 1000);
					PublicMethod.makeToastText(mContext, "send_code_to_phone");
				} else {
					String msgType = object.optString("ACTION_RETURN_TIPSTYPE"); 
					String msg = object.optString("ACTION_RETURN_TIPS");
//					StackTraceElement ste = Thread.currentThread()
//							.getStackTrace()[2];
//					String errorloc = "CLASS:" + ste.getClassName()
//							+ " METHOD:" + ste.getMethodName() + " LINE:"
//							+ ste.getLineNumber();
//					er.report("Data Error", "USER_VERIFY", "999",
//							errorloc, msg + "(" + code + ")");
					showMarkedWords(msgType, msg, code);
				}
			} catch (JSONException e) {
//				StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
//				String errorloc = "CLASS:" + ste.getClassName() + " METHOD:"
//						+ ste.getMethodName() + " LINE:" + ste.getLineNumber();
//				er.report("Json Error", "USER_VERIFY",
//						"999", errorloc, e.getMessage());
			}
		}
	};
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 10) {
				String timeStr = mContext.getResources().getString(R.string.limit_time,timeInt);
				limitTimeBtn.setText(timeStr);
			} else if (msg.what == 20) {
				getAuthCodeBtn.setVisibility(View.VISIBLE);
				limitTimeBtn.setVisibility(View.GONE);
				timer.cancel();
			}
		}
		
	};
	
	/**
	 * 找回密码
	 * 数据解析/处理
	 */
	private OnLoadListener findPsdListener = new OnLoadListener() {
		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			dialog.dialogDismiss();
//			StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
//			String errorloc = "CLASS:" + ste.getClassName() + " METHOD:"
//					+ ste.getMethodName() + " LINE:" + ste.getLineNumber();
//			er.report("Http Error", "USER_FORGETPWD", "999", errorloc,
//					mContext.getString(R.string.network_requet_error));
			return;
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			dialog.dialogDismiss();
			String json = new String(data);
			Log.v("silen", "json == " + json.toString());
			try {
				JSONObject object = new JSONObject(json);
				String code = object.optString("ACTION_RETURN_CODE");
				if (code.equals("000000")) {
					PublicMethod.makeToastText(mContext, "update_psd_success");
					finish();
				} else {
					String msgType = object.optString("ACTION_RETURN_TIPSTYPE"); 
					String msg = object.optString("ACTION_RETURN_TIPS");
//					StackTraceElement ste = Thread.currentThread()
//							.getStackTrace()[2];
//					String errorloc = "CLASS:" + ste.getClassName()
//							+ " METHOD:" + ste.getMethodName() + " LINE:"
//							+ ste.getLineNumber();
//					er.report("Data Error", "USER_FORGETPWD", "999",
//							errorloc, msg + "(" + code + ")");
					showMarkedWords(msgType, msg, code);
					
					if (code.equals("200002")) {//如果是验证码错误就清空验证码 
						authCodeEdt.setText("");
					}
				}
			} catch (JSONException e) {
//				StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
//				String errorloc = "CLASS:" + ste.getClassName() + " METHOD:"
//						+ ste.getMethodName() + " LINE:" + ste.getLineNumber();
//				er.report("Json Error", "USER_FORGETPWD",
//						"999", errorloc, e.getMessage());
			}
		}
	};


	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
	}
}
