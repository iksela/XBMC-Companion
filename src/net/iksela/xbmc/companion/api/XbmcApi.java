package net.iksela.xbmc.companion.api;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class XbmcApi {
	
	private final static String TAG = "API";

	private final static String JSONRPC_ID = "42";
	private final static String JSONRPC_VERSION = "2.0";

	protected enum JSONRPC {
		Ping
	}

	protected enum Player {
		GetActivePlayers
	}

	protected static class JsonRpcRequest {
		protected JSONObject _json;
		protected JsonRpcResponse _response = null;

		private void basicConstructor() throws JSONException {
			_json = new JSONObject();
			_json.put("id", XbmcApi.JSONRPC_ID);
			_json.put("jsonrpc", XbmcApi.JSONRPC_VERSION);
		}

		public JsonRpcRequest() {
			try {
				basicConstructor();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public JsonRpcRequest(String method) {
			try {
				basicConstructor();
				setMethod(method);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public JsonRpcRequest(@SuppressWarnings("rawtypes") Enum method) {
			try {
				basicConstructor();
				setMethod(method.getDeclaringClass().getSimpleName() + "." + method.name());
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		private void setMethod(String method) {
			try {
				_json.put("method", method);
				Log.v(TAG, "Prepared API method: " + method);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public StringEntity getStringEntity() {
			try {
				return new StringEntity(_json.toString());
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
		
		public void setResponse(JsonRpcResponse response) {
			this._response = response;
		}
	}

	
	public static class JsonRpcResponse {

		public final static String RESULT = "result";
		protected JSONObject _json;

		public JsonRpcResponse(String response) {
			try {
				_json = new JSONObject(response);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public String getStringResult() {
			try {
				return _json.getString(RESULT);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
		
		public JSONArray getArrayResult() {
			try {
				return _json.getJSONArray(RESULT);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
	}

	public static class Ping extends JsonRpcRequest {
		public Ping() {
			super(XbmcApi.JSONRPC.Ping);
		}
		
		public boolean hasPong() {
			return this._response.getStringResult().equals("pong");
		}
	}

	public static class GetActivePlayers extends JsonRpcRequest {
		public GetActivePlayers() {
			super(XbmcApi.Player.GetActivePlayers);
		}
		
		public boolean hasVideoPlayer() {
			JSONArray result = this._response.getArrayResult();
			if (result.length() > 0) {
				try {
					JSONObject object = result.getJSONObject(0);
					return object.getString("type").equals("video");
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			return false;
		}
	}
}
