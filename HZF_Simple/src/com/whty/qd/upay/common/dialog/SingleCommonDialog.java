package com.whty.qd.upay.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.whty.qd.upay.R;

/**
 * 通用dialog
 * 
 * @author Cai
 */
public class SingleCommonDialog {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private TextView dialog_title;
	private TextView dialog_message;
	private Button button_confirm;
	private ImageView dialog_img;
	
	public SingleCommonDialog(){}
	
	public SingleCommonDialog(Context context) {
		mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.single_common_dialog_layout, null);
		button_confirm = (Button) view.findViewById(R.id.button_confirm);
		dialog_message = (TextView) view.findViewById(R.id.dialog_message);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
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
	 */
	public void showDialog(String message, String buttonOneText,
			View.OnClickListener listener_confirm) {
		dialog_message.setText(message);
		button_confirm.setText(buttonOneText);
		button_confirm.setOnClickListener(listener_confirm);
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
