package com.whty.qd.upay.account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.SuperDateSetDialog;
import com.whty.qd.upay.common.SuperDateSetDialog.ConfirmListener;
import com.whty.qd.upay.common.methods.HttpUtil;

public class ModifyUserInfo extends BaseActivity implements OnClickListener {

	protected static final String TAG = "ModifyUserInfo";
	protected static final int SHOW_TOAST = 0;
	protected static final int MODIFY_SUCCESS = 1;
	LinearLayout backArea;
	View confirm;
	CheckBox showPsdChk;

	Context mContext;
	private TextView lblUserName;
	private TextView lblPhoneNo;
	private EditText txtPwd;
	private EditText txtTel;
	private EditText txtAddress;
	private EditText txtBirth;
	private String loginPwd;
	private String tel;
	private String addr;
	private String birthStr;
	private RadioGroup rGroup;
	private int sexuality;
	private String sex;
	int nowStartYear, nowStartMonth, nowStartDay;
	int nowEndYear, nowEndMonth, nowEndDay;
	private Button btnClock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_userinfo_layout);
		mContext = this;
		initView();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.text_main_title);
		title.setText("基本信息");
		backArea = (LinearLayout) findViewById(R.id.left_linear);
		confirm = findViewById(R.id.confirm);

		lblUserName = (TextView) findViewById(R.id.user_name);
		lblUserName.setText(ApplicationConfig.accountInfo.getRealName());
		lblPhoneNo = (TextView) findViewById(R.id.phone);
		lblPhoneNo.setText(ApplicationConfig.accountInfo.getPhone());

		txtPwd = (EditText) findViewById(R.id.psd);
		txtTel = (EditText) findViewById(R.id.tel);
		txtAddress = (EditText) findViewById(R.id.address);
		txtBirth = (EditText) findViewById(R.id.birthday);
		rGroup = (RadioGroup) findViewById(R.id.user_sexuality_rdg);
		btnClock = (Button) findViewById(R.id.spinner);

		Calendar c = Calendar.getInstance();
		nowStartYear = c.get(Calendar.YEAR);
		nowStartMonth = c.get(Calendar.MONTH) + 1;
		nowStartDay = c.get(Calendar.DAY_OF_MONTH);
		btnClock.setOnClickListener(this);
		txtBirth.setOnClickListener(this);
		backArea.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btnClock || v == txtBirth) {
			SuperDateSetDialog d = new SuperDateSetDialog(mContext);
			d.setListener(new ConfirmListener() {

				@Override
				public void onFinish(int year, int month, int day) {
					nowStartYear = year;
					nowStartMonth = month;
					nowStartDay = day;
					String birthday = year + "-" + intMoD2Str(month) + "-"
							+ intMoD2Str(day);
					txtBirth.setText(birthday);
				}
			});
			d.showDialog(nowStartYear, nowStartMonth, nowStartDay);
		} else if (v == backArea) {
			finish();
		} else if (v == confirm) {
			if (validate()) {
				sendRequest();
			}
		}
	}

	protected boolean validate() {
		loginPwd = txtPwd.getText().toString().trim();
		tel = txtTel.getText().toString().trim();
		addr = txtAddress.getText().toString().trim();
		birthStr = txtBirth.getText().toString().trim();
		sexuality = rGroup.getCheckedRadioButtonId();
		sex = sexuality == R.id.radio1 ? "1" : "0";
		Log.e(TAG, "sexStr====" + sex);

		if (TextUtils.isEmpty(loginPwd)) {
			showToast("请输入密码！");
			return false;
		}
		
//		if (TextUtils.isEmpty(birthStr)) {
//			showToast("请填写生日！");
//			return false;
//		}
//		
//		if (TextUtils.isEmpty(addr)) {
//			showToast("请填写地址！");
//			return false;
//		}
//
//		if (TextUtils.isEmpty(tel)) {
//			showToast("请输入联系电话！");
//			return false;
//		}

		return true;
	}

	protected void sendRequest() {
		showDialog();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo",
				ApplicationConfig.accountInfo.getPhone()));
		params.add(new BasicNameValuePair("loginPwd", Constant.md5(loginPwd)));
		params.add(new BasicNameValuePair("sex", sex));
		params.add(new BasicNameValuePair("addr", addr));
		params.add(new BasicNameValuePair("tel", tel));
		params.add(new BasicNameValuePair("birthday", birthStr.replace("-", "")));
		params.add(new BasicNameValuePair("para", "updateAccountInfo"));
		HttpUtil.HttpsPost(mContext, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				dismissDialog();
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
				dismissDialog();
				try {
					JSONObject jsonObject = new JSONObject(json);
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					if (RetCode.OK.equals(retCode)) {
						handler.obtainMessage(MODIFY_SUCCESS).sendToTarget();
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
		}, false);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST:
				if (null != msg.obj) {
					showToast(msg.obj.toString());
				}
				break;
			case MODIFY_SUCCESS:
				showToast("修改成功");
				finish();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private void showToast(String str) {
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 将月份或日期整数，转换成带0或不带0的字符串
	 * 
	 * @param d
	 *            //月份或日期
	 */
	private String intMoD2Str(int d) {
		return (d < 10 ? "0" : "") + d;
	}

}
