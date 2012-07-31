package net.iksela.xbmc.companion.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonRpc {

	private final static String TAG = "API";

	private final static String JSONRPC_ID = "42";
	private final static String JSONRPC_VERSION = "2.0";

	public static class Request {
		protected JSONObject _json;
		protected Response _response = null;

		private void basicConstructor() throws JSONException {
			_json = new JSONObject();
			_json.put("id", JSONRPC_ID);
			_json.put("jsonrpc", JSONRPC_VERSION);
		}

		public Request() {
			try {
				basicConstructor();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public Request(String method) {
			try {
				basicConstructor();
				setMethod(method);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public Request(@SuppressWarnings("rawtypes") Enum method) {
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
				Log.v(TAG, "Preparing API method: " + method);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		protected void setParameters(JSONObject params) {
			try {
				_json.put("params", params);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public StringEntity getStringEntity() {
			try {
				String payload = _json.toString();
				Log.v(TAG, payload);
				return new StringEntity(payload);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
		
		public void setResponse(Response response) {
			this._response = response;
		}
		
		public Response send(XbmcConnection connection) {
			HttpPost post = connection.getHttpPost();
			if (post != null) {
				post.setEntity(this.getStringEntity());
				
				HttpClient client = connection.getHttpClient();

				ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
					public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
						StatusLine statusLine = response.getStatusLine();
						if (statusLine.getStatusCode() >= 300) {
							throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
						}

						HttpEntity entity = response.getEntity();
						return entity == null ? null : EntityUtils.toString(entity, "UTF-8");
					}
				};
				
				try {
					String responseBody = client.execute(post, responseHandler);
					Log.v(TAG, "Response: "+responseBody);
					Response r = new Response(responseBody);
					this._response = r;
					return r;
				} catch (ClientProtocolException e) {
					Log.e(TAG, "ClientProtocolException: "+e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, "IOException: "+e.getMessage());
				}
			}
			return null;
		}
		
	}

	public static class Response {

		public final static String RESULT = "result";
		protected JSONObject _json;

		public Response(String response) {
			try {
				_json = new JSONObject(response);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		private JSONObject getSimpleObjectResult() throws JSONException {
			return _json.getJSONObject(RESULT);
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

		public JSONObject getObjectResult(String name) {
			try {
				return getSimpleObjectResult().getJSONObject(name);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
		
		public int getIntFromResult(String propertyName) {
			try {
				return this.getSimpleObjectResult().getInt(propertyName);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return -1;
		}
		
		public int getIntFromObjectResult(String objectName, String propertyName) {
			try {
				return this.getObjectResult(objectName).getInt(propertyName);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return -1;
		}
		
		public String getStringFromObjectResult(String objectName, String propertyName) {
			try {
				return this.getObjectResult(objectName).getString(propertyName);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
		
		public JSONArray getArrayFromObjectResult(String objectName, String propertyName) {
			try {
				return this.getObjectResult(objectName).getJSONArray(propertyName);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
	}
}
