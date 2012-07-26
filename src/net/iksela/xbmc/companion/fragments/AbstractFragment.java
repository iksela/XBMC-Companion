package net.iksela.xbmc.companion.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractFragment extends Fragment {

	private final static String TAG = "FRAGMENT";

	private int titleID;
	private int layoutID;

	public AbstractFragment() {
		init();
	}

	protected abstract void init();

	public int getTitleID() {
		return titleID;
	}

	public void setTitleID(int titleID) {
		this.titleID = titleID;
	}

	public int getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: " + this.getClass().getSimpleName());
		View view = inflater.inflate(this.getLayoutID(), container, false);
		updateUI(view);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState: " + this.getClass().getSimpleName());
	}
	
	public abstract void updateUI(View view);
}
