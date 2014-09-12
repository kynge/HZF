package com.whty.qd.upay.collection;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;


/**
 * 收款界面
 * @author shawn
 *
 */
public class CollectionActivity extends BaseActivity implements OnClickListener{
	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;
	/**
	 * 确认收款
	 */
	private Button btn_sure_coll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		//初始化控件
		initView();
	}
	//初始化控件
	private void initView(){
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
		lblTitle.setText(R.string.collection_txt);
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		btn_sure_coll = (Button) findViewById(R.id.btn_sure_coll);
		btn_sure_coll.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btn_sure_coll:
		case R.id.left_linear:{
			finish();
		}break;
		default:
			break;
		}
		
	}
}
