package com.whty.qd.upay.home.widget;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.ResourceUtils;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.devices.Device;
import com.whty.qd.pay.info.AccountInfo;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.business.BusinessItem;
import com.whty.qd.upay.collection.CollectionActivity;
import com.whty.qd.upay.common.dialog.CommonDialog;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.creditcard.CreditCardPaymentActivity;
import com.whty.qd.upay.home.HomeUtils;
import com.whty.qd.upay.mybankcard.MyBankCardActivity;
import com.whty.qd.upay.mypayment.MyPaymentActivity;

/**
 * 首页网格视图
 */
public class HomeGrid {
	private Context mContext;
	private CustomGridView mGridView;
	private GridAdapter adapter;
	private CommonDialog commonDialog;
	private CommonProgressDialog commonProgressDialog00;
	private ImageView[] imageViews = null;
	private ImageView imageView = null;
	private int pageIndex = 0;
	private String errMessage;
	private Intent intent;
	
	public HomeGrid(Context context, ViewPager viewPager) {
		mContext = context;
		commonDialog = new CommonDialog(mContext);
		commonProgressDialog00 = new CommonProgressDialog(mContext);
	}

	public View getGridView(List<BusinessItem> busiList) {
		mGridView = new CustomGridView(mContext);
		mGridView.setLayoutParams(new LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mGridView.setPadding(0, 10, 0, 0);
		mGridView.setHorizontalSpacing(20);
		mGridView.setNumColumns(3);
		mGridView.setVerticalSpacing(15);
		mGridView.setSelector(R.color.mytransparent);
		mGridView.setStretchMode(2);
		adapter = new GridAdapter(mContext, busiList);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		return mGridView;
	}

	/**
	 * 获取LinearLayout
	 * 
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	public View getLinearLayout(View view) {
		LinearLayout linerLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams linerLayoutParames = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		// 可自定义设置
		linerLayout.setPadding(10, 0, 10, 0);
		linerLayout.addView(view, linerLayoutParames);

		return linerLayout;
	}

	/**
	 * 设置圆点个数
	 * 
	 * @param size
	 */
	public void setCircleImageLayout(int size) {
		imageViews = new ImageView[size];
	}

	/**
	 * 生成圆点图片区域布局对象
	 * 
	 * @param index
	 * @return
	 */
	public ImageView getCircleImageLayout(int index) {
		imageView = new ImageView(mContext);
		imageView.setLayoutParams(new LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		imageViews[index] = imageView;

		if (index == 0) {
			// 默认选中第一张图片
			imageViews[index].setBackgroundResource(R.drawable.point_focus);
		} else {
			imageViews[index].setBackgroundResource(R.drawable.point_default);
		}

		return imageViews[index];
	}
	
	/**
	 * 设置当前滑动图片的索引
	 * 
	 * @param index
	 */
	public void setPageIndex(int index) {
		pageIndex = index;
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BusinessItem businessItem = (BusinessItem) parent.getAdapter()
					.getItem(position);
			if (businessItem.getBusinessId().equals("-1")) {
				
			} else {
				if (!HomeUtils.isRequireUserLogin(businessItem.getMethod())) {// 没有登录
					HomeUtils.redrectToLoginPage(mContext);
					return;
				}else{
					openIntent(businessItem);
				}
			}
		}
	};

	/***
	 * @author super body
	 * @descriptions open application
	 * @param businessItem
	 */
	protected void openIntent(BusinessItem businessItem) {
		if ("0".equals(businessItem.getVersion())) {
			showDevelopingDialog(); // 未实现功能提示
			return;
		}
		if (businessItem.getBusinessId().equals("107")
				&& Device.headDeviceType==-1) {// 资金归集必须要多惠拉设备
			Toast.makeText(mContext, "请插入多惠拉设备！", Toast.LENGTH_SHORT).show();
			return;
		}

		Log.e("openIntent", "BusinessId:" + businessItem.getBusinessId());
		Log.e("openIntent", "DownUrl:" + businessItem.getDownUrl());
		Log.e("openIntent", "PackageName:" + businessItem.getPackageName());
		Log.e("openIntent", "ClassName:" + businessItem.getClassName());
		Log.e("openIntent", "PayMethod:" + businessItem.getPaymethod());
		intent = new Intent();
		
		if(businessItem.getBusinessId().equals("201")){
			//手机充值
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("PKGNAME", ResourceUtils.packageName);
			intent.putExtra("PAYNAME",
					"com.whty.qd.upay.payment.OrderConfirmActivity");
			intent.putExtra("MULTICHANNELQUERYNAME",
					"com.whty.qd.upay.convenience.MultiChannelQueryActivity");
			intent.putExtra("MULTICHANNELCHARGENAME",
					"com.whty.qd.upay.convenience.MultiChannelChargeActivity");
	
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
			
			checkAccountState();
		}
		else if(businessItem.getBusinessId().equals("103")){
			//转账
			Log.e("转账", "转账");
			
			Intent intent = new Intent();
			intent.setClass(mContext, CreditCardPaymentActivity.class);
			mContext.startActivity(intent);
			
		}else if(businessItem.getBusinessId().equals("203")){
			//付款
			Log.e("付款", "付款");
			Intent intent = new Intent();
			intent.setClass(mContext, MyPaymentActivity.class);
			mContext.startActivity(intent);
		}else if(businessItem.getBusinessId().equals("301")){
			//收款
			Log.e("收款", "收款");
			Intent intent = new Intent();
			intent.setClass(mContext, CollectionActivity.class);
			mContext.startActivity(intent);
		}else if(businessItem.getBusinessId().equals("303")){
			//我的银行卡
			Log.e("我的银行卡", "我的银行卡");
			Intent intent = new Intent();
			intent.setClass(mContext, MyBankCardActivity.class);
			mContext.startActivity(intent);
		}
	}
	
	/**
	 * @Description: 账户状态查询
	 */
	private void checkAccountState() {
//		showDialog("处理中...");
		((BaseActivity)mContext).showDialog();
		//本地测试
//		String accountId = "1120140310145403001"; 
//		String accountId = "14030617173030300001";
//		String accountId = "11";
		//快捷通服务器商户号
//		String accountId = "1120140310183957001"; 
		//锦付通服务器商户号
//		String accountId = "1120140312125256001";
		//锦付通测试服务器商户号
//		String accountId = "1120140317102936001";
		//伍平本机测试
//		String accountId ="1120140309213231001";
		
		String accountId = "1120140703115450001";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("accountId", accountId));
		params.add(new BasicNameValuePair("para", "getAccountStatus"));
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
			Log.e("checkAccountState", description);
			dismissDialog();
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			dismissDialog();
			String json = new String(data);
			Log.e("checkAccountState", json);
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
			Log.e("checkAccountState", "retCode: " + retCode);
			if (retCode.equals("000000")) {
				String status = object.optString("status");
				if(status.equals("3")){
					mHandler.sendEmptyMessage(10);
				}else if(status.equals("7")){
					mHandler.sendEmptyMessage(11);
				}
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
			((BaseActivity)mContext).dismissDialog();
			switch (msg.what) {
			case 10:
				showToast("商户已冻结");
				break;
			case 11:
				goIntoBusiness();
				break;
			case 1:
				showToast(errMessage);
				break;
			}
		}
	};
	
	private void goIntoBusiness() {
		try {
			if (ApplicationConfig.isLogon) {
				AccountPref account = AccountPref.get(mContext);
				intent.putExtra("userid", account.getUserId());
			}
			intent.putExtra("appId", "201");
			intent.putExtra("name", "手机充值");
			intent.putExtra("THEME_SELECT", "_redtea");
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			showDevelopingDialog(); // 未实现功能提示
		}
	}
	
	private void showToast(String str){
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}

	private void showDevelopingDialog() {
		commonDialog.showDialog(mContext.getString(R.string.text_app_not_ok),
				mContext.getString(R.string.text_confirm),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();

					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						commonDialog.dismissDialog();

					}
				});
	}

	public void showDialog(String msg) {
		if (commonProgressDialog00.isShow()) {
			return;
		} else {
			commonProgressDialog00.showDialog(msg);
		}
	}

	public void setCancelable(boolean is) {
		commonProgressDialog00.setCancelable(is);
	}

	public void dismissDialog() {
		if (commonProgressDialog00.isShow()) {
			commonProgressDialog00.dialogDismiss();
		} else {
			return;
		}
	}

}
