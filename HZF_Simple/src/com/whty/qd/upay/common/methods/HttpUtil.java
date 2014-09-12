package com.whty.qd.upay.common.methods;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.whty.qd.pay.common.ApplicationConfig;
import com.whty.qd.pay.common.net.OnLoadListener;
import com.whty.qd.upay.R;
import com.whty.qd.upay.account.AccountPref;
import com.whty.qd.upay.account.LoginActivity;
import com.whty.qd.upay.common.Constant;
import com.whty.qd.upay.common.RetCode;
import com.whty.qd.upay.home.HomeUtils;

public class HttpUtil {
	
	private static Context context;
	public static String resultStr;
	
//	public static void post(final Context ctx,
//			final List<NameValuePair> params, final OnLoadListener listener) {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					String result = null;
//					// 新建HttpPost对象
//					HttpPost httpPost = new HttpPost(Constant.URL);
//					// 设置字符集
//					Log.e("HttpUtil", "params:" + params.toString());
//					HttpEntity entity = new UrlEncodedFormEntity(params,
//							HTTP.UTF_8);
//					// 设置参数实体
//					httpPost.setEntity(entity);
//					// 获取HttpClient对象
//					DefaultHttpClient httpClient = new DefaultHttpClient();
//					// 连接超时
//					httpClient.getParams().setParameter(
//							CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
//					// 请求超时
//					httpClient.getParams().setParameter(
//							CoreConnectionPNames.SO_TIMEOUT, 30000);
//					// 获取HttpResponse实例
//					HttpResponse httpResp = httpClient.execute(httpPost);
//					// 判断是够请求成功
//					if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//						// 获取返回的数据
//						result = EntityUtils.toString(httpResp.getEntity(),
//								"UTF-8");
//						listener.data(result.getBytes(), 0, null, 0, 0);
//
//						return;
//					} else {
//						throw new Exception("errorcode："
//								+ httpResp.getStatusLine().getStatusCode());
//						
//					}
//				} catch (ConnectTimeoutException e) {
//					listener.error(0, ctx.getString(R.string.link_time_out),
//							null, 0, 0);
//					e.printStackTrace();
//					return;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				listener.error(0, "HttpPost方式请求失败", null, 0, 0);
//			}
//		}).start();
//
//	}
	
//	public static void HttpsPost(final Context ctx,
//			final List<NameValuePair> _params, final OnLoadListener listener) {
////		checkSessions();
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					Map<String, String> params = new HashMap<String, String>();
//					for (NameValuePair nameValuePair : _params) {
//						params.put(nameValuePair.getName(), nameValuePair.getValue());
//					}
//					Log.e("HttpUtil", "params:" + params.toString());
//					InputStream is = MyHttpClient.sendPOSTRequestForInputStream(Constant.URL, params, "UTF-8");
//					if(is!=null){
//						listener.data(readStream(is), 0, null, 0, 0);
//						return;
//					}else{
//						listener.error(0, "网络请求失败", null, 0, 0);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally{
//					checkSession();
//				}
//				listener.error(0, "网络请求失败", null, 0, 0);
//			}
//		}).start();
//
//	}
	
	public static void HttpsPost(final Context ctx,
			final List<NameValuePair> params, final OnLoadListener listener, boolean isLogin) {
		context = ctx;
		if(!isLogin){
			checkSessions(ctx, params, listener);
		}else{
			new Thread(new Runnable() {

				@Override
				public void run() {
					int result = myPosts(ctx, params);
					if(result == 1){
						listener.data(resultStr.getBytes(), 0, null, 0, 0);
					} else if(result == 0){
						listener.error(0, "网络请求失败", null, 0, 0);
					} else {
						listener.error(0, ctx.getString(R.string.link_time_out),null, 0, 0);
					}
				}
			}).start();
		}
	}
	
	public static int myPosts(final Context ctx,final List<NameValuePair> params){
		try {
			Map<String, String> mparams = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			for (NameValuePair nameValuePair : params) {
				mparams.put(nameValuePair.getName(), nameValuePair.getValue());
				sb.append(nameValuePair.getName());
				sb.append("=");
				sb.append(nameValuePair.getValue());
				sb.append("&");
			}
			String mParams = sb.deleteCharAt(sb.toString().length()-1).toString();
			Log.e("HttpUtil", Constant.URL + "?" + mParams);
			InputStream is = MyHttpClient.sendPOSTRequestForInputStream(Constant.URL, mparams, "UTF-8");
			if(is!=null){
				resultStr = new String(readStream(is));
				return 1;
			}else{
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return 0;
	}
	
	/** 
     * @功能 读取流 
     * @param inStream 
     * @return 字节数组 
     * @throws Exception 
     */  
    public static byte[] readStream(InputStream inStream) throws Exception {  
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        while ((len = inStream.read(buffer)) != -1) {  
            outSteam.write(buffer, 0, len);  
        }  
        outSteam.close();  
        inStream.close();  
        return outSteam.toByteArray();  
    }  
    
    /**
   	 * 接收消息 更新UI
   	 */
   	static Handler handler = new Handler() {
   		@Override
   		public void handleMessage(Message msg) {
   			super.handleMessage(msg);
   			switch (msg.what) {
   			case 1:
   				Toast.makeText(context, "请重新登录", Toast.LENGTH_SHORT).show();
   				context.startActivity(new Intent(context, LoginActivity.class));
   				break;
   			case 2:
   				if(context!=null){
   					Toast.makeText(context, "已注销", Toast.LENGTH_SHORT).show();
   				}
   				break;
   			case 3:
   				Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
   				break;
   			default:
   				break;
   			}
   		}
   	};
   	
   	
   	public static void checkSessions2(final Context ctx,
			final List<NameValuePair> sparams, final OnLoadListener listener){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					context = ctx;
					List<NameValuePair> _params = new ArrayList<NameValuePair>();
					_params.add(new BasicNameValuePair("phoneNo",
							""));
					_params.add(new BasicNameValuePair("para", "checkSession"));

					StringBuilder sb = new StringBuilder();
					
					Map<String, String> params = new HashMap<String, String>();
					for (NameValuePair nameValuePair : _params) {
						params.put(nameValuePair.getName(),
								nameValuePair.getValue());
						sb.append(nameValuePair.getName());
						sb.append("=");
						sb.append(nameValuePair.getValue());
						sb.append("&");
					}
					
					String myParams = sb.deleteCharAt(sb.toString().length()-1).toString();
					Log.e("HttpUtil", Constant.URL+"?"+myParams);
					InputStream is = MyHttpClient.sendPOSTRequestForInputStream(Constant.URL, params, "UTF-8");
					if(is!=null){
						String result = new String(readStream(is));
						try {
							JSONObject jsonObject = new JSONObject(result);
							String retCode = jsonObject.optString(RetCode.RET_CODE);
							
							if (RetCode.OK.equals(retCode)) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										int result = myPosts(ctx, sparams);
										if(result == 1){
											listener.data(resultStr.getBytes(), 0, null, 0, 0);
										} else if(result == 0){
											listener.error(0, "网络请求失败", null, 0, 0);
										} else {
											listener.error(0, ctx.getString(R.string.link_time_out),null, 0, 0);
										}
									}
								}).start();
							} else {
								AccountPref pre = AccountPref.get(ctx);
								ApplicationConfig.isLogon = false;
//								ApplicationConfig.accountInfo = null;
								pre.setAutoLogin(false);
								HomeUtils.isNeedRefreshRec = true;
								String msg = jsonObject.optString(RetCode.ERROR_MSG);
								handler.sendEmptyMessage(1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return;
					}else{
						listener.error(0, "网络请求失败", null, 0, 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.e("checkSession", "checkSession error");
			}
		}).start();
	}
   	
   	public static void checkSessions(final Context ctx,
			final List<NameValuePair> sparams, final OnLoadListener listener){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					context = ctx;
					List<NameValuePair> _params = new ArrayList<NameValuePair>();
					_params.add(new BasicNameValuePair("phoneNo",
							ApplicationConfig.accountInfo.getPhone()));
					_params.add(new BasicNameValuePair("para", "checkSession"));

					
					StringBuilder sb = new StringBuilder();
					Map<String, String> params = new HashMap<String, String>();
					for (NameValuePair nameValuePair : _params) {
						params.put(nameValuePair.getName(),
								nameValuePair.getValue());
						sb.append(nameValuePair.getName());
						sb.append("=");
						sb.append(nameValuePair.getValue());
						sb.append("&");
					}
					
					String mParams = sb.deleteCharAt(sb.toString().length()-1).toString();
					Log.e("HttpUtil", Constant.URL + "?" + mParams);
					
					InputStream is = MyHttpClient.sendPOSTRequestForInputStream(Constant.URL, params, "UTF-8");
					if(is!=null){
						String result = new String(readStream(is));
						try {
							JSONObject jsonObject = new JSONObject(result);
							String retCode = jsonObject.optString(RetCode.RET_CODE);
							
							if (RetCode.OK.equals(retCode)) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										int result = myPosts(ctx, sparams);
										if(result == 1){
											listener.data(resultStr.getBytes(), 0, null, 0, 0);
										} else if(result == 0){
											listener.error(0, "网络请求失败", null, 0, 0);
										} else {
											listener.error(0, ctx.getString(R.string.link_time_out),null, 0, 0);
										}
									}
								}).start();
							} else {
								AccountPref pre = AccountPref.get(ctx);
								ApplicationConfig.isLogon = false;
//								ApplicationConfig.accountInfo = null;
								pre.setAutoLogin(false);
								HomeUtils.isNeedRefreshRec = true;
								String msg = jsonObject.optString(RetCode.ERROR_MSG);
								handler.sendEmptyMessage(1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return;
					}else{
						listener.error(0, "网络请求失败", null, 0, 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.e("checkSession", "checkSession error");
			}
		}).start();
	}
   	
   	public static void closeSession(final int flag){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String result = null;
					String phoneNo = "";
					if(ApplicationConfig.accountInfo != null){
						if(!TextUtils.isEmpty(ApplicationConfig.accountInfo.getPhone())){
							phoneNo = ApplicationConfig.accountInfo.getPhone();
						}else{
							phoneNo = "";
						}
					}else{
						phoneNo = "";
					}
					List<NameValuePair> _params = new ArrayList<NameValuePair>();
					_params.add(new BasicNameValuePair("phoneNo", phoneNo));
					_params.add(new BasicNameValuePair("para", "shutdown"));
					
					StringBuilder sb = new StringBuilder();
					
					Map<String, String> params = new HashMap<String, String>();
					for (NameValuePair nameValuePair : _params) {
						params.put(nameValuePair.getName(),
								nameValuePair.getValue());
						sb.append(nameValuePair.getName());
						sb.append("=");
						sb.append(nameValuePair.getValue());
						sb.append("&");
					}
					
					String myParams = sb.deleteCharAt(sb.toString().length()-1).toString();
					Log.e("HttpUtil", Constant.URL+"?"+myParams);
					
					InputStream is = MyHttpClient.sendPOSTRequestForInputStream(Constant.URL, params, "UTF-8");
					if(is!=null){
						result = new String(readStream(is));
						Log.e("checkSession", "result:"+result);
						if(flag == 0){
							android.os.Process.killProcess(android.os.Process.myPid());
						}else if(flag == 1){
							handler.sendEmptyMessage(2);
						}
					}else{
						handler.sendEmptyMessage(3);
					}
				} catch (ConnectTimeoutException e) {
					Log.e("checkSession", "closeSession Timeout");
					e.printStackTrace();
					return;
				} catch (Exception e) {
					Log.e("checkSession", "closeSession error");
					e.printStackTrace();
				}
			}
		}).start();
    }
   	
   	
   	public static void closeSession2(final int flag,final String phone_num){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String result = null;
					String phoneNo = "";
					if(ApplicationConfig.accountInfo != null){
						if(!TextUtils.isEmpty(ApplicationConfig.accountInfo.getPhone())){
							phoneNo = ApplicationConfig.accountInfo.getPhone();
						}else{
							phoneNo = "";
						}
					}else{
						phoneNo = "";
					}
					List<NameValuePair> _params = new ArrayList<NameValuePair>();
					_params.add(new BasicNameValuePair("phoneNo", phone_num));
					_params.add(new BasicNameValuePair("para", "shutdown"));
					
					StringBuilder sb = new StringBuilder();
					
					Map<String, String> params = new HashMap<String, String>();
					for (NameValuePair nameValuePair : _params) {
						params.put(nameValuePair.getName(),
								nameValuePair.getValue());
						sb.append(nameValuePair.getName());
						sb.append("=");
						sb.append(nameValuePair.getValue());
						sb.append("&");
					}
					
					String myParams = sb.deleteCharAt(sb.toString().length()-1).toString();
					Log.e("HttpUtil", Constant.URL+"?"+myParams);
					
					InputStream is = MyHttpClient.sendPOSTRequestForInputStream(Constant.URL, params, "UTF-8");
					if(is!=null){
						result = new String(readStream(is));
						Log.e("checkSession", "result:"+result);
						if(flag == 0){
							android.os.Process.killProcess(android.os.Process.myPid());
						}else if(flag == 1){
							handler.sendEmptyMessage(2);
						}
					}else{
						handler.sendEmptyMessage(3);
					}
				} catch (ConnectTimeoutException e) {
					Log.e("checkSession", "closeSession Timeout");
					e.printStackTrace();
					return;
				} catch (Exception e) {
					Log.e("checkSession", "closeSession error");
					e.printStackTrace();
				}
			}
		}).start();
    }
	
	
//	public static int myPost(final Context ctx,final List<NameValuePair> params){
//		try {
//			resultStr = null;
//			// 新建HttpPost对象
//			HttpPost httpPost = new HttpPost(Constant.URL);
//			// 设置字符集
//			Log.e("HttpUtil", "params:" + params.toString());
//			HttpEntity entity = new UrlEncodedFormEntity(params,
//					HTTP.UTF_8);
//			// 设置参数实体
//			httpPost.setEntity(entity);
//			// 获取HttpClient对象
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			// 连接超时
//			httpClient.getParams().setParameter(
//					CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
//			// 请求超时
//			httpClient.getParams().setParameter(
//					CoreConnectionPNames.SO_TIMEOUT, 30000);
//			// 获取HttpResponse实例
//			HttpResponse httpResp = httpClient.execute(httpPost);
//			// 判断是够请求成功
//			if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				// 获取返回的数据
//				resultStr = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
//				return 1;
//			} else {
//				throw new Exception("errorcode："
//						+ httpResp.getStatusLine().getStatusCode());
//			}
//		} catch (ConnectTimeoutException e) {
//			e.printStackTrace();
//			return 2;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
	
//    public static void checkSession(final Context ctx,
//			final List<NameValuePair> _params, final OnLoadListener listener){
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					context = ctx;
//					String result = null;
////					String url = "http://192.168.8.156:8085/qdpay/mobileTrans.do";
////					HttpPost httpPost = new HttpPost(url);
//					HttpPost httpPost = new HttpPost(Constant.URL);
//					List<NameValuePair> params = new ArrayList<NameValuePair>();
//					params.add(new BasicNameValuePair("phoneNo", ApplicationConfig.accountInfo.getPhone()));
//					params.add(new BasicNameValuePair("para", "checkSession"));
//					Log.e("HttpUtil", "params:" + params.toString());
//					HttpEntity entity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
//					httpPost.setEntity(entity);
//					DefaultHttpClient httpClient = new DefaultHttpClient();
//					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
//					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
//					HttpResponse httpResp = httpClient.execute(httpPost);
//					if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//						result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
//						Log.e("checkSession", "result:"+result);
//						try {
//							JSONObject jsonObject = new JSONObject(result);
//							String retCode = jsonObject.optString(RetCode.RET_CODE);
//							
//							if (RetCode.OK.equals(retCode)) {
//								new Thread(new Runnable() {
//
//									@Override
//									public void run() {
//										int result = myPost(ctx, _params);
//										if(result == 1){
//											listener.data(resultStr.getBytes(), 0, null, 0, 0);
//										} else if(result == 0){
//											listener.error(0, "HttpPost方式请求失败", null, 0, 0);
//										} else {
//											listener.error(0, ctx.getString(R.string.link_time_out),null, 0, 0);
//										}
//									}
//								}).start();
//							} else {
//								AccountPref pre = AccountPref.get(ctx);
//								ApplicationConfig.isLogon = false;
//								ApplicationConfig.accountInfo = null;
//								pre.setAutoLogin(false);
//								HomeUtils.isNeedRefreshRec = true;
//								String msg = jsonObject.optString(RetCode.ERROR_MSG);
//								handler.sendEmptyMessage(1);
////								Toast.makeText(mContext, msg+"请重新登录", Toast.LENGTH_SHORT).show();
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						return;
//					} else {
//						throw new Exception("errorcode："+ httpResp.getStatusLine().getStatusCode());
//					}
//				} catch (ConnectTimeoutException e) {
//					Log.e("checkSession", "checkSession Timeout");
//					e.printStackTrace();
//					return;
//				} catch (Exception e) {
//					Log.e("checkSession", "checkSession error");
//					e.printStackTrace();
//				}
//			}
//		}).start();
//    }
    
    
    
//    public static void closeSession(final int flag){
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					String result = null;
//					HttpPost httpPost = new HttpPost(Constant.URL);
//					List<NameValuePair> params = new ArrayList<NameValuePair>();
//					String phoneNo = "";
//					if(ApplicationConfig.accountInfo != null){
//						if(!TextUtils.isEmpty(ApplicationConfig.accountInfo.getPhone())){
//							phoneNo = ApplicationConfig.accountInfo.getPhone();
//						}else{
//							phoneNo = "";
//						}
//					}else{
//						phoneNo = "";
//					}
//					
//					params.add(new BasicNameValuePair("phoneNo", phoneNo));
//					params.add(new BasicNameValuePair("para", "shutdown"));
//					Log.e("HttpUtil", "params:" + params.toString());
//					HttpEntity entity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
//					httpPost.setEntity(entity);
//					DefaultHttpClient httpClient = new DefaultHttpClient();
//					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
//					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
//					HttpResponse httpResp = httpClient.execute(httpPost);
//					if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//						result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
//						Log.e("checkSession", "result:"+result);
//						if(flag == 0){
//							android.os.Process.killProcess(android.os.Process.myPid());
//						}else if(flag == 1){
//							
//						}
//						
//						return;
//					} else {
//						throw new Exception("errorcode："+ httpResp.getStatusLine().getStatusCode());
//					}
//				} catch (ConnectTimeoutException e) {
//					Log.e("checkSession", "closeSession Timeout");
//					e.printStackTrace();
//					return;
//				} catch (Exception e) {
//					Log.e("checkSession", "closeSession error");
//					e.printStackTrace();
//				}
//			}
//		}).start();
//    }
}
