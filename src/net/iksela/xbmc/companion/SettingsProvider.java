package net.iksela.xbmc.companion;

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
	
	private SharedPreferences.Editor getEditor() {
		return this._prefs.edit();
	}
	
	private String getPref(int res) {
		return this._context.getString(res);
	}
	
	public String getIP() {
		return this._prefs.getString(getPref(R.string.settings_ip), null);
	}
	
	public void setIP(String value) {
		this.getEditor().putString(getPref(R.string.settings_ip), value).commit();
	}
	
	public int getPort() {
		return this._prefs.getInt(getPref(R.string.settings_port), 80);
	}
	
	public void setPort(int value) {
		this.getEditor().putInt(getPref(R.string.settings_port), value).commit();
	}
	
	public String getUserName() {
		return this._prefs.getString(getPref(R.string.settings_username), null);
	}
	
	public void setUserName(String value) {
		this.getEditor().putString(getPref(R.string.settings_username), value).commit();
	}
	
	public String getPassword() {
		return this._prefs.getString(getPref(R.string.settings_password), null);
	}
	
	public void setPassword(String value) {
		this.getEditor().putString(getPref(R.string.settings_password), value).commit();
	}
}
