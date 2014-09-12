package com.whty.qd.upay.payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;
import com.whty.qd.pay.appinterface.DeviceChangedListener;
import com.whty.qd.pay.cardmanage.CardOperation;
import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.common.net.ResourceLoader;
import com.whty.qd.pay.common.net.WebSettings;
import com.whty.qd.pay.info.AccountInfo;
import com.whty.qd.pay.info.TransactionInfo;
import com.whty.qd.pay.payment.CheckOrder;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.UpayApplication;
//import com.whty.qd.upay.common.StaticData;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.dialog.CommonDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
//import com.whty.qd.upay.devices.PswdUnlockActivity;
import com.whty.qd.upay.home.HomeUtils;

/**
 * 订单确认（收银台）界面
 * 
 * @author Cai
 */
public class OrderConfirmActivity extends BaseActivity implements
		DeviceChangedListener, OnClickListener {
	private static String TAG = "OrderConfirmActivity";
	private Context mContext;
	private LinearLayout left_linear;
	private LinearLayout right_linear;
	private Button button_title_back;
	private TextView text_main_title;
	private Button button_title_ok;
	private TextView text_merchant;
	private TextView text_sum;
	private TextView text_order_id;
	private TextView text_phone;
	private TextView text_sign;
	private LinearLayout linear_order_sign;
	private TextView pay_method_tv;
	private View listview;
	private ListView payMethod_lv;
	private LinearLayout spinnerLayout;
	private boolean payMethodListSpinner_flag = false;
	private String welcome = null;
	private String orderId = null;
	private String appId = null;
	private TransactionInfo tInfo;
//	private ErrorReporter er = null;
	private static final int TimeOutTag = -100;
	private static final int HeartBeat = -200;
	protected static final int SHOW_TOAST = 1001;
	protected static final int CHECK_OK = 1002;
	private boolean isTimeout = false;
	private int timeout = -1;
	private int costTime = -1;
	private boolean isChkTime = false;
	private CardOperation cop;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> arrayList = new ArrayList<String>();
	private int payType = 2;
	private CommonDialog commonDialog;
	Button payBtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_confirm_layout);
		initView();
		initData();
		setListener();

		// 进入订单确认页则刷新标志置为true，交易记录resume时会刷新
		HomeUtils.isNeedRefreshRec = true;
	}

	private void initView() {
		mContext = this;
		ApplicationConfig.dcl = this;
		ApplicationConfig.activityList.clear();
		ApplicationConfig.activityList.add(this);
		commonDialog = new CommonDialog(mContext);
		cop = CardOperation.getInstance(mContext);
		left_linear = (LinearLayout) findViewById(R.id.left_linear);
		right_linear = (LinearLayout) findViewById(R.id.right_linear);
		button_title_back = (Button) findViewById(R.id.button_title_back);
		button_title_ok = (Button) findViewById(R.id.button_title_ok);
		text_main_title = (TextView) findViewById(R.id.text_main_title);
		text_merchant = (TextView) findViewById(R.id.text_merchant);
		text_sum = (TextView) findViewById(R.id.text_sum);
		text_order_id = (TextView) findViewById(R.id.text_order_id);
		text_phone = (TextView) findViewById(R.id.text_phone);
		text_sign = (TextView) findViewById(R.id.text_sign);
		linear_order_sign = (LinearLayout) findViewById(R.id.linear_order_sign);
		pay_method_tv = (TextView) findViewById(R.id.pay_method_tv);
		spinnerLayout = (LinearLayout) findViewById(R.id.spinnerLayout);
		text_main_title.setText(mContext
				.getString(R.string.text_order_title_name));
		payBtn = (Button) findViewById(R.id.pay_submit_button);
		
		//初始化付款方式下拉框
		listview = LayoutInflater.from(mContext).inflate(
				R.layout.pay_credit_repayment_listview, null);
		payMethod_lv = (ListView) listview.findViewById(R.id.credit_repayment_list);
		spinnerLayout.addView(listview);
	}

	private void initData() {
		orderId = getIntent().getStringExtra("ORDERID");
		appId = getIntent().getStringExtra("APP_ID");
		requestOrder();
	}

	@Override
	protected void onResume() {
		ApplicationConfig.dcl = this;
		refreshData();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (ApplicationConfig.dcl instanceof OrderConfirmActivity) {
			ApplicationConfig.dcl = null;
		}
		super.onDestroy();
	}

	private void refreshData() {
		arrayList.clear();
		arrayList.add("无卡支付");
		adapter = new ArrayAdapter<String>(mContext,
				R.layout.pay_method_list_item, android.R.id.text1,
				arrayList);
		payMethod_lv.setAdapter(adapter);

		if (arrayList.size() == 0) {
			Toast.makeText(mContext, "暂无可选支付方式，请插入支付设备！", Toast.LENGTH_LONG)
					.show();
		} else {
			pay_method_tv.setText(payMethod_lv.getItemAtPosition(0).toString());
		}
	}

	private void setListener() {
		left_linear.setOnClickListener(this);
		payBtn.setOnClickListener(this);
		pay_method_tv.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void requestOrder() {
		showDialog(mContext.getString(R.string.text_order_get_order));
		MyCheckOrder checkOrder = new MyCheckOrder(mContext, mHandler);
		checkOrder.queryOrder(orderId);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissDialog();
			switch (msg.what) {
			case CheckOrder.GET_ORDER_FAIL:
				Toast.makeText(mContext, "获取订单失败！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case CheckOrder.ORDER_CLOSED:
				Toast.makeText(mContext, "订单已关闭！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case CheckOrder.ORDER_UNUSUAL:
				Toast.makeText(mContext, "订单处理中，请10分钟后查看支付状态",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case CheckOrder.ORDER_DATA_ERROR:
				Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				finish();
				break;
			case CheckOrder.ORDER_REQUEST_ERROR:
				Toast.makeText(mContext, "网络请求失败！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case CheckOrder.ORDER_TIMEOUT_GREATER_THAN_ZERO:
				isChkTime = true;
				timeout = (Integer) msg.obj;
				timeOutHandler.sendEmptyMessageDelayed(HeartBeat, 1000);
				break;
			case CheckOrder.ORDER_TIMEOUT_EQUAL_TO_ZERO:
				Toast.makeText(mContext, "订单已超时，请重新生成订单", Toast.LENGTH_LONG)
						.show();
				finish();
				break;
			case CheckOrder.GET_ORDER_OK:
				tInfo = new TransactionInfo();
				tInfo = (TransactionInfo) msg.obj;
				//为了检测把"武汉擎动网络科技有限公司"修改为"浙江快捷通网络技术有限公司"
//				text_merchant.setText(tInfo.getMerchantName());
				text_merchant.setText(getResources().getString(R.string.qd));
				String money = "";
				if (TextUtils.isEmpty(tInfo.getMoney())) {
					money = "0.00";
				} else {
					money = StringUtil.convertMoneyForDisplay(tInfo.getMoney());
				}
				text_sum.setText(money);
				text_order_id.setText(orderId);
				
				AccountPref account = AccountPref.get(mContext);
				text_phone.setText(account.getPhoneNo());

				welcome = "";//ApplicationConfig.accountInfo.getWelcome();
				if (welcome.equals("null") || TextUtils.isEmpty(welcome)) {
					linear_order_sign.setVisibility(View.GONE);
				} else {
					linear_order_sign.setVisibility(View.VISIBLE);
					text_sign.setText(welcome);
				}

				break;

			default:
				break;
			}
		}
	};

	private Handler timeOutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (!isChkTime || OrderConfirmActivity.this.isFinishing()) {
				return;
			}
			if (msg.what == HeartBeat) {
				costTime += 1000;
				if (costTime >= timeout) {
					timeOutHandler.sendEmptyMessage(TimeOutTag);
				} else {
					timeOutHandler.sendEmptyMessageDelayed(HeartBeat, 1000);
				}
			} else if (msg.what == TimeOutTag) {
				isTimeout = true;
				Toast.makeText(mContext, "订单已超时，请重新生成订单", Toast.LENGTH_LONG)
						.show();
				finish();
			}
		}
	};

	private OnLoadListener mOnLoadListener = new OnLoadListener() {

		JSONObject decodeInfoObject = null;

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			String jsonStr = new String(data);
			dismissDialog();
			Log.e(TAG, jsonStr);
			JSONObject jObject = null;
			try {
				jObject = new JSONObject(jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String reCode = jObject.optString("ACTION_RETURN_CODE");
			if (reCode.equals("000000")) {
				if (actionId == 1) {
					
				} else if (actionId == 2) {
					String encodeInfoStr = jObject.optString("ACTION_INFO");
					Log.d("encodeInfoStr-->>>", encodeInfoStr);
					try {
						decodeInfoObject = new JSONObject(encodeInfoStr);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (decodeInfoObject == null) {
						Toast.makeText(mContext, R.string.order_confirm_fail,
								Toast.LENGTH_SHORT).show();
					}
					String TN = decodeInfoObject.optString("TN", null);
					if (TextUtils.isEmpty(TN)) {
						Toast.makeText(mContext, R.string.order_confirm_fail,
								Toast.LENGTH_SHORT).show();
						return;
					}
					String spid = decodeInfoObject.optString("SP_ID");
					Log.i("spid:", spid);
					String sysprovide = decodeInfoObject
							.optString("SYS_PROVIDE");
					Log.i("sysprovide:", sysprovide);
					String mMode = "00";
					Log.i("mMode:", mMode);
					//如果是测试，直接修改订单状态为成功
//					if(!Constant.IS_FACK){
//						UPPayAssistEx.startPayByJAR(OrderConfirmActivity.this,
//								PayActivity.class, spid, sysprovide, TN, mMode);
//					}else{
						//1、创建订单
						Log.e("创建订单", "Constant.DEAL_STATE_0");
						createUpdateOrder(getApplicationContext(),
								StringUtil.convertMoneyForDisplay2(tInfo.getMoney()),
								StringUtil.convertMoneyForDisplay2(tInfo.getMoney()),
								Constant.DEAL_STATE_0,
								text_phone.getText().toString());
						//2、修改等待状态为成功
//					}
					
					
				}
			} else {
				String msgType = jObject.optString("ACTION_RETURN_TIPSTYPE");
				String msg = jObject.optString("ACTION_RETURN_TIPS");
				showMarkedWords(msgType, msg, reCode);
			}
		}

		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description + ":[" + failingUrl + "]");
			dismissDialog();

			Toast.makeText(mContext,
					getString(R.string.network_requet_error) + description,
					Toast.LENGTH_SHORT).show();
		}

	};
	
	
	
	/**
	 * 
	 * @Title getOrderId 
	 * @Description 获取订单id
	 * @return
	 * String    返回类型 
	 * @throws
	 */
	public static final String getOrderId(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String m_date = format.format(new Date());
		
		Random random = new Random();
		int i_ran = random.nextInt(999);
		String str_ran = String.valueOf(i_ran);
		
		
		StringBuilder sb = new StringBuilder();
		if(m_date!=null&&m_date.length()>0){
			sb.append(m_date);
		}
		if(str_ran!=null&&str_ran.length()>0){
			sb.append(str_ran);
		}
		
		return sb.toString();
	}
	
	
	/**
	 * 
	 * @Title getPayDate 
	 * @Description 获取支付时间
	 * @return
	 * String    返回类型 
	 * @throws
	 */
	public static String getPayDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return format.format(new Date());
	}
	
	/**
	 * 创建订单
	 */
	private void createUpdateOrder(final Context context,
			final String payAmount, 
			final String changeAmount,
			final String status,
			final String phoneNo) {
		// 1 phoneNo 手机号码 C 11 手机号码
		// 2 loginPwd 登录密码 C 32 采用固MD5算法加密。
		// 3 para 参数 C 固定值：mobileLogon
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("banNo", "97"));
		Log.e("UpayApplication.ACCOUNT_ID:", UpayApplication.ACCOUNT_ID);
		params.add(new BasicNameValuePair("accountId", UpayApplication.ACCOUNT_ID));
		params.add(new BasicNameValuePair("payAmount", payAmount));
		params.add(new BasicNameValuePair("orderId", getOrderId()));
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("changeAmount", changeAmount));
		params.add(new BasicNameValuePair("payDate", getPayDate()));
		params.add(new BasicNameValuePair("status", status));
		params.add(new BasicNameValuePair("para", "createOrUpdateQdTrans"));
		// 发送网络请求
		HttpUtil.HttpsPost(context, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
			}

			@Override
			public void data(byte[] data, int length, String url,
					int requestId, int actionId) {
				try {
					Log.e("loading auto login", "data:" + new String(data));
					JSONObject jsonObject = new JSONObject(new String(data));
					System.out.println();
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					if (RetCode.OK.equals(retCode)) {

//						String userName = jsonObject.optString("userName");
//						String accountId = jsonObject.optString("accountId");
//						double balance = jsonObject.optDouble("balance", 0);
//						String userId = jsonObject.optString("userId");
//
//						AccountPref account = AccountPref.get(context);
//						account.setUserName(userName);
//						account.setAccountId(accountId);
//						account.setBalance(balance);
//						account.setUserId(userId);

//						account.setPhoneNo(phoneNo);
						// 此处采用广播的方式发出登录成功通知，因为首页采用的是TabActivity，
						// 无法拦截onActivityResult,接收次通知的广播注册在BaseActivity
						// 中，需要接收此广播继承实现onReceiveLoginSuccess()方法即可
//						if (ApplicationConfig.isLogon == false) {
//							ApplicationConfig.accountInfo = new AccountInfo();
////							ApplicationConfig.accountInfo.setPhone(phoneNo);
//							ApplicationConfig.accountInfo.setRealName(userName);
//							ApplicationConfig.accountInfo.setUserId(userId);
//						}
//						ApplicationConfig.isLogon = true;
//						HomeUtils.userLoginPwdCache = Constant.md5(loginPwd);

//						Intent intent = new Intent();
//						intent.putExtra("userName", userName);
//						intent.putExtra("phoneNo", phoneNo);
//						intent.putExtra("balance", balance);
//						handler.obtainMessage(LOGIN_SUCCESS, intent)
//								.sendToTarget();
					} else {
//						handler.sendEmptyMessage(LOGIN_ERROR);
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
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {// 无卡
			String code = "";
			/*
			 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
			 */
			String str = data.getExtras().getString("pay_result");
			if (str != null && str.equalsIgnoreCase("success")) {
				code = "00";
				Log.e("修改订单状态为", "Constant.DEAL_STATE_88");
				createUpdateOrder(getApplicationContext(),
						StringUtil.convertMoneyForDisplay2(tInfo.getMoney()),
						StringUtil.convertMoneyForDisplay2(tInfo.getMoney()),
						Constant.DEAL_STATE_88,
						text_phone.getText().toString());
			} else if (str != null && str.equalsIgnoreCase("fail")) {
				code = "-2";
			} else if (str != null && str.equalsIgnoreCase("cancel")) {
				code = "-2";
			}
			Message notifymsg = new Message();
			notifymsg.what = 0;
			notifymsg.obj = new String[] { code, orderId };
			notifyhandler.sendMessage(notifymsg);
			finish();
		}
	}

	@Override
	public void deviceChanged() {
		ApplicationConfig.dcl = null;
		refreshData();
	}

	@Override
	public void onClick(View v) {
		if (v == left_linear) {
			Message notifymsg = new Message();
			notifymsg.what = 0;
			notifymsg.obj = new String[] { "-2", orderId };
			notifyhandler.sendMessage(notifymsg);
			this.finish();
		} else if (v == pay_method_tv) {
			if (!payMethodListSpinner_flag) {
				payMethodListSpinner_flag = true;
				spinnerLayout.setVisibility(View.VISIBLE);

				payMethod_lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						String item = (String) parent.getAdapter().getItem(
								position);
						Log.e(TAG, "选中" + item);
						if (item.contains("银行卡支付")) {
							payType = 0;
						} else if (item.contains("金融")) {
							payType = 1;
						} else {
							payType = 2;
						}
						Log.e("选中", "" + payType);

						pay_method_tv.setText(payMethod_lv.getItemAtPosition(
								position).toString());
						spinnerLayout.setVisibility(View.GONE);
						payMethodListSpinner_flag = false;
					}
				});
			} else {
				payMethodListSpinner_flag = false;
				spinnerLayout.setVisibility(View.GONE);
			}

		} else {
			spinnerLayout.setVisibility(View.GONE);
			payMethodListSpinner_flag = false;
			
			if (isChkTime) {
				if (isTimeout) {
					Toast.makeText(mContext, "订单已超时，请重新生成订单", Toast.LENGTH_LONG).show();
					finish();
					return;
				} else {
					timeout = timeout - costTime;
					if (timeout > 0) {// 大于零表示1.此刻未超时；2.此交易有时间限制；
						isChkTime = false;// 不用检测时间了
					} else {
						Toast.makeText(mContext, "订单已超时，请重新生成订单",
								Toast.LENGTH_LONG).show();
						finish();
						return;
					}
				}
			}

			switch (payType) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				checkRule();
				break;
			default:
				break;
			}
		}
	}
	
	private Handler checkRuleHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissDialog();
			switch (msg.what) {
			case SHOW_TOAST:
				Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case CHECK_OK:
				nocardCheckOrder();
				break;
			default:
				break;
			}
		}
	};

	private void checkRule() {
		showDialog();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", ApplicationConfig.accountInfo.getPhone()));
		params.add(new BasicNameValuePair("payAmount", StringUtil.myMoneyFormat(tInfo.getMoney())));
		params.add(new BasicNameValuePair("para", "checkRule"));
		// 发送网络请求
		HttpUtil.HttpsPost(mContext, params, new OnLoadListener() {
			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				checkRuleHandler.obtainMessage(SHOW_TOAST, description).sendToTarget();
			}

			@Override
			public void data(byte[] data, int length, String url,int requestId, int actionId) {
				Log.e(TAG, "data:" + new String(data));
				try {
					JSONObject jsonObject = new JSONObject(new String(data));
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					if (RetCode.OK.equals(retCode)) {
						checkRuleHandler.obtainMessage(CHECK_OK).sendToTarget();
					} else {
						checkRuleHandler.obtainMessage(SHOW_TOAST,
								jsonObject.optString(RetCode.ERROR_MSG))
								.sendToTarget();
					}
				} catch (JSONException e) {
					checkRuleHandler.obtainMessage(SHOW_TOAST, "数据解析失败").sendToTarget();
					e.printStackTrace();
				}
			}
		}, false);
	}

	private void nocardCheckOrder() {
		showDialog();
		String csn = "";
		try {
			JSONObject data = new JSONObject();
			data.put("ORDER_ID", orderId);
			data.put("CSN", csn);
			JSONObject root = HomeUtils.buildJSON("FIND_ORDER", data, this,
					null, false);
			ResourceLoader.getInstance(mContext).startLoadingResource(
					HomeUtils.NocardCheckOrder, "POST", null, null,
					(root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
					false, false, mOnLoadListener, 2);
		} catch (JSONException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}

	/**
	 * 支付完成后通知服务器（对账）
	 */
	Handler notifyhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String[] args = (String[]) msg.obj;
			try {
				JSONObject data = new JSONObject();
				data.put("ORDER_ID", args[1]);
				data.put("STATUS", args[0]);
				JSONObject root = HomeUtils.buildJSON("TRADE_STATE", data,
						(Activity) mContext, null, true);
				Log.e("PayResuletReceiver", root.toString());
				ResourceLoader.getInstance(mContext).startLoadingResource(
						HomeUtils.MPIURL, "POST", null, null,
						(root.toString()).getBytes(), 0,
						WebSettings.LOAD_NORMAL, false, false,
						transValidOnLoadListener, 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	private OnLoadListener transValidOnLoadListener = new OnLoadListener() {
		@Override
		public void data(byte[] data, int length, String url, int requestId,int actionId) {}
		@Override
		public void error(int id, String description, String failingUrl,int requestId, int actionId) {}
	};

	@Override
	public void onBackPressed() {
		if (isDialogShowing()) {
			dismissDialog();
		} else {
			Message notifymsg = new Message();
			notifymsg.what = 0;
			notifymsg.obj = new String[] { "-2", orderId };
			notifyhandler.sendMessage(notifymsg);
			this.finish();
		}
	}
}
