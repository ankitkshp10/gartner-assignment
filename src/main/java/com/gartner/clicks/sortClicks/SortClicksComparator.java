package com.gartner.clicks.sortClicks;

import java.util.Comparator;

import com.gartner.clicks.clicksPojo.Clicks;

public class SortClicksComparator implements Comparator<Clicks> {

	@Override
	public int compare(Clicks o1, Clicks o2) {
		int ipCompare = o1.getIp().compareTo(o2.getIp());
		int amountCompare = o1.getAmount().compareTo(o2.getAmount());
		int timeCompare = o2.getTimestamp().compareTo(o1.getTimestamp());

		int amountTimeCompare = amountCompare == 0 ? timeCompare : amountCompare;
		return (ipCompare == 0) ? (amountTimeCompare) : ipCompare;
	}

}
