package com.whty.qd.upay.finance.record;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.net.LoadControler;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.dialog.CommonDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.home.HomeUtils;
import com.whty.qd.upay.payment.OrderConfirmActivity;

public class HomeTradeRecConfirmActivity extends BaseActivity {
	private static String TAG = "HomeTradeRecConfirmActivity";
	Context mContext;

	TextView titleText;
	LinearLayout linearLayout;
	TradeRecord record;
	LinearLayout left_linear;
	Button payButton;
	private Button cancelButton;
	ProgressDialog dialog = null;
	ListView confirmListView = null;

	LoadControler mLoadControler;
	List<SimpleKeyValue> items;

	String payOrderId;// 订单信息
	String appId = "";
	String[] datas;
	String orderId;
	String bookTid = "";
	String bookPrice = "";
	
	private String phoneNo;
	private String amount;
	private String changeamount;
	private String bankAccount;
	private String payDate;
	private String stateCode;
	private String actionSeq;
	
	private TextView order_no_txt;
	private TextView phoneNo_txt;
	private TextView chargemoney_txt;
	private TextView trac_amount_txt;
	private TextView bank_account_txt;
	private TextView create_time_txt;
	private String errMessage;
	private CommonDialog commonDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = HomeTradeRecConfirmActivity.this;
		Log.d(TAG, "onCreate...");
		initView();
		orderId = getIntent().getStringExtra("record_transid");
		payDate = getIntent().getStringExtra("record_time");
		getMyData();
	}
	
	
	
	
	
	/**
	 * @Description: 取交易记录
	 */
	private void getMyData() {
		showDialog();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderId", orderId));
		params.add(new BasicNameValuePair("para", "searchTransDetail"));
		try {
			HttpUtil.HttpsPost(mContext, params, onLoadListener, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private OnLoadListener onLoadListener = new OnLoadListener() {

		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description);
			mHandler.sendEmptyMessage(1);
			errMessage = description;
			dismissDialog();
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			dismissDialog();
			String json = new String(data);
			Log.e(TAG, json);
			JSONObject object = null;
			JSONObject itemObject = null;
			try {
				object = new JSONObject(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (object == null) {
				return;
			}
			String retCode = object.optString("retCode");
			Log.e(TAG, "retCode: " + retCode);
			if (retCode.equals("000000")) {
				itemObject = object.optJSONObject("detail");
				if (itemObject == null) {
					return ;
				}
				amount = itemObject.optString("amount");
				changeamount = itemObject.optString("returnAmount");
				bankAccount = itemObject.optString("bankSeq");
				payOrderId = itemObject.optString("orderId");
				phoneNo = itemObject.optString("remark");
				stateCode = itemObject.optString("transStatus");
				actionSeq = itemObject.optString("actionSeq");
				mHandler.sendEmptyMessage(0);
				return ;
			} else {
				mHandler.sendEmptyMessage(1);
				errMessage = object.optString("errmsg");
				return ;
			}
		}
	};
	
	/**
	 * @Description: 撤销交易
	 */
	private void doCancel() {
		showDialog();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("orderId", orderId));
		params.add(new BasicNameValuePair("para", "cancelOrder"));
		try {
			HttpUtil.HttpsPost(mContext, params, onLoadListener1, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private OnLoadListener onLoadListener1 = new OnLoadListener() {

		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description);
			dismissDialog();
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			dismissDialog();
			String json = new String(data);
			Log.e(TAG, json);
			JSONObject object = null;
			try {
				object = new JSONObject(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (object == null) {
				return;
			}
			String retCode = object.optString("retCode");
			Log.e(TAG, "retCode: " + retCode);
			if (retCode.equals("000000")) {
				mHandler.sendEmptyMessage(10);
				return ;
			} else {
				mHandler.sendEmptyMessage(1);
				errMessage = object.optString("errmsg");
				return ;
			}
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				setView();
				break;
			case 1:
				showToast(errMessage);
				break;
			case 10:
				showToast("撤销成功");
				finish();
				HomeUtils.isNeedRefreshRec = true;
				break;
			}
		}
	};

	/**
	 * 初始化界面
	 */
	private void initView() {
		setContentView(R.layout.traderec_confirm);
		titleText = (TextView) findViewById(R.id.text_main_title);
		titleText.setText("交易详情");
		left_linear = (LinearLayout) findViewById(R.id.left_linear);
		left_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		payButton = (Button) findViewById(R.id.pay_button);
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, OrderConfirmActivity.class);
				intent.putExtra("ORDERID", payOrderId); // 订单信息数据对象
				intent.putExtra("APP_ID", "201");
				startActivity(intent);
				finish();
			}
		});
		
		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showConfirmCancelDialog();
			}
		});
		
		order_no_txt = (TextView)findViewById(R.id.order_no);
		phoneNo_txt = (TextView)findViewById(R.id.phoneNo);
		trac_amount_txt = (TextView)findViewById(R.id.trac_amount);
		chargemoney_txt = (TextView)findViewById(R.id.chargemoney);
		bank_account_txt = (TextView)findViewById(R.id.bank_account);
		bank_account_txt.setVisibility(View.GONE);
		create_time_txt = (TextView)findViewById(R.id.create_time);
	}
	
	protected void initPayBtn() {
		if("99".equals(actionSeq)){
			if (stateCode.equals("0") || stateCode.equals("1")) {
				payButton.setVisibility(View.VISIBLE);
			} else {
				payButton.setVisibility(View.GONE);
			}
		}
		
		if(!stateCode.equals("91")){
			cancelButton.setVisibility(View.VISIBLE);
		}
	}

	private void setView() {
		order_no_txt.setText(payOrderId); 
		phoneNo_txt.setText(phoneNo);  
		trac_amount_txt.setText("¥"+amount);  
		chargemoney_txt.setText("¥"+changeamount);  
		bank_account_txt.setText(myCardNumFormat(bankAccount));  
		create_time_txt.setText(payDate); 
		initPayBtn();
	}
	
	private void showToast(String str){
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 银行卡号字符串格式化
	 */
	public static String myCardNumFormat(String num) {
		if(TextUtils.isEmpty(num)||num.length()<10){
			return "--";
		}
		StringBuilder sb = new StringBuilder("");
		sb.append(num.substring(0, 4));
		for(int i=0;i<8;i++){
			sb.append("*");
		}
		sb.append(num.substring(num.length()-4));
		return sb.toString();
	}
	
	private void showConfirmCancelDialog() {
		commonDialog = new CommonDialog(mContext);
		commonDialog.showDialog(
				mContext.getString(R.string.cancel_order_prompt),
				mContext.getString(R.string.text_confirm),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						doCancel();
						commonDialog.dismissDialog();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();
					}
				});
	}

}
