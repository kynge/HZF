package com.whty.qd.upay.mybankcard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whty.qd.upay.BaseActivity;
import com.whty.qd.upay.R;
import com.whty.qd.upay.common.YTFileHelper;

public class MyBankCardActivity extends BaseActivity implements OnClickListener{
	
	
	public static final String BANK_CARD_DATA = "BANK_CARD_DATA";
	
	/** @Fields titleLayout : 标题栏 */
	private RelativeLayout titleLayout;
	/** @Fields lblTitle : 标题 */
	private TextView lblTitle;
	/** @Fields left_linear : 标题栏左侧容器 */
	private LinearLayout left_linear;
	
	private Button button_title_ok;
	
	
	private ListView lv_bankcard;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_bankcard);
		initView();
	}
	
	@SuppressWarnings("deprecation")
	private void initView(){
		
		// 设置标题文字
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		lblTitle = (TextView) titleLayout.findViewById(R.id.text_main_title);
//		lblTitle.setText(R.string.credit_card_payment_txt);
		lblTitle.setText("我的银行卡");
		// 设置左侧监听，点击关闭界面
		left_linear = (LinearLayout) titleLayout.findViewById(R.id.left_linear);
		left_linear.setOnClickListener(this);
		button_title_ok = (Button) findViewById(R.id.button_title_ok);
		button_title_ok.setBackgroundDrawable(getResources().getDrawable(R.drawable.time_add_btn_selector));
		button_title_ok.setVisibility(View.VISIBLE);
//		button_title_ok.setText("添加");
		button_title_ok.setOnClickListener(this);
		
		lv_bankcard = (ListView) findViewById(R.id.lv_bankcard);
		
		myAdapter = new MyAdapter(MyBankCardActivity.this);
		lv_bankcard.setAdapter(myAdapter);
		
		
		/**
		 * 判断是否有保存的银行卡数据
		 */
		mBankCardNodes  = (List<BankCardNode>) YTFileHelper.getInstance(MyBankCardActivity.this).deSerialObject(DATA_KEY);
		if(mBankCardNodes!=null&&mBankCardNodes.size()>0){
			myAdapter.clear();
			myAdapter.addNodes(mBankCardNodes);
			myAdapter.notifyDataSetChanged();
		}	
		
		lv_bankcard.setOnItemLongClickListener(new MyBankItemLongClickListener());
	}
	
	
	/**
	 * 
	 *银行卡长按监听器事件
	 */
	private class MyBankItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			final BankCardNode node = (BankCardNode) myAdapter.getItem(position);
			int len = node.getBankCardNum().length();
			String last_num = node.getBankCardNum().substring(len-4, len);
			showCommonDialog(MyBankCardActivity.this,"是否删除尾号为"+last_num +"的"+node.getBelongBank()+"卡" ,
					"删除", 
					new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							commonDialog.dismissDialog();
							myAdapter.removeNode(node);
							mBankCardNodes.remove(node);
							YTFileHelper.getInstance(MyBankCardActivity.this).serialObject(DATA_KEY, mBankCardNodes);
							myAdapter.notifyDataSetChanged();
							
						}
					},
					"取消", 
					new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							commonDialog.dismissDialog();
						}
					});
			return true;
		}
		
	}
	
	
	private MyAdapter myAdapter ;
	
	private class MyAdapter extends BaseAdapter{
		
		private List<BankCardNode> mNodes;
		private Context mContext ;
		public MyAdapter(Context context){
			mNodes = new ArrayList<BankCardNode>();
			mContext = context;
		}
		
		public void removeNode(BankCardNode node){
			if(node!=null){
				mNodes.remove(node);
			}
		}
		
		public void clear(){
			mNodes.clear();
		}
		
		public void addNodes(List<BankCardNode> nodes){
			if(nodes!=null&&nodes.size()>0){
				mNodes.addAll(nodes);
			}
		}
		
		public void addNode(BankCardNode node){
			mNodes.add(node);
		}

		@Override
		public int getCount() {
			return mNodes.size();
		}

		@Override
		public Object getItem(int position) {
			return mNodes.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			BankCardNode node = mNodes.get(position);
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.bank_card_info_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_card_info = (TextView) convertView.findViewById(R.id.tv_card_info);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			if(node!=null){
				int len = node.getBankCardNum().length();
				String lastNum = node.getBankCardNum().substring(len-4, len);
				viewHolder.tv_card_info.setText("尾号为:"+lastNum+"的"+node.getBelongBank()+"卡");
			}
			
			return convertView;
		}
		
		class ViewHolder{
			TextView tv_card_info;
		}
		
	}
	
//	public static final String REQUEST_ADD_BANKCARD = "REQUEST_ADD_BANKCARD";

	public static final int REQUEST_ADD_BANKCARD = 111;
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.left_linear:{
			finish();
		}break;
		case R.id.button_title_ok:
			//进入添加银行卡信息界面
			Intent intent = new Intent(MyBankCardActivity.this,AddBankCardActivity.class);
			MyBankCardActivity.this.startActivityForResult(intent, REQUEST_ADD_BANKCARD);
			break;
		default:
			break;
		}
			
	}
	
	
	public static final String DATA_KEY = "DATA_KEY";
	
	
	private List<BankCardNode> mBankCardNodes;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("onActivityResult", "onActivityResult");
		Log.e("requestCode", ""+requestCode);
		Log.e("resultCode", ""+resultCode);
		
		if(requestCode == REQUEST_ADD_BANKCARD){
			if(resultCode == Activity.RESULT_OK){
				BankCardNode node = (BankCardNode) data.getExtras().get(BANK_CARD_DATA);
				Log.e("node==null", ""+(node==null));
				Log.e("mBankCardNodes== null", ""+(mBankCardNodes==null));
				if(node!=null){
					if(mBankCardNodes!=null&&mBankCardNodes.size()>0){
						mBankCardNodes.clear();
						mBankCardNodes.add(node);
					}else{
						mBankCardNodes = new ArrayList<BankCardNode>();
						mBankCardNodes.add(node);
					}
					
					YTFileHelper.getInstance(MyBankCardActivity.this).serialObject(DATA_KEY, mBankCardNodes);
					myAdapter.clear();
					myAdapter.addNodes(mBankCardNodes);
					myAdapter.notifyDataSetChanged();
				}
				
			}
		}
	}
}
