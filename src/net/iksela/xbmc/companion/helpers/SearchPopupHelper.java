package net.iksela.xbmc.companion.helpers;

import net.iksela.xbmc.companion.R;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class SearchPopupHelper {
	
	public final static String SEARCH_TYPE_ACTOR = "nm";
	public final static String SEARCH_TYPE_TITLE = "tt";
	public final static String SEARCH_TYPE_EPISODE = "ep";
	
	private Activity _activity;
	
	public SearchPopupHelper(Activity activity) {
		this._activity = activity;
	}
	
	public class SearchTermsByProvider {
		protected String google;
		protected String wikipedia;
		protected String imdbApp;
		protected String imdbWeb;
		
		public SearchTermsByProvider() {}
		
		public SearchTermsByProvider(String google, String wikipedia, String imdb) {
			super();
			this.setGoogle(google);
			this.setWikipedia(wikipedia);
			this.setImdbApp(imdb);
			this.setImdbWeb(imdb);
		}
		
		public void setGoogle(String google) {
			this.google = google;
		}
		public void setWikipedia(String wikipedia) {
			this.wikipedia = wikipedia.replaceAll(" ", "_");
		}
		public void setImdbApp(String imdbApp) {
			this.imdbApp = imdbApp;
		}
		public void setImdbWeb(String imdbWeb) {
			this.imdbWeb = imdbWeb;
		}
	}

	private void startSearchActivity(String uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		_activity.startActivity(intent);
	}
	
	public boolean isUriAvailable(String uri) {
	    Intent test = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	    return _activity.getPackageManager().resolveActivity(test, 0) != null;
	}
	
	public void show(final String term, final String searchType, View view) {
		this.show(new SearchTermsByProvider(term, term, term), searchType, view);
	}
	
	public void show(final SearchTermsByProvider terms, final String searchType, View view) {
		PopupMenu menu = new PopupMenu(_activity, view);
		menu.getMenuInflater().inflate(R.menu.cast_search, menu.getMenu());
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.itemCastSearchGoogle:
						Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
						intent.putExtra(SearchManager.QUERY, terms.google);
						_activity.startActivity(intent);
						return true;
					case R.id.itemCastSearchWikipedia:
						startSearchActivity("http://en.wikipedia.org/wiki/" + terms.wikipedia);
						return true;
					case R.id.itemCastSearchImdb:
						String uriIntent = "imdb:///find?q=" + terms.imdbApp;
						if (isUriAvailable(uriIntent)) {
							startSearchActivity(uriIntent);
						}
						else {
							startSearchActivity("http://www.imdb.com/find?q=" + terms.imdbWeb + "&s=" + searchType);
						}
						return true;
					default:
						return false;
				}
			}
			
		});
		menu.show();
	}
}
