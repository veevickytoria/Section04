package com.droidrage.meetingninja.user;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Name of the SharedPreferences file
	private static final String PREF_NAME = SessionManager.class
			.getSimpleName();

	// Sharedpref username
	public static final String USER = "username";

	// Sharedpref login state
	public static final String LOGGED_IN = "isLoggedIn";

	// Sharedpref user ID
	public static final String USERID = "userID";

	// Shared Preferences
	private SharedPreferences pref;

	// Editor for Shared preferences
	private Editor editor;

	// Context
	Context _context;

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String user) {
		// Storing login value as TRUE
		editor.putString(USER, user);
		editor.putBoolean(LOGGED_IN, true);
		editor.putString(USERID, "1234");
		// commit changes
		editor.commit();
	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(USER, pref.getString(USER, null));
		user.put(USERID, pref.getString(USERID, null));

		// return user
		return user;
	}

	public void checkLogin() {
		if (!this.isLoggedIn()) {
			// Go back to login page
			Intent login = new Intent(_context, LoginActivity.class);
			// Close all other activities
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// Add flag to start new activity
			login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// User cannot go back to this activity
			login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// Show no animation when launching login page
			login.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			// Show login page
			_context.startActivity(login);
		}

	}

	public void clear() {
		editor.clear();
		editor.commit();
	}

	public void logoutUser() {
		clear();
		// Will always go to login page after clearing preferences
		checkLogin();

	}

	public boolean isLoggedIn() {
		return pref.getBoolean(LOGGED_IN, false);
	}
}