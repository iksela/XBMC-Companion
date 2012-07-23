package net.iksela.xbmc.companion.api;

import java.io.InputStream;

import net.iksela.xbmc.companion.SettingsProvider;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class XbmcConnection {

	private final static String TAG = "NET";
	
	private final static String URL_JSON = "jsonrpc";
	private final static String URL_VFS = "vfs/";

	private HttpClient _client;

	private SettingsProvider _settings;

	public XbmcConnection(Context ctx) {
		this._settings = new SettingsProvider(ctx);

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		HttpConnectionParams.setSoTimeout(params, 1000);
		this._client = new DefaultHttpClient(params);
	}

	public HttpClient getHttpClient() {
		return _client;
	}

	public HttpPost getHttpPost() {
		String ip = _settings.getIP();
		if (ip != null) {
			String url = "http://" + ip + ":" + _settings.getPort() + "/" + URL_JSON;
			HttpPost httppost = new HttpPost(url);
			
			Log.v(TAG, "POST URL: " + url);
			return httppost;
		}
		return null;
	}
	
	public Bitmap getImage(String vfsFile) {
		Bitmap img = null;
		String url = "http://" + _settings.getIP() + ":" + _settings.getPort() + "/" + URL_VFS + vfsFile;
		HttpGet get = new HttpGet(url);
		HttpClient client = getHttpClient();
		try {
			InputStream is = client.execute(get).getEntity().getContent();
			img = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return img;
	}
}
