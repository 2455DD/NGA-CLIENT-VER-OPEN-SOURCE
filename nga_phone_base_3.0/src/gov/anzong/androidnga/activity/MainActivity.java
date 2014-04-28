package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import io.vov.vitamio.LibsChecker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.LoginFragment;
import sp.phone.fragment.ProfileSearchDialogFragment;
import sp.phone.fragment.SearchDialogFragment;
import sp.phone.fragment.TopiclistContainer;
import sp.phone.interfaces.PageCategoryOwnner;
import sp.phone.task.AppUpdateCheckTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

public class MainActivity extends BaseListSample implements PerferenceConstant,
		OnItemClickListener, PageCategoryOwnner {
	static final String TAG = MainActivity.class.getSimpleName();
	ActivityUtil activityUtil = ActivityUtil.getInstance();
	private MyApp app;
	ViewPager pager;
	View view;
	AppUpdateCheckTask task = null;
	OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();
	int ifRecentExist = 0;// ifRecentExist��menu item click right

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		this.setTheme(R.style.AppTheme);
		Intent intent = getIntent();
		app = ((MyApp) getApplication());
		loadConfig(intent);
		initDate();
		initView();

		if (boardInfo.getCategoryName(0).equals("�������")) {
			setLocItem(5, "�������");
			if (boardInfo.getCategoryCount() > 14) {
				if (boardInfo.getCategoryName(14).equals("�û��Զ���")) {
					setLocItem(18, "�û��Զ���");
				}
			}
		} else {
			if (boardInfo.getCategoryCount() == 14) {
				setLocItem(17, "�û��Զ���");
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		mMenuDrawer
				.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
					@Override
					public boolean isViewDraggable(View v, int dx, int x, int y) {
						return v instanceof SeekBar;
					}
				});
		// task = new AppUpdateCheckTask(this);
		// task.execute("");

	}

	// 3 menu function
	@Override
	protected void onMenuItemClicked(int position, Item item) {
		// do click
		// i=0
		if (!boardInfo.getCategoryName(0).equals("�������")) {
			ifRecentExist = 1;
		}
		if (item.mTitle.equals("��½�˺�")) {
			jumpToLogin();
		} else if (item.mTitle.equals("yoooo")) {
			jumpToNearby();
		} else if (item.mTitle.equals("�������")) {
			pager.setCurrentItem(0 - ifRecentExist);
		} else if (item.mTitle.equals("ǩ������")) {
			signmission();
		} else if (item.mTitle.equals("�����û���Ϣ")) {
			search_profile();
		} else if (item.mTitle.equals("�ۺ�����")) {
			pager.setCurrentItem(1 - ifRecentExist);
		} else if (item.mTitle.equals("������ϵ��")) {
			pager.setCurrentItem(2 - ifRecentExist);
		} else if (item.mTitle.equals("ְҵ������")) {
			pager.setCurrentItem(3 - ifRecentExist);
		} else if (item.mTitle.equals("ð���ĵ�")) {
			pager.setCurrentItem(4 - ifRecentExist);
		} else if (item.mTitle.equals("�����֮��")) {
			pager.setCurrentItem(5 - ifRecentExist);
		} else if (item.mTitle.equals("ϵͳ��Ӳ������")) {
			pager.setCurrentItem(6 - ifRecentExist);
		} else if (item.mTitle.equals("������Ϸ")) {
			pager.setCurrentItem(7 - ifRecentExist);
		} else if (item.mTitle.equals("�����ƻ���")) {
			pager.setCurrentItem(8 - ifRecentExist);
		} else if (item.mTitle.equals("¯ʯ��˵")) {
			pager.setCurrentItem(9 - ifRecentExist);
		} else if (item.mTitle.equals("Ӣ������")) {
			pager.setCurrentItem(10 - ifRecentExist);
		} else if (item.mTitle.equals("���˰���")) {
			pager.setCurrentItem(11 - ifRecentExist);
		} else if (item.mTitle.equals("�û��Զ���")) {
			pager.setCurrentItem(12 - ifRecentExist);
		} else if (item.mTitle.equals("��������")) {
			jumpToSetting();
		} else if (item.mTitle.equals("��Ӱ���")) {
			add_fid_dialog();
		} else if (item.mTitle.equals("��URL��ȡ")) {
			useurltoactivity_dialog();
		} else if (item.mTitle.equals("����������")) {
			clear_recent_board();
		} else if (item.mTitle.equals("����")) {
			about_ngaclient();
		}

		mMenuDrawer.closeMenu();
	}

	private void signmission() {
//		 TODO Auto-generated method stub
		Intent intent = new Intent();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		intent.setClass(MainActivity.this, config.signActivityClass);
		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation) {
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		}
		
	}
	
	private void search_profile(){

		Bundle arg = new Bundle();
		DialogFragment df = new ProfileSearchDialogFragment();
		df.setArguments(arg);
		final String dialogTag = "searchpaofile_dialog";
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(dialogTag);
		if (prev != null) {
			ft.remove(prev);
		}

		try {
			df.show(ft, dialogTag);
		} catch (Exception e) {
			Log.e(TopiclistContainer.class.getSimpleName(),
					Log.getStackTraceString(e));

		}
	}

	private AlertDialog about_ngaclient() {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.client_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle("����");
		String versionName = null;
		TextView textview = (TextView) view
				.findViewById(R.id.client_device_dialog);
		try {
			PackageManager pm = MainActivity.this.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(
					MainActivity.this.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionName = pi.versionName == null ? "null" : pi.versionName;
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		String textviewtext = MainActivity.this
				.getString(R.string.about_client) + versionName;
		textview.setText(textviewtext);
		alert.setPositiveButton("֪����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});

		return alert.show();
	}

	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_CONTENT;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int drawerState = mMenuDrawer.getDrawerState();
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (drawerState == MenuDrawer.STATE_OPEN
					|| drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
			}
			if (drawerState == MenuDrawer.STATE_CLOSED
					|| drawerState == MenuDrawer.STATE_CLOSING) {
				mMenuDrawer.openMenu();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (drawerState == MenuDrawer.STATE_OPEN
					|| drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
				return true;
			} else {
				finish();
				return true;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.START;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putString(STATE_CONTENT_TEXT, mContentText);
	}

	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN
				|| drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}

		super.onBackPressed();
	}

	private void loadConfig(Intent intent) {
		// initUserInfo(intent);
		this.boardInfo = this.loadDefaultBoard();

	}

	@Override
	protected void onStop() {
		if (task != null) {
			Log.d(TAG, "cancel update check task");
			task.cancel(true);
			task = null;
		}
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// // inflater.inflate(R.menu.main_menu, menu);//���������ʱ���������
	//
	// final int flags = ThemeManager.ACTION_BAR_FLAG;
	// /*
	// int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
	// int i = 0;
	// for(i = 0;i< menu.size();i++){
	// ReflectionUtil.setShowAsAction(
	// menu.getItem(i), actionNum);
	// }
	// */
	// //this.getSupportActionBar().setDisplayOptions(flags);
	// ReflectionUtil.actionBar_setmDisplayOption(this, flags);
	//
	//
	// return super.onCreateOptionsMenu(menu);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
				setRequestedOrientation(orentation);
		} else {
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
					|| getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		int width = getResources().getInteger(R.integer.page_category_width);
		pager.setAdapter(new BoardPagerAdapter(getSupportFragmentManager(),
				this, width));
		super.onResume();
	}

	public boolean isTablet() {
		boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 0x04);// Configuration.SCREENLAYOUT_SIZE_XLARGE);
		boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large) && ActivityUtil.isGreaterThan_2_3_3();
	}

	private void jumpToLogin() {
		if (isTablet()) {
			DialogFragment df = new LoginFragment();
			df.show(getSupportFragmentManager(), "login");
			return;
		}

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		try {
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation) {
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		} catch (Exception e) {

		}

	}

	private void jumpToSetting() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		try {
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation)
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

		} catch (Exception e) {

		}

	}

	void jumpToNearby() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, NearbyUserActivity.class);

		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation)
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

	}

	private void clear_recent_board() {
		SharedPreferences share = getSharedPreferences(PERFERENCE,
				Activity.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(RECENT_BOARD, "");
		editor.commit();
		Intent iareboot = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		iareboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(iareboot);
	}

	private AlertDialog useurltoactivity_dialog() {
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater
				.inflate(R.layout.useurlto_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle(R.string.urlto_title_hint);
		final EditText urladd = (EditText) view.findViewById(R.id.urladd);
		urladd.requestFocus();
		String clipdata=null;
		if(ActivityUtil.isLessThan_3_0()){
			ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);  
			if (clipboardManager.hasText()){  
				clipdata=clipboardManager.getText().toString();  
			}  
		}else{
			ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);  
			if (clipboardManager.hasPrimaryClip()){  
				clipdata=clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();  
			}  
		}
		if(!StringUtil.isEmpty(clipdata)){
			urladd.setText(clipdata);
			urladd.selectAll();
		}
		
		
		alert.setPositiveButton("����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String url = urladd.getText().toString().trim();
				if (StringUtil.isEmpty(url)) {// ��
					Toast.makeText(MainActivity.this, "������URL��ַ",
							Toast.LENGTH_SHORT).show();
					urladd.setFocusable(true);
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					PhoneConfiguration conf = PhoneConfiguration.getInstance();
					url=url.toLowerCase(Locale.US).trim();
					if(url.indexOf("thread.php")>0){
						url= url.replaceAll("(?i)[^\\[|\\]]+fid=(-{0,1}\\d+)[^\\[|\\]]{0,}",
								"http://nga.178.com/thread.php?fid=$1");
						Intent intent = new Intent();
								intent.setData(Uri.parse(url));
								intent.setClass(view.getContext(), conf.topicActivityClass);
								view.getContext().startActivity(intent);
					}else if(url.indexOf("read.php")>0){
						if(url.indexOf("tid")>0 && url.indexOf("pid")>0){
							if(url.indexOf("tid")<url.indexOf("pid"))
								url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
									"http://nga.178.com/read.php?pid=$2&tid=$1");
							else
								url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
										"http://nga.178.com/read.php?pid=$1&tid=$2");
						}else if(url.indexOf("tid")>0 && url.indexOf("pid")<=0){
							url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
								"http://nga.178.com/read.php?tid=$1");
						}else if(url.indexOf("pid")>0 && url.indexOf("tid")<=0){
							url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
									"http://nga.178.com/read.php?pid=$1");
						}
						Intent intent = new Intent();
						intent.setData(Uri.parse(url));
						intent.setClass(view.getContext(), conf.articleActivityClass);
						view.getContext().startActivity(intent);
					}else{
						Toast.makeText(MainActivity.this, "����ĵ�ַ����NGA�İ���ַ�����ӵ�ַ,��ȱ��fid/pid/tid��Ϣ,���������",
								Toast.LENGTH_SHORT).show();
						urladd.setFocusable(true);
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		alert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return alert.show();
	}

	private AlertDialog add_fid_dialog() {
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.addfid_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle(R.string.addfid_title_hint);
		final EditText addfid_name = (EditText) view
				.findViewById(R.id.addfid_name);
		final EditText addfid_id = (EditText) view.findViewById(R.id.addfid_id);
		alert.setPositiveButton("���", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String name = addfid_name.getText().toString();
				String fid = addfid_id.getText().toString();
				if (name.equals("")) {
					Toast.makeText(MainActivity.this, "�������������",
							Toast.LENGTH_SHORT).show();
					addfid_name.setFocusable(true);
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {

					Pattern pattern = Pattern.compile("-{0,1}[0-9]*");
					Matcher match = pattern.matcher(fid);
					if (match.matches() == false || fid.equals("")) {
						addfid_id.setText("");
						addfid_id.setFocusable(true);
						Toast.makeText(MainActivity.this,
								"��������ȷ�İ���ID(���˰���Ҫ�Ӹ���)", Toast.LENGTH_SHORT)
								.show();
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {// CHECK PASS, READY TO ADD FID
						boolean FidAllreadyExist = false;
						int i = 0;
						for (i = 0; i < boardInfo.getCategoryCount(); i++) {
							BoardCategory curr = boardInfo.getCategory(i);
							for (int j = 0; j < curr.size(); j++) {
								String URL = curr.get(j).getUrl();
								if (URL.equals(fid)) {
									FidAllreadyExist = true;
									addfid_id.setText("");
									addfid_id.setFocusable(true);
									Toast.makeText(
											MainActivity.this,
											"�ð����Ѿ��������б�"
													+ boardInfo
															.getCategoryName(i)
													+ "��", Toast.LENGTH_SHORT)
											.show();
									try {
										Field field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, false);
									} catch (Exception e) {
										e.printStackTrace();
									}
									break;
								}
							}// for j
						}// for i
						if (!FidAllreadyExist) {
							addToaddFid(name, fid);
							pager.getAdapter().notifyDataSetChanged();
							// //add menu item
							// if(!isBoardExist("�û��Զ���")){
							// if(boardInfo.getCategoryCount()==12){
							// setLocItem(15,"�û��Զ���");
							// }
							// else{
							// setLocItem(16,"�û��Զ���");
							// }
							// }
							Toast.makeText(MainActivity.this, "��ӳɹ�",
									Toast.LENGTH_SHORT).show();
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

			}
		});
		alert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return alert.show();
	}

	// private boolean isBoardExist(String boardname){
	// for(int i =0; i < boardInfo.getCategoryCount(); i++){
	// //System.out.println(boardname+boardInfo.getCategoryName(i));
	// if(boardname.equals(boardInfo.getCategoryName(i))){
	// return true;
	// }
	// }
	// return false;
	// }
	private void addToaddFid(String Name, String Fid) {
		boolean addFidAlreadExist = false;
		BoardCategory addFid = null;
		int i = 0;
		for (i = 0; i < boardInfo.getCategoryCount(); i++) {
			if (boardInfo.getCategoryName(i).equals(getString(R.string.addfid))) {
				addFidAlreadExist = true;
				addFid = boardInfo.getCategory(i);
				break;
			}
			;
		}

		if (!addFidAlreadExist) {// û��
			List<Board> boardList = new ArrayList<Board>();
			Board b = new Board(i + 1, Fid, Name, R.drawable.pdefault);
			boardList.add(b);
			saveaddFid(boardList);
			boardInfo = loadDefaultBoard();
			// add menu item
			if (boardInfo.getCategoryCount() == 14) {
				setLocItem(17, "�û��Զ���");
			} else {
				setLocItem(18, "�û��Զ���");
			}
			return;
		} else {// ����
			Board b = new Board(i, Fid, Name, R.drawable.pdefault);
			addFid.add(b);
		}
		addFid = boardInfo.getCategory(i);
		this.saveaddFid(addFid.getBoardList());
		return;

	}

	private void saveaddFid(List<Board> boardList) {
		// TODO Auto-generated method stub

		String addFidStr = JSON.toJSONString(boardList);
		SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(ADD_FID, addFidStr);
		editor.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.mainmenu_login:
		// this.jumpToLogin();
		// break;
		// case R.id.mainmenu_setting:
		// this.jumpToSetting();
		// break;
		// case R.id.mainmenu_exit:
		// //case android.R.id.home: //this is a system id
		// //this.finish();
		// jumpToNearby();
		// break;
		// case R.id.add_fid:
		// add_fid_dialog();
		// break;
		default:
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_OPEN
					|| drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
			}
			if (drawerState == MenuDrawer.STATE_CLOSED
					|| drawerState == MenuDrawer.STATE_CLOSING) {
				mMenuDrawer.openMenu();
			}
			break;

		}
		return true;
	}

	private void initView() {

		setTitle(R.string.start_title);

		ThemeManager.SetContextTheme(this);
		view = LayoutInflater.from(this).inflate(R.layout.viewpager_main, null);
		view.setBackgroundResource(ThemeManager.getInstance()
				.getBackgroundColor());
		mMenuDrawer.setContentView(view);

		// left drawer
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
		mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
		mMenuDrawer.setDrawerIndicatorEnabled(true);

		pager = (ViewPager) findViewById(R.id.pager);

		if (app.isNewVersion()) {
			new AlertDialog.Builder(this).setTitle(R.string.prompt)
					.setMessage(StringUtil.getTips())
					.setPositiveButton(R.string.i_know, null).show();
			app.setNewVersion(false);

		}

	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	private void initDate() {

		new Thread() {
			public void run() {

				File filebase = new File(HttpUtil.PATH);
				if (!filebase.exists()) {
					delay(getString(R.string.create_cache_dir));
					filebase.mkdirs();
				}
				if (ActivityUtil.isGreaterThan_2_1()) {
					File f = new File(HttpUtil.PATH_AVATAR_OLD);
					if (f.exists()) {
						f.renameTo(new File(HttpUtil.PATH_AVATAR));
						delay(getString(R.string.move_avatar));
					}
				}

				File file = new File(HttpUtil.PATH_NOMEDIA);
				if (!file.exists()) {
					Log.i(getClass().getSimpleName(), "create .nomedia");
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();

	}

	/*
	 * public BoardCategory getCategory(int page){ if(this.boardInfo == null)
	 * return null; return boardInfo.getCategory(page); }
	 */
	private BoardHolder loadDefaultBoard() {

		MyApp app = (MyApp) getApplication();
		return app.loadDefaultBoard();

	}

	private BoardHolder boardInfo;

	private void delay(String text) {
		final String msg = text;
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT)
						.show();
			}

		});
	}

	class EnterToplistLintener implements OnItemClickListener, OnClickListener {
		int position;
		String fidString;

		public EnterToplistLintener(int position, String fidString) {
			super();
			this.position = position;
			this.fidString = fidString;
		}

		public EnterToplistLintener() {// constructoer
		}

		public void onClick(View v) {

			if (position != 0 && !HttpUtil.HOST_PORT.equals("")) {
				HttpUtil.HOST = HttpUtil.HOST_PORT + HttpUtil.Servlet_timer;
			}
			int fid = 0;
			try {
				fid = Integer.parseInt(fidString);
			} catch (Exception e) {
				final String tag = this.getClass().getSimpleName();
				Log.e(tag, Log.getStackTraceString(e));
				Log.e(tag, "invalid fid " + fidString);
			}
			if (fid == 0) {
				String tip = fidString + "�ӵ�һ���������";// �����ǰ�������Ҳ�֪���Ǹ����
				Toast.makeText(app, tip, Toast.LENGTH_LONG).show();
				return;
			}

			Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
					+ "&rss=1";
			PhoneConfiguration config = PhoneConfiguration.getInstance();
			if (!StringUtil.isEmpty(config.getCookie())) {

				url = url + "&" + config.getCookie().replace("; ", "&");
			} else if (fid < 0 && fid != -7) {
				jumpToLogin();
				return;
			}

			if (StringUtil.isEmpty(getSharedPreferences(PERFERENCE,
					Activity.MODE_PRIVATE).getString(RECENT_BOARD, ""))) {
				Intent intenta = getIntent();
				loadConfig(intenta);
				initView();
			}
			addToRecent();
			if (!StringUtil.isEmpty(url)) {
				Intent intent = new Intent();
				intent.putExtra("tab", "1");
				intent.putExtra("fid", fid);
				intent.setClass(MainActivity.this, config.topicActivityClass);
				// intent.setClass(MainActivity.this, TopicListActivity.class);
				startActivity(intent);
				if (PhoneConfiguration.getInstance().showAnimation) {
					overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
				}
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			position = arg2;
			fidString = (String) arg0.getItemAtPosition(position);
			onClick(arg1);

		}

		private void saveRecent(List<Board> boardList) {
			String rescentStr = JSON.toJSONString(boardList);
			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);
			Editor editor = share.edit();
			editor.putString(RECENT_BOARD, rescentStr);
			editor.commit();
		}

		private void addToRecent() {

			boolean recentAlreadExist = boardInfo.getCategoryName(0).equals(
					getString(R.string.recent));

			BoardCategory recent = boardInfo.getCategory(0);
			if (recent != null && recentAlreadExist)
				recent.remove(fidString);
			// int i = 0;
			for (int i = 0; i < boardInfo.getCategoryCount(); i++) {
				BoardCategory curr = boardInfo.getCategory(i);
				for (int j = 0; j < curr.size(); j++) {
					Board b = curr.get(j);
					if (b.getUrl().equals(fidString)) {
						Board b1 = new Board(0, b.getUrl(), b.getName(),
								b.getIcon());

						if (!recentAlreadExist) {
							List<Board> boardList = new ArrayList<Board>();
							boardList.add(b1);
							saveRecent(boardList);
							// add recent menu item
							setLocItem(5, "�������");
							// set menu click right
							ifRecentExist = 0;
							boardInfo = loadDefaultBoard();
							return;
						} else {
							recent.addFront(b1);
						}
						recent = boardInfo.getCategory(0);
						this.saveRecent(recent.getBoardList());

						return;
					}// if
				}// for j

			}// for i

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.onItemClickListenerlistener
				.onItemClick(parent, view, position, id);

	}

	@Override
	public int getCategoryCount() {
		if (boardInfo == null)
			return 0;
		return boardInfo.getCategoryCount();
	}

	@Override
	public String getCategoryName(int position) {
		if (boardInfo == null)
			return "";
		return boardInfo.getCategoryName(position);
	}

	@Override
	public BoardCategory getCategory(int category) {
		if (boardInfo == null)
			return null;
		return boardInfo.getCategory(category);
	}

}
