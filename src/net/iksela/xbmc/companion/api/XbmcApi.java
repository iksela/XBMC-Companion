package net.iksela.xbmc.companion.api;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class XbmcApi {
	
	public final static String JSONRPC_ID = "42";
	public final static String JSONRPC_VERSION = "2.0";
	
	public abstract static class JsonRpcRequest {
		protected JSONObject _json;
		
		public JsonRpcRequest() {
			_json = new JSONObject();
			
			try {
				_json.put("id", XbmcApi.JSONRPC_ID);
				_json.put("jsonrpc", XbmcApi.JSONRPC_VERSION);
			} catch (JSONException e) {
				Log.e("API", e.getMessage());
			}
		}
		
		public void setMethod(String method) {
			try {
				_json.put("method", method);
			} catch (JSONException e) {
				Log.e("API", e.getMessage());
			}
		}
		
		public StringEntity getStringEntity() {
			try {
				return new StringEntity(_json.toString());
			} catch (UnsupportedEncodingException e) {
				Log.e("API", e.getMessage());
			}
			return null;
		}
	}
	
	public static class JsonRpcResponse {
		
		public final static String RESULT = "result";
		protected JSONObject _json;
		
		public JsonRpcResponse(String response) {
			try {
				_json = new JSONObject(response);
			} catch (JSONException e) {
				Log.e("API", e.getMessage());
			}
		}
		
		public String getStringResult() {
			try {
				return _json.getString(RESULT);
			} catch (JSONException e) {
				Log.e("API", e.getMessage());
			}
			return null;
		}
	}

	public static class Ping extends JsonRpcRequest {

		public Ping() {
			super();
			setMethod("JSONRPC.Ping");
		}
	}
}
