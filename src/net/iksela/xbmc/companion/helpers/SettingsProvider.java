package net.iksela.xbmc.companion.helpers;

import net.iksela.xbmc.companion.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsProvider {
	
	private Context _context;
	private SharedPreferences _prefs;
	
	public SettingsProvider(Context context) {
		this._context = context;
		this._prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	private String getPref(int res) {
		return this._context.getString(res);
	}
	
	private String getString(int res, String defaultString) {
		String preference = this._prefs.getString(getPref(res), defaultString);
		return (preference == null) ? null : preference.trim();
	}
	
	public String getIP() {
		return getString(R.string.settings_ip, null);
	}
	
	public String getPort() {
		return getString(R.string.settings_port, "80");
	}
	
	public String getUserName() {
		return getString(R.string.settings_username, null);
	}
	
	public String getPassword() {
		return getString(R.string.settings_password, null);
	}

	public boolean getAutoPause() {
		return this._prefs.getBoolean(getPref(R.string.settings_autopause), true);
	}
	
	public int getDarkness() {
		return this._prefs.getInt(getPref(R.string.settings_darkness), 25);
	}
	
	public boolean getDebug() {
		return this._prefs.getBoolean(getPref(R.string.settings_debug), false);
	}
}
