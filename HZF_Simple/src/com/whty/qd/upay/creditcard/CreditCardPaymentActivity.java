package com.whty.qd.upay.creditcard;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.Log;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.common.dialog.CommonProgressDialog;
import com.whty.qd.upay.common.methods.HttpUtil;


/**
 * 信用卡还款
 * @author shawn
 *
 */
public class CreditCardPaymentActivity extends BaseActivity implements OnClickListener{
	
	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;
	
	/**
	 * EditText 账号
	 */
	private EditText et_account_num_2;
	
	/**
	 * EditText 转账金额
	 */
	private EditText et_transport_money_2;
	
	
	/**
	 * EditText 备注
	 */
	private EditText et_mark_2;
	
	/**
	 * Button 确定转账
	 */
	private Button btn_sure_transport;
	
	private ArrayAdapter<CharSequence> spAdapterIsuuBank = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creditcardpayment);
		//初始化控件
		initView();
	}
	
	//初始化控件
	private void initView(){
		mContext = CreditCardPaymentActivity.this;
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
//		lblTitle.setText(R.string.credit_card_payment_txt);
		lblTitle.setText("转账");
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		
		et_account_num_2 = (EditText) findViewById(R.id.et_account_num_2);
		et_transport_money_2 = (EditText) findViewById(R.id.et_transport_money_2);
		
		et_mark_2 = (EditText) findViewById(R.id.et_mark_2);
		
		btn_sure_transport = (Button) findViewById(R.id.btn_sure_transport);
		btn_sure_transport.setOnClickListener(this);
		
		
		btn_sure_transport.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btn_sure_transport:
//			Toast.makeText(CreditCardPaymentActivity.this, "转账成功", Toast.LENGTH_LONG).show();
//			showDialog("转账成功");
		
			final String trans_acco = et_account_num_2.getText().toString();
			final String trans_mone = et_transport_money_2.getText().toString();
			
			if(TextUtils.isEmpty(trans_acco)){
				showShortMsg("收款账号不能为空");
				return ;
			}
			
			if(TextUtils.isEmpty(trans_mone)){
				showShortMsg("转账金额不能为空");
				return ;
			}
			
			String message = "确定给"+trans_acco+"转账"+trans_mone+"元?";
			
			showCommonDialog(CreditCardPaymentActivity.this, 
					message, 
					new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							commonDialog.dismissDialog();
							transForm(trans_acco,trans_mone);
						}
					}, 
					new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							commonDialog.dismissDialog();
						}
					});
			
			break;
		case R.id.left_linear:{
			finish();
		}break;
		default:
			break;
		}
		
	}
	private Context mContext;
	private CommonProgressDialog dialog;
	protected static final int SHOW_TOAST = 0;
	
	private static final String TAG = "CreditCardPaymentActivity";
	
	private void transForm(String transAccount,String transMoney){
		dialog = new CommonProgressDialog(mContext);
		dialog.showDialog("正在处理请稍后...");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("para", "transfer"));
		params.add(new BasicNameValuePair("phoneNo", ApplicationConfig.accountInfo.getPhone()));
		params.add(new BasicNameValuePair("receivePhone", transAccount));
		params.add(new BasicNameValuePair("payAmount", transMoney));


		
		HttpUtil.HttpsPost(mContext, params, new OnLoadListener() {
			
			@Override
			public void error(int id, String description, String failingUrl,
					int requestId, int actionId) {
				dialog.dialogDismiss();
				handler.obtainMessage(SHOW_TOAST, description).sendToTarget();
			}
			
			@Override
			public void data(byte[] data, int length, String url, int requestId,
					int actionId) {
				Log.e(TAG, "data:" + new String(data));
				dialog.dialogDismiss();
				try {
					JSONObject jsonObject = new JSONObject(new String(data));
					String retCode = jsonObject.optString(RetCode.RET_CODE);
					String errmsg = jsonObject.optString(RetCode.ERROR_MSG);
					Log.e("retCode", ""+retCode);
					if (RetCode.OK.equals(retCode)) {
						handler.obtainMessage(SHOW_TOAST,
								"转账成功")
								.sendToTarget();
						
						CreditCardPaymentActivity.this.finish();
					}else{
						if(errmsg!=null&&errmsg.length()>0){
							handler.obtainMessage(SHOW_TOAST,
									errmsg)
									.sendToTarget();
						}else{
							handler.obtainMessage(SHOW_TOAST,
									"转账失败")
									.sendToTarget();
						}
						
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		}, true);
	}
	
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
			default:
				break;
			}
			
		}

	};
}
