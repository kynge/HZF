package com.whty.qd.upay.phonefee;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;
import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.SecurityUtil;
import com.whty.qd.pay.common.net.LoadControler;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.common.net.ResourceLoader;
import com.whty.qd.pay.common.net.WebSettings;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.UpayApplication;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.dialog.ChooseDialog;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.creditcard.CreditCardPaymentActivity;
import com.whty.qd.upay.home.GetBankCardInfo;
import com.whty.qd.upay.home.HomeUtils;
import com.whty.qd.upay.payment.OrderConfirmActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 手机充值确认信息界面类
 * 
 * @author super
 */
public class PayPhoneFeeConfirmActivity extends BaseActivity  implements Callback,OnClickListener{
	private static String TAG = "PayPhoneFeeConfirmActivity";

	Context mContext;
	TextView titleText;
	TextView numberText;
	TextView typeText;
	TextView areaText;
	TextView numOfRechargeSpinner; // 充值面额Spinner
	Button payBtn;
	Button button_title_back;
	
	String userid;
	String phoneNumber;
	String phoneType;
	String phoneArea;
	String merchantId;
	String merchantName;
	String merchantType;
	PhoneCardPriceList cardList;

	String productId = ""; // 存放商品ＩＤ
	String productName = ""; // 存放商品名称
	String realPrice = "";// 实付价格
	String totalPrice = ""; // 面额
	String orderId;// 订单信息

	LoadControler mLoadControler;
	ProgressDialog dialog = null;

	private boolean isSelect = false; // 防止双击
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = PayPhoneFeeConfirmActivity.this;
		Log.d(TAG, "onCreate...");
		setupView();
		initData();
		setListener();
	}

//	private Button pay_finish_pay;
	
	private void setupView() {
		setContentView(R.layout.pay_charge_phone_confirm);
		HomeUtils.activityList.add(PayPhoneFeeConfirmActivity.this);
		button_title_back = (Button) findViewById(R.id.button_title_back);
		((TextView) findViewById(R.id.text_main_title)).setText("手机充值");

		numberText = (TextView) findViewById(R.id.phoneNum);
		typeText = (TextView) findViewById(R.id.phoneType);
		areaText = (TextView) findViewById(R.id.phoneArea);
		payBtn = (Button) findViewById(R.id.pay_submit_button);
		payBtn.setTag(0);

		numOfRechargeSpinner = (TextView) findViewById(R.id.num_of_recharge_spinner);
		
		mHandler = new Handler(this);
		
//		pay_finish_pay = (Button) findViewById(R.id.pay_finish_pay);
		
		chosePayWayDialog = new ChooseDialog(mContext);
		
	}

	/**
	 * 初始化界面
	 */
	private void initData() {

		Intent data = getIntent();
		userid = data.getStringExtra("userid");
		phoneNumber = data.getStringExtra("PHONE");
		phoneType = data.getStringExtra("PHONE_TYPE");
		phoneArea = data.getStringExtra("PHONE_PROVINCE");
		merchantType = data.getStringExtra("MERCHANT_TYPE");
		numberText.setText(phoneNumber);
		typeText.setText(phoneType);
		areaText.setText(phoneArea);

		merchantId = data.getStringExtra("MERCHANT_ID");
		cardList = (PhoneCardPriceList) data.getSerializableExtra("PRODUCTS");
		
	}
	

	/**
	 * 设置按钮事件监听器
	 */
	private void setListener() {
		button_title_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		PhoneCardPrice item = cardList.getCardList().get(0);
		productId = item.getProductId();
		productName = item.getName();

		numOfRechargeSpinner.setText(" ￥"
				+ StringUtil.longToDoubleString(item.getPurchasePrice()));

		realPrice = StringUtil.longToIntString(item.getRealPrice());
		totalPrice = StringUtil.longToDoubleString(item.getPurchasePrice());
		numOfRechargeSpinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(mContext, R.style.dialog);
				Window w = dialog.getWindow();
				LayoutParams lp = w.getAttributes();
				lp.y = -120;
				w.setAttributes(lp);
				
				View view = LayoutInflater.from(mContext).inflate(
						R.layout.alert_layout, null);
				ListView listView = (ListView) view.findViewById(R.id.listView);
				final List<PhoneCardPrice> list = cardList.getCardList();

				listView.setAdapter(new ListViewAdapter(mContext, list));
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						PhoneCardPrice cardItem = list.get(position);
						productId = cardItem.getProductId();
						productName = cardItem.getName();
						numOfRechargeSpinner.setText("￥"
								+ StringUtil.longToDoubleString(cardItem
										.getPurchasePrice()));

						realPrice = StringUtil.longToIntString(cardItem
								.getRealPrice());
						totalPrice = StringUtil.longToDoubleString(cardItem
								.getPurchasePrice());
						dialog.dismiss();
					}
				});
				dialog.setContentView(view);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		});

		payBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				  mGoodsIdx = (Integer) v.getTag();
				
				if(Constant.IS_PAY_BY_BANK){
					//这里跳转银行界面
		            Log.e(LOG_TAG, " " + v.getTag());
		            mGoodsIdx = (Integer) v.getTag();
	
		            mLoadingDialog = ProgressDialog.show(mContext, // context 
		                    "", // title 
		                    "正在努力的获取tn中,请稍候...", // message 
		                    true); //进度是否是不确定的，这只和创建进度条有关 
	
		            /************************************************* 
		             * 
		             *  步骤1：从网络开始,获取交易流水号即TN 
		             *  
		             ************************************************/
		            new Thread(new GetTnThread()).run();
				}else{
//					Intent intent = new Intent(PayPhoneFeeConfirmActivity.this,GetBankCardInfo.class);
//					intent.putExtra("realPrice", realPrice);
//					intent.putExtra("phoneNumber", phoneNumber);
//					PayPhoneFeeConfirmActivity.this.startActivity(intent);
					
					//弹出选择支付方式对话框
					
					chosePayWayDialog.showPayWayDialog(new MyPayWayItemClickListener());
				}
				
//				if (!isSelect) {
//					isSelect = true;
//					if (productId.equals("") || productName.equals("")) {
//						Toast.makeText(mContext,
//								"请选择充值面额",
//								Toast.LENGTH_SHORT).show();
//						isSelect = false;
//						return;
//					}
//					try {// 请求数据
//						Intent intent = new Intent(PayPhoneFeeConfirmActivity.this,GetBankCardInfo.class);
//						intent.putExtra("realPrice", realPrice);
//						intent.putExtra("phoneNumber", phoneNumber);
//						PayPhoneFeeConfirmActivity.this.startActivity(intent);
//						
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//						dismissDialog();
//						isSelect = false;
//					}
//				}

			}
		});
		
		
//		pay_finish_pay.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				createUpdateOrder(getApplicationContext(),
//						realPrice,
//						realPrice,
//						Constant.DEAL_STATE_88,
//						phoneNumber);
//			}
//		});
	}
	
	
	/*
	 * 选择支付方式对话框
	 */
	private ChooseDialog chosePayWayDialog;
	
	
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
//				isFitCustomer = true;
				if(payType == YUE_PAY){
					transForm("11",realPrice);
				}else if(payType == BANK_CARD_PAY){
//					createUpdateOrder(getApplicationContext(),
//							realPrice,
//							realPrice,
//							Constant.DEAL_STATE_0,
//							phoneNumber);
				
					
					Intent intent = new Intent(PayPhoneFeeConfirmActivity.this,GetBankCardInfo.class);
					intent.putExtra("realPrice", realPrice);
					intent.putExtra("phoneNumber", phoneNumber);
					PayPhoneFeeConfirmActivity.this.startActivity(intent);
				}
				break;
			default:
				break;
			}
		}
	};
	
	//是否符合商户充值规则
	
	protected static final int CHECK_OK = 1002;
	
	private void checkRule() {
		showDialog();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", ApplicationConfig.accountInfo.getPhone()));
		params.add(new BasicNameValuePair("payAmount", realPrice));
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
		}, true);
		
	}
	
	/*
	 * 支付方式点击监听器
	 */
	private class MyPayWayItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			chosePayWayDialog.dismissDialog();
			//余额支付
			if(position == 0){
				Log.e("余额支付", "余额支付");
				commonDialog.showInputPwdDialog(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String pwd = commonDialog.getPwdEditText().getText().toString();
						if(TextUtils.isEmpty(pwd)){
							showShortMsg("密码不能为空");
							return;
						}
						commonDialog.dismissDialog();
						
						payType = YUE_PAY;
						checkRule();
						
						
					}
				}, new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						commonDialog.dismissDialog();
						
						payType = BANK_CARD_PAY;
						checkRule();
					}
				});
				
			}else if(position == 1){
				Log.e("银行卡支付", "银行卡支付");
//				 mGoodsIdx = (Integer) v.getTag();
					
//	            mLoadingDialog = ProgressDialog.show(mContext, // context 
//	                    "", // title 
//	                    "正在努力的获取tn中,请稍候...", // message 
//	                    true); //进度是否是不确定的，这只和创建进度条有关 
//
//	            /************************************************* 
//	             * 
//	             *  步骤1：从网络开始,获取交易流水号即TN 
//	             *  
//	             ************************************************/
//	            new Thread(new GetTnThread()).run();
				
				
				
				payType = BANK_CARD_PAY;
				checkRule();
				
//				Intent intent = new Intent(PayPhoneFeeConfirmActivity.this,GetBankCardInfo.class);
//				intent.putExtra("realPrice", realPrice);
//				intent.putExtra("phoneNumber", phoneNumber);
//				PayPhoneFeeConfirmActivity.this.startActivity(intent);
			}
			
		}
		
	}
	
	
	public static final int YUE_PAY = 2221;
	public static final int BANK_CARD_PAY = 2223;
	
	private int payType = -1;
	
	private CommonProgressDialog dialog2 = null;
	
	private void transForm(String transAccount,String transMoney){
		dialog2 = new CommonProgressDialog(mContext);
		dialog2.showDialog("正在处理请稍后...");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("para", "transfer"));
		params.add(new BasicNameValuePair("phoneNo", ApplicationConfig.accountInfo.getPhone()));
		params.add(new BasicNameValuePair("receivePhone", transAccount));
		params.add(new BasicNameValuePair("payAmount", transMoney));


		
		HttpUtil.HttpsPost(mContext, params, new OnLoadListener() {
			
			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				dialog2.dialogDismiss();
				handler.obtainMessage(SHOW_TOAST, description).sendToTarget();
			}
			
			@Override
			public void data(byte[] data, int length, String url, int requestId,
					int actionId) {
				Log.e(TAG, "data:" + new String(data));
				dialog2.dialogDismiss();
				try {
					JSONObject jsonObject = new JSONObject(new String(data));
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					Log.e("retCode", ""+retCode);
					if (RetCode.OK.equals(retCode)) {
						handler.obtainMessage(TRANSFORM_SUCC,
								"转账成功")
								.sendToTarget();
						
						PayPhoneFeeConfirmActivity.this.finish();
					}else{
						handler.obtainMessage(SHOW_TOAST,
								jsonObject.optString(RetCode.ERROR_MSG))
								.sendToTarget();
					}
				}catch(JSONException e){
					e.printStackTrace();
					
				}
			}
		}, true);
	}
	
	
	protected static final int SHOW_TOAST = 0;
	
	
	//转账成功
	protected static final int TRANSFORM_SUCC = 1;
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
				
			case TRANSFORM_SUCC:{
				createUpdateOrder(getApplicationContext(),
						realPrice,
						realPrice,
						Constant.DEAL_STATE_88,
						phoneNumber);
			}break;
			default:
				break;
			}
			
		}

	};
	
	
	private class GetTnThread implements Runnable{

		@Override
		public void run() {
		       String tn = null;
		        InputStream is;
		        try {

		            String url = TN_URL_01;

		            URL myURL = new URL(url);
		            URLConnection ucon = myURL.openConnection();
		            ucon.setConnectTimeout(120000);
		            is = ucon.getInputStream();
		            int i = -1;
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            while ((i = is.read()) != -1) {
		                baos.write(i);
		            }

		            tn = baos.toString();
		            is.close();
		            baos.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }

		        Message msg = mHandler.obtainMessage();
//		        tn = "201406031233270035252";
		        msg.obj = tn;
		        
		        mHandler.sendMessage(msg);
		}
		
	}
	
	
	 private static final String TN_URL_01="http://211.144.201.244:8080/upmp/CreateOrderServlet";
	
	 
    public static final String LOG_TAG = "PayDemo";
    private int mGoodsIdx = 0;
    private Handler mHandler = null;
    private ProgressDialog mLoadingDialog = null;

    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;
	
	/*****************************************************************
	 * mMode参数解释：
	 *      "00" - 启动银联正式环境
	 *      "01" - 连接银联测试环境
	 *****************************************************************/
    private String mMode = "01";

	/**
	 * 用户业务锁定接口---组装网络请求参数
	 */
	// private void lockUserRequest() throws JSONException {
	// JSONObject data = new JSONObject();
	// data.put("USER_ID", ApplicationConfig.USER_ID);
	// data.put("APP_ID", ApplicationConfig.APP_ID);
	// data.put("ORDER_ID", "");
	// data.put("STATUS", "1");
	//
	// JSONObject root = ApplicationConfig.buildJSON(
	// "LOCK_USER", data, this, null, true);
	//
	// Log.d(TAG, root.toString());
	// mLoadControler = ResourceLoader.getInstance(this).startLoadingResource(
	// ApplicationConfig.MPIURL, "POST", null, null,
	// (root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
	// false, false, mOnLoadListener, 1);
	// }

	/**
	 * 组装网络请求参数
	 * 
	 * @throws JSONException
	 */
	private void createBillRequest() throws JSONException {
		String appId = "202";
		JSONObject data = new JSONObject();
		data.put("PHONE", phoneNumber);
		data.put("PRODUCT_ID", productId);
		data.put("PRODUCT_NAME", productName);
		data.put("MERCHANT_ID", merchantId);
		data.put("MERCHANT_TYPE", merchantType);
		data.put("USER_ID", userid);
		data.put("TRANS_TYPE", "");

		JSONObject root = HomeUtils.buildJSON("CREATE_ORDER", data,
				this, appId, true);

		Log.d(TAG, root.toString());
		mLoadControler = ResourceLoader.getInstance(this).startLoadingResource(
				HomeUtils.BusinessURL, "POST", null, null,
				(root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
				false, false, mOnLoadListener, 0);
	}

	/**
	 * 数据解析/处理
	 */
	private OnLoadListener mOnLoadListener = new OnLoadListener() {
		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description + ": " + failingUrl);
			dismissDialog();
			isSelect = false;
			Toast.makeText(mContext, R.string.network_requet_error,
					Toast.LENGTH_SHORT).show();
			return;
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			String json = new String(data);
			Log.e(TAG, json);
			if (actionId == 1) {// 用户锁定判断
				dismissDialog();
				JSONObject object = null;
				try {
					object = new JSONObject(json);
				} catch (JSONException e) {
					e.printStackTrace();
					isSelect = false;
				}
				if (object == null) {
					isSelect = false;
					Toast.makeText(mContext, R.string.data_requet_error,
							Toast.LENGTH_SHORT).show();
					return;
				}
				String returnCode = object.optString("ACTION_RETURN_CODE");
				if (returnCode.equals("000000")||returnCode.equals("100002")) {
					try {
						showDialog();
						createBillRequest();
					} catch (JSONException e) {
						e.printStackTrace();
						dismissDialog();
						isSelect = false;
					}
				} else {
					isSelect = false;
					
					String msgType = object.optString("ACTION_RETURN_TIPSTYPE");
					String msg = object.optString("ACTION_RETURN_TIPS");
					
					showMarkedWords(msgType, msg, returnCode);
				}
			} else {
				// 在创建完手机充值订单后，保存当前手机号码到SharedPreference；
				SharedPreferences sp = PayPhoneFeeConfirmActivity.this
						.getSharedPreferences("phoneRecharge", MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("phoneRechargeNum", numberText.getText()
						.toString());
				editor.commit();

				if (!isDataValidated(json)) { // failed
					dismissDialog();
					isSelect = false;
					return;
				} else { // successed
					dismissDialog();
					isSelect = false;
					Toast.makeText(mContext, "订单创建成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(mContext, OrderConfirmActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("ORDERID", orderId); // 订单信息数据对象
					intent.putExtra("APP_ID", "201");
					startActivity(intent);

					for (int i = 0; i < HomeUtils.activityList.size(); i++) {
						if (null != HomeUtils.activityList.get(i)) {
							HomeUtils.activityList.get(i).finish();
						}
					}
					PayPhoneFeeConfirmActivity.this.finish();
				}
			}
		}
	};

	/**
	 * 数据校验
	 * 
	 * @param string
	 * @return
	 */
	private boolean isDataValidated(final String string) {
		JSONObject object = null;
		JSONObject decodeInfoObject = null;
		try {
			object = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (object == null) {
			Toast.makeText(mContext, R.string.data_requet_error,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		String returnCode = object.optString("ACTION_RETURN_CODE");
		if (returnCode.equals("000000")) {// APP账单激活文件
			String encodeInfoStr = object.optString("ACTION_INFO");
			encodeInfoStr = SecurityUtil.decode(encodeInfoStr);
			Log.d("decodeStr-->>>", encodeInfoStr);
			try {
				decodeInfoObject = new JSONObject(encodeInfoStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			orderId = decodeInfoObject.optString("PAY_ORDER_ID");
			merchantName = decodeInfoObject.optString("MERCHANT_NAME");
			return true;
		} else if(returnCode.equals("100002")){
			Log.e("realPrice", ""+realPrice);
			createUpdateOrder(getApplicationContext(),
					realPrice,
					realPrice,
					Constant.DEAL_STATE_0,
					phoneNumber);
		}
		else{
			String msgType = object.optString("ACTION_RETURN_TIPSTYPE");
			String msg = object.optString("ACTION_RETURN_TIPS");
			
			showMarkedWords(msgType, msg, returnCode);
		}

		return false;
	}
	
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
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
		showDialog();
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
		params.add(new BasicNameValuePair("signMsg", AccountPref.get(mContext).getSignMsg()));
		
		
		// 发送网络请求
		HttpUtil.HttpsPost(context, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
//				Toast.makeText(PayPhoneFeeConfirmActivity.this, "完成", Toast.LENGTH_LONG).show();
				for(Activity activity:HomeUtils.activityList){
					activity.finish();
				}
				
				PayPhoneFeeConfirmActivity.this.finish();
				dismissDialog();
			}

			@Override
			public void data(byte[] data, int length, String url,
					int requestId, int actionId) {
				try {
					Log.e("loading auto login", "data:" + new String(data));
					JSONObject jsonObject = new JSONObject(new String(data));
					System.out.println();
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					String errmsg = jsonObject.optString(RetCode.ERROR_MSG);
					for(Activity activity:HomeUtils.activityList){
						activity.finish();
					}
					PayPhoneFeeConfirmActivity.this.finish();
					dismissDialog();
					if (RetCode.OK.equals(retCode)) {
						handler.obtainMessage(SHOW_TOAST,
								"充值成功")
								.sendToTarget();
					} else {
						if(errmsg!=null&&errmsg.length()>0){
							handler.obtainMessage(SHOW_TOAST,
									errmsg)
									.sendToTarget();
						}else{
							handler.obtainMessage(SHOW_TOAST,
									"充值失败")
									.sendToTarget();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					dismissDialog();
				}
			}
		}, true);
	}

	class ListViewAdapter extends BaseAdapter {
		List<PhoneCardPrice> list;
		Context context;

		public ListViewAdapter(Context context, List<PhoneCardPrice> list) {
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.price_select_spinner_item, null);
			}
			TextView cardname = (TextView) convertView
					.findViewById(R.id.cardname);
			TextView cardPrive = (TextView) convertView
					.findViewById(R.id.cardPrice);

			PhoneCardPrice cardItem = (PhoneCardPrice) getItem(position);

			cardname.setText("￥"
					+ StringUtil.longToDoubleString(cardItem.getRealPrice()));
			cardPrive
					.setText("￥"
							+ StringUtil.longToDoubleString(cardItem
									.getPurchasePrice()));
			return convertView;
		}

	}

	/**
	 * 后退键处理
	 */
	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing() && mLoadControler != null) {
			mLoadControler.cancel();
			dismissDialog();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
        Log.e(LOG_TAG, " " + "" + msg.obj);
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }

        String tn = "";
        String orderNo="";
        if (msg.obj == null || ((String) msg.obj).length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("错误提示");
            builder.setMessage("网络连接失败,请重试!");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
//            tn = (String) msg.obj;
        	//{"orderNo":"20140603130825688","tn":"201406031308250035702"}
        	try{
        		JSONObject jo = new JSONObject((String) msg.obj);
        		if(jo!=null&&jo.has("orderNo")){
        			orderNo = jo.getString("orderNo");
        		}
        		if(jo!=null&&jo.has("tn")){
        			tn = jo.getString("tn");
        		}
        		Log.e("orderNo:", ""+orderNo);
        		Log.e("tn:", ""+tn);
        	}catch(Exception e){
        		
        	}
        	
            /************************************************* 
             * 
             *  步骤2：通过银联工具类启动支付插件 
             *  
             ************************************************/
//            doStartUnionPayPlugin(this, tn, mMode);
            
            UPPayAssistEx.startPayByJAR(PayPhoneFeeConfirmActivity.this, PayActivity.class, null, null,
                    tn, mMode);
        }

        return false;
	}

	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /************************************************* 
         * 
         *  步骤3：处理银联手机支付控件返回的支付结果 
         *  
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel
         *      分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
            //创建一个成功的订单
			createUpdateOrder(getApplicationContext(),
				realPrice,
				realPrice,
				Constant.DEAL_STATE_88,
				phoneNumber);
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
            //创建一个待支付的订单
			createUpdateOrder(getApplicationContext(),
					realPrice,
					realPrice,
					Constant.DEAL_STATE_0,
					phoneNumber);
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
            //创建一个待支付的订单
            createUpdateOrder(getApplicationContext(),
					realPrice,
					realPrice,
					Constant.DEAL_STATE_0,
					phoneNumber);
         
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        //builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	
            	//结束手机支付相关的界面
            	HomeUtils.finishPhoneRecharge();
            	
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

	@Override
	public void onClick(View arg0) {
		if(commonDialog.isShow()){
			commonDialog.dismissDialog();
		}
		
		if(chosePayWayDialog.isShow()){
			chosePayWayDialog.dismissDialog();
		}
	}
}
