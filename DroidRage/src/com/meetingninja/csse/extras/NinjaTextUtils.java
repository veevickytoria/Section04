package com.meetingninja.csse.extras;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NinjaTextUtils {
	/**
	 * Tests if the text is null or 0-length
	 * 
	 * @param text
	 * @return true if text is null or 0-length
	 */
	public static boolean isEmpty(CharSequence text) {
		return text == null || text.length() == 0;
	}

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

	/**
	 * Computes the SHA-256 of a string.
	 * 
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String computeHash(String input)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();

		byte[] byteData = digest.digest(input.getBytes("UTF-8"));
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return sb.toString();
	}
}
