package net.iksela.xbmc.companion.fragments;

import net.iksela.xbmc.companion.R;
import android.view.View;

public class LoadingFragment extends AbstractFragment {

	@Override
	protected void init() {
		this.setTitleID(R.string.loading);
		this.setLayoutID(R.layout.loading);
	}

	@Override
	public void updateUI(View view) {
		// TODO Auto-generated method stub
		
	}

}
