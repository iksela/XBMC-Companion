package net.iksela.xbmc.companion;

public class UIHelper {

	public final static String FORMAT_SEASON = "S";
	public final static String FORMAT_EPISODE = "E";

	public static String formatNumber(String type, int value) {
		String tmp = Integer.toString(value);
		if (tmp.length() == 1) {
			tmp = "0" + tmp;
		}
		tmp = type + tmp;
		return tmp;
	}
}
