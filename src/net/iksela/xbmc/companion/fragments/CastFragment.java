package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.CastAdapter;
import net.iksela.xbmc.companion.MainActivity;
import net.iksela.xbmc.companion.R;
import net.iksela.xbmc.companion.data.Actor;
import net.iksela.xbmc.companion.data.Episode;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

public class CastFragment extends AbstractFragment {

	@Override
	protected void init() {
		this.setTitleID(R.string.title_section3);
		this.setLayoutID(R.layout.page3);
	}

	@Override
	public void updateUI(View view) {
		final MainActivity activity = (MainActivity) getActivity();

		if (activity.video != null) {
			Episode episode = (Episode) activity.video;
			CastAdapter adapter = new CastAdapter(activity, R.layout.cast, episode.getCast());
			ListView list = (ListView) view.findViewById(R.id.listView1);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					/*
					 * Actor actor =
					 * ((CastAdapter)parent.getAdapter()).getItem(position);
					 * Log.d("test",
					 * "position: "+position+" - actor:"+actor.getName());
					 * Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
					 * String term = actor.getName();
					 * intent.putExtra(SearchManager.QUERY, term);
					 * activity.startActivity(intent);
					 */
					final Actor actor = ((CastAdapter) parent.getAdapter()).getItem(position);

					PopupMenu menu = new PopupMenu(activity, view);
					menu.getMenuInflater().inflate(R.menu.cast_search, menu.getMenu());
					menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
								case R.id.itemCastSearchGoogle:
									Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
									String term = actor.getName();
									intent.putExtra(SearchManager.QUERY, term);
									activity.startActivity(intent);
									return true;
								case R.id.itemCastSearchWikipedia:
									startSearchActivity("http://en.wikipedia.org/wiki/" + actor.getName());
									return true;
								case R.id.itemCastSearchImdb:
									String uriIntent = "imdb:///find?q=" + actor.getName();
									if (MainActivity.isUriAvailable(activity, uriIntent)) {
										startSearchActivity(uriIntent);
									}
									else {
										startSearchActivity("http://www.imdb.com/find?q=" + actor.getName() + "&s=nm");
									}
									return true;
								default:
									return false;
							}
						}

						private void startSearchActivity(String uri) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
							startActivity(intent);
						}
					});
					menu.show();
				}
			});
			((RelativeLayout) view.findViewById(R.id.page3)).setBackgroundDrawable(activity.backgrounds[2]);
		}
	}
}
