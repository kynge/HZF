package com.whty.qd.upay.phonefee;

import java.util.List;

import com.whty.qd.upay.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SaveInfoAdapter extends BaseAdapter{

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
              convertView = inflater.inflate(R.layout.save_account_list_item, null);
              TextView text_num=(TextView)convertView.findViewById(R.id.text_num);
//              TextView text_name=(TextView)convertView.findViewById(R.id.text_name);
              Button button_delete=(Button)convertView.findViewById(R.id.button_delete);
              
              text_num.setText(item.getNum());
//              text_name.setText(item.getName());
              button_delete.setFocusable(false);
              button_delete.setOnClickListener(new OnClickListener() {				
				public void onClick(View v) {
					
				}
			});
              
              
              
          }
		return convertView;
	}

}
