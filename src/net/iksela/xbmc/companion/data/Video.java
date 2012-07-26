package net.iksela.xbmc.companion.data;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Video {
	private String title;
	private String plot;
	private Bitmap image;
	private List<Actor> cast;
	
	public Video() {
		cast = new ArrayList<Actor>();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getPlot() {
		return plot;
	}
	public void setPlot(String plot) {
		this.plot = plot;
	}
	public List<Actor> getCast() {
		return cast;
	}
	public void setCast(List<Actor> cast) {
		this.cast = cast;
	}
}
