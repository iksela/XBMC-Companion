package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.CastAdapter;
import net.iksela.xbmc.companion.MainActivity;
import net.iksela.xbmc.companion.R;
import net.iksela.xbmc.companion.data.Actor;
import net.iksela.xbmc.companion.data.Episode;
import net.iksela.xbmc.companion.helpers.SearchPopupHelper;
import android.view.View;
import android.widget.AdapterView;
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
		final MainActivity activity = (MainActivity) getActivity();

		if (activity.video != null) {
			Episode episode = (Episode) activity.video;
			CastAdapter adapter = new CastAdapter(activity, R.layout.cast, episode.getCast());
			ListView list = (ListView) view.findViewById(R.id.listView1);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Actor actor = ((CastAdapter) parent.getAdapter()).getItem(position);
					SearchPopupHelper popup = new SearchPopupHelper(activity);
					popup.show(actor.getName(), SearchPopupHelper.SEARCH_TYPE_ACTOR, view);
				}
			});
			((RelativeLayout) view.findViewById(R.id.page3)).setBackgroundDrawable(activity.backgrounds[2]);
		}
	}
}
