package net.iksela.xbmc.companion;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/*
 public class SettingsActivity extends Activity {

 @Override
 public void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_settings);
 getActionBar().setDisplayHomeAsUpEnabled(true);
 }

 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
 getMenuInflater().inflate(R.menu.activity_settings, menu);
 return true;
 }


 @Override
 public boolean onOptionsItemSelected(MenuItem item) {
 switch (item.getItemId()) {
 case android.R.id.home:
 NavUtils.navigateUpFromSameTask(this);
 return true;
 }
 return super.onOptionsItemSelected(item);
 }

 private SettingsProvider getSettingsProvider() {
 return new SettingsProvider(getApplicationContext());
 }

 private EditText getEditText(int id) {
 return (EditText) findViewById(id);
 }

 @Override
 protected void onResume() {
 super.onResume();
 // Restore settings
 SettingsProvider settings = getSettingsProvider();
 getEditText(R.id.editText1).setText(settings.getIP());
 getEditText(R.id.editText2).setText(Integer.toString(settings.getPort()));
 getEditText(R.id.editText3).setText(settings.getUserName());
 getEditText(R.id.editText4).setText(settings.getPassword());
 }

 @Override
 protected void onPause() {
 super.onPause();
 // Save settings
 SettingsProvider settings = getSettingsProvider();
 settings.setIP(getEditText(R.id.editText1).getText().toString());
 settings.setPort(Integer.parseInt(getEditText(R.id.editText2).getText().toString()));
 settings.setUserName(getEditText(R.id.editText3).getText().toString());
 settings.setPassword(getEditText(R.id.editText4).getText().toString());
 }
 }
 */
public class SettingsActivity extends PreferenceActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new UserPreferencesFragment()).commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class UserPreferencesFragment extends PreferenceFragment {

		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.prefs);
		}
	}
}