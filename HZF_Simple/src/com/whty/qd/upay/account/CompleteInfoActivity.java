package com.whty.qd.upay.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.common.methods.IdcardUtils;

/**
 * @ClassName: RegisterActivity
 * @Description: 注册
 * @author chenzhenlin
 * @date 2013-8-8 上午10:04:56
 */
public class CompleteInfoActivity extends BaseActivity implements
		OnClickListener, TextWatcher {

	private static final String TAG = "RegisterActivity";
	protected static final int SHOW_TOAST = 0;

	/** @Fields mContext : 上下文 */
	private Context mContext;

	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;

	/** @Fields txtLoginPwd : 登录密码6-20位 */
	private EditText txtLoginPwd;
	/** @Fields txtPayPwd : 支付密码 */
	private EditText txtPayPwd;
	/** @Fields txtIdCard : 身份证 */
	private EditText txtIdCard;
	/** @Fields txtRealName : 真实姓名 */
	private EditText txtRealName;
	private View btnComplete;

	/** @Fields dialog : 通用对话框 */
	private CommonProgressDialog dialog;

	/** @Fields phoneNo : 手机号 */
	private String phoneNo;
	/** @Fields activeCode : 激活码 */
	private String activeCode;
	private String loginPwd;
	private String payPwd;
	private String idCardNo;
	private String userName;
	private String actUuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		getData(getIntent());
		setContentView(R.layout.complete_info_layout);
		initComponents();
	}

	/**
	 * @Title: init
	 * @Description: 初始化数据
	 */
	private void init() {
		mContext = this;

	}

	/**
	 * @Title: getData
	 * @Description: 获取数据
	 * @param intent
	 */
	private void getData(Intent intent) {
		if (null != intent) {
			phoneNo = intent.getStringExtra("phoneNo");
			activeCode = intent.getStringExtra("activeCode");
			actUuid = intent.getStringExtra("actUuid");
		}
	}

	/**
	 * @Title: initComponents
	 * @Description: 初始化组件
	 */
	private void initComponents() {
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
		lblTitle.setText(R.string.title_complete_info);
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		// 设置右侧监听，显示确认按钮，点击提交
		btnComplete = findViewById(R.id.complete);
		btnComplete.setOnClickListener(this);

		txtLoginPwd = (EditText) findViewById(R.id.login_pwd);
		txtLoginPwd.addTextChangedListener(this);
		txtLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  
		txtPayPwd = (EditText) findViewById(R.id.pay_pwd);
		txtPayPwd.addTextChangedListener(this);
		txtPayPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		txtIdCard = (EditText) findViewById(R.id.id_card);
		txtRealName = (EditText) findViewById(R.id.real_name);

	}

	@Override
	public void afterTextChanged(Editable s) {
		if(StringUtil.gbk(s.toString())){
			Toast.makeText(mContext, "密码中不能有汉字", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void onClick(View v) {
		if (v == left_linear) {
			finish();
		} else if (v == btnComplete) {// 完成
			readInput();
			if (validateInput()) {
				completeInfo();
			}
		}
	}

	/**
	 * @Title: readInput
	 * @Description: 读取输入
	 */
	private void readInput() {
		loginPwd = txtLoginPwd.getText().toString();
		payPwd = txtPayPwd.getText().toString();
		idCardNo = txtIdCard.getText().toString();
		userName = txtRealName.getText().toString();
	}

	/**
	 * @Title: validateInput
	 * @Description:检查输入是否合法
	 * @return
	 */
	private boolean validateInput() {
		// 校验真实姓名
		if (TextUtils.isEmpty(userName)) {
			Toast.makeText(mContext, R.string.input_real_name,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// 校验身份证号码
		if (TextUtils.isEmpty(idCardNo)
				|| idCardNo.length() != IdcardUtils.CHINA_ID_MAX_LENGTH) {
			Toast.makeText(mContext, R.string.input_id_card_no,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!IdcardUtils.validateIdCard18(idCardNo)) {
			Toast.makeText(mContext, R.string.id_card_no_validate_fail,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// 校验登录密码
		if (TextUtils.isEmpty(loginPwd) || loginPwd.length() < 6
				|| loginPwd.length() > 20) {
			Toast.makeText(mContext, R.string.input_login_pwd,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// 校验支付密码
		if (TextUtils.isEmpty(payPwd) || payPwd.length() < 6
				|| payPwd.length() > 20) {
			Toast.makeText(mContext, R.string.input_pay_pwd, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		
		if(StringUtil.gbk(loginPwd)||StringUtil.gbk(payPwd)){
			Toast.makeText(mContext, "密码中不能有汉字", Toast.LENGTH_SHORT).show();
		}
		
		if(StringUtil.checkStrong(loginPwd) < 2){
			Toast.makeText(mContext, "登录密码必须由字母、数字和特殊字符中的至少2种组成", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(StringUtil.checkStrong(payPwd) < 2){
			Toast.makeText(mContext, "支付密码必须由字母、数字和特殊字符中的至少2种组成", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(loginPwd.equals(payPwd)){
			Toast.makeText(mContext, "登录密码和支付密码不能相同", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @Title: signIn
	 * @Description: 注册
	 */
	private void completeInfo() {
		dialog = new CommonProgressDialog(mContext);
		dialog.showDialog(mContext.getString(ResourceUtils.getResourceId(
				ResourceUtils.packageNameWithR, "string", "apply_register")));
		// 1 phoneNo 手机号码 C 11 手机号码
		// 2 loginPwd 登录密码 C 32 采用MD5算法加密。
		// 3 payPwd 支付密码 C 32 采用MD5算法加密。
		// 4 idCardNo 身份证号码 C 18 二代身份证号码
		// 5 userName 真实姓名 C NC 用户真实姓名
		// 6 activeCode 激活码 C 手机收到的激活码
		// 7 para 参数 C 固定值：registerFromMobile
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("loginPwd", loginPwd));
		params.add(new BasicNameValuePair("payPwd", payPwd));
		params.add(new BasicNameValuePair("idCardNo", idCardNo));
		params.add(new BasicNameValuePair("userName", userName));
		params.add(new BasicNameValuePair("activeCode", activeCode));
		params.add(new BasicNameValuePair("para", "registerFromMobile"));
		params.add(new BasicNameValuePair("actUuid", actUuid));
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
						String accountId = jsonObject.optString("accountId");

						AccountPref account = AccountPref.get(mContext);
						account.setUserName(userName);
						account.setAccountId(accountId);
						account.setPhoneNo(phoneNo);

						Intent intent = new Intent();
						intent.putExtra("phoneNo", phoneNo);
						intent.putExtra("loginPwd", loginPwd);
						setResult(RESULT_OK, intent);
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
}
