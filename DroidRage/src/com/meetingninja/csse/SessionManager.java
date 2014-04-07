/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.meetingninja.csse;

import java.util.HashMap;

import objects.User;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.meetingninja.csse.user.LoginActivity;

// http://stackoverflow.com/a/19613702
public class SessionManager {
	/**
	 * Singleton instance to manage SharedPreferences
	 */
	private static volatile SessionManager sInstance;
	private Context _context;
	// Shared Preferences
	private SharedPreferences pref;

	// Name of the SharedPreferences file
	private static final String PREF_NAME = SessionManager.class
			.getSimpleName();

	// User details
	public static final String KEY_USERID = "edu.meetingninja.rhit.preferences.userID";
	public static final String USER = "edu.meetingninja.rhit.preferences.username";
	public static final String EMAIL = "edu.meetingninja.rhit.preferences.email";
	public static final String PHONE = "edu.meetingninja.rhit.preferences.phone";
	public static final String COMPANY = "edu.meetingninja.rhit.preferences.company";
	public static final String TITLE = "edu.meetingninja.rhit.preferences.title";
	public static final String LOCATION = "edu.meetingninja.rhit.preferences.location";
	public static final String PAGE = "edu.meetingninja.rhit.preferences.page";

	public static final String TIME = "edu.meetingninja.rhit.preferences.syncedTime";

	// Sharedpref login state
	public static final String LOGGED_IN = "edu.meetingninja.rhit.preferences.isLoggedIn";

	/**
	 * Gets a singleton instance of the session manager
	 * 
	 * @return
	 */
	public static synchronized SessionManager getInstance() {
		if (sInstance == null)
			sInstance = new SessionManager();
		return sInstance;
	}

	public void init() {
		this._context = ApplicationController.getInstance()
				.getApplicationContext();
		pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	private SessionManager() {
	}

	/**
	 * Create login session with userID
	 * */
	public synchronized void createLoginSession(String userID) {
		Editor editor = pref.edit();
		// Storing login value as TRUE
		editor.putString(USER, "user");
		editor.putString(KEY_USERID, userID);
		editor.putBoolean(LOGGED_IN, true);
		// commit changes
		editor.commit();
	}

	/**
	 * Create login session with user object
	 * */
	public synchronized void createLoginSession(User u) {
		Editor editor = pref.edit();
		// Storing login value as TRUE
		// editor.putString(KEY_USERID, u.getUserID());
		editor.putString(USER, u.getDisplayName());
		editor.putString(EMAIL, u.getEmail());
		editor.putString(PHONE, u.getPhone());
		editor.putString(COMPANY, u.getCompany());
		editor.putString(TITLE, u.getTitle());
		editor.putString(LOCATION, u.getLocation());
		editor.putBoolean(LOGGED_IN, true);

		// commit changes
		editor.commit();
	}

	/**
	 * Get stored session data
	 * */
	public synchronized HashMap<String, String> getUserDetails() {
		HashMap<String, String> details = new HashMap<String, String>();

		details.put(KEY_USERID, pref.getString(KEY_USERID, null));
		details.put(USER, pref.getString(USER, null));
		details.put(EMAIL, pref.getString(EMAIL, null));
		details.put(PHONE, pref.getString(PHONE, null));
		details.put(COMPANY, pref.getString(COMPANY, null));
		details.put(TITLE, pref.getString(TITLE, null));
		details.put(LOCATION, pref.getString(LOCATION, null));
		// return details
		return details;
	}

	public synchronized static String getUserID() {
		return SessionManager.getInstance().getUserDetails().get(KEY_USERID);
	}

	public synchronized void checkLogin() {
		if (!this.isLoggedIn()) {
			// Go back to login page
			Intent login = new Intent(_context, LoginActivity.class);
			// Close all other activities
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// Add flag to start new activity
			login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// User cannot go back to this activity
			// login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// Show no animation when launching login page
			login.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			// Show login page
			_context.startActivity(login);
		}

	}

	public synchronized void clear() {
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	public synchronized void logoutUser() {
		clear();
		// Will always go to login page after clearing preferences
		checkLogin();

	}

	public synchronized boolean isLoggedIn() {
		return pref.getBoolean(LOGGED_IN, false);
	}

	/**
	 * Store the last pressed navigation drawer position
	 */
	public synchronized void setPage(int position) {
		Editor editor = pref.edit();
		editor.putInt(PAGE, position);
		editor.commit();
	}

	/**
	 * Get the last pressed navigation drawer position. If doesn't exist,
	 * returns 0.
	 * 
	 * @return the last pressed navigation drawer position, or 0, if doesn't
	 *         exist
	 */
	public synchronized int getPage() {
		return pref.getInt(PAGE, 0);
	}

	public synchronized long getLastSyncTime() {
		return pref.getLong(TIME, System.currentTimeMillis());
	}

	public synchronized boolean needsSync() {
		int _3min = 3 * 60 * 1000;
		if (System.currentTimeMillis() - getLastSyncTime() >= _3min) {
			return true;
		}
		return false;
	}

	public synchronized void setSynced() {
		Editor editor = pref.edit();
		editor.putLong(TIME, System.currentTimeMillis());
		editor.commit();
	}
}
