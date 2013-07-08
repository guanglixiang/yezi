package com.example.contactsdemo;

import android.util.Log;


public class Contact {

	/**
	 * 联系人姓名
	 */
	private String name;

	/**
	 * 排序字母
	 */
	private String sortKey;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		Log.d("name=",name);
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

}