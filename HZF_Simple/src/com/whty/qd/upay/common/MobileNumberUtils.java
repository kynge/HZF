package com.whty.qd.upay.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.whty.qd.upay.R;

public class MobileNumberUtils {

	/**
	 * 判断11位数字是否属于三家运营商的手机号段
	 * @return 
	 */
	public static boolean isMobileNumberSegment(Context context, final String number) {
		String[] array = context.getResources().getStringArray(R.array.phone_numbers);
		List<String> list = array2list(array);
		if(!list.contains(number.substring(0, 3))) { //取前三位
			return false;
		} else {
			return  true;
		}
	}

	/**
	 * 手机号码列表
	 * @param array
	 * @return
	 */
    private static List<String> array2list(String array[]) {
        int len = array.length;
        List<String> list = new ArrayList<String>();
        
        for(int index = len-1; index >= 0; index--) {
        	list.add(array[index]);
        }
        return list;
    }
}
