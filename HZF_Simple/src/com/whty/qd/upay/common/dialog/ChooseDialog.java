package com.whty.qd.upay.common.dialog;

import com.whty.qd.upay.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseDialog {
	private Context mContext;
	private Dialog dialog;
	private LayoutInflater inflater;
	private View view;
	private ListView mListView;
	private MyAdapter adapter;
	
	//标题容器
	private LinearLayout ll_title;
	
	/**
	 * 标题文字
	 */
	private TextView dialog_title_text;

	public ChooseDialog(Context context) {
		mContext = context;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_choose_layout, null);
		ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
		mListView = (ListView) view.findViewById(R.id.listView);
		dialog_title_text = (TextView) view.findViewById(R.id.dialog_title_text);
		
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
	}
	
	private MyAdapter2 mAdapter2;
	
	public void showPayWayDialog(OnItemClickListener listener){
		mAdapter2 = new MyAdapter2(mContext);
//		dialog_title_text.setText("选择支付方式?");
//		ll_title.setVisibility(View.VISIBLE);
		mListView.setAdapter(mAdapter2);
		mListView.setOnItemClickListener(listener);
		dialog.show();
	}
	
	
	public void showDialog(OnItemClickListener listener) {
		adapter = new MyAdapter(mContext);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(listener);
		dialog.show();
	}

	public void dismissDialog() {
		dialog.dismiss();
	}

	public boolean isShow() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}

	public Dialog getDialog() {
		return dialog;
	}
   
	public void setCancelable(boolean is){
		dialog.setCancelable(is);
	}
	
	public Window getWindow(){
		return dialog.getWindow();
	}
	
	
	class MyAdapter2 extends BaseAdapter {
		public Context mContext;
		
		public MyAdapter2(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.spiner_item_layout, null);
				viewHolder.titleTv = (TextView) convertView.findViewById(R.id.textView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				viewHolder.titleTv.setText("余额支付");
			} else if (position == 1) {
				viewHolder.titleTv.setText("银行卡支付");
			}
			
			return convertView;
		}
		
		public final class ViewHolder {
			public TextView titleTv;
		}
	}
	
	
	
	class MyAdapter extends BaseAdapter {
		public Context mContext;
		
		public MyAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.spiner_item_layout, null);
				viewHolder.titleTv = (TextView) convertView.findViewById(R.id.textView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				viewHolder.titleTv.setText("当月交易");
			} else if (position == 1) {
				viewHolder.titleTv.setText("近一周交易");
			} else if (position == 2) {
				viewHolder.titleTv.setText("近一个月交易");
			} 
			return convertView;
		}
		
		public final class ViewHolder {
			public TextView titleTv;
		}
	}
}
