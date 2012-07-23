package net.iksela.xbmc.companion;

import net.iksela.xbmc.companion.api.XbmcApi;
import net.iksela.xbmc.companion.api.XbmcConnection;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
		new StartConnectionTask().execute();
	}

	private class StartConnectionTask extends AsyncTask<String, Void, Integer> {

		private static final int OK				= 0;
		private static final int UNREACHABLE	= 1;
		private static final int NO_VIDEOPLAYER	= 2;

		@Override
		protected Integer doInBackground(String... params) {
			XbmcConnection connection = new XbmcConnection(getApplicationContext());
			
			XbmcApi.Ping ping = new XbmcApi.Ping();
			ping.send(connection);
			if (ping.hasPong()) {
				XbmcApi.GetActivePlayers gap = new XbmcApi.GetActivePlayers();
				gap.send(connection);
				if (gap.hasVideoPlayer()) {
					int playerID = gap.getPlayerID();
					XbmcApi.GetNowPlaying gnp = new XbmcApi.GetNowPlaying(playerID);
					gnp.send(connection);
					int itemID = gnp.getItemID();
					if (gnp.getItemType().equals(XbmcApi.VIDEO_TYPE_EPISODE)) {
						XbmcApi.GetEpisodeDetails ged = new XbmcApi.GetEpisodeDetails(itemID);
						ged.send(connection);
						
						XbmcApi.GetTVShowDetails gtd = new XbmcApi.GetTVShowDetails(ged.getTVShowID());
						gtd.send(connection);
						
						final String episodeTitle = ged.getTitle();
						final String episode = UIHelper.formatNumber(UIHelper.FORMAT_EPISODE, ged.getEpisode());
						final String season = UIHelper.formatNumber(UIHelper.FORMAT_SEASON, ged.getSeason());
						final String tvshow = gtd.getTitle();
						
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								((TextView)findViewById(R.id.textViewEpisodeTitle)).setText(episodeTitle);
								((TextView)findViewById(R.id.textViewEpisodeXX)).setText(episode);
								((TextView)findViewById(R.id.textViewSeasonXX)).setText(season);
								((TextView)findViewById(R.id.textViewTVShowTitle)).setText(tvshow);
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
			
			/*
			if (xbmc.isReachable()) {
				if (xbmc.hasVideoPlayer()) {
					Log.d("PROGRESS", xbmc.getVideoType());
					if (xbmc.getVideoType().equals(XbmcApi.VIDEO_TYPE_EPISODE)) {
						Log.d("PROGRESS", "So far, so good...");
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
			*/
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
			default:
				return super.onOptionsItemSelected(item);
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
