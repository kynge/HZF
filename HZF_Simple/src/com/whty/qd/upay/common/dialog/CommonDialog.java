package com.whty.qd.upay.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whty.qd.upay.R;

/**
 * 通用dialog
 * 
 * @author Cai
 */
public class CommonDialog {
	public static final String UPDATE_PROGRESS_ACTION = "com.whty.qd.upay.UPDATE_PROGRESS_ACTION";
	public static final String PCT_TAG = "PCT";
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private TextView dialog_message;
	public TextView servicePhone_tv;
	private Button button_confirm;
	private Button button_cancel;
	
	/**
	 * 支付密码容器
	 */
	private RelativeLayout layout2;
	/**
	 * 密码
	 */
	private EditText psd;
	
	private TextView dialog_title_text;

	public CommonDialog(Context context) {
		mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.common_dialog_layout, null);
	
		dialog_title_text = (TextView) view.findViewById(R.id.dialog_title_text);
		
		button_confirm = (Button) view.findViewById(R.id.button_confirm);
		button_cancel = (Button) view.findViewById(R.id.button_cancel);
		
		dialog_message = (TextView) view.findViewById(R.id.dialog_message);
		servicePhone_tv = (TextView) view.findViewById(R.id.servicePhone_tv);
		
		psd = (EditText) view.findViewById(R.id.psd);
		layout2 = (RelativeLayout) view.findViewById(R.id.layout2);
		
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
	}
	
	
	public EditText getPwdEditText(){
		return psd;
	}
	
	
	public void showInputPwdDialog(View.OnClickListener listener_confirm,
			View.OnClickListener listener_cancel){
		dialog_message.setVisibility(View.GONE);
		layout2.setVisibility(View.VISIBLE);
		
		button_confirm.setText("支付");
		button_confirm.setOnClickListener(listener_confirm);
		button_cancel.setText("取消支付");
		button_cancel.setOnClickListener(listener_cancel);
		dialog.show();
	}
	
	

	/**
	 * showDialog
	 * 
	 * @param message
	 *            提示语
	 * @param buttonOneText
	 *            第一个按钮文字
	 * @param listener_confirm
	 *            第一个按钮事件
	 * @param listener_cancel
	 *            第二个按钮事件
	 */
	public void showDialog(String message, String buttonOneText,
			View.OnClickListener listener_confirm,
			View.OnClickListener listener_cancel) {
		dialog_message.setText(message);
		button_confirm.setText(buttonOneText);
		button_confirm.setOnClickListener(listener_confirm);
		button_cancel.setOnClickListener(listener_cancel);
		dialog.show();
	}
	
	/**
	 * showDialog
	 * 
	 * @param message
	 *            提示语
	 * @param buttonOneText
	 *            第一个按钮文字
	 * @param listener_confirm
	 *            第一个按钮事件
	 * @param listener_cancel
	 *            第二个按钮事件
	 */
	public void showDialog(String message,
			String btnConfirmTxt,
			View.OnClickListener listener_confirm,
			String btnCancleTxt,
			View.OnClickListener listener_cancel
			) {
		dialog_message.setText(message);
		button_confirm.setText(btnConfirmTxt);
		button_confirm.setOnClickListener(listener_confirm);
		button_cancel.setText(btnCancleTxt);
		button_cancel.setOnClickListener(listener_cancel);
		dialog.show();
	}
	

	/**
	 * showDialog
	 * 
	 * @param message
	 *            提示语
	 * @param buttonOneText
	 *            第一个按钮文字
	 * @param listener_confirm
	 *            第一个按钮事件
	 * @param listener_cancel
	 *            第二个按钮事件
	 * @param isServicePhoneVisible
	 *            是否让客服电话显示（该方法只在显示设备被锁时让客服电话visible时调用）
	 */
	public void showDialog(String message, String buttonOneText,
			View.OnClickListener listener_confirm,
			View.OnClickListener listener_cancel, boolean isServicePhoneVisible) {
		dialog_message.setText(message);
		servicePhone_tv.setVisibility(View.VISIBLE);
		/*servicePhone_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:4000111095");   
				Intent intent = new Intent(Intent.ACTION_DIAL, uri);     
				mContext.startActivity(intent);  
			}
		});*/
		button_confirm.setText(buttonOneText);
		button_confirm.setOnClickListener(listener_confirm);
		button_cancel.setOnClickListener(listener_cancel);
		dialog.show();
	}

	public void dismissDialog() {
		dialog.dismiss();
	}

	public boolean isShow() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setCancelable(boolean is) {
		dialog.setCancelable(is);
	}
}
