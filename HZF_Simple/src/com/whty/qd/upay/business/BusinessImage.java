package com.whty.qd.upay.business;

import android.text.TextUtils;

import com.whty.qd.pay.common.ResourceUtils;

/**
 * 动态获取业务小图标和更新loading图标
 * 
 * @author Cai
 */
public class BusinessImage {
	/**
	 * 获取带“新”字气泡小图标图片的id
	 * 
	 * @param iconIndex
	 *            小图标索引
	 */
	public static int getLittleNewIcon(String iconIndex) {
		if (TextUtils.isEmpty(iconIndex)) {
			return ResourceUtils.getResourceId(ResourceUtils.packageNameWithR,
					"drawable", "icon_new_00");
		} else {
			return ResourceUtils.getResourceId(ResourceUtils.packageNameWithR,
					"drawable", "icon_new_0" + iconIndex);

		}
	}
	
	/**
	 * 获取待上线图片的id
	 * 
	 * @param iconIndex
	 *            图标索引
	 */
	public static int getOffLineIcon(String iconIndex) {
		if (TextUtils.isEmpty(iconIndex)) {
			return ResourceUtils.getResourceId(ResourceUtils.packageNameWithR,
					"drawable", "icon_daishangxian_00");
		} else {
			return ResourceUtils.getResourceId(ResourceUtils.packageNameWithR,
					"drawable", "icon_daishangxian_0" + iconIndex);

		}
	}
	
	/**
	 * 获取loading图片的id
	 * 
	 * @param iconIndex
	 *           loading图标索引
	 */
	public static int getLoadingIcon(String iconIndex) {
		if (TextUtils.isEmpty(iconIndex)) {
			return ResourceUtils.getResourceId(ResourceUtils.packageNameWithR,
					"drawable", "icon_update_00");
		} else {
			return ResourceUtils.getResourceId(ResourceUtils.packageNameWithR,
					"drawable", "icon_update_0" + iconIndex);

		}
	}
	
}
