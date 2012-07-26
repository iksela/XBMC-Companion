package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.MainActivity;
import net.iksela.xbmc.companion.R;
import net.iksela.xbmc.companion.data.Episode;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlotFragment extends AbstractFragment {

	@Override
	protected void init() {
		this.setTitleID(R.string.title_section2);
		this.setLayoutID(R.layout.page2);
	}

	@Override
	public void updateUI(View view) {
		MainActivity activity = (MainActivity) getActivity();

		if (activity.video != null) {
			Episode episode = (Episode) activity.video;
			((TextView) view.findViewById(R.id.textViewPlot)).setText(episode.getPlot());
			((RelativeLayout) view.findViewById(R.id.page2)).setBackgroundDrawable(activity.backgrounds[1]);
		}
	}

}
