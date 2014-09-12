package com.whty.qd.upay.finance.record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.net.LoadControler;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.account.LoginActivity;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.common.dialog.ChooseDialog;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.common.methods.PublicMethod;
import com.whty.qd.upay.common.xlist.XListView;
import com.whty.qd.upay.common.xlist.XListView.IXListViewListener;
import com.whty.qd.upay.home.HomeUtils;

public class HomeTradeRecActivity extends BaseActivity implements
		IXListViewListener, OnClickListener {
	private static final int HAS_REC = 0;
	private static final int NO_REC = 1;
	private static final int HAS_NEW_REC = 100;
	private static final String ALL_TRADE_STATE = "0";// 所有交易
	private static final String NOT_PAY_STATE = "1";// 未支付交易
	private static final String HAS_OVER_STATE = "2";// 已完成交易
	private static final String HAS_OVER_STATE1 = "3";
	private static final String HAS_OVER_STATE2 = "88";
	private static final int SHOW_TOAST = 200;
	private static String TAG = "HomeTradeRecActivity";
	private static String nowPageStatus = "0";// 当前所在标签
	Context mContext;

	LinearLayout unloginTextLayout;// 未登录提示文字
	LinearLayout wholeLayout;// 登录后显示的页面
	LinearLayout allTradeView, noRecPromptArea;
	TextView allTradeText;
	LinearLayout notPayView;
	TextView notPayText;
	LinearLayout hasOverView;
	TextView hasOverText;
	TextView noRecPromptTxt, gotoPhoneTxt, toCreditTxt;
	XListView recListView;// 可下拉更新、上拉更多的自定义控件
	public static boolean isFirstNoLoginFalg = true;

	private LinearLayout right_linear;
	ImageButton saixuan_btn;
	ImageButton chooseBtn;

	List<TradeRecord> records;
	List<TradeRecord> nowPageRecords;
	LoadControler mLoadControler;

	int pageNum = 1; // 当前页 从1开始
	int pageSize = 10; // 每页记录
	int totalPage = 0; // 记录总页数
	int total = 0; // 总记录条数

	String startTime = "";
	String endTime = "";

	MyTradeAdapter adapter = null;

	int year;
	int month;
	int day;
	private String date;
	private ChooseDialog cDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = HomeTradeRecActivity.this;
		this.setContentView(R.layout.traderec);
		right_linear = (LinearLayout) findViewById(R.id.right_linear);
		chooseBtn = (ImageButton) this.findViewById(R.id.choose_time_btn);
		 ((TextView) findViewById(R.id.text_main_title)).setText(getResources().getString(R.string.kjt));
		cDialog = new ChooseDialog(mContext);
		unloginTextLayout = (LinearLayout) findViewById(R.id.unlogin_text_layout);
		wholeLayout = (LinearLayout) findViewById(R.id.whole_layout);
		
	}
	
	private class MyComparator implements Comparator<TradeRecord>{

		@Override
		public int compare(TradeRecord node1, TradeRecord node2) {
			if(node1.getDate().compareTo(node2.getDate())<0){
				
				return 1;
			}else if(node1.getDate().compareTo(node2.getDate())==0){
				if(node1.getDate().compareTo(node2.getDate())<0){
					return 1;
				}else if(node1.getDate().compareTo(node2.getDate())==0){
					return 0;
				}else{
					return -1;
				}
				
			}else{
				
				return -1;
			}
			
		}
		
	}	

	/**
	 * 初始化UI
	 */
	private void initUI() {
		allTradeView = (LinearLayout) findViewById(R.id.all_trade_layout);
		noRecPromptArea = (LinearLayout) findViewById(R.id.no_record_prompt_area);
		allTradeText = (TextView) findViewById(R.id.all_trade_text);
		notPayView = (LinearLayout) findViewById(R.id.not_pay_layout);
		notPayText = (TextView) findViewById(R.id.not_pay_text);
		gotoPhoneTxt = (TextView) findViewById(R.id.goto_phone_txt);
		gotoPhoneTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// BusinessItem business = getBusinessItem("201");
				// HomeUtils.openIntent(business, mContext);
			}
		});
		toCreditTxt = (TextView) findViewById(R.id.goto_creditcard_txt);
		toCreditTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// BusinessItem business = getBusinessItem("103");
				// HomeUtils.openIntent(business, mContext);
			}
		});
		gotoPhoneTxt.setText(Html.fromHtml("<u>充话费</u>"));
		toCreditTxt.setText(Html.fromHtml("<u>信用卡还款</u>"));
		hasOverView = (LinearLayout) findViewById(R.id.has_over_layout);
		hasOverText = (TextView) findViewById(R.id.has_over_text);
		recListView = (XListView) this.findViewById(R.id.tradelist);
		recListView.setCacheColorHint(Color.TRANSPARENT);
		// recListView.setPullLoadEnable(true);
		recListView.setXListViewListener(this);
		recListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Log.e(TAG, "position===" + position);
				if (position > 0){// 新xlist控件position从1开始
					TradeRecord node = records.get(position - 1);
					Log.e("!node.getTitle().equals(99)", ""+(!node.getTitle().equals("99")));
					if(!node.getTitle().equals("99")){
						return;
					}else{
						Intent intent = new Intent(mContext,
								HomeTradeRecConfirmActivity.class);
						intent.putExtra("record_transid", records.get(position - 1).getTransId());
						intent.putExtra("record_time", records.get(position - 1).getDate());
						intent.putExtra("record_phoneNo", records.get(position - 1).getPhoneNo());
						startActivity(intent);
					}
				}else{
					TradeRecord node = records.get(position);
					Log.e("!node.getTitle().equals(99)", ""+(!node.getTitle().equals("99")));
					if(!node.getTitle().equals("99")){
						return;
					}else{
						Intent intent = new Intent(mContext,
								HomeTradeRecConfirmActivity.class);
						intent.putExtra("record_transid", records.get(position).getTransId());
						intent.putExtra("record_time", records.get(position).getDate());
						intent.putExtra("record_phoneNo", records.get(position).getPhoneNo());
						startActivity(intent);
					}
				}
				
			}
		});

		List<String> conditionList = new ArrayList<String>();
		conditionList.add("当月交易");
		conditionList.add("近一周交易");
		conditionList.add("近一个月交易");

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH) + 1;
		date = year + "-" + (month < 10 ? "0" : "") + month + "-"
				+ (day < 10 ? "0" : "") + day;

		right_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Window dialogWindow = cDialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();

				lp.x = 0; // 新位置X坐标
				lp.y = -180; // 新位置Y坐标
				dialogWindow.setAttributes(lp);
				cDialog.showDialog(listenner);
			}
		});

		allTradeView.setOnClickListener(this);
		notPayView.setOnClickListener(this);
		hasOverView.setOnClickListener(this);
	}

	private OnItemClickListener listenner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			cDialog.dismissDialog();
			pageNum = 1;// 每次查询时，将从第1页开始加载
			switch (position) {
			case 0:
				startTime = year + "-" + (month < 10 ? "0" : "") + month
						+ "-01" + " " + "00:00:01";
				endTime = date + " " + "00:00:01";
				break;
			case 1:
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -7);
				startTime = new SimpleDateFormat("yyyy-MM-dd").format(cal
						.getTime())  + " " + "00:00:01";
				endTime = date  + " " + "00:00:01";
				break;
			case 2:
				Calendar calother = Calendar.getInstance();
				calother.add(Calendar.DATE, -30);
				startTime = new SimpleDateFormat("yyyy-MM-dd").format(calother
						.getTime())  + " " + "00:00:01";
				endTime = date  + " " + "00:00:01";
				break;
			}
			getMyData();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all_trade_layout:
			if (!nowPageStatus.equals(ALL_TRADE_STATE)) {// 判断是否为重复点击，重复时不加载数据
				recListView.setVisibility(View.VISIBLE);
				noRecPromptArea.setVisibility(View.GONE);
				stopNetLoader();// 停止当前的网络请求线程,避免数据覆盖
				allTradeView.setBackgroundColor(Color.parseColor("#e6a37a"));
				allTradeText.setTextColor(Color.parseColor("#ffffff"));
				notPayView.setBackgroundColor(Color.parseColor("#dfdfdf"));
				notPayText.setTextColor(Color.parseColor("#999999"));
				hasOverView.setBackgroundColor(Color.parseColor("#dfdfdf"));
				hasOverText.setTextColor(Color.parseColor("#999999"));
				pageNum = 1;// 每次更换标签时，将从第1页开始加载
				startTime = "";
				endTime = "";
				nowPageStatus = ALL_TRADE_STATE;
				getMyData();
			}
			break;
		case R.id.not_pay_layout:
			if (!nowPageStatus.equals(NOT_PAY_STATE)) {
				recListView.setVisibility(View.VISIBLE);
				noRecPromptArea.setVisibility(View.GONE);
				stopNetLoader();// 停止当前的网络请求线程,避免数据覆盖
				notPayView.setBackgroundColor(Color.parseColor("#e6a37a"));
				notPayText.setTextColor(Color.parseColor("#ffffff"));
				allTradeView.setBackgroundColor(Color.parseColor("#dfdfdf"));
				allTradeText.setTextColor(Color.parseColor("#999999"));
				hasOverView.setBackgroundColor(Color.parseColor("#dfdfdf"));
				hasOverText.setTextColor(Color.parseColor("#999999"));
				pageNum = 1;
				startTime = "";
				endTime = "";
				nowPageStatus = NOT_PAY_STATE;
				getMyData();
			}
			break;
		case R.id.has_over_layout:
			if (!nowPageStatus.equals(HAS_OVER_STATE)) {
				recListView.setVisibility(View.VISIBLE);
				noRecPromptArea.setVisibility(View.GONE);
				stopNetLoader();// 停止当前的网络请求线程,避免数据覆盖
				hasOverView.setBackgroundColor(Color.parseColor("#e6a37a"));
				hasOverText.setTextColor(Color.parseColor("#ffffff"));
				notPayView.setBackgroundColor(Color.parseColor("#dfdfdf"));
				notPayText.setTextColor(Color.parseColor("#999999"));
				allTradeView.setBackgroundColor(Color.parseColor("#dfdfdf"));
				allTradeText.setTextColor(Color.parseColor("#999999"));
				pageNum = 1;
				startTime = "";
				endTime = "";
				nowPageStatus = HAS_OVER_STATE;
				getMyData();
			}
			break;
		}
	}

	/**
	 * @Description: 取交易记录
	 */
	private void getMyData() {
		showDialog();
		AccountPref account = AccountPref.get(mContext);
		String phoneNo = account.getPhoneNo();//"qgytqrenqb@nowmymail.com";
		String payPwd = HomeUtils.userLoginPwdCache;//已经md5加密了
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("loginPwd", payPwd));
		params.add(new BasicNameValuePair("startTime", startTime.replace("-", "")));
		params.add(new BasicNameValuePair("endTime", endTime.replace("-", "")));
		params.add(new BasicNameValuePair("para", "getTransHisFromMobile"));
		try {
			HttpUtil.HttpsPost(mContext, params, onLoadListener, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private OnLoadListener onLoadListener = new OnLoadListener() {

		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description);
			Message msg = new Message();
			msg.what = SHOW_TOAST;
			msg.obj = description;
			handler.sendMessage(msg);
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			String json = new String(data);
			Log.e(TAG, json);
			JSONObject object = null;
			JSONObject itemObject = null;
			try {
				object = new JSONObject(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (object == null) {
				Message msg = new Message();
				msg.what = NO_REC;
				handler.sendMessage(msg);
				return;
			}
			String retCode = object.optString("retCode");
			Log.e(TAG, "retCode: " + retCode);
			if (retCode.equals("000000")) {
				JSONArray jsonArray = object.optJSONArray("resultList");
				if (jsonArray == null) {
					Message msg = new Message();
					msg.what = NO_REC;
					handler.sendMessage(msg);
					return ;
				}
				Log.e(TAG, "jsonArray.length(): " + jsonArray.length());
				List<NewTradeRecord> newRecords = new ArrayList<NewTradeRecord>();
				for (int i = 0; i < jsonArray.length(); i++) {
					itemObject = jsonArray.optJSONObject(i);
					NewTradeRecord nrecord;
					try {
						nrecord = paserNewTradeRecord(itemObject);
						
//						if(nowPageStatus.equals(NOT_PAY_STATE)&&nrecord.getTransType().equals("99")){
						if(nowPageStatus.equals(NOT_PAY_STATE)){
							if(nrecord.getTransType().equals("99")){
								if(nrecord.getStatus().equals("0")||nrecord.getStatus().equals("1")){
									newRecords.add(nrecord);
								}
							}else{ 
								if(nrecord.getStatus().equals("5")){
									newRecords.add(nrecord);
								}
							}
						}
//						else if(nowPageStatus.equals(HAS_OVER_STATE)&&nrecord.getTransType().equals("99")){
						else if(nowPageStatus.equals(HAS_OVER_STATE)){
//							if(nrecord.getStatus().equals("3")){
							if(nrecord.getStatus().equals("88")||nrecord.getStatus().equals("3")){
								newRecords.add(nrecord);
							}
						}else if(nowPageStatus.equals(ALL_TRADE_STATE)){
							newRecords.add(nrecord);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				Log.e(TAG, "newRecords.size(): " + newRecords.size());
				if(newRecords == null || newRecords.size()==0){
					Message msg = new Message();
					msg.what = NO_REC;
					handler.sendMessage(msg);
				}else{
					Message msg = new Message();
					msg.what = HAS_NEW_REC;
					msg.obj = newRecords;
					handler.sendMessage(msg);
				}
				return ;
			}else if(retCode.equals("999995")){
				Message msg = new Message();
				msg.what = NO_REC;
				handler.sendMessage(msg);
			}else {
				String errMessage = object.optString("errmsg");
				if(TextUtils.isEmpty(errMessage)){
					errMessage = object.optString("description");
				}
				Message msg = new Message();
				msg.what = SHOW_TOAST;
				msg.obj = errMessage+"("+retCode+")";
				handler.sendMessage(msg);
				return ;
			}
		}
	};
	
	/**
	 * 解析新交易记录
	 * @throws JSONException 
	 */
	private NewTradeRecord paserNewTradeRecord(JSONObject jsonObject) throws JSONException {
		NewTradeRecord nrecord = new NewTradeRecord();

		String accountSeq = jsonObject.optString("accountSeq");
		String amount = jsonObject.optString("amount");
		String changeamount = jsonObject.optString("changeamount");
		String fee = jsonObject.optString("fee");
		String id = jsonObject.optString("id");
		String status = jsonObject.optString("transStatus");
		String transId = jsonObject.optString("transId");
		String transType = jsonObject.optString("actionSeq");
		String phoneNo = jsonObject.optString("phoneNo");

		JSONObject timeJson = jsonObject.optJSONObject("createTime");
		String createTime = "";
		if(timeJson == null){
			createTime = "";
		}else{
		
			String year = timeJson.optString("year");
			String month = timeJson.optString("month");
			String date = timeJson.optString("date");
			String hours = timeJson.optString("hours");
			String minutes = timeJson.getString("minutes");
			String seconds = timeJson.getString("seconds");
			String allYear = String.valueOf(Integer.parseInt(year)+1900);
			createTime = allYear + "-"
					+ intMoD2Str(Integer.parseInt(month) + 1) + "-" 
					+ intMoD2Str(Integer.parseInt(date)) + " "
					+ intMoD2Str(Integer.parseInt(hours)) + ":" 
					+ intMoD2Str(Integer.parseInt(minutes)) + ":" 
					+ intMoD2Str(Integer.parseInt(seconds));
		}

		nrecord.setAccountSeq(accountSeq);
		nrecord.setAmount(amount);
		nrecord.setChangeamount(changeamount);
		nrecord.setFee(fee);
		nrecord.setId(id);
		nrecord.setStatus(status);
		nrecord.setTransId(transId);
		nrecord.setTransType(transType);
		nrecord.setCreateTime(createTime);
		nrecord.setPhoneNo(phoneNo);
		return nrecord;
	}

	// 停止加载框
	private void onLoad() {
		recListView.stopRefresh();
		recListView.stopLoadMore();
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		recListView.setRefreshTime("" + hour + ":" + (minute < 10 ? "0" : "")
				+ minute);
	}

	// 下拉更新
	@Override
	public void onRefresh() {
		Log.e(TAG, "onLoadMore");
		pageNum = 1;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					getMyData();
				} catch (Exception e) {
					e.printStackTrace();
					dismissAllDialog();
					Toast.makeText(mContext, R.string.network_requet_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		}, 10);
	}

	// 上滑更多
	@Override
	public void onLoadMore() {
		Log.e(TAG, "onLoadMore");
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				pageNum++;
				getMyData();
			}
		}, 10);
	}

	/**
	 * 接收消息 更新UI
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissAllDialog();
			switch (msg.what) {
			case NO_REC:
				if (adapter == null || pageNum == 1) {
					records.clear();
					adapter = new MyTradeAdapter(mContext, records);
					recListView.setAdapter(adapter);
					recListView.setPullLoadEnable(false);
					recListView.setVisibility(View.GONE);
					noRecPromptArea.setVisibility(View.VISIBLE);
				}
				break;
			case HAS_NEW_REC:
				recListView.setVisibility(View.VISIBLE);
				noRecPromptArea.setVisibility(View.GONE);
				if (adapter == null || pageNum == 1) {
					List<NewTradeRecord> _list = (List<NewTradeRecord>) msg.obj;
					List<TradeRecord> list = new ArrayList<TradeRecord>();
					for (NewTradeRecord newTradeRecord : _list) {
						TradeRecord record = new TradeRecord();
						record.setTitle(newTradeRecord.getTransType());
						record.setDate(newTradeRecord.getCreateTime());
						record.setMoney(newTradeRecord.getAmount());
						record.setState(newTradeRecord.getStatus());
						record.setTransId(newTradeRecord.getTransId());
						record.setPhoneNo(newTradeRecord.getPhoneNo());
						list.add(record);
					}
					records.clear();
					records.addAll(list);
					adapter = new MyTradeAdapter(mContext, records);
					recListView.setAdapter(adapter);
				} else {
					records.addAll((List<TradeRecord>) msg.obj);
					adapter.notifyDataSetChanged();
				}
				Log.e(TAG, "records.size()===" + records.size());
				if (records.size() >= total)// 当前已加载的数据小于总数据条数时，显示底部更多，否则隐藏
					recListView.setPullLoadEnable(false);
				else
					recListView.setPullLoadEnable(true);
				break;
			case SHOW_TOAST:
				recListView.setVisibility(View.GONE);
				noRecPromptArea.setVisibility(View.VISIBLE);
				showToast((String)msg.obj);
				break;
			default:
				break;

			}
		}
	};

	/**
	 * 交易记录adapter
	 */
	class MyTradeAdapter extends BaseAdapter {
		private List<TradeRecord> recs;
		private Context context;
		LayoutInflater factory = null;

		public MyTradeAdapter(Context _context, List<TradeRecord> _recs) {
			context = _context;
			factory = LayoutInflater.from(context);
			recs = _recs;
			Collections.sort(recs, new MyComparator());
		}

		@Override
		public int getCount() {
			return recs.size();
		}

		@Override
		public TradeRecord getItem(int position) {
			return recs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = factory.inflate(R.layout.traderrec_item, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.tradetitle);
			TextView money = (TextView) convertView.findViewById(R.id.trademoney);
			TextView date = (TextView) convertView.findViewById(R.id.tradedate);
			TextView state = (TextView) convertView.findViewById(R.id.tradestate);

			int titleCode = 0; 
			try{
				titleCode = Integer.parseInt(getItem(position).getTitle());
			}catch (Exception e) {
				titleCode = 0;
			}
			switch (titleCode) {
			case 1: //1网关
				title.setText("B2C网上银行充值");
				break;
			case 2://2充值
				title.setText("线下充值");
				break;
			case 3://3转账银行卡
				title.setText("账户间交易");
				break;
			case 4://4冻结
				title.setText("提现");
				break;
			case 5://5解冻
				title.setText("冲正");
				break;
			case 6://6结算
				title.setText("B2C网上银行消费");
				break;
			case 7://7补款
				title.setText("账户直冲");
				break;
			case 8://8提现
				title.setText("子账户间转账");
				break;
			case 9://9退款
				title.setText("调整");
				break;
			case 10://10付款
				title.setText("利息");
				break;
			case 15://11收款 内部转账使用
				title.setText("预付费卡转入");
				break;
			case 16://11收款 内部转账使用
				title.setText("转出至预付费卡");
				break;
			case 17://11收款 内部转账使用
				title.setText("B2B网上银行充值");
				break;
			case 18://11收款 内部转账使用
				title.setText("B2B网上银行消费");
				break;	
			case 19://11收款 内部转账使用
				title.setText("慧卡消费");
				break;
			case 20://11收款 内部转账使用
				title.setText("退款");
				break;
			case 21://11收款 内部转账使用
				title.setText("外卡网上银行消费");
				break;	
			case 99://擎动业务
				title.setText("手机充值");
				break;
			}
			
			money.setText("¥" + StringUtil.backDataFomat(getItem(position).getMoney()));
			date.setText(this.getItem(position).getDate());
			if(!getItem(position).getTitle().equals("99")){
				int AJStateCode = 0; 
				try{
					AJStateCode = Integer.parseInt(getItem(position).getState());
				}catch (Exception e) {
					AJStateCode = 0;
				}
				switch (AJStateCode) {
				case -1:
					state.setText("已撤销");
					break;
				case 0:
					state.setText("待确认");
					break;
				case 1:
					state.setText("买方已付款，等待卖方发货");
					break;
				case 2:
					state.setText("卖方已发货，等待买方确认");
					break;
				case 3:
					state.setText("交易成功");
					break;
				case 4:
					state.setText("交易失败");
					break;
				case 5:
					state.setText("等待买方付款");
					break;
				case 6:
					state.setText("交易取消");
					break;
				case 7:
					state.setText("申请退款");
					break;
				case 8:
					state.setText("争议处理");
					break;
				case 9:
					state.setText("退款成功");
					break;
				case 10:
					state.setText("取消退款");
					break;
				case 11:
					state.setText("待银行反馈");
					break;
				case 91:
					state.setText("已经撤销交易");
					break;
				default:
					state.setText("");
					break;
				}
			}else{
				int QDStateCode = 0; 
				try{
					QDStateCode = Integer.parseInt(getItem(position).getState());
				}catch (Exception e) {
					QDStateCode = 0;
				}
				switch (QDStateCode) {
				case -1:
					state.setText("已撤销");
					break;
				case 0:
					state.setText("未支付");
					break;
				case 1:
					state.setText("交易失败");
					break;
				case 2:
					state.setText("交易关闭");
					break;
				case 82:
					state.setText("已经支付，正在发货");
					break;
				case 81:
					state.setText("已经支付，发货失败");
					break;
				case 88:
					state.setText("已经支付，交易完成");
					break;
				case 91:
					state.setText("已经撤销交易");
					break;
				case 92:
					state.setText("已经退货");
					break;
				default:
					state.setText("");
					break;
				}
			}
			
			return convertView;
		}

	}

	/**
	 * 后退键处理
	 */
	@Override
	public void onBackPressed() {
		if (mLoadControler != null && isDialogShowing()) {
			mLoadControler.cancel();
			dismissAllDialog();
		} else {
			PublicMethod.appExit(mContext);
		}
	}

	/**
	 * 关闭所有进度框和上下进度条
	 */
	private void dismissAllDialog() {
		dismissDialog();
		onLoad();
	}

	/**
	 * 停止网络加载
	 */
	protected void stopNetLoader() {
		if (mLoadControler != null) {
			mLoadControler.cancel();
		}
	}

	/**
	 * 为了避免未登录进入页面，弹到登录页面返回后，页面数据不能加载情况
	 */
	@Override
	protected void onResume() {
		super.onResume();
		 Log.e(TAG, "start record onresume....");
		 dismissDialog();
		if (ApplicationConfig.isLogon == false) {
			chooseBtn.setVisibility(View.GONE);
			if (isFirstNoLoginFalg) {// 第一次未登录时直接跳转到登录页面
				Log.e(TAG, "isFirstNoLoginFalg: go into login...");
				isFirstNoLoginFalg = false;
				startActivity(new Intent(mContext, LoginActivity.class));
			} else {
				unloginTextLayout.setVisibility(View.VISIBLE);// 未登录则显示登录提示
				wholeLayout.setVisibility(View.GONE);
				TextView goLoginText = (TextView) findViewById(R.id.go_login_text);
				goLoginText.setText(Html
						.fromHtml("<u><font color='#FF0000'>登录</font></u>"));// 登录文字做成连接
				// 点击进入登录页面
				goLoginText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Log.e(TAG, "go into login...");
						Intent intent = new Intent(mContext,
								LoginActivity.class);
						startActivity(intent);
					}
				});
			}
		} else {
			isFirstNoLoginFalg = true;
			chooseBtn.setVisibility(View.VISIBLE);
			unloginTextLayout.setVisibility(View.GONE);
			wholeLayout.setVisibility(View.VISIBLE);
			if (HomeUtils.isNeedRefreshRec) {// 首次进入时候加载数据，当已有数据时回至该页面则不作为
				records = new ArrayList<TradeRecord>();
				initUI();
				pageNum = 1;
				getMyData();
				HomeUtils.isNeedRefreshRec = false;
			}
		}
	}
	
	private void showToast(String str){
		Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 将月份或日期整数，转换成带0或不带0的字符串
	 * @param d//月份或日期
	 */
	private String intMoD2Str(int d){
		return (d<10?"0":"") +d;
	}

}
