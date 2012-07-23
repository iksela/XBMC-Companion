package net.iksela.xbmc.companion.api;

import net.iksela.xbmc.companion.SettingsProvider;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

public class XbmcConnection {

	private final static String TAG = "NET";

	// private Context _context;

	private HttpClient _client;

	private SettingsProvider _settings;
	
	private int _playerID = -1;
	private int _videoID = -1;

	public XbmcConnection(Context ctx) {
		// this._context = ctx;
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
			String url = "http://" + ip + ":" + _settings.getPort() + "/jsonrpc";
			HttpPost httppost = new HttpPost(url);
			
			Log.i(TAG, "POST URL: " + url);
			return httppost;
		}
		return null;
	}

	private JsonRpc.Response send(JsonRpc.Request request) {
		HttpPost post = getHttpPost();
		if (post != null) {
			post.setEntity(request.getStringEntity());
			HttpClient client = getHttpClient();
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
	
			try {
				String responseBody = client.execute(post, responseHandler);
				Log.v(TAG, responseBody);
				return new JsonRpc.Response(responseBody);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return null;
	}

	public boolean isReachable() {
		XbmcApi.Ping ping = new XbmcApi.Ping();
		ping.setResponse(this.send(ping));
		return ping.hasPong();
	}

	/*
	 * JsonRpc.Response response = this.send(ping); if (response != null) {
	 * return response.getStringResult().equals("pong"); } return false;
	 */

	/*
	 * JSONObject json = new JSONObject(); try { json.put("id", "1");
	 * json.put("jsonrpc", "2.0"); json.put("method", "JSONRPC.Ping");
	 * 
	 * StringEntity entity = new StringEntity(json.toString());
	 * post.setEntity(entity); HttpClient client = initHttpClient();
	 * 
	 * ResponseHandler<String> responseHandler = new BasicResponseHandler();
	 * String responseBody = client.execute(post, responseHandler);
	 * Log.i("pwet", responseBody);
	 * 
	 * JSONObject pong = new JSONObject(responseBody); if
	 * (pong.getString("result").equals("pong")) { return true; }
	 * 
	 * } catch (Exception e) { Log.e("pwet", e.getMessage()); }
	 */

	public boolean hasVideoPlayer() {
		XbmcApi.GetActivePlayers gap = new XbmcApi.GetActivePlayers();
		gap.setResponse(this.send(gap));
		if (gap.hasVideoPlayer()) {
			this._playerID = gap.getPlayerID();
			return true;
		}
		return false;
	}
	
	public String getVideoType() {
		XbmcApi.GetNowPlaying request = new XbmcApi.GetNowPlaying(this._playerID);
		request.setResponse(this.send(request));
		this._videoID = request.getItemID();
		return request.getItemType();
	}

}
