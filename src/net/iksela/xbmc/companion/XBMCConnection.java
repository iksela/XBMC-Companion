package net.iksela.xbmc.companion;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class XBMCConnection {

	private Context _context;

	public XBMCConnection(Context ctx) {
		this._context = ctx;
	}

	private HttpClient initHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		HttpConnectionParams.setSoTimeout(params, 1000);

		return new DefaultHttpClient(params);
	}

	private HttpPost initHttpPost() {
		SettingsProvider settings = new SettingsProvider(_context);
		String url = "http://" + settings.getIP() + ":" + settings.getPort() + "/jsonrpc";
		HttpPost httppost = new HttpPost(url);

		Log.i("pwet", "Preparing POST URL: "+url);
		return httppost;
	}

	public boolean isReachable() {
		Log.i("pwet", "isReachable?");
		HttpPost post = initHttpPost();
		JSONObject json = new JSONObject();
		try {
			json.put("id", "1");
			json.put("jsonrpc", "2.0");
			json.put("method", "JSONRPC.Ping");

			StringEntity entity = new StringEntity(json.toString());
			post.setEntity(entity);
			HttpClient client = initHttpClient();

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = client.execute(post, responseHandler);
			Log.i("pwet", responseBody);
			
			JSONObject pong = new JSONObject(responseBody);
			if (pong.getString("result").equals("pong")) {
				return true;
			}
			
		} catch (Exception e) {
			Log.e("pwet", e.getMessage());
		}
		/*
		 * URL url = new URL("http://www.android.com/"); HttpURLConnection
		 * httpCon = (HttpURLConnection) url.openConnection(); InputStream in =
		 * httpCon.getInputStream();
		 * 
		 * byte content[] = new byte[in.available()]; in.read(content, 0,
		 * content.length); String receivedString = new String(content);
		 * Log.d("Received Stringggggg", receivedString);
		 */
		return false;
	}
}
