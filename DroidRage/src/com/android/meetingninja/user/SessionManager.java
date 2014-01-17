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
package com.android.meetingninja.user;

import java.util.HashMap;

import objects.User;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

// 
// http://stackoverflow.com/a/19613702
public class SessionManager {
	/**
	 * Singleton instance to manage SharedPreferences
	 */
	private static SessionManager sInstance;
	private Context _context;
	// Shared Preferences
	private SharedPreferences pref;

	// Name of the SharedPreferences file
	private static final String PREF_NAME = SessionManager.class
			.getSimpleName();

	// User details
	public static final String KEY_USERID = "com.android.meetingninja.preferences.userID";
	public static final String USER = "com.android.meetingninja.preferences.username";
	public static final String EMAIL = "com.android.meetingninja.preferences.email";
	public static final String PHONE = "com.android.meetingninja.preferences.phone";
	public static final String COMPANY = "com.android.meetingninja.preferences.company";
	public static final String TITLE = "com.android.meetingninja.preferences.title";
	public static final String LOCATION = "com.android.meetingninja.preferences.location";
	public static final String PAGE = "com.android.meetingninja.preferences.page";

	// Sharedpref login state
	public static final String LOGGED_IN = "com.android.meetingninja.preferences.isLoggedIn";

	public static synchronized SessionManager getInstance() {
		if (sInstance == null)
			sInstance = new SessionManager();
		return sInstance;
	}

	public void init(Context context) {
		this._context = context.getApplicationContext();
		pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	private SessionManager() {
	}

	private SessionManager(Context context) {
		this.init(context);
	}

	/**
	 * Create login session with userID
	 * */
	public void createLoginSession(String userID) {
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
	public void createLoginSession(User u) {
		Editor editor = pref.edit();
		// Storing login value as TRUE
//		editor.putString(KEY_USERID, u.getUserID());
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
	public HashMap<String, String> getUserDetails() {
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

	public String getUserID() {
		return pref.getString(KEY_USERID, null);
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
			// login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// Show no animation when launching login page
			login.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			// Show login page
			_context.startActivity(login);
		}

	}

	public void clear() {
		Editor editor = pref.edit();
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
	
	public void setPage(int position) {
		Editor editor = pref.edit();
		editor.putInt(PAGE, position);
		editor.commit();
	}
	
	public int getPage() {
		return pref.getInt(PAGE, 0);
	}
}
