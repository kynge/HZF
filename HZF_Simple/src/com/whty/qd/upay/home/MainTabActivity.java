package com.whty.qd.upay.home;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.whty.qd.upay.R;
import com.whty.qd.upay.common.methods.PublicMethod;
import com.whty.qd.upay.finance.record.HomeTradeRecActivity;

/**
 * 主界面
 */
public class MainTabActivity extends TabActivity implements
		OnCheckedChangeListener {

	private Context mContext;
	private TabHost tabHost;
	private RadioButton radio_button_home;
	private RadioButton radio_button_record;
	private RadioButton radio_button_more;

	private Intent homeIntent; // 首页
	private Intent recordIntent;// 记录
	private Intent moreIntent;// 更多

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_tab_layout);
		intView();
		setupIntent();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void intView() {
		mContext = this;
		radio_button_home = (RadioButton) findViewById(R.id.radio_button_home);
		radio_button_record = (RadioButton) findViewById(R.id.radio_button_record);
		radio_button_more = (RadioButton) findViewById(R.id.radio_button_more);
		homeIntent = new Intent(this, HomePageActivity.class);
		recordIntent = new Intent(this, HomeTradeRecActivity.class);
		moreIntent = new Intent(this, HomeSettingActivity.class);
		radio_button_home.setOnCheckedChangeListener(this);
		radio_button_record.setOnCheckedChangeListener(this);
		radio_button_more.setOnCheckedChangeListener(this);
	}

	private void setupIntent() {
		this.tabHost = getTabHost();
		setupSettingTab(R.string.text_tab_home, "tab_home", homeIntent);
		setupSettingTab(R.string.text_tab_wallet, "tab_wallet", moreIntent);
		setupSettingTab(R.string.text_tab_record, "tab_record", recordIntent);
		setupSettingTab(R.string.text_tab_more, "tab_more", moreIntent);
	}

	private void setupSettingTab(int id, String tag, Intent intent) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(getView(id))
				.setContent(intent));
	}

	private View getView(int id) {
		View view = (View) LayoutInflater.from(this).inflate(
				R.layout.tab_item_layout, null);
		LinearLayout linear_bottom = (LinearLayout) view
				.findViewById(R.id.linear_bottom);
		linear_bottom.setBackgroundResource(R.drawable.tab_line_selector);
		TextView tab_item_name = (TextView) view
				.findViewById(R.id.tab_item_name);
		tab_item_name.setText(mContext.getString(id));
		return view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			// tab改变时，如果未登录，则将登录标志置为true，再次进入页面时仍跳到登录页面
			// HomeWalletActivity.isFirstNoLoginFalg = true;
			HomeTradeRecActivity.isFirstNoLoginFalg = true;

			switch (buttonView.getId()) {
			case R.id.radio_button_home:
				tabHost.setCurrentTabByTag("tab_home");
				break;
			case R.id.radio_button_record:
				tabHost.setCurrentTabByTag("tab_record");
				break;
			case R.id.radio_button_more:
				tabHost.setCurrentTabByTag("tab_more");
				break;
			default:
				break;
			}
		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_UP) {
			// if (slid_drawer.isOpened()) {
			// slid_drawer.toggle();
			// } else {
			PublicMethod.appExit(mContext);
			// }
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	// @Override
	// public void onBackPressed() {
	// PublicMethod.appExit(mContext);
	// }

}
