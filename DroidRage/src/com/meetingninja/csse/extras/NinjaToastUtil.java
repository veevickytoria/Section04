package com.meetingninja.csse.extras;

import android.content.Context;
import android.widget.Toast;

public class NinjaToastUtil {
	public static void show(Context context, CharSequence text) {
		show(context, text, false);
	}

	public static void show(Context context, String fmt, Object... args) {
		show(context, String.format(fmt, args));
	}

	public static void show(Context context, CharSequence text,
			boolean displayLonger) {
		Toast.makeText(context, text, getLength(displayLonger)).show();
	}

	private static int getLength(boolean isLong) {
		return isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
	}
}
