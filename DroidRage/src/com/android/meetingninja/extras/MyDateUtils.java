package com.android.meetingninja.extras;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyDateUtils {
	public static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat(
			"EEEE, d-MMM-yy HH:mm:ss zzz", Locale.US);
	public static final SimpleDateFormat APP_DATE_FORMAT = new SimpleDateFormat(
			"EEE, MMM dd, yyyy", Locale.US);
	public static final SimpleDateFormat _24_TIME_FORMAT = new SimpleDateFormat(
			"HH:mm", Locale.US);
	public static final SimpleDateFormat _12_TIME_FORMAT = new SimpleDateFormat(
			"hh:mma", Locale.US);
}
