package com.meetingninja.csse.extras;

public class NinjaTextUtils {
	/**
	 * Tests if the text is null or 0-length
	 * @param text
	 * @return true if text is null or 0-length
	 */
	public static boolean isEmpty(CharSequence text) {
		return text == null || text.length() == 0;
	}
}
