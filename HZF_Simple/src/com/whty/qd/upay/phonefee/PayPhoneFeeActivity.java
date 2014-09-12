package com.whty.qd.upay.phonefee;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.SecurityUtil;
import com.whty.qd.pay.common.net.LoadControler;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.pay.common.net.ResourceLoader;
import com.whty.qd.pay.common.net.WebSettings;
import com.whty.qd.pay.info.TransactionInfo;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.StringUtil;
import com.whty.qd.upay.home.HomeUtils;

/**
 * 手机话费充值界面类
 * 
 * @author super
 */
public class PayPhoneFeeActivity extends BaseActivity {
	private static String TAG = "PayPhoneFeeActivity";
	Context mContext;
	EditText phoneNubmerEdit; // 手机号码输入
	ImageButton phomeSelectButton;
	Button chargeButton; // 开始充值按钮
	LinearLayout phoneNumList_layout;
	Button button_title_back;
	boolean phoneNumListSpinner_flag = false;

	// 商品信息
	String phoneNumber;
	String merchantId = "";
	String phoneType = "";
	String phoneProvince = "";
	String merchantType = "";
	PhoneCardPriceList cardList = new PhoneCardPriceList();

	LoadControler mLoadControler;
	ProgressDialog dialog = null;
	List<String> spinnerValues = new ArrayList<String>();
	ListView phoneList;
	Dialog mDialog;
	private ImageButton select_no;
	private boolean isSelect = false; // 防止双击
	private List<SaveInfo> accountList;
	public SaveInfoAdapter adapter;

	private String SelectedStyle = "";
	private String userid;
	private String appId;
	private ImageView select_bottom_line;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = PayPhoneFeeActivity.this;
		Log.d(TAG, "onCreate...");
		HomeUtils.activityList.add(PayPhoneFeeActivity.this);

		Bundle extra = this.getIntent().getExtras();
		// 取用户名
		userid = getIntent().getStringExtra("userid");
		Log.e(TAG, "userid: " + userid);
		// 取appid
		appId = getIntent().getStringExtra("appId");

		initView();
		setListener();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		setContentView(R.layout.pay_charge_phone);
		((TextView) findViewById(R.id.text_main_title)).setText("手机充值");
		phoneNubmerEdit = (EditText) findViewById(R.id.phone_number_edit);
		select_no = (ImageButton) findViewById(R.id.select_no);
		select_bottom_line = (ImageView) findViewById(R.id.history_bottom_pic);
		phomeSelectButton = (ImageButton) findViewById(R.id.select_number_button);
		phoneNumList_layout = (LinearLayout) findViewById(R.id.spinnerLayout);
		button_title_back = (Button) findViewById(R.id.button_title_back);
		chargeButton = (Button) findViewById(R.id.start_recharge_button);

		// 获取上次成功创建手机充值订单的手机号为本次手机号默认值；若无上次成功号码，则以本机号码为默认值；
		SharedPreferences sp = this.getSharedPreferences("phoneRecharge",
				MODE_PRIVATE);
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		
		String my_phone_num = tm.getLine1Number();
		Log.e("my_phone_num:", ""+my_phone_num);
		if(my_phone_num!=null&&my_phone_num.length()>3){
			if(my_phone_num.startsWith("+")){
				my_phone_num = my_phone_num.substring(3, my_phone_num.length());
			}
		}
		phoneNubmerEdit.setText(sp.getString("phoneRechargeNum",
				my_phone_num));
	}

	/* 列表对话框 */
	private void showListDia() {
		phoneNumList_layout.removeAllViews();

		adapter = new SaveInfoAdapter(mContext, accountList);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.pay_credit_repayment_listview,
				null);
		phoneList = (ListView) view.findViewById(R.id.credit_repayment_list);
		phoneList.setAdapter(adapter);

		phoneNumList_layout.addView(view);

		if (!phoneNumListSpinner_flag) {
			phoneNumListSpinner_flag = true;
			phoneNumList_layout.setVisibility(View.VISIBLE);
		} else {
			phoneNumListSpinner_flag = false;
			phoneNumList_layout.setVisibility(View.GONE);
		}

		phoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SaveInfo info = (SaveInfo) parent.getAdapter()
						.getItem(position);
				phoneNubmerEdit.setText(info.getNum());
				phoneNumList_layout.setVisibility(View.GONE);
				phoneNumList_layout.removeAllViews();
				phoneNumListSpinner_flag = false;
				Message msg = new Message();
				msg.what = 10;
				handler.sendMessage(msg);
			}
		});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 10) {
				Log.v(TAG, "000000000000");
				phoneNumList_layout.invalidate();
			}
		}
	};

	/**
	 * 设置按钮事件监听器
	 */
	private void setListener() {
		button_title_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		phomeSelectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (phoneNumListSpinner_flag) {
					phoneNumList_layout.removeAllViews();
					phoneNumListSpinner_flag = false;
					phoneNumList_layout.setVisibility(View.GONE);
				}

				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_LOOKUP_URI);
				intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
				startActivityForResult(intent, 0);
			}
		});

		select_bottom_line.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					if (!phoneNumListSpinner_flag) {
						Log.e("PayPhoneFeeActivity", "777777777777777"
								+ phoneNumListSpinner_flag);
						showDialog();
					}
					getNumListRequest();
				} catch (JSONException e) {
					dismissDialog();
					e.printStackTrace();
				}
			}
		});

		chargeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (phoneNumListSpinner_flag) {
					phoneNumList_layout.removeAllViews();
					phoneNumListSpinner_flag = false;
					phoneNumList_layout.setVisibility(View.GONE);
				}

				if (!isSelect) {
					isSelect = true;
					showDialog();
					phoneNumber = phoneNubmerEdit.getText().toString();
					if (!StringUtil.isPhoneValid(phoneNumber)) {
						Toast.makeText(mContext, R.string.mobile_number_error,
								Toast.LENGTH_SHORT).show();
						dismissDialog();
						isSelect = false;
						return;
					}
					if (!StringUtil
							.isMobileNumberSegment(mContext, phoneNumber)) {
						Toast.makeText(mContext, R.string.mobile_segment_error,
								Toast.LENGTH_SHORT).show();
						dismissDialog();
						isSelect = false;
						return;
					}
					try {
						showDialog();
						startRequest(phoneNumber); // 请求面额等数据
					} catch (JSONException e) {
						e.printStackTrace();
						dismissDialog();
						isSelect = false;
					}
				}

			}
		});
	}

	private void getNumListRequest() throws JSONException {
		JSONObject data = new JSONObject();
		data.put("USER_ID", userid);
		data.put("APP_ID", appId);
		JSONObject root = HomeUtils.buildJSON("SELECT_INDIVIDUATION",
				data, this, "999", true);
		Log.d(TAG, root.toString());
		mLoadControler = ResourceLoader.getInstance(this).startLoadingResource(
				HomeUtils.BusinessURL, "POST", null, null,
				(root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
				false, false, mOnLoadListener00, 0);
		
//		createUpdateOrder(getApplicationContext(),
//				StringUtil.convertMoneyForDisplay2(tInfo.getMoney()),
//				StringUtil.convertMoneyForDisplay2(tInfo.getMoney()),
//				Constant.DEAL_STATE_0,
//				text_phone.getText().toString());
	}
	private TransactionInfo tInfo;
	/**
	 * 解析
	 */
	private OnLoadListener mOnLoadListener00 = new OnLoadListener() {
		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description + ": " + failingUrl);
			dismissDialog();

			Toast.makeText(mContext, R.string.network_requet_error,
					Toast.LENGTH_SHORT).show();
			return;
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			// dismissDialog();
			if (data != null) {
				String json = new String(data);
				Log.e(TAG, json);
				JSONObject object = null;
				JSONArray decodeInfoObject = null;
				try {
					object = new JSONObject(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String code = object.optString("ACTION_RETURN_CODE");
				if (code.equals("000000")||code.equals("100002")) {
					if (actionId == 1) {
						Toast.makeText(mContext, "删除账号成功！", Toast.LENGTH_SHORT)
								.show();
						dismissDialog();
						try {
							getNumListRequest();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						String encodeInfoStr = object.optString("ACTION_INFO");
						encodeInfoStr = SecurityUtil.decode(encodeInfoStr);
						try {
							decodeInfoObject = new JSONArray(encodeInfoStr);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (decodeInfoObject == null
								|| decodeInfoObject.length() == 0) {
							dismissDialog();
							Toast.makeText(
									mContext,
									R.string.pay_credit_repayment_search_history_empty,
									Toast.LENGTH_SHORT).show();
							return;
						}
						dismissDialog();
						// 取出卡号记录信息
						Log.d(TAG,
								"decodeInfoObject==>>"
										+ decodeInfoObject.toString());
						accountList = new ArrayList<SaveInfo>();

						for (int i = 0; i < decodeInfoObject.length(); i++) {
							JSONObject cardNumObject = decodeInfoObject
									.optJSONObject(i);
							String id = cardNumObject.optString("ID");
							String ruleString = cardNumObject.optString("RULE");
							JSONObject rule = null;
							try {
								rule = new JSONObject(ruleString);
							} catch (JSONException e) {}
							accountList.add(new SaveInfo(rule
									.optString("accountNum"), id));
						}

						// SaveInfo si1 = new SaveInfo("13764353746", "123");
						// accountList.add(si1);
						// SaveInfo si2 = new SaveInfo("13764353222", "456");
						// accountList.add(si2);
						// SaveInfo si3 = new SaveInfo("13764353333", "789");
						// accountList.add(si3);
						// SaveInfo si4 = new SaveInfo("13764353444", "159");
						// accountList.add(si4);

						showListDia();
					}
				} else {
					dismissDialog();

					String msgType = object.optString("ACTION_RETURN_TIPSTYPE");
					String msg = object.optString("ACTION_RETURN_TIPS");
					Log.e("*******************", "msgType:"+msgType+" msg:"+msg+" code:"+code);
					showMarkedWords(msgType, msg, code);
					return;
				}
			} else {
				dismissDialog();
				if (actionId == 1) {
					Toast.makeText(mContext, "系统繁忙，请稍后再试！", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(mContext, R.string.no_about_data,
							Toast.LENGTH_SHORT).show();
				}
			}

		}
	};

	/**
	 * 组装网络请求参数
	 * 
	 * @param userMobile
	 * @throws JSONException
	 */
	private void startRequest(final String userMobile) throws JSONException {
		JSONObject data = new JSONObject();
		data.put("PHONE", userMobile);
		JSONObject root = HomeUtils.buildJSON("SELECT_PRODUCT", data,
				this, appId, true);
		Log.d(TAG, root.toString());
		mLoadControler = ResourceLoader.getInstance(this).startLoadingResource(
				HomeUtils.BusinessURL, "POST", null, null,
				(root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
				false, false, mOnLoadListener, 0);
	}

	/**
	 * 数据解析/处理
	 */
	private OnLoadListener mOnLoadListener = new OnLoadListener() {
		@Override
		public void error(int id, String description, String failingUrl,
				int requestId, int actionId) {
			Log.e(TAG, description + ": " + failingUrl);
			isSelect = false;
			dismissDialog();

			Toast.makeText(mContext, R.string.network_requet_error,
					Toast.LENGTH_SHORT).show();
			return;
		}

		@Override
		public void data(byte[] data, int length, String url, int requestId,
				int actionId) {
			String json = new String(data);
			Log.d(TAG, json);
			if (!isDataValidated(json)) {// failed
				dismissDialog();
				// startActivityForResult(intent, 0);// 进入选择面值及信息确认界面
				isSelect = false;
				return;
			} else { // successed
				dismissDialog();
				Intent intent = new Intent(mContext,
						PayPhoneFeeConfirmActivity.class);
				intent.putExtra("userid", userid);
				intent.putExtra("PHONE", phoneNumber);
				intent.putExtra("PHONE", phoneNumber);
				intent.putExtra("MERCHANT_ID", merchantId);
				intent.putExtra("PHONE_TYPE", phoneType);
				intent.putExtra("PHONE_PROVINCE", phoneProvince);
				intent.putExtra("MERCHANT_TYPE", merchantType);
				intent.putExtra("PRODUCTS", cardList);
				// startActivityForResult(intent, 0);// 进入选择面值及信息确认界面
				startActivity(intent);
				isSelect = false;
			}
		}
	};

	/**
	 * 数据校验
	 * 
	 * @param string
	 * @return
	 */
	private boolean isDataValidated(final String string) {
		JSONObject object = null;
		JSONObject decodeInfoObject = null;
		try {
			object = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (object == null) {
			Toast.makeText(mContext, R.string.data_requet_error,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		String returnCode = object.optString("ACTION_RETURN_CODE");
		if (returnCode.equals("000000")) {
			String encodeInfoStr = object.optString("ACTION_INFO");
			encodeInfoStr = SecurityUtil.decode(encodeInfoStr);
			Log.d("decodeStr-->>>", encodeInfoStr);
			try {
				decodeInfoObject = new JSONObject(encodeInfoStr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isSelect = false;
			}
			if (decodeInfoObject == null) {
				Toast.makeText(mContext, R.string.no_about_data,
						Toast.LENGTH_SHORT).show();
				return false;
			}
			// 取出商信息
			merchantId = decodeInfoObject.optString("MERCHANT_ID");
			phoneType = decodeInfoObject.optString("PHONE_TYPE");
			phoneProvince = decodeInfoObject.optString("PHONE_PROVINCE");
			merchantType = decodeInfoObject.optString("MERCHANT_TYPE");
			JSONArray productsArray = decodeInfoObject.optJSONArray("PRODUCTS");
			if (productsArray == null || productsArray.length() == 0) {
				Toast.makeText(mContext, R.string.no_about_data,
						Toast.LENGTH_SHORT).show();
				return false;
			}
			List<PhoneCardPrice> list = new ArrayList<PhoneCardPrice>();
			int len = productsArray.length();
			for (int index = 0; index < len; index++) {
				PhoneCardPrice cardItem = new PhoneCardPrice();
				JSONObject one = productsArray.optJSONObject(index);
				cardItem.setName(one.optString("NAME"));
				cardItem.setProductId(one.optString("PRODUCT_ID"));
				cardItem.setRealPrice(one.optLong("PAR_PRICE"));
				cardItem.setPurchasePrice(one.optLong("PURCHASE_PRICE"));
				list.add(cardItem);
				cardItem = null;
				one = null;
			}
			cardList.setCardList(list); // 存储到类变量
			return true;
		} else {
			String msgType = object.optString("ACTION_RETURN_TIPSTYPE");
			String msg = object.optString("ACTION_RETURN_TIPS");
			
			showMarkedWords(msgType, msg, returnCode);
			return false;
		}
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		if (reqCode == 0 && resultCode == Activity.RESULT_OK) { // 选择号码后返回
			try {
				// List<String> spinnerValues = new ArrayList<String>();
				spinnerValues.clear();
				Uri contactData = data.getData();
				Cursor cur = managedQuery(contactData, null, null, null, null);
				ContentResolver contect_resolver = getContentResolver();
				if (cur.moveToFirst()) {
					String id = cur
							.getString(cur
									.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
					String number = "";
					Cursor phoneCur = contect_resolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (phoneCur.moveToNext()) {
						number = phoneCur
								.getString(phoneCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						spinnerValues.add(number);
						// phoneNubmerEdit.setText(number.replace("-", ""));
					}
				}
				if (spinnerValues == null || spinnerValues.size() == 0) {
					phoneNubmerEdit.setText("");
					Toast.makeText(mContext, "所选择联系人未添加电话联系方式",
							Toast.LENGTH_SHORT).show();
				} else if (spinnerValues.size() == 1) {
					phoneNubmerEdit.setText((spinnerValues.get(0)).replace("-",
							""));
				} else {

					final Dialog dialog = new Dialog(mContext, R.style.dialog);
					View view = LayoutInflater.from(mContext).inflate(
							R.layout.alert_layout, null);
					ListView listView = (ListView) view
							.findViewById(R.id.listView);
					listView.setAdapter(new ArrayAdapter<String>(mContext,
							R.layout.youxi_select_spinner_item, spinnerValues));
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							phoneNubmerEdit.setText(spinnerValues.get(position));
							dialog.dismiss();
						}
					});
					dialog.setContentView(view);
					dialog.show();

				}

			} catch (Exception e) {
				Toast.makeText(mContext, "读取通讯录出错，请手动输入", Toast.LENGTH_SHORT).show();
			}
		}
		if (reqCode == 0 && resultCode == 1) { // close when tranction success
			finish();
		}
	}

	public class SaveInfoAdapter extends BaseAdapter {

		LayoutInflater inflater;
		private List<SaveInfo> list1;

		public SaveInfoAdapter(Context context, List<SaveInfo> list) {
			inflater = LayoutInflater.from(context);
			list1 = list;
		}

		public void remove(SaveInfo itemInfo) {
			list1.remove(itemInfo);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list1.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list1.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final SaveInfo item = (SaveInfo) getItem(position);
			if (null == convertView) {
				convertView = inflater.inflate(R.layout.save_account_list_item,
						null);
			}

			TextView text_num = (TextView) convertView
					.findViewById(R.id.text_num);
			// TextView text_name = (TextView) convertView
			// .findViewById(R.id.text_name);
			Button button_delete = (Button) convertView
					.findViewById(R.id.button_delete);

			text_num.setText(item.getNum());
			// text_name.setText("(" + item.getName() + ")");
			button_delete.setFocusable(false);
			button_delete.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					try {
						// mDialog.dismiss();
						showDialog();
						deleteRequest(item.getId());
					} catch (JSONException e) {
						dismissDialog();
						e.printStackTrace();
					}
				}
			});

			return convertView;
		}
	}

	private void deleteRequest(String id) throws JSONException {
		JSONObject data = new JSONObject();
		data.put("ID", id);
		JSONObject root = HomeUtils.buildJSON("DELETE_INDIVIDUATION",
				data, this, "999", true);
		Log.d(TAG, root.toString());
		mLoadControler = ResourceLoader.getInstance(this).startLoadingResource(
				HomeUtils.BusinessURL, "POST", null, null,
				(root.toString()).getBytes(), 0, WebSettings.LOAD_NORMAL,
				false, false, mOnLoadListener00, 1);

	}

	/**
	 * 后退键处理
	 */
	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing() && mLoadControler != null) {
			mLoadControler.cancel();
			dismissDialog();
		} else {
			super.onBackPressed();
		}
	}
}
