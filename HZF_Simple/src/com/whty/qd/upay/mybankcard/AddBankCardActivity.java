package com.whty.qd.upay.mybankcard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.YTFileHelper;

/**
 * 添加银行卡信息界面
 * 
 * @author Administrator
 * 
 */
public class AddBankCardActivity extends BaseActivity implements
		OnClickListener {

	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;

	/**
	 * EditText 持卡人
	 */
	private EditText et_bankcard_holder;
	
	/**
	 * EditText 卡号
	 */
	private EditText et_bankcard_num;
	
	
	/**
	 * EditText 所属银行
	 */
	private EditText et_belongbank;
	
	/**
	 * Button 确定
	 */
	private Button btn_sure_add;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addbankcard);
		initView();
	}

	private void initView() {
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
		// lblTitle.setText(R.string.credit_card_payment_txt);
		lblTitle.setText("添加银行卡");
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		
		
		et_bankcard_holder = (EditText) findViewById(R.id.et_bankcard_holder);
		et_bankcard_num = (EditText) findViewById(R.id.et_bankcard_num);
		et_belongbank = (EditText) findViewById(R.id.et_belongbank);
		
		btn_sure_add = (Button) findViewById(R.id.btn_sure_add);
		
		btn_sure_add.setOnClickListener(this);
		
	}
	
	

	private boolean isDataNull(String holder,String bankCardNum,String belongBank){
		boolean result = true;
		if(TextUtils.isEmpty(holder)){
			showShortMsg(R.string.bankcard_holder_can_not_null);
			
		}else{
			if(TextUtils.isEmpty(bankCardNum)){
				showShortMsg(R.string.bankcard_num_can_not_null);
			}else{
				if(TextUtils.isEmpty(belongBank)){
					showShortMsg(R.string.belong_bank_can_not_null);
				}else{
					result = false;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.left_linear: {
			finish();
		}break;
		case R.id.btn_sure_add:{
			//判断数据是否为空
			String holder = et_bankcard_holder.getText().toString();
			String bankCardNum = et_bankcard_num.getText().toString();
			String belongBank = et_belongbank.getText().toString();
			
			
			//判断数据是否为空
			if(isDataNull(holder,bankCardNum,belongBank)){
				return;
			}
			
//			//判断银行卡号是否正确
//			if(!checkBankCard(bankCardNum)){
//				showShortMsg(R.string.input_correct_bancard_num);
//				return ;
//			}
			
			//判断银行卡号是否重复
			if(bankCardNumRepeat(bankCardNum)){
				showShortMsg(R.string.input_unrepeat_bankcard_num);
				return;
			}
			
			
			//确认添加银行卡
			Intent intent  = new Intent();
			BankCardNode node = new BankCardNode();
			node.setHolder(holder);
			node.setBankCardNum(bankCardNum);
			node.setBelongBank(belongBank);
			
			intent.putExtra(MyBankCardActivity.BANK_CARD_DATA, node);
			setResult(Activity.RESULT_OK, intent);
			finish();
			
		}break;
			
		default:
			break;
		}

	}
	
	private boolean bankCardNumRepeat(String bankCardNum){
		boolean result = false;
		List<BankCardNode> nodes = (List<BankCardNode>) YTFileHelper.getInstance(AddBankCardActivity.this).deSerialObject(MyBankCardActivity.DATA_KEY);
		if(nodes!=null&&nodes.size()>0){
			for(BankCardNode node:nodes){
				if(node.getBankCardNum().equals(bankCardNum)){
					result = true;
				}
			}
		}
		
		return result;
	}
	
	
	 /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
    	if(cardId.length()!=16){
    		return false;
    	}
    	
         char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
         if(bit == 'N'){
             return false;
         }
         return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId){
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;           
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }

}
