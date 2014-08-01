package gov.anzong.mediaplayer;

import io.vov.vitamio.LibsChecker;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ReceiveIntentActivity extends Activity {
	public String uri, title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		Intent intent = getIntent();
		uri = intent.getStringExtra("uri");
		title = intent.getStringExtra("title");
		if (!isEmpty(uri)) {
			if (isEmpty(title)) {
				title = "δ֪��Դ��Ƶ";
			}
			VideoActivity.openVideo(this, Uri.parse(uri), title);
			this.finish();
		} else {
			Toast.makeText(this, "��Ƶ��ַ����", Toast.LENGTH_SHORT).show();
			try {
				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e) {
			}
		}
	}

	/** �ж��Ƿ��� "" ���� null */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}
}
