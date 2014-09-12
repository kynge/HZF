package com.whty.qd.upay.phonefee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//面值存放列表
public class PhoneCardPriceList  implements Serializable{
	private static final long serialVersionUID = 1L;
	List<PhoneCardPrice> cardList = new ArrayList<PhoneCardPrice>();

	public List<PhoneCardPrice> getCardList() {
		return cardList;
	}

	public void setCardList(List<PhoneCardPrice> cardList) {
		this.cardList = cardList;
	}
	
}
