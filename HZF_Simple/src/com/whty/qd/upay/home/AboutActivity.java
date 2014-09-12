package com.whty.qd.upay.home;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whty.qd.pay.appinterface.DeviceChangedListener;
import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Utils;
import com.whty.qd.pay.devices.DeviceManager;
import com.whty.qd.pay.devices.impl.HeadSetDevice;
import com.whty.qd.pay.devices.impl.ICDevice;
import com.whty.qd.pay.devices.impl.hardware.headset.DovilaAPI;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;

/**
 * 关于模块
 * 
 * @author 吴非
 * 
 */
public class AboutActivity extends BaseActivity {
	private Context mContext;
	private RelativeLayout titleLayout;
	private TextView titleTv, introText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		findView();
	}

	private void findView() {
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		titleLayout.findViewById(R.id.button_title_back).setVisibility(
				View.INVISIBLE);
		titleTv = (TextView) titleLayout.findViewById(R.id.text_main_title);
		titleTv.setText(R.string.guanyu);
		introText = (TextView) findViewById(R.id.introTextView);
		introText.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		LinearLayout left_linear = (LinearLayout) findViewById(R.id.left_linear);
		left_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		String info = mContext.getResources().getString(R.string.app_intro_string);
		introText.setText(info);
	}
}
