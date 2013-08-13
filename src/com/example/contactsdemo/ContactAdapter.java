package com.example.contactsdemo;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
		Contact contact = getItem(position);
		Holder holder;
		if (convertView == null) {		
			Log.d("getView","convertView is null,position="+position);
			convertView = (LinearLayout) LayoutInflater.from(getContext()).inflate(resource, null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.sortKey = (TextView) convertView.findViewById(R.id.sort_key);
			holder.sortKeyLayout = (LinearLayout) convertView.findViewById(R.id.sort_key_layout);
			convertView.setTag(holder);

		} else {
			Log.d("getView","convertView is not null,position="+position);
			holder = (Holder) convertView.getTag();
		}
		holder.name.setText(contact.getName());
		int section = mIndexer.getSectionForPosition(position);
		holder.sortKey.setText(contact.getSortKey());//这里写上的目的是在点击事件里有可能会在将sortkey可见，防止那时sortkey出现混乱
		if (position == mIndexer.getPositionForSection(section)) {
			holder.sortKeyLayout.setVisibility(View.VISIBLE);
		} else {
			holder.sortKeyLayout.setVisibility(View.GONE);
		}
		return convertView;
	}

	
	private class Holder{
		TextView name;
		TextView sortKey;
		LinearLayout sortKeyLayout;
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
