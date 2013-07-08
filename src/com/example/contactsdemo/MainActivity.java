package com.example.contactsdemo;

import java.util.ArrayList;
import java.util.List;

import com.example.contactsdemo.view.animation.*;
import com.example.contactsdemo.view.InOutImageButton;



import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import com.example.contactsdemo.ContactAdapter;

public class MainActivity extends Activity {

	/**
	 * 分组的布局
	 */
	private LinearLayout titleLayout;

    /** 
     * 弹出式分组的布局 
     */  
    private RelativeLayout sectionToastLayout; 
    
    /**
     *  侧边监听
     */
    private RelativeLayout content_layout;
	/**
	 * 分组上显示的字母
	 */
	private TextView title;
	
	/**
	 * 显示共有多少位联系人
	 */
	private TextView totalContacts;

	/**
	 * 联系人ListView
	 */
	private ListView contactsListView;
	
    /** 
     * 右侧可滑动字母表 
     */  
	private Button alphabetButton;  
	
    /** 
     * 弹出式分组上的文字 
     */  
    private TextView sectionToastText;  

	/**
	 * 联系人列表适配器
	 */
	private ContactAdapter adapter;

	/**
	 * 用于进行字母表分组
	 */
	private AlphabetIndexer indexer;

	/**
	 * 存储所有手机中的联系人
	 */
	private List<Contact> contacts = new ArrayList<Contact>();

	/**
	 * 定义字母表的排序规则
	 */
	private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
	
	
	private boolean		areButtonsShowing = false; 
	private ViewGroup	composerButtonsWrapper;
	private ImageView		composerButtonsShowHideButtonIcon;
	
	private Animation	rotateStoryAddButtonIn;
	private Animation	rotateStoryAddButtonOut;
	
	public static String TAG="xiangkang";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
		Log.d("getView","adapter="+adapter);
		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout); 
		title = (TextView) findViewById(R.id.title);
		totalContacts = (TextView) findViewById(R.id.total);
		sectionToastText = (TextView)findViewById(R.id.section_toast_text);
		alphabetButton = (Button) findViewById(R.id.alphabetButton);
		contactsListView = (ListView) findViewById(R.id.contacts_list_view);
	    content_layout = (RelativeLayout) findViewById(R.id.content_layout);

//		composerButtonsWrapper = (ViewGroup) findViewById(R.id.composer_buttons_wrapper);
//		composerButtonsShowHideButton = findViewById(R.id.composer_buttons_show_hide_button);
//		composerButtonsShowHideButtonIcon = findViewById(R.id.composer_buttons_show_hide_button_icon);
	
		
		rotateStoryAddButtonIn = AnimationUtils.loadAnimation(this,
				R.anim.rotate_story_add_button_in);
		rotateStoryAddButtonOut = AnimationUtils.loadAnimation(this,
				R.anim.rotate_story_add_button_out);
		
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri,
				new String[] { "display_name", "sort_key" }, null, null, "sort_key");
		int total = cursor.getCount();
		String sTotalFormat = getResources().getString(R.string.total);
		String sFinalTotal = String.format(sTotalFormat,total);
		totalContacts.setText(sFinalTotal);
		if (cursor.moveToFirst()) {
			do {
				int  len = cursor.getCount();
				Log.d(TAG, "len="+len);
				String name = cursor.getString(0);
				String sortKey = getSortKey(cursor.getString(1));
				Contact contact = new Contact();
				contact.setName(name);
				contact.setSortKey(sortKey);
				contacts.add(contact);
			} while (cursor.moveToNext());
		}
		
	
		//startManagingCursor(cursor);
		indexer = new AlphabetIndexer(cursor, 1, alphabet);
		adapter.setIndexer(indexer);
		if (contacts.size() > 0) {
			setupContactsListView();
			setAlpabetListener();  
			setItemClick();
		}
	}


	private void setItemClick() {
		// TODO Auto-generated method stub
		Log.d("getView","setItemClick--------");
		contactsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d("onItemClick","arg1="+arg1);
				Log.d("onItemClick","arg2="+arg2);
				Log.d("onItemClick","arg3="+arg3);
				composerButtonsWrapper =(ViewGroup) arg1.findViewById(R.id.composer_buttons_wrapper);
				composerButtonsShowHideButtonIcon= (ImageView) arg1.findViewById(R.id.composer_buttons_show_hide_button_icon);
				RelativeLayout composer_buttons_show_hide_button_Layout = (RelativeLayout) arg1.findViewById(R.id.composer_buttons_show_hide_button);
//				MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
//				params.topMargin = 0;
//				titleLayout.setLayoutParams(params);
				LayoutParams params = (LayoutParams) arg1.findViewById(R.id.item_layout).getLayoutParams();
				
				//分配弹出菜单图标的宽度
				int totle_width=arg1.getWidth();
				InOutImageButton composer_button_photo = (InOutImageButton) arg1.findViewById(R.id.composer_button_photo);
				MarginLayoutParams photo_width = (MarginLayoutParams) composer_button_photo.getLayoutParams();
				photo_width.width=photo_width.height=totle_width/8;
				
				InOutImageButton composer_button_music = (InOutImageButton) arg1.findViewById(R.id.composer_button_music);
				MarginLayoutParams music_width = (MarginLayoutParams) composer_button_music.getLayoutParams();
				music_width.width=music_width.height=totle_width/8;
				
				InOutImageButton composer_button_people = (InOutImageButton) arg1.findViewById(R.id.composer_button_people);
				MarginLayoutParams people_width = (MarginLayoutParams) composer_button_people.getLayoutParams();
				people_width.width=people_width.height=totle_width/8;
				
				InOutImageButton composer_button_place = (InOutImageButton) arg1.findViewById(R.id.composer_button_place);
				MarginLayoutParams place_width = (MarginLayoutParams) composer_button_place.getLayoutParams();
				place_width.width=place_width.height=totle_width/8;
				
				InOutImageButton composer_button_sleep = (InOutImageButton) arg1.findViewById(R.id.composer_button_sleep);
				MarginLayoutParams sleep_width = (MarginLayoutParams) composer_button_sleep.getLayoutParams();
				sleep_width.width=sleep_width.height=totle_width/8;
				
				InOutImageButton composer_button_thought = (InOutImageButton) arg1.findViewById(R.id.composer_button_thought);
				MarginLayoutParams thought_width = (MarginLayoutParams) composer_button_thought.getLayoutParams();
				thought_width.width=thought_width.height=totle_width/8;
				
				arg1.findViewById(R.id.composer_button_photo).setLayoutParams(photo_width);
				arg1.findViewById(R.id.composer_button_music).setLayoutParams(music_width);
				arg1.findViewById(R.id.composer_button_people).setLayoutParams(people_width);
				arg1.findViewById(R.id.composer_button_place).setLayoutParams(place_width);
				arg1.findViewById(R.id.composer_button_sleep).setLayoutParams(sleep_width);
				arg1.findViewById(R.id.composer_button_thought).setLayoutParams(thought_width);
				if (!areButtonsShowing) {
					params.height=500;
					arg1.findViewById(R.id.item_layout).setLayoutParams(params);
					Log.d("onItemClick","composer_buttons_show_hide_button_Layout="+composer_buttons_show_hide_button_Layout);
					composer_buttons_show_hide_button_Layout.setVisibility(View.VISIBLE);
					composer_buttons_show_hide_button_Layout.getBackground().setAlpha(250);
					composerButtonsShowHideButtonIcon.setVisibility(View.VISIBLE);
					ComposerButtonAnimation.startAnimations(
							composerButtonsWrapper, InOutAnimation.Direction.IN);
					composerButtonsShowHideButtonIcon
							.startAnimation(rotateStoryAddButtonIn);
				} else {
					params.height=LayoutParams.MATCH_PARENT;
					arg1.findViewById(R.id.item_layout).setLayoutParams(params);
					ComposerButtonAnimation.startAnimations(
							composerButtonsWrapper, InOutAnimation.Direction.OUT);
					composerButtonsShowHideButtonIcon
							.startAnimation(rotateStoryAddButtonOut);
					Log.d("onItemClick","composer_buttons_show_hide_button_Layout="+composer_buttons_show_hide_button_Layout);

					composer_buttons_show_hide_button_Layout.getBackground().setAlpha(0);
					composer_buttons_show_hide_button_Layout.setVisibility(View.GONE);
					composerButtonsShowHideButtonIcon.setVisibility(View.GONE);
				}
				areButtonsShowing = !areButtonsShowing;
				
			}
			
		});
		
	}

	/**
	 * 为联系人ListView设置监听事件，根据当前的滑动状态来改变分组的显示位置，从而实现挤压动画的效果。
	 */
	private void setupContactsListView() {
		Log.d("onScroll","setupContactsListView-----");
		contactsListView.setAdapter(adapter);

		contactsListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				int section = indexer.getSectionForPosition(firstVisibleItem);
				int nextSecPosition = indexer.getPositionForSection(section + 1);
				Log.d("onScroll","section="+section);
				Log.d("onScroll","nextSecPosition="+nextSecPosition);
				Log.d("onScroll","firstVisibleItem="+firstVisibleItem);
				Log.d("onScroll","visibleItemCount="+visibleItemCount);
				Log.d("onScroll","totalItemCount="+totalItemCount);
				if (firstVisibleItem != lastFirstVisibleItem) {
					MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
					params.topMargin = 0;
					titleLayout.setLayoutParams(params);
					title.setText(String.valueOf(alphabet.charAt(section)));
				}
				if (nextSecPosition == firstVisibleItem + 1) {
					View childView = view.getChildAt(0);
					if (childView != null) {
						int titleHeight = titleLayout.getHeight();
						int bottom = childView.getBottom();
						Log.d("onScroll","titleHeight="+titleHeight);
						Log.d("onScroll","bottom="+bottom);
						MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
						if (bottom < titleHeight) {
							float pushedDistance = bottom - titleHeight;
							Log.d("onScroll","pushedDistance="+pushedDistance);
							params.topMargin = (int) pushedDistance;
							titleLayout.setLayoutParams(params);
						} else {
							if (params.topMargin != 0) {
								params.topMargin = 0;
								titleLayout.setLayoutParams(params);
							}
						}
					}
				}
				lastFirstVisibleItem = firstVisibleItem;
			}
		});

	}


	/**
	 * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
	 * 
	 * @param sortKeyString
	 *            数据库中读取出的sort key
	 * @return 英文字母或者#
	 */
	private String getSortKey(String sortKeyString) {
		String key = sortKeyString.substring(0, 1).toUpperCase();
		if (key.matches("[A-Z]")) {
			return key;
		}
		return "#";
	}
	
	private void setAlpabetListener(){
		Log.d("getView","MotionEvent.ACTION_DOWN----");
	    alphabetButton.setOnTouchListener(new OnTouchListener() {  
	        @Override  
	        public boolean onTouch(View v, MotionEvent event) {  
	        	Log.d("setAlpabetListener","MotionEvent.ACTION_DOWN----");
	        	//alphabetButton.setVisibility(View.VISIBLE);
	            float alphabetHeight = alphabetButton.getHeight();  
	            float y = event.getY();  
	            int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));  
	            if (sectionPosition < 0) {  
	                sectionPosition = 0;  
	            } else if (sectionPosition > 26) {  
	                sectionPosition = 26;  
	            }  
	            String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));  
	            int position = indexer.getPositionForSection(sectionPosition);  
	            switch (event.getAction()) {  
	            case MotionEvent.ACTION_DOWN:  
	            	Log.d("setAlpabetListener","MotionEvent.ACTION_DOWN----");
	                alphabetButton.setBackgroundResource(R.drawable.a_z_click);  
	                sectionToastLayout.setVisibility(View.VISIBLE);  
	                sectionToastText.setText(sectionLetter);  
	                contactsListView.setSelection(position);  
	                break;  
	            case MotionEvent.ACTION_MOVE:  
	            	Log.d("setAlpabetListener","MotionEvent.ACTION_MOVE----");
	                sectionToastText.setText(sectionLetter);  
	                contactsListView.setSelection(position);  
	                break;  
	            default:  
	            	Log.d("setAlpabetListener","default----");
	                alphabetButton.setBackgroundResource(R.drawable.a_z);  
	                sectionToastLayout.setVisibility(View.GONE);  
	            }  
	            return true;  
	        }  
	    });
	} 
	private void printCurosr(Cursor cursor){
		int clumn=cursor.getColumnCount();
		Log.d("cur","clumn="+clumn);
		Log.d("cur","---display_name----    --------sort_key-----------");
		if (cursor.moveToFirst()){
			do{
				Log.d("cur","    "+cursor.getString(0)+"               "+cursor.getString(1));
			}while(cursor.moveToNext());
		}
	}
}