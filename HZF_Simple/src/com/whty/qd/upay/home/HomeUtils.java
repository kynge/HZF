package com.whty.qd.upay.home;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.whty.qd.pay.cardmanage.CardOperation;
import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.SecurityUtil;
import com.whty.qd.pay.devices.DeviceManager;
import com.whty.qd.upay.R;
import com.whty.qd.upay.account.LoginActivity;
import com.whty.qd.upay.business.BusinessItem;
//import com.whty.qd.upay.cardmanage.SwipCardActivity;
import com.whty.qd.upay.common.dialog.CommonDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class HomeUtils {

	/**
	 * 建立一个public static的list用来放activity 用以在退出子业务时，清空其页面
	 */
	public static List<Activity> activityList = new ArrayList<Activity>();

	/**
	 * 交易记录是否需要刷新
	 */
	public static boolean isNeedRefreshRec = true;

	/**
	 * 账单记录是否需要刷新
	 */
	public static boolean isNeedRefreshBill = true;

	//保存的登录密码
	public static String userLoginPwdCache = "";
	//保存的支付密码
	public static String userPayPwdCache = "";

	/**
	 * check require login status
	 * 
	 * @param method
	 */
	public static boolean isRequireUserLogin(final String method) {
		if (ApplicationConfig.isLogon == false && method.equals("1")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * check login status
	 * 
	 * @param method
	 */
	public static boolean isUserLogin() {
		if (ApplicationConfig.accountInfo == null) {
			return false;
		} else {
			if (ApplicationConfig.isLogon == false) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * 对List<BusinessItem>进行排序,将待上线业务放到后面
	 * 
	 * @param List
	 *            <BusinessItem>
	 */
	public static void sortList(List<BusinessItem> _list) {
		List<BusinessItem> listOnLine = new ArrayList<BusinessItem>();
		List<BusinessItem> listUnLine = new ArrayList<BusinessItem>();
		if (_list == null) {
			_list = new ArrayList<BusinessItem>();
		} else {
			for (BusinessItem businessItem : _list) {
				if ("0".equals(businessItem.getVersion())
						|| "".equals(businessItem.getVersion())
						|| "null".equals(businessItem.getVersion())
						|| businessItem.getVersion() == null) {
					listUnLine.add(businessItem);
				} else {
					listOnLine.add(businessItem);
				}
			}
			_list.clear();
			_list.addAll(listOnLine);
			_list.addAll(listUnLine);
		}
	}
	
	
	public static final String VERSION = "100";
	public static final String VERSION_1 = "1";
	
	public static final String VERSION_0 = "0";

	public static List<BusinessItem> buildBusinessList() {
		List<BusinessItem> list = new ArrayList<BusinessItem>();
		BusinessItem item = new BusinessItem();
		item.setBusinessId("201");
		item.setIcon("icon_shoujichongzhi_default");// 手机充值online_201信用卡online_103.png
									// 电费online_203.png qqonline_301.png
									// 机票online_402.png电影票online_371.png
		item.setName("手机充值");
		item.setVersion(VERSION_1);
		item.setMethod("1");
		item.setClassName("com.whty.qd.upay.phonefee.PayPhoneFeeActivity");
		item.setPackageName("com.whty.qd.upay");

		BusinessItem item1 = new BusinessItem();
		item1.setBusinessId("103");
		item1.setIcon("icon_xinyongkahuankuan_default");// 手机充值online_201信用卡online_103.png
									// 电费online_203.png qqonline_301.png
									// 机票online_402.png电影票online_371.png
		item1.setName("转账");
		item1.setBusinessminicon("2");
		item1.setVersion(VERSION_0);
		item1.setMethod("1");

		BusinessItem item2 = new BusinessItem();
		item2.setBusinessId("203");
		item2.setIcon("icon_dianfei_default");// 手机充值online_201信用卡online_103.png
									// 电费online_203.png qqonline_301.png
									// 机票online_402.png电影票online_371.png
		item2.setName("电费");
		item2.setBusinessminicon("2");
		item2.setVersion(VERSION_0);
		item2.setMethod("1");
//		item2.setName("付款");
//		item2.setBusinessminicon("2");
//		item2.setVersion(VERSION_1);
//		item2.setMethod("1");

		BusinessItem item3 = new BusinessItem();
		item3.setBusinessId("301");
		item3.setIcon("icon_zijinguiji_default");// 手机充值online_201信用卡online_103.png
									// 电费online_203.png qqonline_301.png
									// 机票online_402.png电影票online_371.png
		
//		item3.setName("收款");
//		item3.setBusinessminicon("1");
//		item3.setVersion(VERSION_1);
//		item3.setMethod("1");
		item3.setName("Q币充值");
		item3.setBusinessminicon("1");
		item3.setVersion("0");
		item3.setMethod("1");
//		item3.setClassName("com.whty.qd.huiyuan.qq.QQCoinsDeltaFillActivity");
//		item3.setPackageName("com.whty.qd.huiyuan.qq");

		BusinessItem item4 = new BusinessItem();
		item4.setBusinessId("402");
		item4.setIcon("icon_xianhua_default");// 手机充值online_201信用卡online_103.png
									// 电费online_203.png qqonline_301.png
									// 机票online_402.png电影票online_371.png
		item4.setName("鲜花礼品");
		item4.setBusinessminicon("4");
		item4.setVersion("0");
		item4.setMethod("1");

		BusinessItem item5 = new BusinessItem();
		item5.setBusinessId("303");
		item5.setIcon("icon_youxikami_default");// 手机充值online_201信用卡online_103.png
									// 电费online_203.png qqonline_301.png
									// 机票online_402.png电影票online_371.png
		item5.setName("我的银行卡");
		item5.setBusinessminicon("7");
		item5.setVersion(VERSION_0);
		item5.setMethod("1");

		list.add(item);
		list.add(item1);
		list.add(item5);
		list.add(item3);
		list.add(item4);
		list.add(item2);

		return list;
	}

	public static String UserInfoURL = "http://uc.qdone.net.cn/puc/userServlet?sp=199";

	public static String BusinessURL = "http://sdp.qdone.net.cn/mall/paymentServer?sp=199";
	
//	public static String BusinessURL = "http://59.175.217.22:8081/mall/paymentServer?sp=199";
	
//	public static String BusinessURL = "http://192.168.8.119:8080/mall/paymentServer?sp=199";

	public static String MPIURL = "http://remotepay1.qdone.net.cn/mpi/terminalServer?sp=199";
	
//	public static String MPIURL = "http://59.175.217.22:8081/mpi/terminalServer?sp=199";

	public static String NocardCheckOrder = "http://remotepay1.qdone.net.cn/mpi/nocardUpmp?sp=199";

	public static JSONObject JSONHEAD;

	/**
	 * 生成JSON数据
	 * 
	 * @param isSecret
	 *            是否加密
	 */
	public static JSONObject buildJSON(String actionName, JSONObject body,
			Activity activity, String appId, boolean isSecret)
			throws JSONException {
		JSONObject obj = new JSONObject();
		if (appId != null) {
			obj.put("APP_ID", appId);
		}
		obj.put("ACTION_NAME", actionName);
		String CityCode = "0";
		obj.put("CITY_CODE", CityCode);
		obj.put("ACTION_INVOKER", getJSONHEAD(activity));
		obj.put("ACTION_INFO", isSecret ? SecurityUtil.encode(body.toString())
				: body);
		Log.e("BODY", body.toString());
		return obj;
	}

	public static void BuildJSONHead(Activity activity) throws JSONException {// 先0
																				// 后1
		try {
			JSONHEAD = new JSONObject();
			TelephonyManager tm = (TelephonyManager) activity
					.getApplicationContext().getSystemService(
							Context.TELEPHONY_SERVICE);
			String IMEI = tm.getDeviceId();
			JSONHEAD.put("IMEI", IMEI);
			String IMSI = tm.getSimSerialNumber();
			JSONHEAD.put("IMSI", IMSI);
			JSONHEAD.put("PHONE", "");
			JSONHEAD.put("OSNAME", "Android");
			String OSVER = android.os.Build.VERSION.RELEASE;
			JSONHEAD.put("OSVER", OSVER);
			String OSDESCRIPT = android.os.Build.MODEL;
			JSONHEAD.put("OSDESCRIPT", OSDESCRIPT);
			JSONHEAD.put("VER", "0");
			JSONHEAD.put("DEBUG", "0");
		} catch (JSONException e) {
			JSONHEAD = null;
			throw new JSONException("生成头文件失败！");
		} finally {
			Log.e("JSONHEAD>>", JSONHEAD + "");
		}
	}

	public static JSONObject getJSONHEAD(Activity activity)
			throws JSONException {
		if (JSONHEAD == null) {
			BuildJSONHead(activity);
		}
		return JSONHEAD;
	}

	/**
	 * login page
	 * 
	 * @param context
	 */
	public static void redrectToLoginPage(final Context context) {
		Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("needreturn", true);
		((Activity) context).startActivityForResult(intent, 0);
	}

	/***
	 * @author super body
	 * @descriptions open application
	 * @param businessItem
	 */
	public static void openIntent(BusinessItem businessItem, Context mContext) {
		CardOperation cop = CardOperation.getInstance(mContext);
		int dovilaState = -1;
		if (businessItem.getAid() != -1
				&& (cop.BankCards == null || cop.BankCards.size() == 0)
				&& !businessItem.getClassName().endsWith(
						"PayCardsManageActivity")) {// 没有绑卡
			dovilaState = DeviceManager.DEVICES.get(DeviceManager.HeadSet).STATE;
			if (dovilaState != 0 && dovilaState != 2) {
				Toast.makeText(mContext, "暂无银行卡相关信息，请插入多惠拉设备进行绑卡",
						Toast.LENGTH_SHORT).show();
				return;
			}
			showBindCardDialog(mContext);
			return;
		}
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("PKGNAME", ResourceUtils.packageName);
		intent.putExtra("PAYNAME",
				"com.whty.qd.upay.payment.OrderConfirmActivity");
		String clsName = businessItem.getClassName();
		if (!TextUtils.isEmpty(clsName.trim())) {
			if (clsName.startsWith(".")) {
				intent.setClassName(
						businessItem.getPackageName(),
						businessItem.getPackageName()
								+ businessItem.getClassName());
			} else {
				intent.setClassName(businessItem.getPackageName(),
						businessItem.getClassName());
			}
		} else {
			intent.setAction(businessItem.getAction());
		}
		try {
			intent.putExtra("userid", ApplicationConfig.accountInfo.getUserId());
			intent.putExtra("appId", businessItem.getBusinessId());
			intent.putExtra("THEME_SELECT", "_redtea");
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			showDevelopingDialog(mContext); // 未实现功能提示
		}
	}

	public static CommonDialog commonDialog;

	public static void showBindCardDialog(final Context mContext) {
		commonDialog = new CommonDialog(mContext);
		commonDialog.showDialog(
				mContext.getString(R.string.home_bindcard_first),
				mContext.getString(R.string.text_confirm),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();
						if (!HomeUtils.isUserLogin()) {
							HomeUtils.redrectToLoginPage(mContext);
						} else { // direct to card manage page
						// mContext.startActivity(new Intent(mContext,
						// SwipCardActivity.class));
						}
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();
					}
				});
	}

	public static void showDevelopingDialog(final Context mContext) {
		commonDialog = new CommonDialog(mContext);
		commonDialog.showDialog(
				mContext.getString(R.string.text_app_start_failed),
				mContext.getString(R.string.text_confirm),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent changeTabIntent = new Intent("ACTION_TAB_CHANGE");
						changeTabIntent.putExtra("TAB_NAME", "tab_home");
						mContext.sendBroadcast(changeTabIntent);
						commonDialog.dismissDialog();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();
					}
				});
	}

	public static void finishPhoneRecharge(){
		for(Activity activity : activityList){
			activity.finish();
		}
	}
	
}
