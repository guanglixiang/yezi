package com.example.contactsdemo;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact> {

	/**
	 * 需要渲染的item布局文件
	 */
	private int resource;
     
	/**
	 * 字母表分组工具
	 */
	private SectionIndexer mIndexer;
	
	//构造函数不能有返回值，方法名必须和类名一样。可以有多个构造函数，也就是重载。new 的时候是根据参数类型来选择使用哪个构造函数的
	//调用语句： adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
	public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects) {
		super(context, textViewResourceId, objects);
		resource = textViewResourceId;
		Log.d("Xiangkang", "resource="+resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("getView","position="+position);
		Contact contact = getItem(position);
		LinearLayout layout = null;
		if (convertView == null) {
			layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(resource, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(resource, null);
		
		TextView name = (TextView) layout.findViewById(R.id.name);
		TextView sortKey = (TextView) layout.findViewById(R.id.sort_key);
		name.setText(contact.getName());
		LinearLayout sortKeyLayout = (LinearLayout) layout.findViewById(R.id.sort_key_layout);
		int section = mIndexer.getSectionForPosition(position);
		if (position == mIndexer.getPositionForSection(section)) {
			sortKey.setText(contact.getSortKey());
			sortKeyLayout.setVisibility(View.VISIBLE);
		} else {
			sortKeyLayout.setVisibility(View.GONE);
		}
		RelativeLayout composer_buttons_show_hide_button_Layout = (RelativeLayout) layout.findViewById(R.id.composer_buttons_show_hide_button);
		composer_buttons_show_hide_button_Layout.getBackground().setAlpha(0);
		return layout;
	}

	/**
	 * 给当前适配器传入一个分组工具。
	 * 
	 * @param indexer
	 */
	public void setIndexer(SectionIndexer indexer) {
		mIndexer = indexer;
	}

}