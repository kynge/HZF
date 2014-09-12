package com.whty.qd.upay.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.SecurityUtil;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.info.AccountInfo;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.UpayApplication;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.MobileNumberUtils;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.common.methods.PublicMethod;
import com.whty.qd.upay.home.HomeUtils;
import com.whty.qd.upay.reciever.SMSReceiver;

/**
 * @ClassName: LoginActivity
 * @Description:登录模块
 * @author chenzhenlin
 * @date 2013-8-8 下午1:12:28
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener {
	private static final String TAG = "LoginActivity";

	protected static final int SHOW_TOAST = 0;

	private static final int REQUEST_REGISTER = 0;

	/** @Fields mContext : 上下文 */
	private Context mContext;

	/** @Fields titleLayout : 标题布局 */
	private RelativeLayout titleLayout;
	/** @Fields left_linear : 左侧返回 */
	private LinearLayout left_linear;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields findPsdTv : 找回密码 */
	private TextView lblFindPwd;

	/** @Fields spinnerLayout : 下拉布局 */
	private LinearLayout spinnerLayout;
	/** @Fields spinnerBtn : 下拉更多手机号选项 */
	private Button spinnerBtn;
	/** @Fields txtPhoneNo : 手机号 */
	private EditText txtPhoneNo;
	/** @Fields txtPwd : 密码 */
	private EditText txtPwd;
	/** @Fields checkAutoLogin : 自动登录 */
	private CheckBox checkAutoLogin;
	/** @Fields checkShowPwd : 显示密码 */
	private CheckBox checkShowPwd;
	/** @Fields btnRegister : 注册按钮 */
	private Button btnRegister;
	/** @Fields btnLogin : 登录按钮 */
	private Button btnLogin;

	private Map<String, ?> savedPhoneNumMap = new HashMap<String, Object>();
	/**
	 * 1 表示未下拉 0 表示下拉
	 */
	private int SPINNER_CLICK = 1;

	private String phoneNo;

	private String loginPwd;

	private CommonProgressDialog dialog;
	
	 private SMSReceiver smsReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		init();
		setContentView(R.layout.login_layout);
		initComponents();
	}

	/**
	 * @Title: init
	 * @Description: 初始化数据
	 */
	private void init() {
		mContext = this;
		
		
		smsReceiver = new SMSReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE+1);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
        
//        if(ApplicationConfig.isLogon){
//			HomeUtils.isNeedRefreshRec = true;
//			HttpUtil.closeSession2(1,ApplicationConfig.accountInfo.getPhone());
//			AccountPref pre = AccountPref.get(mContext);
//			ApplicationConfig.isLogon = false;
//			ApplicationConfig.accountInfo = null;
//			pre.setAutoLogin(false);
//        }
        
        timer.schedule(task, 0);
	}
	
	private Timer timer = new Timer(true);
	
	
	//任务
	private TimerTask task = new TimerTask(){

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			handler2.sendMessage(msg);
		}
		
	};
	
	private Handler handler2 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e("handler2", "handler2");
			HttpUtil.closeSession(1);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
//	       if(ApplicationConfig.isLogon){
//				HomeUtils.isNeedRefreshRec = true;
//				HttpUtil.closeSession2(1,ApplicationConfig.accountInfo.getPhone());
//				AccountPref pre = AccountPref.get(mContext);
//				ApplicationConfig.isLogon = false;
//				ApplicationConfig.accountInfo = null;
//				pre.setAutoLogin(false);
//	        }
	}

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
		timer.cancel();
	}
	/**
	 * @Title: initComponents
	 * @Description: 初始化组件
	 */
	private void initComponents() {
		titleLayout = (RelativeLayout) findViewById(R.id.layout);
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
		lblTitle.setText(R.string.user_login);
		lblFindPwd = (TextView) titleLayout.findViewById(R.id.text_minor);
		// lblFindPwd.setText(R.string.text_get_code_back);
		lblFindPwd.setOnClickListener(this);

		spinnerLayout = (LinearLayout) findViewById(R.id.spinnerLayout);
		spinnerBtn = (Button) findViewById(R.id.spinner);
		spinnerBtn.setOnClickListener(this);
		txtPhoneNo = (EditText) findViewById(R.id.phoneNu);
		txtPhoneNo.setOnClickListener(this);
		txtPhoneNo.setOnClickListener(this);
		txtPhoneNo.setOnFocusChangeListener(this);
		txtPwd = (EditText) findViewById(R.id.psd);
//		txtPwd.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				String temp = s.toString();
//				if (temp.length() - 1 < 0) {
//					return;
//				}
//				String tem = temp.substring(temp.length() - 1, temp.length());
//				char[] temC = tem.toCharArray();
//				int mid = temC[0];
//				if (mid == 32) {
//					s.delete(temp.length() - 1, temp.length());
//				}
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//
//		});
		txtPwd.setOnFocusChangeListener(this);
		checkAutoLogin = (CheckBox) findViewById(R.id.autoLogin);
		checkAutoLogin
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (SPINNER_CLICK == 0) {
							SPINNER_CLICK = 1;
//							spinnerBtn
//									.setBackgroundResource(R.drawable.spinner_def);
							spinnerLayout.setVisibility(View.INVISIBLE);
						}
					}
				});
		checkShowPwd = (CheckBox) findViewById(R.id.showPsd);
		btnRegister = (Button) findViewById(R.id.register);
		btnLogin = (Button) findViewById(R.id.login);
		checkShowPwd
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (SPINNER_CLICK == 0) {
							SPINNER_CLICK = 1;
//							spinnerBtn
//									.setBackgroundResource(R.drawable.spinner_def);
							spinnerLayout.setVisibility(View.INVISIBLE);
						}
						if (isChecked) {// 显示密码
//							txtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							txtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  
						} else {
//							txtPwd.setInputType(InputType.TYPE_CLASS_TEXT
//									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							txtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); 
						}
					}
				});
		btnRegister.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		setShowPhone();// 设置显示的号码
	}

	/**
	 * 设置显示的号码
	 */
	private void setShowPhone() {
		savedPhoneNumMap = getSharedPreferences("SavePhone", MODE_PRIVATE)
				.getAll();
		if (savedPhoneNumMap.size() != 0) {
			String encodePhone = (String) savedPhoneNumMap.get("1");
//			String phone = SecurityUtil.decode(encodePhone);
			Log.v("silen", "setShowPhone = " + encodePhone);
			txtPhoneNo.setText(encodePhone);
		} else {
			txtPhoneNo.setText("");
		}
	}

	@Override
	public void onClick(View v) {

		if (v == lblFindPwd) {// 找回密码
			Intent intent1 = new Intent();
			intent1.setClass(mContext, FindPasswordActivity.class);
			startActivity(intent1);
		} else if (v == spinnerBtn) {// 已保存手机号
			if (savedPhoneNumMap.size() == 0) {
				PublicMethod.makeToastText(mContext, "no_phoneNum_record");
				spinnerLayout.setVisibility(View.INVISIBLE);
			} else {
				if (SPINNER_CLICK == 1) {
					SPINNER_CLICK = 0;
//					spinnerBtn.setBackgroundResource(R.drawable.spinner_foc);
					showPhoneLayout();// 显示所有登陆过的电话号码
					spinnerLayout.setVisibility(View.VISIBLE);
				} else if (SPINNER_CLICK == 0) {
					SPINNER_CLICK = 1;
//					spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
					spinnerLayout.setVisibility(View.INVISIBLE);
				}
			}
		} else if (v == btnRegister) {// 注册
			if(!Constant.ZHU_XIAO_TEST){
				PublicMethod.addActivity(this);
				startActivityForResult(
						new Intent(mContext, RegisterActivity.class),
						REQUEST_REGISTER);
			}else{
				//注销
//				if (!HomeUtils.isUserLogin()) {
//					Toast.makeText(mContext, "未登录", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if(ApplicationConfig.accountInfo.getPhone()!=null
//						&&ApplicationConfig.accountInfo.getPhone().length()>0){
//					phoneNo = ApplicationConfig.accountInfo.getPhone();
//				}else if(txtPhoneNo.getText().toString()!=null
//						&&txtPhoneNo.getText().toString().length()>0){
//					phoneNo = txtPhoneNo.getText().toString();
//				}
				
				HomeUtils.isNeedRefreshRec = true;
				HttpUtil.closeSession2(1,phoneNo);
				AccountPref pre = AccountPref.get(mContext);
				ApplicationConfig.isLogon = false;
	//			ApplicationConfig.accountInfo = null;
				pre.setAutoLogin(false);
			}
		} else if (v == btnLogin) {// 登录
			readInput();
			if (validateInput()) {
				SharedPreferences savePhoneSharePre = getSharedPreferences("SavePhone", MODE_PRIVATE);
				Editor savePhoneEditor = savePhoneSharePre.edit();
				savePhoneEditor.putString("1", phoneNo);
				savePhoneEditor.commit();
				login();
			}
		} else if (v == left_linear) {// 返回按钮
			finish();
			// startActivity(new Intent().setClass(mContext,
			// MainTabActivity.class));
		} else if (v == txtPhoneNo) {// 电话编辑框被点击
			if (SPINNER_CLICK == 0) {
				SPINNER_CLICK = 1;
//				spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
				spinnerLayout.setVisibility(View.INVISIBLE);
			}
		} else if (v == txtPwd) {// 登录密码框被点击
			if (SPINNER_CLICK == 0) {
				SPINNER_CLICK = 1;
//				spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
				spinnerLayout.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * @Title: readInput
	 * @Description: 读取输入
	 */
	private void readInput() {
		phoneNo = txtPhoneNo.getText().toString().trim();
		loginPwd = txtPwd.getText().toString();
	}

	/**
	 * @Title: validateInput
	 * @Description: 验证输入
	 * @return
	 */
	private boolean validateInput() {
		if (phoneNo.trim().equals("")) {
			Toast.makeText(mContext, R.string.phoneNum_null, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!StringUtil.isPhoneValid(phoneNo)) {
			Toast.makeText(mContext, R.string.mobile_number_error,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (!MobileNumberUtils.isMobileNumberSegment(mContext, phoneNo)) {
			Toast.makeText(mContext, R.string.mobile_segment_error,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (loginPwd.equals("")) {
			Toast.makeText(mContext, R.string.fill_in_psd, Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (loginPwd.length() < 6 || loginPwd.length() > 20) {
			Toast.makeText(mContext, R.string.user_pswd_illegality,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 显示所有登陆过的电话号码
	 */
	private void showPhoneLayout() {
		spinnerLayout.removeAllViews();
		for (int i = 0; i < savedPhoneNumMap.size(); i++) {
			final View view = LayoutInflater.from(mContext).inflate(
					R.layout.phone_number_item, null);
			final TextView text = (TextView) view.findViewById(R.id.text);
			final RelativeLayout layout = (RelativeLayout) view
					.findViewById(R.id.layout);
			final String encodePhone = (String) savedPhoneNumMap.get(""
					+ (i + 1));
			final String phone = encodePhone;//SecurityUtil.decode(encodePhone);
			text.setText(phone);
			final ImageView image = (ImageView) view.findViewById(R.id.image);
			image.setVisibility(View.GONE);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					txtPwd.setText("");
					txtPhoneNo.setText(phone);
					spinnerLayout.setVisibility(View.INVISIBLE);
//					spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
				}
			});
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						layout.setBackgroundResource(R.color.trac_title_click_backg);
						image.setBackgroundResource(R.drawable.delete_icon_focus);
						text.setTextColor(getResources().getColor(
								R.color.gffffff));
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						layout.setBackgroundResource(R.drawable.linear_frame);
						image.setBackgroundResource(R.drawable.list_delete_default);
						text.setTextColor(getResources().getColor(
								R.color.g000000));
					}
					return false;
				}
			});

			final int num = i + 1;// num 要删除的那项
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.v("silen", "delete num = " + num);
					Log.v("silen", "savedPhoneNumMap.size = "
							+ savedPhoneNumMap.size());
					if (savedPhoneNumMap.size() == 2) {
						String phone1;
						if ((num) == 1) {
							phone1 = (String) savedPhoneNumMap.get("2");
						}
					} else if (savedPhoneNumMap.size() == 3) {
						String phone1, phone2;
						if ((num) == 1) {
							phone1 = (String) savedPhoneNumMap.get("2");
							phone2 = (String) savedPhoneNumMap.get("3");
						} else if ((num) == 2) {
							phone2 = (String) savedPhoneNumMap.get("3");
						}
					} else if (savedPhoneNumMap.size() == 4) {
						String phone1, phone2, phone3;
						if ((num) == 1) {
							phone1 = (String) savedPhoneNumMap.get("2");
							phone2 = (String) savedPhoneNumMap.get("3");
							phone3 = (String) savedPhoneNumMap.get("4");
						} else if ((num) == 2) {
							phone2 = (String) savedPhoneNumMap.get("3");
							phone3 = (String) savedPhoneNumMap.get("4");
						} else if ((num) == 3) {
							phone3 = (String) savedPhoneNumMap.get("4");
						}
					} else if (savedPhoneNumMap.size() == 5) {
						String phone1, phone2, phone3, phone4;
						if ((num) == 1) {
							phone1 = (String) savedPhoneNumMap.get("2");
							phone2 = (String) savedPhoneNumMap.get("3");
							phone3 = (String) savedPhoneNumMap.get("4");
							phone4 = (String) savedPhoneNumMap.get("5");
						} else if ((num) == 2) {
							phone2 = (String) savedPhoneNumMap.get("3");
							phone3 = (String) savedPhoneNumMap.get("4");
							phone4 = (String) savedPhoneNumMap.get("5");
						} else if ((num) == 3) {
							phone3 = (String) savedPhoneNumMap.get("4");
							phone4 = (String) savedPhoneNumMap.get("5");
						} else if ((num) == 4) {
							phone4 = (String) savedPhoneNumMap.get("5");
						}
					}
					spinnerLayout.removeView(view);
					if (spinnerLayout.getChildCount() == 0) {
//						spinnerBtn
//								.setBackgroundResource(R.drawable.spinner_def);
					}
					setShowPhone();// 设置显示的号码
					showPhoneLayout();
				}

			});
			spinnerLayout.addView(view);
		}
	}

	/**
	 * @Title: doLogin
	 * @Description: 向服务器发送登录请求
	 * @param phoneNumStr
	 *            电话号码
	 * @param loginPsdStr
	 *            登录密码
	 */
	private void login() {
		dialog = new CommonProgressDialog(mContext);
		dialog.showDialog(mContext.getString(ResourceUtils.getResourceId(
				ResourceUtils.packageNameWithR, "string", "logining")));
		// 1 phoneNo 手机号码 C 11 手机号码
		// 2 loginPwd 登录密码 C 32 采用固MD5算法加密。
		// 3 para 参数 C 固定值：mobileLogon
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("loginPwd", Constant.md5(loginPwd)));
		params.add(new BasicNameValuePair("para", "mobileLogon"));
		// 发送网络请求
		HttpUtil.HttpsPost(mContext, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				dialog.dialogDismiss();
				handler.obtainMessage(SHOW_TOAST, description).sendToTarget();
			}

			@Override
			public void data(byte[] data, int length, String url,
					int requestId, int actionId) {
				Log.e(TAG, "data:" + new String(data));
				dialog.dialogDismiss();
				try {
					JSONObject jsonObject = new JSONObject(new String(data));
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					if (RetCode.OK.equals(retCode)) {
						String userName = jsonObject.optString("userName");
						String accountId = jsonObject.optString("accountId");
						String acctId = jsonObject.optString("acctId");
						String payPwd = jsonObject.optString("payPwd");
						
						String signMSg = jsonObject.optString("signMsg");
						Log.e("signMSg", ""+signMSg);
						//保存登录返回的 accountId
						UpayApplication.ACCOUNT_ID = acctId;
						Log.e("UpayApplication.ACCOUNT_ID:", UpayApplication.ACCOUNT_ID);
						double balance = jsonObject.optDouble("balance", 0);
						String userId = jsonObject.optString("userId");

						AccountPref account = AccountPref.get(mContext);
						account.setUserName(userName);
						account.setAccountId(accountId);
						account.setBalance(balance);
						account.setUserId(userId);
						account.setSignMsg(signMSg);	
						account.setPhoneNo(phoneNo);
						if (checkAutoLogin.isChecked()) {
							account.setLoginPwd(Constant.md5(loginPwd));
							account.setAutoLogin(true);
						} else {
							account.setAutoLogin(false);
							account.removeLoginPwd();
						}
						// 此处采用广播的方式发出登录成功通知，因为首页采用的是TabActivity，
						// 无法拦截onActivityResult,接收次通知的广播注册在BaseActivity
						// 中，需要接收此广播继承实现onReceiveLoginSuccess()方法即可
						if (ApplicationConfig.isLogon == false) {
							ApplicationConfig.accountInfo = new AccountInfo();
							ApplicationConfig.accountInfo.setPhone(phoneNo);
							ApplicationConfig.accountInfo.setRealName(userName);
						}
						ApplicationConfig.isLogon = true;
						HomeUtils.userLoginPwdCache = Constant.md5(loginPwd);
						HomeUtils.userPayPwdCache = payPwd;
						SharedPreferences savePhoneSharePre = getSharedPreferences("SavePhone", MODE_PRIVATE);
						Editor savePhoneEditor = savePhoneSharePre.edit();
						savePhoneEditor.putString("1", phoneNo);
						savePhoneEditor.commit();
						Intent intent = new Intent();
						intent.setAction(Constant.ACTION_LOGIN_SUCCESS);
						intent.putExtra("userName", userName);
						intent.putExtra("phoneNo", phoneNo);
						intent.putExtra("balance", balance);
						sendBroadcast(intent);
						finish();
					} else {
						handler.obtainMessage(SHOW_TOAST,
								jsonObject.optString(RetCode.ERROR_MSG))
								.sendToTarget();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, true);

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST:
				if (null != msg.obj) {
					Toast.makeText(mContext, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && SPINNER_CLICK == 0) {
			SPINNER_CLICK = 1;
//			spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
			spinnerLayout.setVisibility(View.INVISIBLE);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (SPINNER_CLICK == 0) {
			SPINNER_CLICK = 1;
//			spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
			spinnerLayout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (SPINNER_CLICK == 0) {
			SPINNER_CLICK = 1;
//			spinnerBtn.setBackgroundResource(R.drawable.spinner_def);
			spinnerLayout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case REQUEST_REGISTER:
				phoneNo = data.getStringExtra("phoneNo");
				loginPwd = data.getStringExtra("loginPwd");
				Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
				if (!TextUtils.isEmpty(phoneNo) && !TextUtils.isEmpty(loginPwd)) {
					login();
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
