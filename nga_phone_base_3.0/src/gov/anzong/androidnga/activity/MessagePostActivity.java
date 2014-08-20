package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.RelativeSizeSpan;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SettingsActivity.FontSizeListener;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.adapter.SpinnerUserListAdapter;
import sp.phone.bean.User;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.MessagePostAction;
import sp.phone.forumoperation.ThreadPostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.fragment.EmotionDialogFragment;
import sp.phone.fragment.ExtensionEmotionFragment;
import sp.phone.fragment.SearchDialogFragment;
import sp.phone.interfaces.EmotionCategorySelectedListener;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.task.FileUploadTask;
import sp.phone.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class MessagePostActivity extends SwipeBackAppCompatActivity implements
		FileUploadTask.onFileUploaded, EmotionCategorySelectedListener,
		OnEmotionPickedListener {

	private final String LOG_TAG = Activity.class.getSimpleName();
	static private final String EMOTION_CATEGORY_TAG = "emotion_category";
	static private final String EMOTION_TAG = "emotion";
	private String prefix;
	private EditText titleText;
	private EditText toText;
	private EditText bodyText;
	private MessagePostAction act;
	private String action;
	private int mid;
	private String tousername;
	// private Button button_commit;
	// private Button button_cancel;
	// private ImageButton button_upload;
	// private ImageButton button_emotion;
	Object commit_lock = new Object();
	private Spinner userList;
	private String REPLY_URL = "http://nga.178.com/nuke.php?";
	final int REQUEST_CODE_SELECT_PIC = 1;
	private View v;
	private boolean loading;
	private FileUploadTask uploadTask = null;
	private Toast toast = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		int orentation = ThemeManager.getInstance().screenOrentation;
		if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(orentation);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		super.onCreate(savedInstanceState);
		v = this.getLayoutInflater().inflate(R.layout.messagereply, null);
		v.setBackgroundColor(getResources().getColor(
				ThemeManager.getInstance().getBackgroundColor()));
		this.setContentView(v);

		if (PhoneConfiguration.getInstance().uploadLocation
				&& PhoneConfiguration.getInstance().location == null) {
			ActivityUtil.reflushLocation(this);
		}

		Intent intent = this.getIntent();
		prefix = intent.getStringExtra("prefix");
		// if(prefix!=null){
		// prefix=prefix.replaceAll("\\n\\n", "\n");
		// }
		tousername = intent.getStringExtra("to");
		action = intent.getStringExtra("action");
		mid = intent.getIntExtra("mid", 0);
		String title = intent.getStringExtra("title");

		titleText = (EditText) findViewById(R.id.reply_titile_edittext);
		toText = (EditText) findViewById(R.id.reply_titile_edittext_to);
		bodyText = (EditText) findViewById(R.id.reply_body_edittext);

		if (action.equals("new")) {
			getSupportActionBar().setTitle(R.string.new_message);
		} else if (action.equals("reply")) {
			getSupportActionBar().setTitle(R.string.reply_message);
		}
		titleText.setSelected(true);

		act = new MessagePostAction(mid, "", "");
		act.setAction_(action);
		this.act.set__ngaClientChecksum(FunctionUtil.getngaClientChecksum(this));
		loading = false;

		if (!StringUtil.isEmpty(tousername)) {
			toText.setText(tousername);
			if (!StringUtil.isEmpty(title)) {
				titleText.setText(title);
			}
		} else {
			if (!StringUtil.isEmpty(title)) {
				titleText.setText(title);
			}
		}
		if (prefix != null) {
			if (prefix.startsWith("[quote][pid=")
					&& prefix.endsWith("[/quote]\n")) {
				SpannableString spanString = new SpannableString(prefix);
				spanString.setSpan(new BackgroundColorSpan(-1513240), 0,
						prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				spanString.setSpan(
						new StyleSpan(android.graphics.Typeface.BOLD),
						prefix.indexOf("[b]Post by"),
						prefix.indexOf("):[/b]") + 5,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				bodyText.append(spanString);
			} else {
				bodyText.append(prefix);
			}
			bodyText.setSelection(prefix.length());
		}
		ThemeManager tm = ThemeManager.getInstance();
		if (tm.getMode() == ThemeManager.MODE_NIGHT) {
			bodyText.setBackgroundResource(tm.getBackgroundColor());
			toText.setBackgroundResource(tm.getBackgroundColor());
			titleText.setBackgroundResource(tm.getBackgroundColor());
			int textColor = this.getResources().getColor(
					tm.getForegroundColor());
			bodyText.setTextColor(textColor);
			titleText.setTextColor(textColor);
			toText.setTextColor(textColor);
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag >= 4) {// ���ڵ���4�϶���
				getMenuInflater().inflate(R.menu.messagepost_menu_left, menu);
			} else {
				getMenuInflater().inflate(R.menu.messagepost_menu, menu);
			}
		} else {
			getMenuInflater().inflate(R.menu.messagepost_menu, menu);
		}
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		/*
		 * ActionBar.DISPLAY_SHOW_HOME;//2 flags |=
		 * ActionBar.DISPLAY_USE_LOGO;//1 flags |=
		 * ActionBar.DISPLAY_HOME_AS_UP;//4
		 */
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return true;
	}

	private ButtonCommitListener commitListener = null;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.upload:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
			break;
		case R.id.emotion:
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			Fragment prev = getSupportFragmentManager().findFragmentByTag(
					EMOTION_CATEGORY_TAG);
			if (prev != null) {
				ft.remove(prev);
			}

			DialogFragment newFragment = new EmotionCategorySelectFragment();
			newFragment.show(ft, EMOTION_CATEGORY_TAG);
			break;
		case R.id.supertext:
			FunctionUtil.handleSupertext(bodyText, this, v);
			break;
		case R.id.send:
			if (StringUtil.isEmpty(toText.getText().toString())) {
				if (toast != null) {
					toast.setText("�������ռ���");
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(MessagePostActivity.this, "�������ռ���",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} else if (StringUtil.isEmpty(titleText.getText().toString())) {
				if (toast != null) {
					toast.setText("���������");
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(MessagePostActivity.this, "���������",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} else if (StringUtil.isEmpty(bodyText.getText().toString())) {
				if (toast != null) {
					toast.setText("����������");
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(MessagePostActivity.this, "����������",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				if (commitListener == null) {
					commitListener = new ButtonCommitListener(REPLY_URL);
				}
				commitListener.onClick(null);
			}
			break;
		default:
			finish();
		}
		return true;
	}// OK

	@Override
	public void onEmotionPicked(String emotion) {
		final int index = bodyText.getSelectionStart();
		String urltemp = emotion.replaceAll("\\n", "");
		if (urltemp.indexOf("http") > 0) {
			urltemp = urltemp.substring(5, urltemp.length() - 6);
			String sourcefile = ExtensionEmotionAdapter.getPathByURI(urltemp);
			InputStream is = null;
			try {
				is = getResources().getAssets().open(sourcefile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (is != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				Drawable drawable = (Drawable) bd;
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				SpannableString spanString = new SpannableString(emotion);
				ImageSpan span = new ImageSpan(drawable,
						ImageSpan.ALIGN_BASELINE);
				spanString.setSpan(span, 0, emotion.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (bodyText.getText().toString().replaceAll("\\n", "").trim()
						.equals("")) {
					bodyText.append(spanString);
				} else {
					if (index <= 0 || index >= bodyText.length()) {// pos @
																	// begin /
																	// end
						bodyText.append(spanString);
					} else {
						bodyText.getText().insert(index, spanString);
					}
				}
			}
		} else {
			int[] emotions = { 1, 2, 3, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34,
					35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 5, 6, 7, 8 };
			for (int i = 0; i < 27; i++) {
				if (emotion.indexOf("[s:" + String.valueOf(emotions[i]) + "]") == 0) {
					String sourcefile = "a" + String.valueOf(emotions[i])
							+ ".gif";
					InputStream is = null;
					try {
						is = getResources().getAssets().open(sourcefile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (is != null) {
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						BitmapDrawable bd = new BitmapDrawable(bitmap);
						Drawable drawable = (Drawable) bd;
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
								drawable.getIntrinsicHeight());
						SpannableString spanString = new SpannableString(
								emotion);
						ImageSpan span = new ImageSpan(drawable,
								ImageSpan.ALIGN_BASELINE);
						spanString.setSpan(span, 0, emotion.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						if (index <= 0 || index >= bodyText.length()) {// pos @
																		// begin
																		// / end
							bodyText.append(spanString);
						} else {
							bodyText.getText().insert(index, spanString);
						}
					} else {
						bodyText.append(emotion);
					}
					break;
				}
			}
		}
	}// OK

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED || data == null)
			return;
		switch (requestCode) {
		case REQUEST_CODE_SELECT_PIC:
			Log.i(LOG_TAG, " select file :" + data.getDataString());
			uploadTask = new FileUploadTask(this, this, data.getData());
			break;
		default:
			;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		if (action.equals("new")) {
			if (StringUtil.isEmpty(toText.getText().toString())) {
				toText.requestFocus();
			} else {
				titleText.requestFocus();
			}
		} else {
			bodyText.requestFocus();
		}
		if (uploadTask != null) {
			FileUploadTask temp = uploadTask;
			uploadTask = null;
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				RunParallel(temp);
			} else {
				temp.execute();
			}
		}
		if (PhoneConfiguration.getInstance().fullscreen) {
			ActivityUtil.getInstance().setFullScreen(v);
		}
		super.onResume();
	}

	@TargetApi(11)
	private void RunParallel(FileUploadTask task) {
		task.executeOnExecutor(FileUploadTask.THREAD_POOL_EXECUTOR);
	}

	class ButtonCommitListener implements OnClickListener {

		private final String url;

		ButtonCommitListener(String url) {
			this.url = url;
		}

		@Override
		public void onClick(View v) {
			synchronized (commit_lock) {
				if (loading == true) {
					String avoidWindfury = MessagePostActivity.this
							.getString(R.string.avoidWindfury);
					if (toast != null) {
						toast.setText(avoidWindfury);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(MessagePostActivity.this,
								avoidWindfury, Toast.LENGTH_SHORT);
						toast.show();
					}
					return;
				}
				loading = true;
			}
			if (action.equals("reply")) {
				handleReply(v);
			} else if (action.equals("new")) {
				handleNewThread(v);
			}
		}

		public void handleNewThread(View v) {
			handleReply(v);

		}

		public void handleReply(View v1) {

			act.setTo_(toText.getText().toString());
			act.setPost_subject_(titleText.getText().toString());
			if (bodyText.getText().toString().length() > 0) {
				act.setPost_content_(FunctionUtil.ColorTxtCheck(bodyText
						.getText().toString()));
				new MessagePostTask(MessagePostActivity.this).execute(url,
						act.toString());
			}

		}

	}

	private class MessagePostTask extends AsyncTask<String, Integer, String> {

		final Context c;
		private final String result_start_tag = "\"0\":\"";
		private final String result_end_tag = "\"";
		private boolean keepActivity = false;

		public MessagePostTask(Context context) {
			super();
			this.c = context;
		}

		@Override
		protected void onPreExecute() {
			ActivityUtil.getInstance().noticeSaying(c);
			super.onPreExecute();
		}

		@Override
		protected void onCancelled() {
			synchronized (commit_lock) {
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected void onCancelled(String result) {
			synchronized (commit_lock) {
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected String doInBackground(String... params) {
			if (params.length < 2)
				return "parameter error";
			String ret = "�������";
			String url = params[0];
			String body = params[1];

			HttpPostClient c = new HttpPostClient(url);
			String cookie = PhoneConfiguration.getInstance().getCookie();
			c.setCookie(cookie);
			try {
				InputStream input = null;
				HttpURLConnection conn = c.post_body(body);
				if (conn != null) {
					if (conn.getResponseCode() >= 500) {
						input = null;
						keepActivity = true;
						ret = "�������÷�������ëƬ";
					} else {
						if (conn.getResponseCode() >= 400) {
							input = conn.getErrorStream();
							keepActivity = true;
						} else
							input = conn.getInputStream();
					}
				} else
					keepActivity = true;

				if (input != null) {
					String html = IOUtils.toString(input, "gbk");
					ret = getReplyResult(html);
				} else
					keepActivity = true;
			} catch (IOException e) {
				keepActivity = true;
				Log.e(LOG_TAG, Log.getStackTraceString(e));

			}
			return ret;
		}

		private String getReplyResult(String js) {
			if (null == js) {
				return "����ʧ��";
			}
			js = js.replaceAll("window.script_muti_get_var_store=", "");
			if (js.indexOf("/*error fill content") > 0)
				js = js.substring(0, js.indexOf("/*error fill content"));
			js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
			js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
			js = js.replaceAll("/\\*\\$js\\$\\*/", "");
			JSONObject o = null;
			try {
				o = (JSONObject) JSON.parseObject(js).get("data");
			} catch (Exception e) {
				Log.e("TAG", "can not parse :\n" + js);
			}
			if (o == null) {
				try {
					o = (JSONObject) JSON.parseObject(js).get("error");
				} catch (Exception e) {
					Log.e("TAG", "can not parse :\n" + js);
				}
				if (o == null) {
					return "����ʧ��";
				}
				return o.getString("0");
			}
			return o.getString("0");
		}

		@Override
		protected void onPostExecute(String result) {
			String success_results[] = { "������� ...", " @����ÿ24Сʱ���ܳ���50��", "�����ɹ�" };
			if (keepActivity == false) {
				boolean success = false;
				for (int i = 0; i < success_results.length; ++i) {
					if (result.contains(success_results[i])) {
						success = true;
						break;
					}
				}
				if (!success)
					keepActivity = true;
			}
			if (toast != null) {
				toast.setText(result);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(MessagePostActivity.this, result,
						Toast.LENGTH_SHORT);
				toast.show();
			}
			ActivityUtil.getInstance().dismiss();
			if (!keepActivity) {
				if (!action.equals("new")) {
					MessagePostActivity.this.setResult(123);
				} else {
					MessagePostActivity.this.setResult(321);
				}
				MessagePostActivity.this.finish();
			}
			synchronized (commit_lock) {
				loading = false;
			}

			super.onPostExecute(result);
		}

	}

	@Override
	public int finishUpload(String attachments, String attachmentsCheck,
			String picUrl, Uri uri) {
		String selectedImagePath2 = FunctionUtil.getPath(this, uri);
		final int index = bodyText.getSelectionStart();
		this.act.appendAttachments_(attachments);
		act.appendAttachments_check_(attachmentsCheck);
		String spantmp = "[img]./" + picUrl + "[/img]";
		if (!StringUtil.isEmpty(selectedImagePath2)) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2,
					options); // ��ʱ���� bm Ϊ��
			options.inJustDecodeBounds = false;
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenwidth = (int) (dm.widthPixels * 0.75);
			int screenheigth = (int) (dm.heightPixels * 0.75);
			int width = options.outWidth;
			int height = options.outHeight;
			float scaleWidth = ((float) screenwidth) / width;
			float scaleHeight = ((float) screenheigth) / height;
			if (scaleWidth < scaleHeight && scaleWidth < 1f) {// ���ܷŴ�,Ȼ����Ҫ���ĸ�С���ŵ��ĸ�������
				options.inSampleSize = (int) (1 / scaleWidth);
			} else if (scaleWidth >= scaleHeight && scaleHeight < 1f) {
				options.inSampleSize = (int) (1 / scaleHeight);
			} else {
				options.inSampleSize = 1;
			}
			bitmap = BitmapFactory.decodeFile(selectedImagePath2, options);
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			Drawable drawable = (Drawable) bd;
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			SpannableString spanStringS = new SpannableString(spantmp);
			ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
			spanStringS.setSpan(span, 0, spantmp.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			if (bodyText.getText().toString().replaceAll("\\n", "").trim()
					.equals("")) {// NO INPUT DATA
				bodyText.append(spanStringS);
				bodyText.append("\n");
			} else {
				if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
																// end
					if (bodyText.getText().toString().endsWith("\n")) {
						bodyText.append(spanStringS);
						bodyText.append("\n");
					} else {
						bodyText.append("\n");
						bodyText.append(spanStringS);
						bodyText.append("\n");
					}
				} else {
					bodyText.getText().insert(index, spanStringS);
				}
			}
		} else {
			if (bodyText.getText().toString().replaceAll("\\n", "").trim()
					.equals("")) {// NO INPUT DATA
				bodyText.append("[img]./" + picUrl + "[/img]\n");
			} else {
				if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
																// end
					if (bodyText.getText().toString().endsWith("\n")) {
						bodyText.append("[img]./" + picUrl + "[/img]\n");
					} else {
						bodyText.append("\n[img]./" + picUrl + "[/img]\n");
					}
				} else {
					bodyText.getText().insert(index,
							"[img]./" + picUrl + "[/img]");
				}
			}
		}
		InputMethodManager imm = (InputMethodManager) bodyText.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		return 1;
	}

	@Override
	public void onEmotionCategorySelected(int category) {
		final FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		final Fragment categoryFragment = getSupportFragmentManager()
				.findFragmentByTag(EMOTION_CATEGORY_TAG);
		if (categoryFragment != null)
			ft.remove(categoryFragment);
		ft.commit();

		ft = fm.beginTransaction();
		final Fragment prev = getSupportFragmentManager().findFragmentByTag(
				EMOTION_TAG);
		if (prev != null) {
			ft.remove(prev);
		}

		DialogFragment newFragment = null;
		switch (category) {
		case CATEGORY_BASIC:
			newFragment = new EmotionDialogFragment();
			break;
		case CATEGORY_BAOZOU:
		case CATEGORY_XIONGMAO:
		case CATEGORY_TAIJUN:
		case CATEGORY_ALI:
		case CATEGORY_DAYANMAO:
		case CATEGORY_LUOXIAOHEI:
		case CATEGORY_MAJIANGLIAN:
		case CATEGORY_ZHAIYIN:
		case CATEGORY_YANGCONGTOU:
		case CATEGORY_ACNIANG:
		case CATEGORY_BIERDE:
		case CATEGORY_LINDABI:
		case CATEGORY_QUNIANG:
		case CATEGORY_NIWEIHEZHEMEDIAO:
		case CATEGORY_PST:
			Bundle args = new Bundle();
			args.putInt("index", category - 1);
			newFragment = new ExtensionEmotionFragment();
			newFragment.setArguments(args);
			break;
		default:

		}
		// ft.commit();
		// ft.addToBackStack(null);

		if (newFragment != null) {
			ft.commit();
			newFragment.show(fm, EMOTION_TAG);
		}

	}

}