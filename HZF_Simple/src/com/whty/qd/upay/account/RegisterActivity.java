package com.whty.qd.upay.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.MobileNumberUtils;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.HttpUtil;

/**
 * 注册
 * 
 * @author 吴非
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "RegisterActivity";
	private Context mContext;
	private RelativeLayout titleLayout;
	private TextView titleTv;
	private LinearLayout left_linear;
	private View btnActive;
	private Button getAuthCodeBtn, limitTimeBtn;
	/** @Fields checkAgree : 同意慧支付协议 */
	private CheckBox checkAgree;
	private EditText phoneNuEdt;
	private EditText authCodeEdt;
	private TextView readTreatyTv;
	private CommonProgressDialog dialog;

	/** @Fields SHOW_TOAST : 显示Toast */
	protected static final int SHOW_TOAST = 0;
	/** @Fields SUCCESS_GET_AUTH_CODE : 成功发送验证码请求 */
	protected static final int SUCCESS_GET_AUTH_CODE = 1;
	/** @Fields LENGTH_AUTO_CODE : 验证码长度 */
	private static final int LENGTH_AUTO_CODE = 6;
	/** @Fields REQUEST_COMPLETE_INFO : 完善信息请求 */
	private static final int REQUEST_COMPLETE_INFO = 0;
	/** @Fields REQUEST_GET_CHECK : 阅读协议 */
	public static final int REQUEST_GET_CHECK = 1;
	private int timeInt;
	private Timer timer;
	/** @Fields phoneNo : 手机号 */
	private String phoneNo;
	/** @Fields activeCode : 激活码/验证码 */
	private String activeCode;
	protected String actUuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = RegisterActivity.this;
		setContentView(R.layout.register_layout);

		init();
	}

	private void init() {
		timer = new Timer();
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		titleTv = (TextView) titleLayout.findViewById(R.id.text_main_title);
		titleTv.setText(R.string.user_register);
		left_linear = (LinearLayout) findViewById(R.id.left_linear);
		btnActive = findViewById(R.id.active);
		btnActive.setOnClickListener(this);
		btnActive.setVisibility(View.VISIBLE);
		left_linear.setOnClickListener(this);
		getAuthCodeBtn = (Button) findViewById(R.id.getAuthCode);
		getAuthCodeBtn.setOnClickListener(this);
		checkAgree = (CheckBox) findViewById(R.id.agreePay);
		phoneNuEdt = (EditText) findViewById(R.id.phoneNu);
		authCodeEdt = (EditText) findViewById(R.id.auth_code);
		readTreatyTv = (TextView) findViewById(R.id.read_treaty);
		readTreatyTv.setOnClickListener(this);
		limitTimeBtn = (Button) findViewById(R.id.limitTime);
		limitTimeBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == left_linear) {// 返回
			finish();
		} else if (v == getAuthCodeBtn) {// 获取验证码
			readInput();
			if (validatePhoneNo()) {
				getAuthCode();
			}
		} else if (v == btnActive) {// 完成
			readInput();
			if (validateInput()) {
				redirect();
			}
		} else if (v == readTreatyTv) {// 阅读协议
			Intent intent = new Intent(mContext, AgreementActivity.class);
			startActivityForResult(intent, REQUEST_GET_CHECK);
		}
	}

	/**
	 * @Title: readInput
	 * @Description: 读取输入
	 */
	private void readInput() {
		phoneNo = phoneNuEdt.getText().toString().trim();
		activeCode = authCodeEdt.getText().toString();
	}

	/**
	 * @Title: validateInput
	 * @Description: 验证输入
	 * @return
	 */
	private boolean validateInput() {
		// 校验手机号
		if (validatePhoneNo()) {
			// 校验验证码
			if (TextUtils.isEmpty(activeCode)
					|| activeCode.length() != LENGTH_AUTO_CODE) {
				Toast.makeText(mContext, R.string.auth_code_segment_error,
						Toast.LENGTH_SHORT).show();
				return false;
			}
			// 是否勾选同意慧支付协议
			if (!checkAgree.isChecked()) {
				Toast.makeText(mContext, getResources().getString(R.string.agree_protocol), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Title: redirect
	 * @Description: 页面跳转
	 */
	private void redirect() {
		Intent intent = new Intent(mContext, CompleteInfoActivity.class);
		intent.putExtra("phoneNo", phoneNo);
		intent.putExtra("activeCode", activeCode);
		intent.putExtra("actUuid", actUuid);
		startActivityForResult(intent, REQUEST_COMPLETE_INFO);
	}

	/**
	 * @Title: validatePhoneNo
	 * @Description: 验证电话号码是否合法
	 * @return
	 */
	private boolean validatePhoneNo() {
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
		return true;
	}

	/**
	 * @Title: getAuthCode
	 * @Description: 获取验证码
	 */
	private void getAuthCode() {
		dialog = new CommonProgressDialog(mContext);
		dialog.showDialog(mContext.getString(ResourceUtils.getResourceId(
				ResourceUtils.packageNameWithR, "string", "get_auth_code")));
		// 1 phoneNo 手机号码 C 11 手机号码
		// 2 para 参数 C 固定值：activeFromMobile
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("para", "activeFromMobile"));
		// 发送网络请求
		HttpUtil.HttpsPost(mContext, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				Log.e(TAG, description);
				dialog.dialogDismiss();
				handler.obtainMessage(
						SHOW_TOAST,
						TextUtils.isEmpty(description) ? "connection_fail"
								: description).sendToTarget();
			}

			@Override
			public void data(byte[] data, int length, String url,
					int requestId, int actionId) {
				String json = new String(data);
				Log.e(TAG, "data:" + json);
				dialog.dialogDismiss();
				try {
					JSONObject jsonObject = new JSONObject(json);
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					if (RetCode.OK.equals(retCode)) {
						actUuid = jsonObject.optString("actUuid");
						handler.obtainMessage(SUCCESS_GET_AUTH_CODE)
								.sendToTarget();
					} else {
						String errmsg = jsonObject.optString(RetCode.ERROR_MSG);
						handler.obtainMessage(SHOW_TOAST, errmsg)
								.sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.obtainMessage(SHOW_TOAST, e.getMessage())
							.sendToTarget();
				}
			}
		}, true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case REQUEST_GET_CHECK:
				checkAgree.setChecked(true);
				break;
			case REQUEST_COMPLETE_INFO:// 完善信息时直接关闭页面
				setResult(RESULT_OK, data);
				finish();
				break;
			default:
				break;
			}
		} else {
			if (requestCode == REQUEST_COMPLETE_INFO) {
				startActivity(new Intent(mContext, RegisterActivity.class));
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
			case SUCCESS_GET_AUTH_CODE:
				doSuccessGet();
				break;
			case 10:
				String timeStr = mContext.getResources().getString(
						R.string.limit_time, timeInt);
				limitTimeBtn.setText(timeStr);
				break;
			case 20:
				getAuthCodeBtn.setVisibility(View.VISIBLE);
				limitTimeBtn.setVisibility(View.GONE);
				timer.cancel();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * @Title: doSuccessGet
	 * @Description: 成功获取注册码后做界面刷新
	 */
	protected void doSuccessGet() {
		timer = new Timer();
		getAuthCodeBtn.setVisibility(View.GONE);
		timeInt = 120;
		String timeStr = getString(R.string.limit_time, timeInt);
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
	}

}
