package com.whty.qd.upay.reciever;


import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsObserver extends ContentObserver {
	
	private static final String TAG = "com.qunsuan.qspayhelper.ContentObserver";
	
	private Cursor cursor = null;
	private Context ctx = null;
	private String[] keywords;
	
	private static SmsObserver observer;

	public SmsObserver(Handler handler, String[] keys, Context context) {
		super(handler);
		keywords = keys;
		ctx = context;
	}
	
	public Context getContext() {
		return ctx;
	}
	
	public static SmsObserver createInstance(Handler handler, String[] keys, Context context) {  
        if(observer == null)  {
        	observer = new SmsObserver(handler, keys,context);  
        	register(observer);
        }
        return observer;  
    }
	
	public static void register(SmsObserver obs) {
		obs.getContext().getContentResolver().registerContentObserver(
//				Uri.parse("content://sms/"), 
				Uri.parse("content://sms/inbox"), 
				true, 
				obs);
		Log.i(TAG, "Observer registed.");
	}
	
	/** 
     * @Description 当短信表发送改变时，调用该方法 需要两种权限 android.permission.READ_SMS读取短信 
     *              android.permission.WRITE_SMS写短信 
     * @Author Snake 
     * @Date 2010-1-12 
     */  
    @Override  
    public void onChange(boolean selfChange) {  
        super.onChange(selfChange);  

       Log.i(TAG, "Detect SMS changes. Start processing ...");
        // 读取收件箱中指定号码的短信
        String queryBody = " (";
        String[] queryValues = new String[keywords.length];
        int i = 0;
        for(String str : keywords){
        	queryBody += "body like ? or ";
        	queryValues[i] = "%" + str + "%";
        	queryValues[i] = "0";
        	i++;
        }
        queryBody = (queryBody.subSequence(0, queryBody.length()-4)).toString();
        queryBody += ")";
//        queryValues[i] = "0";
        
       Log.v(TAG, "Query body: " + queryBody);
        for(int k=0;k<queryValues.length;k++){
        	Log.v(TAG, "queryValues:"+"["+k+"]"+queryValues[k]);
        }
//       Log.v("msg", ""+Uri.parse("content://sms/inbox"));
//       Log.v("msg", ""+Uri.parse("content://sms"));
        
        cursor = ctx.getContentResolver().query(
        		Uri.parse("content://sms/inbox"),  
                new String[] { "_id", "address", "read", "body" },  
//        		  null,
                queryBody + " and read=?", 
//                " read=?",
                queryValues, 
//                new String[] { "0" },
                "date desc"
//                  null
                  );
        
//       Log.i("msg", cursor.toString());
        
       Log.i("cursor != null", ""+(cursor != null));
       Log.i("cursor.getCount() > 0", ""+(cursor.getCount() > 0));
       Log.i("cursor != null && cursor.getCount() > 0", ""+(cursor != null && cursor.getCount() > 0));
        
        if (cursor != null && cursor.getCount() > 0) {  
        	Log.i(TAG, "Clearing marked sms ...");
//            ContentValues values = new ContentValues();  
//            values.put("read", "1"); // 修改短信为已读模式  
//            cursor.moveToFirst();
           Log.i(TAG, "Marked sms list cursor is last: "+cursor.isLast());
           Log.i(TAG, "Marked sms list count: "+cursor.getCount());
//            while (!cursor.isLast()) {  
            while (cursor.moveToNext()) {
               Log.i(TAG, "Clearing one marked sms ...");
               Log.i(TAG, "shield number: " + cursor.getString(1) + ", ID: " + cursor.getString(0));
                // 更新当前未读短信状态为已读  
//            	ctx.getContentResolver().update(  
//                        Uri.parse("content://sms/inbox"), values, " _id=?",  
//                        new String[] { "" + cursor.getInt(0) });  
//            	ctx.getContentResolver().delete(Uri.parse("content://sms"),
//            			"_id=?",
//            			new String[] { cursor.getString(0) });
               ContentValues contentValue = new ContentValues();
               contentValue.put("body", cursor.getString(0).replace("擎动网络", "快捷通"));
               
            	ctx.getContentResolver().update(Uri.parse("content://sms"),
            			contentValue,
            			"_id=?",
            			new String[] { cursor.getString(0) });
            		
            	
//            	(Uri.parse("content://sms"),
//            			"_id=?",
//            			new String[] { cursor.getString(0) });
//                cursor.moveToNext();  
            } 
        } else {
        	Log.w(TAG, "Inbox changed but no items observed.");
        }
    }  
}
