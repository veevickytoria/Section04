package com.droidrage.meetingninja.extras;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	/**
	 * Checks an email using regex
	 * 
	 * @param email
	 * @return true if email is a valid email string
	 */
	public static boolean isValidEmailAddress(String email) {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
