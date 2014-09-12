package com.whty.qd.upay.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.account.ModifyLoginPwd;
import com.whty.qd.upay.account.ModifyPayPwd;
import com.whty.qd.upay.account.ModifyUserInfo;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.common.methods.PublicMethod;

public class HomeSettingActivity extends BaseActivity {

	private HomeSettingActivity mContext;
	private ListView cornerListView1;
	private ListView cornerListView2;
	private ListView cornerListView3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.home_setting_layout);
		cornerListView1 = (ListView) findViewById(R.id.list1);
		cornerListView2 = (ListView) findViewById(R.id.list2);
		cornerListView3 = (ListView) findViewById(R.id.list3);
		List<String> myList1 = new ArrayList<String>();
		myList1.add("个人信息");
		List<String> myList2 = new ArrayList<String>();
		myList2.add("登录密码");
		myList2.add("支付密码");
		List<String> myList3 = new ArrayList<String>();
		myList3.add("关于");
		myList3.add("客服热线");
		myList3.add("注销");
		myList3.add("退出客户端");
		MyAdapter myListAdapter1 = new MyAdapter(mContext, myList1);
		MyAdapter myListAdapter2 = new MyAdapter(mContext, myList2);
		MyAdapter myListAdapter3 = new MyAdapter(mContext, myList3);
		cornerListView1.setAdapter(myListAdapter1);
		cornerListView2.setAdapter(myListAdapter2);
		cornerListView3.setAdapter(myListAdapter3);
		cornerListView1.setOnItemClickListener(myOnItemClickListener1);
		cornerListView2.setOnItemClickListener(myOnItemClickListener2);
		cornerListView3.setOnItemClickListener(myOnItemClickListener3);
	}

	private OnItemClickListener myOnItemClickListener1 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// 未登录应该跳到登录界面
			if (!HomeUtils.isUserLogin()) {
				HomeUtils.redrectToLoginPage(mContext);
				return;
			}
			Intent intent = new Intent(mContext, ModifyUserInfo.class);
			startActivity(intent);
		}
	};

	private OnItemClickListener myOnItemClickListener2 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// 未登录应该跳到登录界面
			if (!HomeUtils.isUserLogin()) {
				HomeUtils.redrectToLoginPage(mContext);
				return;
			}
			switch (position) {
			case 0:
				Intent intent = new Intent(mContext, ModifyLoginPwd.class);
				startActivity(intent);
				break;
			case 1:
				Intent intent1 = new Intent(mContext, ModifyPayPwd.class);
				startActivity(intent1);
				break;
			default:
				break;
			}

		}
	};

	private OnItemClickListener myOnItemClickListener3 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			switch (position) {
			case 0:
				Intent intent1 = new Intent();
				intent1.setClass(mContext, AboutActivity.class);
				startActivity(intent1);
				break;
			case 1:
				String phoneno = "400-630-6888";
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ phoneno));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
			case 2:
				if (!HomeUtils.isUserLogin()) {
					Toast.makeText(mContext, "未登录", Toast.LENGTH_SHORT).show();
					return;
				}
				HomeUtils.isNeedRefreshRec = true;
				HttpUtil.closeSession(1);
				AccountPref pre = AccountPref.get(mContext);
				ApplicationConfig.isLogon = false;
//				ApplicationConfig.accountInfo = null;
				pre.setAutoLogin(false);
				break;
			case 3:
				PublicMethod.appExit(mContext);
				break;
			default:
				break;
			}
		}
	};

	class MyAdapter extends BaseAdapter {
		private List<String> list;
		private Context context;

		MyAdapter(Context _context, List<String> _list) {
			this.context = _context;
			this.list = _list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(R.layout.home_setting_list_item, null);
			TextView myTxt = (TextView) convertView.findViewById(R.id.item_title_txt);
			ImageView leftArrow = (ImageView)convertView.findViewById(R.id.item_imageView);
			TextView telTxt = (TextView) convertView.findViewById(R.id.item_tel_txt);
			myTxt.setText(list.get(position));
			if(list.get(position).equals("客服热线")){
				leftArrow.setVisibility(View.INVISIBLE);
				telTxt.setVisibility(View.VISIBLE);
			}else{
				leftArrow.setVisibility(View.VISIBLE);
				telTxt.setVisibility(View.GONE);
			}
			return convertView;
		}
	}
}
