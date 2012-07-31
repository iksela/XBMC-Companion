package net.iksela.xbmc.companion.api;

import java.util.ArrayList;
import java.util.List;

import net.iksela.xbmc.companion.data.Actor;
import net.iksela.xbmc.companion.data.Episode;
import net.iksela.xbmc.companion.data.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class XbmcApi {
	
	private final static String TAG = "API";
	
	public final static String VIDEO_TYPE_EPISODE = "episode";
	public final static String VIDEO_TYPE_MOVIE = "movie";
	
	public final static int PLAYER_PLAYING = 1;
	public final static int PLAYER_PAUSED = 0;
	
	private XbmcConnection _connection;
	
	private int _playerID = -42;
	private int _videoID = -42;
	private String _videoType; 

	protected enum JSONRPC {
		Ping
	}

	protected enum Player {
		GetActivePlayers,
		GetItem,
		GetProperties,
		PlayPause
	}
	
	protected enum VideoLibrary {
		GetEpisodeDetails,
		GetTVShowDetails,
		GetMovieDetails
	}
	
	/**
	 * Creates the API object.
	 * @param connection
	 */
	public XbmcApi(XbmcConnection connection) {
		this._connection = connection;
	}
	
	public String getVideoType() {
		return _videoType;
	}
	
	/**
	 * Is XBMC reachable?
	 * @return
	 */
	public boolean isReachable() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.JSONRPC.Ping);
		JsonRpc.Response r = q.send(_connection);
		if (r != null) {
			return r.getStringResult().equals("pong");
		}
		return false;
	}
	
	/**
	 * Has XBMC an active video player? 
	 * @return
	 */
	public boolean hasVideoPlayer() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.Player.GetActivePlayers);
		JsonRpc.Response r = q.send(_connection);
		JSONArray result = r.getArrayResult();
		if (result.length() > 0) {
			try {
				JSONObject object = result.getJSONObject(0);
				this._playerID = object.getInt("playerid");
				return object.getString("type").equals("video");
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Gets the type video playing.
	 * @return
	 */
	public String getNowPlayingType() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.Player.GetItem);
		try {
			JSONObject params = new JSONObject();
			params.put("playerid", this._playerID);
			q.setParameters(params);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		JsonRpc.Response r = q.send(_connection);
		this._videoID = r.getIntFromObjectResult("item", "id");
		this._videoType = r.getStringFromObjectResult("item", "type");
		return this._videoType;
	}
	
	/**
	 * Loads episode details.
	 * @return
	 */
	public Episode getEpisode() {
		Episode episode = loadEpisodeDetails();
		addTvShowDetails(episode);
		
		return episode;
	}

	private void addTvShowDetails(Episode episode) {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.VideoLibrary.GetTVShowDetails);
		try {			
			JSONObject params = new JSONObject();
			params.put("tvshowid", episode.getTvShowID());
			q.setParameters(params);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		JsonRpc.Response r = q.send(_connection);
		
		episode.setTvShowTitle(r.getStringFromObjectResult("tvshowdetails", "label"));
	}

	private Episode loadEpisodeDetails() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.VideoLibrary.GetEpisodeDetails);
		try {
			JSONArray details = new JSONArray();
			details.put("season");
			details.put("episode");
			details.put("tvshowid");
			details.put("fanart");
			details.put("plot");
			details.put("cast");
			
			JSONObject params = new JSONObject();
			params.put("episodeid", this._videoID);
			params.put("properties", details);
			q.setParameters(params);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		JsonRpc.Response r = q.send(_connection);
		String resultName = "episodedetails";
		
		Episode episode = new Episode();
		episode.setEpisodeNumber(r.getIntFromObjectResult(resultName, "episode"));
		episode.setTvShowID(r.getIntFromObjectResult(resultName, "tvshowid"));
		episode.setSeasonNumber(r.getIntFromObjectResult(resultName, "season"));
		episode.setTitle(r.getStringFromObjectResult(resultName, "label"));
		Bitmap image = _connection.getImage(r.getStringFromObjectResult(resultName, "fanart"));
		episode.setImage(image);
		episode.setPlot(r.getStringFromObjectResult(resultName, "plot"));
		episode.setCast(getActorsFromJSON(r.getArrayFromObjectResult(resultName, "cast")));
		return episode;
	}
	
	public Video getMovie() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.VideoLibrary.GetMovieDetails);
		try {
			JSONArray details = new JSONArray();
			details.put("fanart");
			details.put("plot");
			details.put("cast");
			
			JSONObject params = new JSONObject();
			params.put("movieid", this._videoID);
			params.put("properties", details);
			q.setParameters(params);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		JsonRpc.Response r = q.send(_connection);
		String resultName = "moviedetails";
		
		Video movie = new Video();
		movie.setTitle(r.getStringFromObjectResult(resultName, "label"));
		Bitmap image = _connection.getImage(r.getStringFromObjectResult(resultName, "fanart"));
		movie.setImage(image);
		movie.setPlot(r.getStringFromObjectResult(resultName, "plot"));
		movie.setCast(getActorsFromJSON(r.getArrayFromObjectResult(resultName, "cast")));
		return movie;
	}

	private List<Actor> getActorsFromJSON(JSONArray cast) {
		List<Actor> actors = new ArrayList<Actor>();
		for (int i=0; i<cast.length(); i++) {
			try {
				JSONObject object = (JSONObject) cast.get(i);
				String role = object.getString("role");
				if (role != null && !role.isEmpty()) {
					actors.add(new Actor(object.getString("name"), role));
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return actors;
	}
	
	public Video getVideo() {
		String type = (this._videoType == null) ? this.getNowPlayingType() : this._videoType;

		if (type.equals(VIDEO_TYPE_EPISODE)) {
			return this.getEpisode();
		}
		else if (type.equals(VIDEO_TYPE_MOVIE)) {
			return this.getMovie();
		}
		return null;
	}
	
	public int getPlaybackStatus() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.Player.GetProperties);
		try {
			JSONArray properties = new JSONArray();
			properties.put("speed");
			
			JSONObject params = new JSONObject();
			params.put("playerid", this._playerID);
			params.put("properties", properties);
			q.setParameters(params);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		JsonRpc.Response r = q.send(_connection);
		return r.getIntFromResult("speed");
	}
	
	public int playPause() {
		JsonRpc.Request q = new JsonRpc.Request(XbmcApi.Player.PlayPause);
		try {
			JSONObject params = new JSONObject();
			params.put("playerid", this._playerID);
			q.setParameters(params);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		JsonRpc.Response r = q.send(_connection);
		return r.getIntFromResult("speed");
	}
}
