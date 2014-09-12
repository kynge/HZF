package com.whty.qd.upay.common.methods;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.util.Log;

public class MyHttpClient {
	private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
	private static X509TrustManager xtm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	private static X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
	private static HttpURLConnection conn = null;

	public static InputStream sendPOSTRequestForInputStream(String path,
			Map<String, String> params, String encoding) throws Exception {
		// 1> 组拼实体数据
		// method=save&name=liming&timelength=100
		StringBuilder entityBuilder = new StringBuilder("");
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				entityBuilder.append(entry.getKey()).append('=');
				entityBuilder.append(URLEncoder.encode(entry.getValue(),
						encoding));
				entityBuilder.append('&');
			}
			entityBuilder.deleteCharAt(entityBuilder.length() - 1);
		}
		byte[] entity = entityBuilder.toString().getBytes();
		URL url = new URL(path);
		
//		conn = (HttpsURLConnection) url.openConnection();
		conn = (HttpURLConnection)url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			// Trust all certificates
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(new KeyManager[0], xtmArray, new SecureRandom());
			SSLSocketFactory socketFactory = context.getSocketFactory();
			((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);
			((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);
		}
		conn.setConnectTimeout(50 * 1000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);// 允许输出数据
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);
		outStream.flush();
		outStream.close();
		Log.e("MyHttpClient", "conn_ResponseCode() ===" +conn.getResponseCode());
		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return conn.getInputStream();
	}

	public static void closeConnection() {
		if (conn != null)
			conn.disconnect();
	}
}
