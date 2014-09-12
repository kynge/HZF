package com.whty.qd.upay.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.home.HomeUtils;

public class ModifyLoginPwd extends BaseActivity {

	protected static final String TAG = "ModifyLoginPwd";
	protected static final int SHOW_TOAST = 0;
	protected static final int MODIFY_SUCCESS = 1;
	LinearLayout backArea;
	View btnConfirm;
	CheckBox showPsdChk;

	Context mContext;
	private EditText txtPwd;
	private EditText txtNewPwd;
	private EditText txtNewConfirmPwd;
	private String oldPwd;
	private String newPwd;
	private String newConfirmPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_login_pwd_layout);
		mContext = this;
		initView();
	}

	private void initView() {
		TextView title = (TextView) findViewById(R.id.text_main_title);
		title.setText("登录密码");

		backArea = (LinearLayout) findViewById(R.id.left_linear);
		btnConfirm = findViewById(R.id.confirm);
		txtPwd = (EditText) findViewById(R.id.psd);
		txtNewPwd = (EditText) findViewById(R.id.psd_new);
		txtNewConfirmPwd = (EditText) findViewById(R.id.psd_new_confirm);
		showPsdChk = (CheckBox) findViewById(R.id.showPsdChk);
		showPsdChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {// 显示密码
//					txtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//					txtNewPwd
//							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//					txtNewConfirmPwd
//							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					txtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  
					txtNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					txtNewConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
//					txtPwd.setInputType(InputType.TYPE_CLASS_TEXT
//							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
//					txtNewPwd.setInputType(InputType.TYPE_CLASS_TEXT
//							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
//					txtNewConfirmPwd.setInputType(InputType.TYPE_CLASS_TEXT
//							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					txtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());  
					txtNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					txtNewConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
		backArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validate()) {
					sendRequest();
				}
			}
		});
	}

	protected boolean validate() {
		oldPwd = txtPwd.getText().toString().trim();
		newPwd = txtNewPwd.getText().toString().trim();
		newConfirmPwd = txtNewConfirmPwd.getText().toString().trim();
		if (TextUtils.isEmpty(oldPwd)) {
			showToast("请输入原密码！");
			return false;
		}

		if (TextUtils.isEmpty(newPwd)) {
			showToast("请输入新密码！");
			return false;
		}

		if (TextUtils.isEmpty(newConfirmPwd)) {
			showToast("请确认新密码！");
			return false;
		}
		
		if (newPwd.length() < 6 || newPwd.length() > 20) {
			Toast.makeText(mContext, R.string.input_login_pwd, Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!newPwd.equals(newConfirmPwd)) {
			showToast("新密码两次输入不一致！");
			return false;
		}
		
		if(StringUtil.gbk(newPwd)){
			Toast.makeText(mContext, "密码中不能有汉字", Toast.LENGTH_SHORT).show();
		}
		
		if(StringUtil.checkStrong(newPwd) < 2){
			Toast.makeText(mContext, "密码必须由字母、数字和特殊字符中的至少2种组成", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//新密码不能和旧密码相同
		if(oldPwd.equals(newPwd)){
			Toast.makeText(mContext, "新密码不能和旧密码相同", Toast.LENGTH_SHORT).show();
			return false;
		}
		//登录密码不能和支付密码相同
		if(Constant.md5(newPwd).equals(HomeUtils.userPayPwdCache)){
			Toast.makeText(mContext, "登录密码不能和支付密码相同", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	protected void sendRequest() {
		showDialog();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo",
				ApplicationConfig.accountInfo.getPhone()));
		params.add(new BasicNameValuePair("loginPwd", Constant.md5(oldPwd)));
		params.add(new BasicNameValuePair("newLgPwd", Constant.md5(newPwd)));
		params.add(new BasicNameValuePair("para", "updateLoginPwd"));
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
						HomeUtils.userLoginPwdCache =  Constant.md5(newPwd);
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

}
