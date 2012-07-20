package net.iksela.xbmc.companion;

import net.iksela.xbmc.companion.api.XbmcApi;

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

	private Context _context;

	public XbmcConnection(Context ctx) {
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
	
	private XbmcApi.JsonRpcResponse send(XbmcApi.JsonRpcRequest request) {
		HttpPost post = initHttpPost();
		post.setEntity(request.getStringEntity());
		HttpClient client = initHttpClient();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		try {
			String responseBody = client.execute(post, responseHandler);
			Log.i("NET", responseBody);
			return new XbmcApi.JsonRpcResponse(responseBody);
		} catch (Exception e) {
			Log.e("pwet", e.getMessage());
		}
		return null;
	}

	public boolean isReachable() {
		Log.i("pwet", "isReachable?");
		XbmcApi.Ping ping = new XbmcApi.Ping();
		XbmcApi.JsonRpcResponse response = this.send(ping);
		if (response != null) {
			return response.getStringResult().equals("pong");
		}
		return false;
		
		/*
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
		*/
	}
}
