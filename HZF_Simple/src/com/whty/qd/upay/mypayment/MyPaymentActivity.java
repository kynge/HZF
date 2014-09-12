package com.whty.qd.upay.mypayment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.CaptureActivity;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;


/**
 * 付款界面
 * @author shawn
 *
 */
public class MyPaymentActivity extends BaseActivity implements OnClickListener{
	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;
	/**
	 * 扫一扫按钮
	 */
	private Button btn_scan;
	
	private EditText et_credit_card_num;
	
	/**
	 * 付款按钮
	 */
	private Button btn_sure_payment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypayment);
		//初始化控件
		initView();
	}
	//初始化控件
	private void initView(){
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
		lblTitle.setText(R.string.mypayment_txt);
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		btn_sure_payment = (Button) findViewById(R.id.btn_sure_payment);
		btn_sure_payment.setOnClickListener(this);
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_scan.setOnClickListener(this);
		et_credit_card_num = (EditText) findViewById(R.id.et_credit_card_num);
	}
	
	/**
	 * 获取二维码指令
	 */
	private static final int REQUEST_GET_CONDE = 0;
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btn_sure_payment:
		case R.id.left_linear:{
			finish();
		}break;
		case R.id.btn_scan:{
			//获取二维码
			Intent intent_query = new Intent(MyPaymentActivity.this,CaptureActivity.class);
			startActivityForResult(intent_query,REQUEST_GET_CONDE);
		}break;
		default:
			break;
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_GET_CONDE && resultCode == RESULT_OK){
			String result = data.getExtras().getString("result");
//			L.e("result", ""+result);
			if(TextUtils.isEmpty(result)){
				showShortMsg("请重新扫描");
				return;
			}
			et_credit_card_num.setText(result);
		}
	}
}
