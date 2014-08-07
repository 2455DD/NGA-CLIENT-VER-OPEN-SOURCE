package sp.phone.proxy;

import gov.anzong.androidnga.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public final class ProxyBridge {

	Context context;
	Toast toast;
	
	public ProxyBridge(Context ccontext,Toast mtoast) {
		// TODO Auto-generated constructor stub
		context=ccontext;
		toast=mtoast;
	}
	
	@JavascriptInterface
	public void postURL(String url) {
		ActivityUtil.getInstance().noticeSaying("�����ύ...", context);
		(new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPostExecute(String result) {
				ActivityUtil.getInstance().dismiss();
				if(StringUtil.isEmpty(result))
					result="δ֪����,������";
				if(result.startsWith("�����ɹ�"))
					result="�����ɹ�";
				if (toast != null) {
					toast.setText(result);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(context,
							result,
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}

			@Override
			protected String doInBackground(String... params) {
				if(StringUtil.isEmpty(params[0]))
					return "ѡ�����";
				String url="http://nga.178.com/nuke.php?"+params[0];
				HttpPostClient c =  new HttpPostClient(url);
				String cookie = PhoneConfiguration.getInstance().getCookie();
				c.setCookie(cookie);
				try {
					InputStream input = null;
					HttpURLConnection conn = c.post_body(params[0]);
					if(conn!=null){
						if (conn.getResponseCode() >= 500) 
						{
							input = null;
						}
						else{
							if(conn.getResponseCode() >= 400)
							{
								input = conn.getErrorStream();
		                    }
							else
								input = conn.getInputStream();
						}
					}else{
						return "�������";
					}

					if(input != null)
					{
					String js = IOUtils.toString(input, "gbk");
					if (null == js) {
						return context.getString(R.string.network_error);
					}
					js = js.replaceAll("window.script_muti_get_var_store=", "");
					JSONObject o = null, oerror = null;
					try {
						o = (JSONObject) JSON.parseObject(js).get("data");
						oerror = (JSONObject) JSON.parseObject(js).get("error");
					} catch (Exception e) {
						Log.e("ProxyBridge", "can not parse :\n" + js);
					}
					if (o == null) {
						if (oerror == null) {
							return "�����µ�¼";
						}else {
							if (!StringUtil.isEmpty(oerror.getString("0"))) {
								return oerror.getString("0");
							}else{
								return "�����ֿ�ʼ�Ҹ���";
							}
						}
					}else{
						if (!StringUtil.isEmpty(o.getString("0"))) {
						return o.getString("0");
						}else{
						return "�����ֿ�ʼ�Ҹ���";
						}
					}
					}else{
						return "�������÷�������ëƬ";
					}
				} catch (IOException e) {
				}
				return "";
			}
		}).execute(url);
	}

}
