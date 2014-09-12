package com.whty.qd.upay.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;

public class Constant {
	
	/**
	 * 是否为测试，直接跳过银行支付
	 */
	public static final boolean IS_FACK = true;
	
	
	/**
	 * 是否需要模拟银行界面
	 */
	public static final boolean IS_PAY_BY_BANK = false;
	
	
	/**
	 * 订单状态  "0", "未支付"
	 */
	public static final String DEAL_STATE_0 = "0";
	/**
	 * 订单状态 "88", "交易完成"
	 */
	public static final String DEAL_STATE_88 = "88";
	
	
	/**
	 * 登录返回的 accountId
	 */
	public static String ACCOUNT_ID = "";
	
	/**
	 * 注销测试
	 */
	public static final boolean ZHU_XIAO_TEST = true;
	
	public static final String XIEYI_URL = "http://211.147.70.6:8080/demo/register_hzf.htm";

//	public static final String URL = "http://192.168.8.155:8989/qdpay/mobileTrans.do";
//	public static final String URL = "https://www.qdonepay.com/qdpay/mobileTrans.do";
	//擎动
//	public static final String URL = "https://ww2.qdonepay.com/qdpay/mobileTrans.do";
//	public static final String URL = "http://192.168.8.156:8085/qdpay/mobileTrans.do";
	//安捷睿达
//	public static final String URL = "http://192.168.1.51:8085/kjtpay/mobileTrans.do";
	//快捷通
//	public static final String URL = "http://192.168.100.86:8085/kjtpay/mobileTrans.do";
	
	//锦付通伍平本地调试地址
//	public static final String URL = "http://192.168.100.192:8085/jinfutong/mobileTrans.do";
	
	//快捷通外网地址
//	public static final String URL = "http://183.136.222.72:8080/kjtpay/mobileTrans.do";
	
	
	//锦付通伍平本地调试外网地址 http://192.168.4.98:8085/jinfutong/mobileTrans.do
//	public static final String URL = "http://192.168.4.98:8085/jinfutong/mobileTrans.do";
	
	//锦付通伍平本地调试外网地址 http://192.168.4.98:8085/jinfutong/mobileTrans.do
//	public static final String URL = "http://192.168.1.59:8085/jinfutong/mobileTrans.do";
	
	//锦付通外网地址
//	public static final String URL = "https://www.jinjiangpay.com/mobileTrans.do";
	//锦付通外网地址压力测试
//	public static final String URL = "http://test.jinjiangpay.com/mobileTrans.do";
	
	//安捷睿达测试地址
//	public static final String URL = "http://211.147.70.6:8080/demo/mobileTrans.do";
	//搜房网测试地址
	public static final String URL = "https://124.250.62.10/soufun/mobileTrans.do";
	
	//周大哥测试地址
//	public static final String URL = "http://192.168.42.152:8080/soufun/mobileTrans.do";
	
//	public static final String URL = "http://192.168.1.7:8080/qdpay/mobileTrans.do";
	
	/** @Fields ACTION_LOGIN_SUCCESS : 登录成功标志 */
	public static final String ACTION_LOGIN_SUCCESS = "com.whty.qd.upay.ACTION_LOGIN_SUCCESS";
	public static final int ACTION_ID = 0;
	public static final int TIME_OUT = 10 * 000;
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) {
		// String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * @Title: md5
	 * @Description: md5加密
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @Title: formatTwoDecimal
	 * @Description: 格式化为2为小数
	 * @param i
	 * @return
	 */
	public static String formatTwoDecimal(double i) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setMinimumFractionDigits(2);
		return nf.format(i);
	}

}
