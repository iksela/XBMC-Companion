package net.iksela.xbmc.companion.data;


public class Episode extends Video {
	
	public final static String FORMAT_SEASON = "S";
	public final static String FORMAT_EPISODE = "E";
	
	private String tvShowTitle;
	private int tvShowID;
	private String episodeNumber;
	private String seasonNumber;
	
	public Episode() {
		super();
	}
	
	public String getTvShowTitle() {
		return tvShowTitle;
	}
	public void setTvShowTitle(String tvShowTitle) {
		this.tvShowTitle = tvShowTitle;
	}
	public int getTvShowID() {
		return tvShowID;
	}
	public void setTvShowID(int tvShowID) {
		this.tvShowID = tvShowID;
	}
	public String getEpisodeNumber() {
		return episodeNumber;
	}
	public void setEpisodeNumber(int episodeNumber) {
		this.episodeNumber = formatNumber(FORMAT_EPISODE, episodeNumber);
	}
	public String getSeasonNumber() {
		return seasonNumber;
	}
	public void setSeasonNumber(int seasonNumber) {
		this.seasonNumber = formatNumber(FORMAT_SEASON, seasonNumber);
	}

	public static String formatNumber(String type, int value) {
		String tmp = Integer.toString(value);
		if (tmp.length() == 1) {
			tmp = "0" + tmp;
		}
		tmp = type + tmp;
		return tmp;
	}
}
