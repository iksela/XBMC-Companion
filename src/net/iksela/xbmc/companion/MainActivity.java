package net.iksela.xbmc.companion;

import java.util.List;
import java.util.Vector;

import net.iksela.xbmc.companion.api.XbmcApi;
import net.iksela.xbmc.companion.api.XbmcConnection;
import net.iksela.xbmc.companion.data.Video;
import net.iksela.xbmc.companion.fragments.AbstractFragment;
import net.iksela.xbmc.companion.fragments.CastFragment;
import net.iksela.xbmc.companion.fragments.NowPlayingFragment;
import net.iksela.xbmc.companion.fragments.PlotFragment;
import net.iksela.xbmc.companion.helpers.SettingsProvider;
import net.iksela.xbmc.companion.helpers.UIHelper;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainActivity extends FragmentActivity {
	
	private final static String TAG = "MAIN";

	SwipePagerAdapter mSwipePagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public ViewPager mViewPager;
	public ViewSwitcher switcher;
	Menu menu;
	
	public boolean isReady = false; 
	XbmcApi xbmc;
	public Video video;
	public BitmapDrawable[] backgrounds;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		
		// Creates switcher for loading screen
		switcher = new ViewSwitcher(this);
		switcher.addView(View.inflate(this, R.layout.loading, null));
		switcher.addView(View.inflate(this, R.layout.activity_main, null));
		setContentView(switcher);

		// Load fragments in swipe pager
		List<AbstractFragment> fragments = new Vector<AbstractFragment>();
		fragments.add((AbstractFragment) Fragment.instantiate(this, NowPlayingFragment.class.getName()));
		fragments.add((AbstractFragment) Fragment.instantiate(this, PlotFragment.class.getName()));
		fragments.add((AbstractFragment) Fragment.instantiate(this, CastFragment.class.getName()));
		this.mSwipePagerAdapter = new SwipePagerAdapter(super.getSupportFragmentManager(), fragments);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSwipePagerAdapter);
		
		// Create XBMC connection and start retrieving data
		if (!isReady) {
			xbmc = new XbmcApi(new XbmcConnection(getApplicationContext()));
			new MainXbmcConnectionTask().execute();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
	}

	private class MainXbmcConnectionTask extends AsyncTask<String, Void, Integer> {

		private static final int OK				= 0;
		private static final int UNREACHABLE	= 1;
		private static final int NO_VIDEOPLAYER	= 2;

		@Override
		protected Integer doInBackground(String... params) {
			updateLoadingMessage(R.string.loading_connect);
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
						updateLoadingMessage(R.string.loading_data);
						// Set Data
						MainActivity.this.video = xbmc.getEpisode();
						// and backgrounds
						updateLoadingMessage(R.string.loading_data_format);
						View root = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
						MainActivity.this.backgrounds = UIHelper.getPieces(
								mSwipePagerAdapter.fragments.size(),
								MainActivity.this.video.getImage(),
								root.getHeight(), root.getWidth(),
								settings.getDarkness(),
								getResources()
						);
						// Update UI
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.v(TAG, "Updating UI");
								for (AbstractFragment f : mSwipePagerAdapter.fragments) {
									if (f.getView() != null) {
										f.updateUI(f.getView());
									}
								}
								// Hide loading screen
								switcher.showNext();
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
				case OK:
					// Set status = ready
					isReady = true;
					break;
				case UNREACHABLE:
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage(R.string.error_unreachable)
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
					updateLoadingMessage(R.string.error_unreachable);
					((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
					break;
				case NO_VIDEOPLAYER:
					updateLoadingMessage(R.string.error_novideoplayer);
					((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
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
	
	public void refreshData() {
		switcher.showPrevious();
		new MainXbmcConnectionTask().execute();
	}
	
	public void updateLoadingMessage(final int string) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView)findViewById(R.id.textViewLoading)).setText(string);
			}
		});
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
				refreshData();
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
	
	public class SwipePagerAdapter extends FragmentPagerAdapter {
		
		private List<AbstractFragment> fragments;
		private AbstractFragment currentFragment;

		public SwipePagerAdapter(FragmentManager fm, List<AbstractFragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(this.fragments.get(position).getTitleID()).toUpperCase();
		}
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			currentFragment = (AbstractFragment)object;
		}
		
		public AbstractFragment getCurrentFragment() {
			return currentFragment;
		}
	}
}
