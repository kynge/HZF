package com.whty.qd.upay.common;

/**
 * @ClassName: Exception
 * @Description: 自定义异常
 * @author chenzhenlin
 * @date 2013-8-9 下午4:02:32
 */
public class QdException {
	private Exception e;
	private boolean isPrint = true;

	public QdException(Exception e) {
		super();
		this.e = e;
	}

	public void printStackTrace() {
		if (isPrint) {
			this.e.printStackTrace();
		}
	}

}
