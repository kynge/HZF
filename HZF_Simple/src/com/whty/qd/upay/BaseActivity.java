package com.whty.qd.upay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.whty.qd.pay.common.Log;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.dialog.CommonDialog;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.dialog.SingleCommonDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.home.HomeUtils;

public class BaseActivity extends Activity {
	private static String TAG = "BaseActivity";
	private Context mContext;
	private CommonProgressDialog commonProgressDialog;
	private SingleCommonDialog singleCommonDialog;
	
	/** @Fields loginSuccessReceiver : 登录成功广播接收器 */
	private BroadcastReceiver loginSuccessReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent data) {
			onReceiveLoginSuccess(context, data);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		register();
		commonProgressDialog = new CommonProgressDialog(mContext);
		singleCommonDialog = new SingleCommonDialog(mContext);
		commonDialog = new CommonDialog(mContext);
	}

	/**
	 * @Title: onReceiveLoginSuccess
	 * @Description:登录成功
	 * @param context
	 * @param data
	 */
	protected void onReceiveLoginSuccess(Context context, Intent data) {

	}

	public void showDialog() {
		try {
			if (commonProgressDialog.isShow()) {
				return;
			} else {
				commonProgressDialog.showDialog(null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showDialog(String msg) {
		try {
			if (commonProgressDialog.isShow()) {
				commonProgressDialog.setMessage(msg);
			} else {
				commonProgressDialog.showDialog(msg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCancelable(boolean is) {
		commonProgressDialog.setCancelable(is);
	}

	public void dismissDialog() {
		try {
			if (commonProgressDialog.isShow()) {
				commonProgressDialog.dialogDismiss();
			} else {
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isDialogShowing() {
		return commonProgressDialog.isShow();
	}

	/**
	 * 弹出提示框
	 * 
	 * @param msg
	 *            提示语
	 */
	public void showPromptDialog(String msg) {
		singleCommonDialog.showDialog(msg,
				mContext.getString(R.string.text_confirm),
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						singleCommonDialog.dismissDialog();

					}
				});
	}
	
	/**
	 * 弹出提示框
	 * 
	 * @param msg
	 *            提示语
	 */
	public void showPromptDialog(String msg,View.OnClickListener listener) {
		singleCommonDialog.showDialog(msg,
				mContext.getString(R.string.text_confirm),
				listener);
	}

	/**
	 * 显示提示语
	 * 
	 * @param msgTpye
	 *            提示语类型 0为弹出框，1为气泡，2为禁用返回键类型弹出框
	 * @param msg
	 *            提示语内容
	 * @param msgCode
	 *            提示语返回码
	 */
	public void showMarkedWords(String msgType, String msg, String msgCode) {
		String realMsg = "";
		if (TextUtils.isEmpty(msg)) {
			realMsg = "通讯错误，请稍后再试(" + msgCode + ")";
		} else {
			realMsg = msg + "(" + msgCode + ")";
		}
		if (TextUtils.isEmpty(msgType) || msgType.equals("1")) {
			Toast.makeText(mContext, realMsg, Toast.LENGTH_SHORT).show();
		} else {
			showPromptDialog(realMsg);
			if (msgType.equals("2")) {
				setCancelable(false);
			}
		}
	}

	/**
	 * @Title: register
	 * @Description:注册音频口广播
	 */
	public void register() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION_LOGIN_SUCCESS);
		registerReceiver(loginSuccessReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(loginSuccessReceiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		dismissDialog();
	}
	
	
	public void showShortMsg(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void showShortMsg(int msg){
		showShortMsg(mContext.getString(msg));
	}
	
	public void showLongMsg(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	
	public void showLongMsg(int msg){
		showLongMsg(mContext.getString(msg));
	}
	
	
	/**
	 * 显示两个按钮的对话框
	 */
	public static void showCommonDialog(final Context mContext,
			String message,
			String sureBtnTxt,
			OnClickListener sureClickListener,
			String cancleBtnTxt,
			OnClickListener cancleClickListener) {
		commonDialog = new CommonDialog(mContext);
		commonDialog.showDialog(message,
				sureBtnTxt,
				sureClickListener,
				cancleBtnTxt,
				cancleClickListener );
	}
	
	/**
	 * 显示两个按钮的对话框
	 */
	public static void showCommonDialog(final Context mContext,
			String message,
			OnClickListener sureClickListener,
			OnClickListener cancleClickListener) {
		showCommonDialog(mContext, message,
				"确定",sureClickListener,
				"取消", cancleClickListener);
	}
	
	public static CommonDialog commonDialog;
	
	/**
	 * 显示两个按钮的对话框
	 */
	public static void showCommonDialog(final Context mContext,
			String message,
			int sureBtnTxt,
			OnClickListener sureClickListener,
			int cancleBtnTxt,
			OnClickListener cancleClickListener) {
		showCommonDialog(mContext,
				message, 
				mContext.getResources().getString(sureBtnTxt), 
				sureClickListener, 
				mContext.getResources().getString(cancleBtnTxt), 
				cancleClickListener);
	}
}
