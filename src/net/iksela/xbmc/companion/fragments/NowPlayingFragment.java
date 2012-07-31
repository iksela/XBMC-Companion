package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.MainActivity;
import net.iksela.xbmc.companion.R;
import net.iksela.xbmc.companion.data.Episode;
import net.iksela.xbmc.companion.helpers.SearchPopupHelper;
import net.iksela.xbmc.companion.helpers.SearchPopupHelper.SearchTermsByProvider;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NowPlayingFragment extends AbstractFragment {

	@Override
	protected void init() {
		this.setTitleID(R.string.title_section1);
		this.setLayoutID(R.layout.page1);
	}

	@Override
	public void updateUI(View view) {
		final MainActivity activity = (MainActivity) getActivity();

		if (activity.video != null) {
			final Episode episode = (Episode) activity.video;

			// Textviews
			TextView tvShowTitle = (TextView) view.findViewById(R.id.textViewTVShowTitle);
			TextView episodeTitle = (TextView) view.findViewById(R.id.textViewEpisodeTitle);
			TextView episodeXX = (TextView) view.findViewById(R.id.textViewEpisodeXX);
			TextView seasonXX = (TextView) view.findViewById(R.id.textViewSeasonXX);

			// Set text
			tvShowTitle.setText(episode.getTvShowTitle());
			episodeTitle.setText(episode.getTitle());
			episodeXX.setText(episode.getFormattedEpisodeNumber());
			seasonXX.setText(episode.getFormattedSeasonNumber());

			// Onclicks
			View.OnClickListener episodeOnClick = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					SearchPopupHelper popup = new SearchPopupHelper(activity);

					SearchTermsByProvider terms = popup.new SearchTermsByProvider();
					terms.setGoogle(episode.getTvShowTitle() + " " + episode.getTitle());
					terms.setWikipedia(episode.getTvShowTitle() + " (season " + episode.getSeasonNumber() + ")");
					terms.setImdbApp(episode.getTvShowTitle());
					terms.setImdbWeb(episode.getTitle());
					
					popup.show(terms, SearchPopupHelper.SEARCH_TYPE_EPISODE, view);
				}
			};
			episodeTitle.setOnClickListener(episodeOnClick);
			episodeXX.setOnClickListener(episodeOnClick);
			seasonXX.setOnClickListener(episodeOnClick);

			tvShowTitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					SearchPopupHelper popup = new SearchPopupHelper(activity);
					popup.show(episode.getTvShowTitle(), SearchPopupHelper.SEARCH_TYPE_TITLE, view);
				}
			});
			((RelativeLayout) view.findViewById(R.id.page1)).setBackgroundDrawable(activity.backgrounds[0]);
		}
	}
}
