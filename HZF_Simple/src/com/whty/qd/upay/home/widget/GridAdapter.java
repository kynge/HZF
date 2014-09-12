package com.whty.qd.upay.home.widget;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.whty.qd.pay.common.InstallUtils;
import com.whty.qd.upay.R;
import com.whty.qd.upay.business.BusinessImage;
import com.whty.qd.upay.business.BusinessItem;

public class GridAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<BusinessItem> list;
	private Context mContext;
	private String DIR_PATH;

	/**
	 * @param context
	 */
	public GridAdapter(Context context, List<BusinessItem> list) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
		mContext = context;
		DIR_PATH = mContext.getFilesDir().getAbsolutePath() + "/"
				+ "upayImages";
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BusinessItem businessItem = (BusinessItem) getItem(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.home_grid_item, null);
		}
		convertView.setTag(businessItem.getDownUrl());
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView1);
		ImageView image_icon_offline = (ImageView) convertView
				.findViewById(R.id.image_icon_offline);
		if (businessItem.getVersion().equals("0")) {
			image_icon_offline.setImageResource(BusinessImage
					.getOffLineIcon(businessItem.getBusinessminicon()));
			image_icon_offline.setVisibility(View.GONE);
		} else {
			image_icon_offline.setVisibility(View.GONE);
		}
		TextView category = (TextView) convertView.findViewById(R.id.textView1);
		ImageView image_loading = (ImageView) convertView
				.findViewById(R.id.image_loading);
		ImageView image_icon_new = (ImageView) convertView
				.findViewById(R.id.image_icon_new);
		ProgressBar progressBar = (ProgressBar) convertView
				.findViewById(R.id.progressBar);

		image_icon_new.setImageResource(BusinessImage
				.getLittleNewIcon(businessItem.getBusinessminicon()));
//		if (!InstallUtils.isApkInstalled(mContext,
//				businessItem.getPackageName())) { // 没有安装
//			image_icon_new.setVisibility(View.VISIBLE);
//		} else {
//			if (!InstallUtils.isApkUpdatable(mContext,
//					businessItem.getPackageName(),
//					Integer.parseInt(businessItem.getVersion()))) {// 没有更新
//				image_icon_new.setVisibility(View.GONE);
//
//			} else {
//				image_icon_new.setVisibility(View.VISIBLE);
//			}
//		}
		if (!TextUtils.isEmpty(businessItem.getBusinessminicon())) {
			image_loading.setImageResource(BusinessImage
					.getLoadingIcon(businessItem.getBusinessminicon()));
			image_loading.setVisibility(View.GONE);

		} else {
			image_loading.setVisibility(View.GONE);
		}

		String iconName = businessItem.getIcon();
		if (businessItem.getBusinessId().equals("-1")) {
			image_loading.setVisibility(View.GONE);
			image_icon_new.setVisibility(View.GONE);
			image_icon_offline.setVisibility(View.GONE);
			int iconId = mContext.getResources().getIdentifier(iconName,
					"drawable", mContext.getPackageName());
			imageView.setImageResource(iconId);
		} else {
			int iconId = mContext.getResources().getIdentifier(iconName,
					"drawable", mContext.getPackageName());
			if (!iconName.startsWith("http")) {
				imageView.setImageResource(iconId);
			} else {
//				String fileName = "";
//				fileName =LoadMethod.getFileName(businessItem.getIcon());
//				File f = new File(DIR_PATH + "/" + fileName);
//
//				if (f.exists()) {
//					Bitmap bm = BitmapFactory.decodeFile(DIR_PATH + "/"
//							+ fileName, null);
//					imageView.setImageBitmap(bm);
//				} else {				
//					iconId = mContext.getResources().getIdentifier(
//							"online_" + businessItem.getBusinessId(), "drawable",
//							mContext.getPackageName());
//					imageView.setImageResource(iconId);
//
//				}
			}
		}
		category.setText(businessItem.getName());
		final TextPaint paint = category.getPaint();
		int width = (int) paint.measureText(businessItem.getName());
		FrameLayout.LayoutParams params = (LayoutParams) progressBar
				.getLayoutParams();
		params.width = width + 20;
		progressBar.setLayoutParams(params);
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
