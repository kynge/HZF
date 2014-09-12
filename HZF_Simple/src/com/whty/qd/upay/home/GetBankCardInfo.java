package com.whty.qd.upay.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.UpayApplication;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.methods.HttpUtil;
import com.whty.qd.upay.phonefee.PayPhoneFeeConfirmActivity;


/**
 * 获取银行卡信息界面
 * @author Administrator
 *
 */
public class GetBankCardInfo extends BaseActivity implements OnClickListener{
	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;
	
	
	
	
	private String phone_num;
	private String recharge_money;
	
	/**
	 * TextView 手机号码
	 */
	private TextView tv_phone_num_value;
	
	/**
	 * TextView 充值金额
	 */
	private TextView tv_recharge_money2_value;
	
	
	
	/**
	 * EditText 银行卡号
	 */
	private EditText ev_bank_card_num2_value;
	
	/**
	 * EditText 支付密码
	 */
	private EditText et_pay_pwd2;
	
	
	/**
	 * 取消
	 */
	private Button btn_cancle;
	
	/**
	 * 确认
	 */
	private Button btn_sure;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getbank_card_info);
		
		initView();
		
	}
	
	private void initView(){
		
		
		mContext = GetBankCardInfo.this;
		HomeUtils.activityList.add(GetBankCardInfo.this);
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
		lblTitle.setText(R.string.phone_recharge);
		
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		
		
		tv_phone_num_value = (TextView) findViewById(R.id.tv_phone_num_value);
		tv_recharge_money2_value = (TextView) findViewById(R.id.tv_recharge_money2_value);
		ev_bank_card_num2_value = (EditText) findViewById(R.id.ev_bank_card_num2_value);
		et_pay_pwd2 = (EditText) findViewById(R.id.et_pay_pwd2);
		
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		
		btn_sure = (Button) findViewById(R.id.btn_sure);
		
		btn_cancle.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		
		
		Intent intent = getIntent();
		phone_num = intent.getExtras().getString("phoneNumber");
		recharge_money = intent.getExtras().getString("realPrice");
		
		
		if(phone_num!=null&&phone_num.length()>0){
			tv_phone_num_value.setText(phone_num);
		}
		
		if(recharge_money!=null&&recharge_money.length()>0){
			tv_recharge_money2_value.setText(recharge_money);
		}
		
		Log.e("phone_num", ""+phone_num);
		Log.e("recharge_money", ""+recharge_money);
		
		
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.left_linear:{
			finish();
		}break;
		case R.id.btn_cancle:
//			createUpdateOrder(getApplicationContext(),
//					recharge_money,
//					recharge_money,
//					Constant.DEAL_STATE_0,
//					phone_num);
			//弹出输入框，输入支付密码
			
			createUpdateOrder(getApplicationContext(),
					recharge_money,
					recharge_money,
					Constant.DEAL_STATE_0,
					phone_num);
			
			for(Activity activity:HomeUtils.activityList){
				activity.finish();
			}
			
			break;
		case R.id.btn_sure:
			String bank_card_num = ev_bank_card_num2_value.getText().toString();
			String pwd = et_pay_pwd2.getText().toString();
			if(TextUtils.isEmpty(bank_card_num)){
				Toast.makeText(getApplicationContext(), "银行卡号不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if(TextUtils.isEmpty(pwd)){
				Toast.makeText(getApplicationContext(), "支付密码不能为空", Toast.LENGTH_LONG).show();
				return ;
			}
			
			createUpdateOrder(getApplicationContext(),
					recharge_money,
					recharge_money,
					Constant.DEAL_STATE_88,
					phone_num);
			break;
		default:
			break;
		}
	}
	
	
	
	/**
	 * 创建订单
	 */
	private void createUpdateOrder(final Context context,
			final String payAmount, 
			final String changeAmount,
			final String status,
			final String phoneNo) {
		showDialog();
		// 1 phoneNo 手机号码 C 11 手机号码
		// 2 loginPwd 登录密码 C 32 采用固MD5算法加密。
		// 3 para 参数 C 固定值：mobileLogon
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("banNo", "97"));
		Log.e("UpayApplication.ACCOUNT_ID:", UpayApplication.ACCOUNT_ID);
		params.add(new BasicNameValuePair("accountId", UpayApplication.ACCOUNT_ID));
		params.add(new BasicNameValuePair("payAmount", payAmount));
		params.add(new BasicNameValuePair("orderId", getOrderId()));
		params.add(new BasicNameValuePair("phoneNo", phoneNo));
		params.add(new BasicNameValuePair("changeAmount", changeAmount));
		params.add(new BasicNameValuePair("payDate", getPayDate()));
		params.add(new BasicNameValuePair("status", status));
		params.add(new BasicNameValuePair("para", "createOrUpdateQdTrans"));
		params.add(new BasicNameValuePair("signMsg", AccountPref.get(mContext).getSignMsg()));
		// 发送网络请求
		HttpUtil.HttpsPost(context, params, new OnLoadListener() {

			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
//				Toast.makeText(PayPhoneFeeConfirmActivity.this, "完成", Toast.LENGTH_LONG).show();
				for(Activity activity:HomeUtils.activityList){
					activity.finish();
				}
				
				GetBankCardInfo.this.finish();
				dismissDialog();
			}

			@Override
			public void data(byte[] data, int length, String url,
					int requestId, int actionId) {
				try {
					Log.e("loading auto login", "data:" + new String(data));
					JSONObject jsonObject = new JSONObject(new String(data));
					System.out.println();
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					String errmsg = jsonObject.optString(RetCode.ERROR_MSG);
					for(Activity activity:HomeUtils.activityList){
						activity.finish();
					}
					GetBankCardInfo.this.finish();
					dismissDialog();
					if (RetCode.OK.equals(retCode)) {
						if(status == Constant.DEAL_STATE_0){
							handler.obtainMessage(SHOW_TOAST,
									"订单未支付")
									.sendToTarget();
						}else if(status == Constant.DEAL_STATE_88){
							handler.obtainMessage(SHOW_TOAST,
									"充值成功")
									.sendToTarget();
						}
						
					} else {
						if(errmsg!=null&&errmsg.length()>0){
							handler.obtainMessage(SHOW_TOAST,
									errmsg)
									.sendToTarget();
						}else{
							handler.obtainMessage(SHOW_TOAST,
									"失败")
									.sendToTarget();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, true);
	}
	
	
	
	protected static final int SHOW_TOAST = 0;
	//转账成功
	protected static final int TRANSFORM_SUCC = 1;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST:
				if (null != msg.obj) {
					Toast.makeText(mContext, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case TRANSFORM_SUCC:{
				
				
			}break;
			default:
				break;
			}
			
		}

	};
	
	
	Context mContext;
	
	/**
	 * 
	 * @Title getOrderId 
	 * @Description 获取订单id
	 * @return
	 * String    返回类型 
	 * @throws
	 */
	public static final String getOrderId(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String m_date = format.format(new Date());
		
		Random random = new Random();
		int i_ran = random.nextInt(999);
		String str_ran = String.valueOf(i_ran);
		
		
		StringBuilder sb = new StringBuilder();
		if(m_date!=null&&m_date.length()>0){
			sb.append(m_date);
		}
		if(str_ran!=null&&str_ran.length()>0){
			sb.append(str_ran);
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @Title getPayDate 
	 * @Description 获取支付时间
	 * @return
	 * String    返回类型 
	 * @throws
	 */
	public static String getPayDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		return format.format(new Date());
	}
}
