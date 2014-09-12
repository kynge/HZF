package com.whty.qd.upay.common.dialog;

import com.whty.qd.upay.R;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 通用ProgressDialog
 * 
 * @author Cai
 */
public class CommonProgressDialog {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private ImageView dialog_image;
	private TextView dialog_text;
	private Animation dialogAnimation;

	public CommonProgressDialog(Context context) {
		this(context, null);
	}

	public CommonProgressDialog(Context context, AttributeSet attrs) {
		super();
		mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.common_progress_dialog_layout, null);
		dialog_image = (ImageView) view.findViewById(R.id.dialog_image);
		dialog_text = (TextView) view.findViewById(R.id.dialog_text);
		dialogAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.dialog_rotate);
		dialog.setContentView(view);
	}

	public void showDialog(String msg) {
		if(TextUtils.isEmpty(msg)){
			dialog_text.setText(mContext.getString(R.string.data_loading));
		}else {
			dialog_text.setText(msg);
		}
		dialog_image.startAnimation(dialogAnimation);
		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void setMessage(String msg) {
		dialog_text.setText(msg);
	}
	
	public void dialogDismiss() {
		try {
			dialog_image.clearAnimation();
			dialog.dismiss();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isShow() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}

	public void setCancelable(boolean is){
		dialog.setCancelable(is);
	}
	
	public Dialog getDialog() {
		return dialog;
	}
}
