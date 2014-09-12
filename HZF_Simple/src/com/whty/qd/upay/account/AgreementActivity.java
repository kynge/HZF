package com.whty.qd.upay.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.whty.qd.upay.R;
import com.whty.qd.upay.common.Constant;
/**
 * 协议
 * 
 * @author 吴非
 *
 */
public class AgreementActivity extends Activity {
	private WebView webView;
	private Button button_agree;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reg_agreement);
		initView();
	}

	private void initView() {
		webView = (WebView) findViewById(R.id.webview);
		button_agree = (Button) findViewById(R.id.button_agree);
		webView.getSettings().setJavaScriptEnabled(true);
//		webView.loadUrl("http://sdp.qdone.net.cn:8081/reg/register_hzf.html");
//		webView.loadUrl("http://192.168.100.86:8085/kjtpay/register_hzf.htm");
		webView.loadUrl(Constant.XIEYI_URL);
		button_agree.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
