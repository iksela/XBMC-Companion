package net.iksela.xbmc.companion;

import net.iksela.xbmc.companion.api.XbmcApi;
import net.iksela.xbmc.companion.api.XbmcConnection;
import net.iksela.xbmc.companion.data.Episode;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	XbmcApi xbmc;
	
	Menu menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		xbmc = new XbmcApi(new XbmcConnection(getApplicationContext()));
		new StartConnectionTask().execute();
	}

	private class StartConnectionTask extends AsyncTask<String, Void, Integer> {

		private static final int OK				= 0;
		private static final int UNREACHABLE	= 1;
		private static final int NO_VIDEOPLAYER	= 2;

		@Override
		protected Integer doInBackground(String... params) {
			if (xbmc.isReachable()) {
				if (xbmc.hasVideoPlayer()) {
					SettingsProvider settings = new SettingsProvider(getApplicationContext());
					int status = xbmc.getPlaybackStatus();
					if (settings.getAutoPause() && status == XbmcApi.PLAYER_PLAYING) {
						new PlayPauseTask().execute();
					}
					else {
						updatePlayPauseMenu(status);
					}
					if (xbmc.getNowPlayingType().equals(XbmcApi.VIDEO_TYPE_EPISODE)) {
						final Episode episode = xbmc.getEpisode();
						final BitmapDrawable drawable = UIHelper.getOptimizedDrawable(episode.getImage(), getResources(), (RelativeLayout)findViewById(R.id.page1));
						
						// Update UI
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								((TextView)findViewById(R.id.textViewEpisodeTitle)).setText(episode.getTitle());
								((TextView)findViewById(R.id.textViewEpisodeXX)).setText(episode.getEpisodeNumber());
								((TextView)findViewById(R.id.textViewSeasonXX)).setText(episode.getSeasonNumber());
								((TextView)findViewById(R.id.textViewTVShowTitle)).setText(episode.getTvShowTitle());
								
								((RelativeLayout)findViewById(R.id.page1)).setBackgroundDrawable(drawable);
							}
						});
					}
					return OK;
				}
				else {
					return NO_VIDEOPLAYER;
				}
			}
			else {
				return UNREACHABLE;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Log.i("task", "onPostExecute: " + result);
			switch (result) {
				case 1:
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage("Couldn't connect to XBMC.")
						.setCancelable(false)
						.setPositiveButton("Show settings", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								launchSettingsActivity();
								dialog.dismiss();
							}
						})
						.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
					builder.create().show();
					break;
				case 2:
					Toast.makeText(MainActivity.this, "No videoplayer is currently on.", Toast.LENGTH_LONG).show();
					break;
			}
		}

	}

	/**
	 * Launchs SettingsActivity.
	 */
	public void launchSettingsActivity() {
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	public void refreshActivity() {
		finish();
		startActivity(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				refreshActivity();
				return true;
			case R.id.menu_settings:
				launchSettingsActivity();
				return true;
			case R.id.menu_playpause:
				new PlayPauseTask().execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void updatePlayPauseMenu(final int status) {
		final MenuItem playPause = menu.findItem(R.id.menu_playpause);

		MainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (status) {
				case XbmcApi.PLAYER_PLAYING:
					playPause.setIcon(android.R.drawable.ic_media_pause);
					break;
				case XbmcApi.PLAYER_PAUSED:
					playPause.setIcon(android.R.drawable.ic_media_play);
					break;
				}
			}
		});
	}
	
	private class PlayPauseTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			return xbmc.playPause();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			updatePlayPauseMenu(result);
			
			int string = R.string.error;
			switch (result) {
				case XbmcApi.PLAYER_PLAYING:
					string = R.string.status_playing;
					break;
				case XbmcApi.PLAYER_PAUSED:
					string = R.string.status_paused;
					break;
			}
			Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
		}
		
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new LayoutInflaterFragment();
			Bundle args = new Bundle();
			args.putInt(LayoutInflaterFragment.INDEX, i);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			/*
			 * return 3;
			 */
			return Page.values().length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			/*
			 * switch (position) { case 0: return
			 * getString(R.string.title_section1).toUpperCase(); case 1: return
			 * getString(R.string.title_section2).toUpperCase(); case 2: return
			 * getString(R.string.title_section3).toUpperCase(); } return null
			 */

			return getString(Page.getByIndex(position).getTitleID()).toUpperCase();
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class LayoutInflaterFragment extends Fragment {
		public LayoutInflaterFragment() {
		}

		public static final String INDEX = "index";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Bundle args = getArguments();
			int pageID = args.getInt(INDEX);

			return inflater.inflate(Page.getByIndex(pageID).getLayoutID(), container, false);
			/*
			 * if (pageID == 1) { return inflater.inflate(R.layout.page1,
			 * container, false); } TextView textView = new
			 * TextView(getActivity()); textView.setGravity(Gravity.CENTER);
			 * textView.setText(Integer.toString(pageID)); return textView;
			 */
		}
	}
}
