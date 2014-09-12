package com.whty.qd.upay.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.account.LoginActivity;
import com.whty.qd.upay.business.BusinessItem;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.home.widget.HomeGrid;

/**
 * 首页
 */
public class HomePageActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private ViewPager view_page;
	private LinearLayout linear_circle;
	private ArrayList<View> imagePageViews = null;
	private ImageView[] imageCircleViews = null;
	private int pageNum = 0; // 页码总数
	private int pageSize = 9; // 页码容量
	private HomeGrid homeGrid;

	/** @Fields imgAvater : 图像 */
	private ImageView imgAvater;
	/** @Fields lblName : 姓名和电话 */
	private TextView lblName;
	/** @Fields lblNotice : 余额 或提示登录 */
	private TextView lblNotice;
	/** @Fields lblRemain : 余额 */
	private TextView lblRemain;
	/** @Fields userName : 用户名 */
	private String userName;
	/** @Fields phoneNo : 手机号 */
	private String phoneNo;
	/** @Fields balance : 余额 */
	private double balance;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page_layout);
		initView();
	}

	private void initView() {
		mContext = this;
		view_page = (ViewPager) findViewById(R.id.view_page);
		homeGrid = new HomeGrid(mContext, view_page);
		linear_circle = (LinearLayout) findViewById(R.id.linear_circle);

		imgAvater = (ImageView) findViewById(R.id.avater);
		lblName = (TextView) findViewById(R.id.lbl_name);
		lblNotice = (TextView) findViewById(R.id.lbl_notice);
		lblRemain = (TextView) findViewById(R.id.lbl_remain);

		imgAvater.setOnClickListener(this);
		if (ApplicationConfig.isLogon) {
			onReceiveLoginSuccess(mContext, getIntent());
		}
		setView();
		
		
		//启动一个定时任务，每隔多少时间去checkSessions并把手机号码设置为空
		//超时，需要重新登录
//		if(ApplicationConfig.isLogon){
//			timer.schedule(task, 0, 1*60*1000);
//			ApplicationConfig.isLogon = false;
//		}
		handler.postDelayed(runnable, start_runn_delay_time);
	}


	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);
	}
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			Log.e("runnable", "run");
			try {
//				Thread.sleep(sleep_time);
				
//				HttpUtil.checkSessions2(mContext, null, null);
				if(ApplicationConfig.isLogon){
//					if(timer!=null){
//						timer=null;
//						timer.cancel();
//						timer = new Timer();
//						timer.schedule(task, 0, 1*60*1000);
//						ApplicationConfig.isLogon = false;
//					}
					
					HttpUtil.checkSessions2(mContext, null, null);
					
//					HomeUtils.isNeedRefreshRec = true;
//					HttpUtil.closeSession(1);
//					AccountPref pre = AccountPref.get(mContext);
//					ApplicationConfig.isLogon = false;
		//			ApplicationConfig.accountInfo = null;
//					pre.setAutoLogin(false);
					handler.postDelayed(runnable, start_runn_delay_time);
				}
				
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	};
	
	/**
	 * 延迟执行任务时间
	 */
	private int start_runn_delay_time = 3*60*1000;
	private int sleep_time = 2*60*1000;
	
	private Handler handler = new Handler();
	
	private Handler handler2 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e("handler2", "handler2");
			HttpUtil.checkSessions2(mContext, null, null);
			
			
//			HomeUtils.isNeedRefreshRec = true;
//			AccountPref pre = AccountPref.get(mContext);
//			HttpUtil.closeSession(2);
//			ApplicationConfig.isLogon = false;
////			ApplicationConfig.accountInfo = null;
//			pre.setAutoLogin(false);
		}
		
	};
	
	
	private Timer timer = new Timer(true);
	
	
	//任务
	private TimerTask task = new TimerTask(){

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			handler2.sendMessage(msg);
		}
		
	};
	
	private void setView() {
		List<BusinessItem> list = null;
		if (list == null) {
			list = new ArrayList<BusinessItem>();
		}

		list = HomeUtils.buildBusinessList();

		int size = list.size();
		pageNum = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;
		homeGrid.setCircleImageLayout(size);

		imagePageViews = new ArrayList<View>();
		imageCircleViews = new ImageView[pageNum];
		linear_circle.removeAllViews();
		for (int i = 0; i < pageNum; i++) {
			imagePageViews.add(homeGrid.getGridView(getbusinessList(list, i)));
			imageCircleViews[i] = homeGrid.getCircleImageLayout(i);
			linear_circle
					.addView(homeGrid.getLinearLayout(imageCircleViews[i]));
		}
		if (pageNum == 1) {
			linear_circle.setVisibility(View.GONE);
		} else {
			linear_circle.setVisibility(View.VISIBLE);
		}
		view_page.setAdapter(new SlideImageAdapter(imagePageViews));
		view_page.setOnPageChangeListener(new ImagePageChangeListener());
	}

	/**
	 * 获取当前页业务列表
	 */
	private List<BusinessItem> getbusinessList(List<BusinessItem> busiList,
			int pageIndex) {
		if (busiList.size() > (pageIndex + 1) * pageSize) {
			return busiList.subList(pageIndex * pageSize, (pageIndex + 1)
					* pageSize);
		} else {
			return busiList.subList(pageIndex * pageSize, busiList.size());
		}
	}

	class SlideImageAdapter extends PagerAdapter {

		ArrayList<View> imagePageViews;

		public SlideImageAdapter(ArrayList<View> imagePageViews) {
			this.imagePageViews = imagePageViews;
		}

		@Override
		public int getCount() {
			return imagePageViews.size();
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View arg0, int position, Object arg2) {
			((ViewPager) arg0).removeView(imagePageViews.get(position));
		}

		@Override
		public Object instantiateItem(View arg0, int position) {
			((ViewPager) arg0).addView(imagePageViews.get(position));

			return imagePageViews.get(position);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	private class ImagePageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int index) {
			homeGrid.setPageIndex(index);

			for (int i = 0; i < imageCircleViews.length; i++) {
				imageCircleViews[index]
						.setBackgroundResource(R.drawable.point_focus);

				if (index != i) {
					imageCircleViews[i]
							.setBackgroundResource(R.drawable.point_default);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (imgAvater == v) {
			if (ApplicationConfig.isLogon) {
				Toast.makeText(mContext, "您已登录", Toast.LENGTH_SHORT).show();
			} else {
				startActivity(new Intent(mContext, LoginActivity.class));
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("HomePageActivity", "onResume()");
		if(ApplicationConfig.isLogon == false){
			Log.e("", "homepage resume reflash login");
			lblName.setText("未登录");
			lblNotice.setText("请点击图标登录");
			lblRemain.setText("");
		}
		
		handler.postDelayed(runnable, start_runn_delay_time);
//		if(timer!=null){
//			timer.cancel();
//			if(ApplicationConfig.isLogon){
//				timer.schedule(task, 0, 1*60*1000);
//				ApplicationConfig.isLogon = false;
//			}
//		}
		
	}

	@Override
	protected void onReceiveLoginSuccess(Context context, Intent data) {
		super.onReceiveLoginSuccess(context, data);
		if (null != data) {
			userName = data.getStringExtra("userName");
			phoneNo = data.getStringExtra("phoneNo");
			balance = data.getDoubleExtra("balance", 0);
			Log.e("", "userName===="+userName);
			Log.e("", "phoneNo===="+phoneNo);
			Log.e("", "balance===="+balance);
		}
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(phoneNo)) {
			userName = AccountPref.get(mContext).getUserName();
			phoneNo = AccountPref.get(mContext).getPhoneNo();
			balance = AccountPref.get(mContext).getBalance();
		}
		
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(phoneNo)) {
			ApplicationConfig.isLogon = true;
			lblName.setText(String.format(getString(R.string.lbl_name),
					userName, phoneNo));
			lblNotice.setText(R.string.lbl_balance);
			lblRemain.setText(Constant.formatTwoDecimal(balance));
		}else{
			ApplicationConfig.isLogon = true;
			lblName.setText(phoneNo);
			lblNotice.setText(R.string.lbl_balance);
			lblRemain.setText(Constant.formatTwoDecimal(balance));
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);
	}
}
