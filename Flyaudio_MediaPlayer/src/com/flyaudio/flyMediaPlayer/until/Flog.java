package com.flyaudio.flyMediaPlayer.until;

import android.util.Log;

public class Flog {
	public static String TAG = "flyMediaPlayer";
	static final boolean DEBUG = true;

	public static void d(String Tag, String msg) {
		if (DEBUG)
			Log.d(TAG + "--" + Tag, msg);
	}

	public static void e(String Tag, String msg) {
		if (DEBUG)
			Log.e(TAG + "--" + Tag, msg);
	}

	public static void i(String Tag, String msg) {
		if (DEBUG)
			Log.i(TAG + "--" + Tag, msg);
	}

}
