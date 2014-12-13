package sp.phone.listener;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MyListenerForClient implements OnClickListener {
	int mPosition;
	ThreadData mdata = new ThreadData();
	Context mcontext;
	View mscrollview;

	public MyListenerForClient(int inPosition, ThreadData data,Context context,View scrollview) {
		mPosition = inPosition;
		mdata = data;
		mcontext = context;
		mscrollview=scrollview;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {

		ThreadRowInfo row = mdata.getRowList().get(mPosition);
		String from_client = row.getFromClient();
		String client_model = row.getFromClientModel();
		String deviceinfo = null;
		if (!StringUtil.isEmpty(client_model)) {
			String clientappcode="";
			if(from_client.indexOf(" ")<0){
				clientappcode = from_client;
			}else{
				clientappcode= from_client.substring(0,
					from_client.indexOf(" "));
			} 
			if (clientappcode.equals("1")) {
				if (from_client.length() <= 2) {
					deviceinfo = "������Life Styleƻ���ͻ��� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "������Life Styleƻ���ͻ��� ���ͼ�ϵͳ:"
							+ from_client.substring(2);
				}
			} else if (clientappcode.equals("7")) {
				if (from_client.length() <= 2) {
					deviceinfo = "������NGAƻ���ٷ��ͻ��� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "������NGAƻ���ٷ��ͻ��� ���ͼ�ϵͳ:"
							+ from_client.substring(2);
				}
			} else if (clientappcode.equals("8")) {
				if (from_client.length() <= 2) {
					deviceinfo = "������NGA��׿�ͻ��� ���ͼ�ϵͳ:δ֪";
				} else {
					String fromdata = from_client.substring(2);
					if (fromdata.startsWith("[")
							&& fromdata.indexOf("](Android") > 0) {
						deviceinfo = "������NGA��׿��Դ��ͻ��� ���ͼ�ϵͳ:"
								+ fromdata.substring(1).replace(
										"](Android", "(Android");
					} else {
						deviceinfo = "������NGA��׿�ٷ��ͻ��� ���ͼ�ϵͳ:" + fromdata;
					}
				}
			} else if (clientappcode.equals("9")) {
				if (from_client.length() <= 2) {
					deviceinfo = "������NGA Windows Phone�ٷ��ͻ��� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "������NGA Windows Phone�ٷ��ͻ��� ���ͼ�ϵͳ:"
							+ from_client.substring(2);
				}
			} else if (clientappcode.equals("100")) {
				if (from_client.length() <= 4) {
					deviceinfo = "�����԰�׿����� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "�����԰�׿����� ���ͼ�ϵͳ:"
							+ from_client.substring(4);
				}
			} else if (clientappcode.equals("101")) {
				if (from_client.length() <= 4) {
					deviceinfo = "������ƻ������� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "������ƻ������� ���ͼ�ϵͳ:"
							+ from_client.substring(4);
				}
			} else if (clientappcode.equals("102")) {
				if (from_client.length() <= 4) {
					deviceinfo = "������Blackberry����� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "������Blackberry����� ���ͼ�ϵͳ:"
							+ from_client.substring(4);
				}
			} else if (clientappcode.equals("103")) {
				if (from_client.length() <=4) {
					deviceinfo = "������Windows Phone�ͻ��� ���ͼ�ϵͳ:δ֪";
				} else {
					deviceinfo = "������Windows Phone�ͻ��� ���ͼ�ϵͳ:"
							+ from_client.substring(4);
				}
			} else {
				if(from_client.indexOf(" ")<0){
					deviceinfo = "������δ֪����� ���ͼ�ϵͳ:δ֪";
				}else{
					if (from_client.length() == (from_client.indexOf(" ") + 1)) {
						deviceinfo = "������δ֪����� ���ͼ�ϵͳ:δ֪";
					} else {
						deviceinfo = "������δ֪����� ���ͼ�ϵͳ:"
								+ from_client.substring(from_client
										.indexOf(" ") + 1);
					}
				}
			}
			final Dialog dialog = new Dialog(mcontext,
					R.style.ClientDialog);
			dialog.setContentView(R.layout.client_dialog);
			TextView textview = (TextView) dialog
					.findViewById(R.id.client_device_dialog);
			textview.setText(deviceinfo);

			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();

			WindowManager wm = (WindowManager) mcontext
					.getSystemService(Context.WINDOW_SERVICE);
			lp.width = (int) (wm.getDefaultDisplay().getWidth()); // ���ÿ��
			dialog.getWindow().setAttributes(lp);
			dialog.show();
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					if (PhoneConfiguration.getInstance().fullscreen) {
						ActivityUtil.getInstance()
								.setFullScreen(mscrollview);
					}
					dialog.dismiss();
				}

			});
			dialog.setCanceledOnTouchOutside(true);
		}

	}
}
