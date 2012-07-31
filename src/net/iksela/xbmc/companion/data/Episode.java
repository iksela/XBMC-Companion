package net.iksela.xbmc.companion.data;


public class Episode extends Video {
	
	public final static String FORMAT_SEASON = "S";
	public final static String FORMAT_EPISODE = "E";
	
	private String tvShowTitle;
	private int tvShowID;
	private int episodeNumber;
	private int seasonNumber;
	
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
	public int getEpisodeNumber() {
		return episodeNumber;
	}
	public String getFormattedEpisodeNumber() {
		return formatNumber(FORMAT_EPISODE, episodeNumber);
	}
	public void setEpisodeNumber(int episodeNumber) {
		this.episodeNumber = episodeNumber;
	}
	public int getSeasonNumber() {
		return seasonNumber;
	}
	public String getFormattedSeasonNumber() {
		return formatNumber(FORMAT_SEASON, seasonNumber);
	}
	public void setSeasonNumber(int seasonNumber) {
		this.seasonNumber = seasonNumber;
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
