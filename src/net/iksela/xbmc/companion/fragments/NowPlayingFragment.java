package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.MainActivity;
import net.iksela.xbmc.companion.R;
import net.iksela.xbmc.companion.data.Episode;
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
		MainActivity activity = (MainActivity) getActivity();

		if (activity.video != null) {
			Episode episode = (Episode) activity.video;
			((TextView) view.findViewById(R.id.textViewEpisodeTitle)).setText(episode.getTitle());
			((TextView) view.findViewById(R.id.textViewEpisodeXX)).setText(episode.getEpisodeNumber());
			((TextView) view.findViewById(R.id.textViewSeasonXX)).setText(episode.getSeasonNumber());
			((TextView) view.findViewById(R.id.textViewTVShowTitle)).setText(episode.getTvShowTitle());
			((RelativeLayout) view.findViewById(R.id.page1)).setBackgroundDrawable(activity.backgrounds[0]);
		}
	}
}
