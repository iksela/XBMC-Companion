package net.iksela.xbmc.companion.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class XbmcApi {
	
	private final static String TAG = "API";
	
	public final static String VIDEO_TYPE_EPISODE = "episode";
	//public final static String VIDEO_TYPE_MOVIE = "movie";

	protected enum JSONRPC {
		Ping
	}

	protected enum Player {
		GetActivePlayers,
		GetItem,
		GetProperties
	}
	
	protected enum VideoLibrary {
		GetEpisodeDetails,
		GetTVShowDetails
	}
	
	public static class Ping extends JsonRpc.Request {
		public Ping() {
			super(XbmcApi.JSONRPC.Ping);
		}
		
		public boolean hasPong() {
			if (this._response != null) {
				return this._response.getStringResult().equals("pong");
			}
			return false;
		}
	}

	public static class GetActivePlayers extends JsonRpc.Request {
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
		
		public int getPlayerID() {
			JSONArray result = this._response.getArrayResult();
			if (result.length() > 0) {
				try {
					JSONObject object = result.getJSONObject(0);
					return object.getInt("playerid");
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
			return -1;
		}
	}
	
	public static class GetNowPlaying extends JsonRpc.Request {
		public GetNowPlaying(int playerID) {
			super(XbmcApi.Player.GetItem);
			try {
				JSONObject params = new JSONObject();
				params.put("playerid", playerID);
				setParameters(params);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		public int getItemID() {
			try {
				return _response.getObjectResult("item").getInt("id");
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return -1;
		}
		
		public String getItemType() {
			try {
				return _response.getObjectResult("item").getString("type");
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}
	}
	
	public static class GetEpisodeDetails extends JsonRpc.Request {
		private final static String RESULT = "episodedetails";
		
		public GetEpisodeDetails(int episodeID) {
			super(XbmcApi.VideoLibrary.GetEpisodeDetails);
			try {
				JSONArray details = new JSONArray();
				details.put("season");
				details.put("episode");
				details.put("tvshowid");
				details.put("fanart");
				
				JSONObject params = new JSONObject();
				params.put("episodeid", episodeID);
				params.put("properties", details);
				setParameters(params);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		public int getTVShowID() {
			return _response.getIntFromObjectResult(RESULT, "tvshowid");
		}
		
		public int getEpisode() {
			return _response.getIntFromObjectResult(RESULT, "episode");
		}
		
		public int getSeason() {
			return _response.getIntFromObjectResult(RESULT, "season");
		}
		
		public String getTitle() {
			return _response.getStringFromObjectResult(RESULT, "label");
		}
		
		public String getImageURL() {
			return _response.getStringFromObjectResult(RESULT, "fanart");
		}
	}
	
	public static class GetTVShowDetails extends JsonRpc.Request {
		private final static String RESULT = "tvshowdetails";
		
		public GetTVShowDetails(int tvshowID) {
			super(XbmcApi.VideoLibrary.GetTVShowDetails);
			try {			
				JSONObject params = new JSONObject();
				params.put("tvshowid", tvshowID);
				setParameters(params);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		public String getTitle() {
			return _response.getStringFromObjectResult(RESULT, "label");
		}
	}
}
