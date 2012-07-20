package net.iksela.xbmc.companion;

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

		new MyTask().execute("pwet");

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	private class MyTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Log.i("pwet", "doInBackground...");
			/*
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost/").openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setConnectTimeout(1000);
				connection.connect();
				
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String result = null;
				while(bufReader.ready()) {
					result += bufReader.readLine();
				}
				
				bufReader.close();
				
				Log.e("pwet", result);
				
				return "yesWeCan!";
			} catch (Exception e) {
				Log.e("pwet", e.getMessage());
			}
			*/
			XbmcConnection xbmc = new XbmcConnection(getApplicationContext());
			return xbmc.isReachable();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.i("task", "onPostExecute: "+result);
			if (!result) {
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
					.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MainActivity.this.finish();
						}
					});
				builder.create().show();
			}
		}

	}
	
	/**
	 * Launchs SettingsActivity.
	 */
	public void launchSettingsActivity() {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
