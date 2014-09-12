package com.whty.qd.upay.common;

import java.util.Calendar;

import com.whty.qd.upay.R;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 通用DateSetDialog
 * @author super
 */
public class SuperDateSetDialog implements OnClickListener{
	
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private ImageButton yearAddBtn, monthAddBtn, dayAddBtn, yearDelBtn, monthDelBtn, dayDelBtn; 
	private Button setBtn,cancelBtn;
	private TextView yearTxt, monthTxt, dayTxt;
	private int todayYear,todayMonth,todayDay;
	private int nowShowYear,nowShowMonth,nowShowDay;
	private ConfirmListener clistener;

	public SuperDateSetDialog(Context context) {
		this(context, null);
	}

	public SuperDateSetDialog(Context context, AttributeSet attrs) {
		super();
		mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.super_time_dialog_layout, null);
		dialog.setContentView(view);
		initChildViews();
	}

	private void initChildViews() {
		yearAddBtn = (ImageButton)view.findViewById(R.id.year_add_btn);
		monthAddBtn = (ImageButton)view.findViewById(R.id.month_add_btn);
		dayAddBtn = (ImageButton)view.findViewById(R.id.day_add_btn);
		yearDelBtn = (ImageButton)view.findViewById(R.id.year_reduce_btn);
		monthDelBtn = (ImageButton)view.findViewById(R.id.month_reduce_btn);
		dayDelBtn = (ImageButton)view.findViewById(R.id.day_reduce_btn);
		setBtn = (Button)view.findViewById(R.id.button_confirm);
		cancelBtn = (Button)view.findViewById(R.id.button_cancel);
		yearTxt = (TextView)view.findViewById(R.id.year_txt);
		monthTxt = (TextView)view.findViewById(R.id.month_txt);
		dayTxt = (TextView)view.findViewById(R.id.day_txt);
		initListeners();
		initShowView();
	}

	private void initListeners() {
		yearAddBtn.setOnClickListener(this);
		monthAddBtn.setOnClickListener(this);
		dayAddBtn.setOnClickListener(this);
		yearDelBtn.setOnClickListener(this); 
		monthDelBtn.setOnClickListener(this); 
		dayDelBtn.setOnClickListener(this); 
		setBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clistener.onFinish(nowShowYear, nowShowMonth, nowShowDay);
				dialogDismiss();
			}
		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialogDismiss();
			}
		});
	}
	
	private void initShowView() {
		Calendar c = Calendar.getInstance();
		todayYear = c.get(Calendar.YEAR);
		todayMonth = c.get(Calendar.MONTH)+1;
		todayDay = c.get(Calendar.DAY_OF_MONTH);
		Log.e("","todayYear==="+todayYear);
		Log.e("","todayMonth==="+todayMonth);
		Log.e("","todayDay==="+todayDay);
		nowShowYear = todayYear;
		nowShowMonth = todayMonth;
		nowShowDay = todayDay;
		yearTxt.setText(""+todayYear);
		monthTxt.setText((todayMonth<10?"0":"") +todayMonth);
		dayTxt.setText((todayDay<10?"0":"") +todayDay);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.year_add_btn:
			if(nowShowYear<2050){//年份不超过2050
				nowShowYear = nowShowYear+1;
				yearTxt.setText(""+nowShowYear);
			}
			break;
		case R.id.month_add_btn:
			if(nowShowMonth<12){//月份不超过12
				nowShowMonth = nowShowMonth+1;
				monthTxt.setText(intMoD2Str(nowShowMonth));
			}else{//月份超过12时，重新循环到1
				nowShowMonth = 1;
				monthTxt.setText(intMoD2Str(nowShowMonth));
			}
			//如果当前日期数大于当前月份的最大天数，那么将日期数设为后者
			if(nowShowDay>getMonthLastDay(nowShowYear, nowShowMonth)){
				nowShowDay = getMonthLastDay(nowShowYear, nowShowMonth);
				dayTxt.setText(intMoD2Str(nowShowDay));
			}
			break;
		case R.id.day_add_btn:
			//日期数不能超过当月最多天数
			if(nowShowDay<getMonthLastDay(nowShowYear, nowShowMonth)){
				nowShowDay = nowShowDay+1;
				dayTxt.setText(intMoD2Str(nowShowDay));
			}else{//若日期数超过当月最多天数，将日期数重新循环到1
				nowShowDay = 1;
				dayTxt.setText(intMoD2Str(nowShowDay));
			}
			break;
		case R.id.year_reduce_btn:
			if(nowShowYear>1900){//年份不小于2000
				nowShowYear = nowShowYear-1;
				yearTxt.setText(""+nowShowYear);
			}
			break;
		case R.id.month_reduce_btn:
			if(nowShowMonth>1){//月份不小于1
				nowShowMonth = nowShowMonth-1;
				monthTxt.setText(intMoD2Str(nowShowMonth));
			}else{//月份小于1时，重新循环到12
				nowShowMonth = 12;
				monthTxt.setText(intMoD2Str(nowShowMonth));
			}
			//如果当前日期数大于当前月份的最大天数，那么将日期数设为后者
			if(nowShowDay>getMonthLastDay(nowShowYear, nowShowMonth)){
				nowShowDay = getMonthLastDay(nowShowYear, nowShowMonth);
				dayTxt.setText(intMoD2Str(nowShowDay));
			}
			break;
		case R.id.day_reduce_btn:
			if(nowShowDay>1){//日期数不能小于1
				nowShowDay = nowShowDay-1;
				dayTxt.setText((nowShowDay<10?"0":"") +nowShowDay);
			}else{//若日期数小于1，将日期数重新循环到当月最大日期
				nowShowDay = getMonthLastDay(nowShowYear, nowShowMonth);
				dayTxt.setText(intMoD2Str(nowShowDay));
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 将月份或日期整数，转换成带0或不带0的字符串
	 * @param d//月份或日期
	 */
	private String intMoD2Str(int d){
		return (d<10?"0":"") +d;
	}
	
	/**
	 * 得到指定月的天数
	 * */
	public int getMonthLastDay(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}
	

	public void showDialog() {
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showDialog(int _year, int _month, int _day) {
		nowShowYear = _year;
		nowShowMonth = _month;
		nowShowDay = _day;
		yearTxt.setText(""+nowShowYear);
		monthTxt.setText(intMoD2Str(nowShowMonth));
		dayTxt.setText(intMoD2Str(nowShowDay));
		showDialog();
	}
	
	/**
	 * set listener
	 * @param li
	 */
	public void setListener(ConfirmListener li) {
		clistener = li;
	}
	
	/**
	 * callback listen interface
	 */
	public interface ConfirmListener {
		public void onFinish(int year, int month, int day);
	}

	public void dialogDismiss() {
		try {
			dialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isShow() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}

	public void setCancelable(boolean is){
		dialog.setCancelable(is);
	}
	
	public Dialog getDialog() {
		return dialog;
	}
}
