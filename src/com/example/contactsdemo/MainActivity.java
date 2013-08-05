package com.example.contactsdemo;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactsdemo.view.animation.ComposerButtonAnimation;
import com.example.contactsdemo.view.animation.InOutAnimation;

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

	private float currentPosition;

	private ViewGroup composerButtonsWrapper;
	private ImageView composerButtonsShowHideButtonIcon;
	// private RelativeLayout composerButtonsShowHideButtonIcon;
	private Animation rotateStoryAddButtonIn;
	private Animation rotateStoryAddButtonOut;
	private LayoutParams item_root_layout_params;
	private View item_root_layout;
	private ImageView image;
	private View show_hide_bt;
	private int RootWidth;
	public static String TAG = "xiangkang";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		DisplayMetrics metric = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metric);
		//RootWidth = metric.widthPixels;
		adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
		title = (TextView) findViewById(R.id.title);
		totalContacts = (TextView) findViewById(R.id.total);
		sectionToastText = (TextView) findViewById(R.id.section_toast_text);
		alphabetButton = (Button) findViewById(R.id.alphabetButton);
		contactsListView = (ListView) findViewById(R.id.contacts_list_view);
		rotateStoryAddButtonIn = AnimationUtils.loadAnimation(this,
				R.anim.rotate_story_add_button_in);
		rotateStoryAddButtonOut = AnimationUtils.loadAnimation(this,
				R.anim.rotate_story_add_button_out);

		// Animation showpopbt = AnimationUtils.loadAnimation(this, R.anim.);

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri,
				new String[] { "display_name", "sort_key" }, null, null,
				"sort_key");
		int total = cursor.getCount();
		String sTotalFormat = getResources().getString(R.string.total);
		String sFinalTotal = String.format(sTotalFormat, total);
		totalContacts.setText(sFinalTotal);
		if (cursor.moveToFirst()) {
			do {
				int len = cursor.getCount();
				Log.d(TAG, "len=" + len);
				String name = cursor.getString(0);
				String sortKey = getSortKey(cursor.getString(1));
				Contact contact = new Contact();
				contact.setName(name);
				contact.setSortKey(sortKey);
				contacts.add(contact);
			} while (cursor.moveToNext());
		}

		// startManagingCursor(cursor);
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
		contactsListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				currentPosition = event.getRawY();
				return false;
			}

		});
		contactsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				composerButtonsWrapper = (ViewGroup) arg1
						.findViewById(R.id.composer_buttons_wrapper);
				composerButtonsShowHideButtonIcon = (ImageView) arg1
						.findViewById(R.id.composer_buttons_show_hide_button_icon);
				// composerButtonsShowHideButtonIcon = (RelativeLayout)
				// arg1.findViewById(R.id.composer_buttons_show_hide_button);
				item_root_layout = arg1.findViewById(R.id.item_layout);
				item_root_layout_params = (LayoutParams) item_root_layout
						.getLayoutParams();
				alphabetButton.setVisibility(View.GONE);
				item_root_layout_params.height = (int) (arg1.getRootView()
						.getWidth() / 1.5);
				item_root_layout_params.width = arg1.getRootView().getWidth();
				item_root_layout.setLayoutParams(item_root_layout_params);
				int totle_width = arg1.getWidth();
				int popbtcount = composerButtonsWrapper.getChildCount();
				composerButtonsShowHideButtonIcon.setVisibility(View.VISIBLE);
				image = (ImageView) arg1.findViewById(R.id.contact_image);
				
				show_hide_bt = arg1
						.findViewById(R.id.composer_buttons_show_hide_button);
				// 根据item位置设置弹出菜单的方向
//				if (arg1.getRootView().getHeight() / 2 > currentPosition) {
//					MarginLayoutParams image_layout = (MarginLayoutParams) image
//							.getLayoutParams();
//					image_layout.topMargin = 200;
//					image.setLayoutParams(image_layout);
//					android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//							LayoutParams.WRAP_CONTENT,
//							LayoutParams.WRAP_CONTENT);
//					params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//					params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//					show_hide_bt.setLayoutParams(params);
//					for (int i = 0; i < popbtcount; i++) {
//						ImageButton v = (ImageButton) composerButtonsWrapper
//								.getChildAt(i);
//						MarginLayoutParams popbt = (MarginLayoutParams) v
//								.getLayoutParams();
//						popbt.width = popbt.height = totle_width / 7;
//						v.setLayoutParams(popbt);
//						// 计算popbt的半圆形分布位置
//						float pi = 3.14f;
//						float rootwidth = v.getRootView().getWidth();
//						float r = rootwidth / 2 - 40;
//						float initangle = (pi / 6);
//						float increase_angle = initangle + i
//								* ((pi - 2 * initangle) / (popbtcount - 1));
//						popbt.leftMargin = (int) (r - Math.cos(increase_angle) * r);
//						popbt.topMargin = (int) ((1-Math.sin(increase_angle)) * r);
//						Log.d("popbtcount","popbt.bottomMargin="+popbt.bottomMargin);
//
//						v.setOnClickListener(new OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								PopBtSpecialEffect(v);
//								Toast.makeText(getApplicationContext(),
//										"composerButtonsWrapper=" + v.getId(),
//										Toast.LENGTH_SHORT).show();
//							}
//						});
//					}
//
//				}else{
//				if (2*arg1.getRootView().getHeight() / 3 < currentPosition){
//					//contactsListView.smoothScrollToPositionFromTop(10, 200, 3000);
//				}
				int section = indexer.getSectionForPosition(arg2);
				Log.d("xiangkang","arg2="+arg2);
				Log.d("xiangkang","indexer.getPositionForSection(section)="+indexer.getPositionForSection(section));
				if (arg2 == indexer.getPositionForSection(section)) {
					contactsListView.smoothScrollBy(arg1.getTop(), 500);
				} else {
					contactsListView.smoothScrollBy(arg1.getTop()-titleLayout.getHeight(), 500);
				}
				for (int i = 0; i < popbtcount; i++) {
					ImageButton v = (ImageButton) composerButtonsWrapper
							.getChildAt(i);
					MarginLayoutParams popbt = (MarginLayoutParams) v
							.getLayoutParams();
					popbt.width = popbt.height = totle_width / 7;
					v.setLayoutParams(popbt);
					// 计算popbt的半圆形分布位置
					float pi = 3.14f;
					float rootwidth = v.getRootView().getWidth();
					float r = rootwidth / 2 - 40;
					float initangle = (pi / 6);
					float increase_angle = initangle + i
							* ((pi - 2 * initangle) / (popbtcount - 1));
					popbt.leftMargin = (int) (r - Math.cos(increase_angle) * r);
					popbt.topMargin = (int) (Math.sin(increase_angle) * r);
					Log.d("popbtcount","popbt.bottomMargin="+popbt.bottomMargin);
					
					v.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							PopBtSpecialEffect(v);
							Toast.makeText(getApplicationContext(),
									"composerButtonsWrapper=" + v.getId(),
									Toast.LENGTH_SHORT).show();
						}
					});
				}
				ComposerButtonAnimation.startAnimations(composerButtonsWrapper,
						InOutAnimation.Direction.IN);
				composerButtonsShowHideButtonIcon
						.startAnimation(rotateStoryAddButtonIn);

				composerButtonsShowHideButtonIcon.setFocusable(true);
				composerButtonsShowHideButtonIcon
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// 布局
//								MarginLayoutParams image_layout = (MarginLayoutParams) image
//										.getLayoutParams();
//								image_layout.topMargin = 1;
//								image.setLayoutParams(image_layout);
//								android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//										LayoutParams.WRAP_CONTENT,
//										LayoutParams.WRAP_CONTENT);
//								params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//								params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//								show_hide_bt.setLayoutParams(params);

								Toast.makeText(getApplicationContext(),
										"composerButtonsShowHideButtonIcon---",
										Toast.LENGTH_SHORT).show();
								composerButtonsShowHideButtonIcon
										.startAnimation(rotateStoryAddButtonOut);
								ComposerButtonAnimation.startAnimations(
										composerButtonsWrapper,
										InOutAnimation.Direction.OUT);
								composerButtonsShowHideButtonIcon
										.setVisibility(View.GONE);
								item_root_layout_params.height = LayoutParams.MATCH_PARENT;
								item_root_layout
										.setLayoutParams(item_root_layout_params);
								alphabetButton.setVisibility(View.VISIBLE);
							}
						});

			}

		});

	}

	/**
	 * 为联系人ListView设置监听事件，根据当前的滑动状态来改变分组的显示位置，从而实现挤压动画的效果。
	 */
	private void setupContactsListView() {
		Log.d("onScroll", "setupContactsListView-----");
		contactsListView.setAdapter(adapter);
		contactsListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int section = indexer.getSectionForPosition(firstVisibleItem);
				int nextSecPosition = indexer
						.getPositionForSection(section + 1);
				if (firstVisibleItem != lastFirstVisibleItem) {
					MarginLayoutParams params = (MarginLayoutParams) titleLayout
							.getLayoutParams();
					params.topMargin = 0;
					titleLayout.setLayoutParams(params);
					title.setText(String.valueOf(alphabet.charAt(section)));
				}
				if (nextSecPosition == firstVisibleItem + 1) {
					View childView = view.getChildAt(0);
					if (childView != null) {
						int titleHeight = titleLayout.getHeight();
						int bottom = childView.getBottom();
						Log.d("onScroll", "titleHeight=" + titleHeight);
						Log.d("onScroll", "bottom=" + bottom);
						MarginLayoutParams params = (MarginLayoutParams) titleLayout
								.getLayoutParams();
						if (bottom < titleHeight) {
							float pushedDistance = bottom - titleHeight;
							Log.d("onScroll", "pushedDistance="
									+ pushedDistance);
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
		String key = sortKeyString.substring(0, 1);
		if (key.matches("[A-Z]")) {
			return key;
		}
		return "#";
	}

	private void setAlpabetListener() {
		alphabetButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("setAlpabetListener", "MotionEvent.ACTION_DOWN----");
				// alphabetButton.setVisibility(View.VISIBLE);
				float alphabetHeight = alphabetButton.getHeight();
				float y = event.getY();
				int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
				if (sectionPosition < 0) {
					sectionPosition = 0;
				} else if (sectionPosition > 26) {
					sectionPosition = 26;
				}
				String sectionLetter = String.valueOf(alphabet
						.charAt(sectionPosition));
				int position = indexer.getPositionForSection(sectionPosition);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d("setAlpabetListener", "MotionEvent.ACTION_DOWN----");
					alphabetButton.setBackgroundResource(R.drawable.a_z_click);
					sectionToastLayout.setVisibility(View.VISIBLE);
					sectionToastText.setText(sectionLetter);
					contactsListView.setSelection(position);
					break;
				case MotionEvent.ACTION_MOVE:
					Log.d("setAlpabetListener", "MotionEvent.ACTION_MOVE----");
					sectionToastText.setText(sectionLetter);
					contactsListView.setSelection(position);
					break;
				default:
					Log.d("setAlpabetListener", "default----");
					alphabetButton.setBackgroundResource(R.drawable.a_z);
					sectionToastLayout.setVisibility(View.GONE);
				}
				return true;
			}
		});
	}

	private void PopBtSpecialEffect(View v) {
		// TODO Auto-generated method stub
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			String clickbtname = composerButtonsWrapper.getResources()
					.getResourceName(
							composerButtonsWrapper.getChildAt(i).getId());
			Animation an;
			if (v.getResources().getResourceName(v.getId()).equals(clickbtname)) {
				an = AnimationUtils.loadAnimation(this,
						R.anim.popbt_special_effect_withclick);
			} else {
				an = AnimationUtils.loadAnimation(this,
						R.anim.popbt_special_effect_withoutclick);
			}
			composerButtonsWrapper.getChildAt(i).startAnimation(an);

		}
	}

	private void printCurosr(Cursor cursor) {
		int clumn = cursor.getColumnCount();
		Log.d("cur", "clumn=" + clumn);
		Log.d("cur", "---display_name----    --------sort_key-----------");
		if (cursor.moveToFirst()) {
			do {
				Log.d("cur", "    " + cursor.getString(0) + "               "
						+ cursor.getString(1));
			} while (cursor.moveToNext());
		}
	}
}
