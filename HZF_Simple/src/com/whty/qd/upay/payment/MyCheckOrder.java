package com.whty.qd.upay.payment;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationUrl;
import com.whty.qd.pay.common.JsonOperation;
import com.whty.qd.pay.common.SecurityUtil;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.common.net.ResourceLoader;
import com.whty.qd.pay.common.net.WebSettings;
import com.whty.qd.pay.devices.DeviceManager;
import com.whty.qd.pay.info.TransactionInfo;
import com.whty.qd.pay.payment.CheckOrder;
import com.whty.qd.upay.home.HomeUtils;

public class MyCheckOrder extends CheckOrder {
	
	private static String TAG = "MyCheckOrder";
	private int timeout = -1;
	Context mContext;
	Handler mHandler;
	private TransactionInfo tInfo;

	public MyCheckOrder(Context context, Handler handler) {
		super(context, handler);
		this.mContext = context;
		this.mHandler = handler;
	}
	
	/**
	 * 请求服务器查询订单
	 * 
	 * @param orderId
	 *            订单号
	 */
	public void queryOrder(String orderId) {
		String csn = "";
//		csn = DeviceManager.DEVICES.get(DeviceManager.HeadSet).CSN;
//		if (TextUtils.isEmpty(csn)) {
//			csn = DeviceManager.DEVICES.get(DeviceManager.SDCARD).CSN;
//		}
		try {
			JSONObject data = new JSONObject();
			data.put("ORDER_ID", orderId);
			data.put("CSN", csn);
			JSONObject root = HomeUtils.buildJSON("FIND_ORDER", data,
					(Activity) mContext, null, true);
			Log.e("queryOrder", data.toString());
			Log.e("queryOrder", root.toString());
			ResourceLoader.getInstance(mContext).startLoadingResource(
					HomeUtils.MPIURL, "POST", null, null,
					(root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
					false, false, mOnLoadListener, 0);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(GET_ORDER_FAIL);
		}
	}
	
	private OnLoadListener mOnLoadListener = new OnLoadListener() {

		JSONObject decodeInfoObject = null;

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			String jsonStr = new String(data);
			Log.e(TAG, jsonStr);
			JSONObject jObject = null;
			try {
				jObject = new JSONObject(jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String reCode = jObject.optString("ACTION_RETURN_CODE");
			if (reCode.equals("000000")) {
				String encodeInfoStr = jObject.optString("ACTION_INFO");
				Log.d(TAG + "encodeInfoStr", encodeInfoStr);
				if (actionId == 0) {
					if (JsonOperation.isStaticKey) {
						encodeInfoStr = SecurityUtil.decode(encodeInfoStr);
					} else {
						encodeInfoStr = JsonOperation
								.DecodeDataToText(encodeInfoStr);
					}
					Log.d(TAG + "encodeInfoStr", encodeInfoStr);
					try {
						decodeInfoObject = new JSONObject(encodeInfoStr);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (decodeInfoObject == null) {
						mHandler.sendEmptyMessage(GET_ORDER_FAIL);
						return;
					}
					String status = decodeInfoObject.optString("STATUS");
					if (status == null
							|| (!status.equals("0") && !status.equals("1")
									&& !status.equals("3") && !status
										.equals("99"))) {
						mHandler.sendEmptyMessage(GET_ORDER_FAIL);
						return;
					} else if ("3".equals(status)) {
						Toast.makeText(mContext, "该订单已关闭！", Toast.LENGTH_SHORT)
								.show();
						mHandler.sendEmptyMessage(ORDER_CLOSED);
						return;
					} else if ("99".equals(status)) {
						mHandler.sendEmptyMessage(ORDER_UNUSUAL);
						return;
					}

					timeout = decodeInfoObject.optInt("TIMEOUT", -1);
					Log.e("timeout", timeout + "");
					if (timeout > 0) {
						Message msg = new Message();
						msg.what = ORDER_TIMEOUT_GREATER_THAN_ZERO;
						msg.obj = timeout;
						mHandler.sendMessage(msg);
					} else if (timeout == 0) {// 已过期
						mHandler.sendEmptyMessage(ORDER_TIMEOUT_EQUAL_TO_ZERO);
						return;
					}

					String spid = decodeInfoObject.optString("SP_ID");
					Log.d("SP_ID", spid);
					String amount = decodeInfoObject.optString("AMOUNT");
					Log.d("AMOUNT", amount);
					String pay_name = decodeInfoObject
							.optString("MERCHANT_NAME");
					String pay_id = decodeInfoObject.optString("MERCHANT_ID");
					String pos_id = decodeInfoObject.optString("TERMINAL_ID");
					String order_id = decodeInfoObject.optString("ORDER_ID");
					String mall_order_id = decodeInfoObject
							.optString("MALL_ORDER_ID");
					Log.d("mall_order_id", "mall_order_id:" + mall_order_id);
					String trans_type = decodeInfoObject
							.optString("TRANS_TYPE");
					String currency = decodeInfoObject.optString("CURRENCY");
					String merchant_country = decodeInfoObject
							.optString("MERCHANT_COUNTRY");
					String SysProvide = decodeInfoObject
							.optString("SYS_PROVIDE");

					tInfo = new TransactionInfo();
					if (currency == null || currency.equals("")) {
						tInfo.setCurrency("156");
					} else {
						tInfo.setCurrency(currency);
					}
					if (merchant_country == null || merchant_country.equals("")) {
						tInfo.setMerchantCountry("156");
					} else {
						tInfo.setMerchantCountry(merchant_country);
					}
					tInfo.setOrderId(order_id);
					tInfo.setMallOrderId(mall_order_id);
					tInfo.setTerminalId(pos_id);
					//为检测需要从"武汉擎动网络科技有限公司"修改为"浙江快捷通网络技术有限公司"
//					tInfo.setMerchantName(pay_name);
					tInfo.setMerchantName("浙江快捷通网络技术有限公司");
					tInfo.setMerchantId(pay_id);
					tInfo.setMoney(amount);
					tInfo.setTransType(trans_type);
					tInfo.setSpid(spid);
					tInfo.setSysProvide(SysProvide);
					Message msg = new Message();
					msg.what = GET_ORDER_OK;
					msg.obj = tInfo;
					mHandler.sendMessage(msg);
				} else if (actionId == 1) {

					try {
						decodeInfoObject = new JSONObject(encodeInfoStr);
					} catch (JSONException e) {
						mHandler.sendEmptyMessage(TRANS_CHECK_DATA_WRONG);
						return;
					}
					if (decodeInfoObject == null) {
						mHandler.sendEmptyMessage(TRANS_CHECK_DATA_WRONG);
						return;
					}

					String riskLevel = decodeInfoObject.optString("RISK_LEVEL");
					String riskMessage = decodeInfoObject
							.optString("RISK_MESSAGE");
					Message msg = new Message();
					HashMap<String, String> riskMessageMap = new HashMap<String, String>();
					riskMessageMap.put("riskLevel", riskLevel);
					riskMessageMap.put("riskMessage", riskMessage);
					msg.obj = riskMessageMap;
					msg.what = TRANS_CHECK_DATA_OK;
					mHandler.sendMessage(msg);
				}
			} else {
				Message msg = new Message();
				if (actionId == 0) {
					msg.what = ORDER_DATA_ERROR;
				} else {
					msg.what = TRANS_CHECK_DATA_ERROR;
				}
				String msg00 = jObject.optString("ACTION_RETURN_TIPS");

				msg.obj = msg00+"("+reCode+")";
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description + ":[" + failingUrl + "]");
			if (actionId == 0) {
				mHandler.sendEmptyMessage(ORDER_REQUEST_ERROR);
			} else {
				mHandler.sendEmptyMessage(TRANS_CHECK_REQUEST_ERROR);
			}
		}

	};


}
