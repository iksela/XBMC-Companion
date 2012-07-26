package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.CastAdapter;
import net.iksela.xbmc.companion.MainActivity;
import net.iksela.xbmc.companion.R;
import net.iksela.xbmc.companion.data.Episode;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class CastFragment extends AbstractFragment {

	@Override
	protected void init() {
		this.setTitleID(R.string.title_section3);
		this.setLayoutID(R.layout.page3);
	}

	@Override
	public void updateUI(View view) {
		MainActivity activity = (MainActivity) getActivity();

		if (activity.video != null) {
			Episode episode = (Episode) activity.video;
			CastAdapter adapter = new CastAdapter(activity, R.layout.cast, episode.getCast());
			((ListView) view.findViewById(R.id.listView1)).setAdapter(adapter);
			((RelativeLayout) view.findViewById(R.id.page3)).setBackgroundDrawable(activity.backgrounds[2]);
		}
	}
}
