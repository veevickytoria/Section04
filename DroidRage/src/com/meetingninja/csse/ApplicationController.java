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

import java.net.HttpURLConnection;
import java.net.URL;

import objects.User;
import objects.parcelable.UserParcel;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.meetingninja.csse.database.BaseDatabaseAdapter;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.local.SQLiteHelper;
import com.meetingninja.csse.database.local.SQLiteNoteAdapter;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.user.ProfileActivity;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

// http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
public class ApplicationController extends Application {

	public static final String TAG = ApplicationController.class
			.getSimpleName();

	/**
	 * Singleton ApplicationController
	 */
	private static ApplicationController sInstance;

	public static synchronized ApplicationController getInstance() {
		return sInstance;
	}

	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		SessionManager.getInstance().init();
		initializeParseSDK();
	}

	/**
	 * Sign-up for push-notifications from Parse.com
	 */
	public void initializeParseSDK() {
		Parse.initialize(this, getString(R.string.parse_application_id),
				getString(R.string.parse_client_key));

		// Specify a Activity to handle all pushes
		PushService.setDefaultPushCallback(this, MainActivity.class);

		Log.d(TAG, "Saving Parse Installation");
		ParseInstallation.getCurrentInstallation().saveInBackground();

	}

	public void loadUsers() {
		SQLiteUserAdapter sqlite = new SQLiteUserAdapter(
				getApplicationContext());
		sqlite.clear();
		sqlite.cacheUsers();
		sqlite.close();
	}

	public void loadNotes() {
		SQLiteNoteAdapter sqlite = new SQLiteNoteAdapter(
				getApplicationContext());
		// sqlite.cacheNotes(false);
		// sqlite.cacheNotes(true);
		sqlite.close();
	}

	public boolean isConnectedToBackend(Activity activity) {
		final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();

		try {
	        if (netInfo != null && netInfo.isConnected()) {
	            // Network is available but check if we can get access from the
	            // network.
	            URL url = new java.net.URL(BaseDatabaseAdapter.getBaseUrl());
	            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	            urlc.setRequestProperty("Connection", "close");
	            urlc.setConnectTimeout(2000); // Timeout 2 seconds.
	            urlc.connect();

	            if (urlc.getResponseCode() == 200) // Successful response.
	            {
	            	urlc.disconnect();
	            	return true;
	            } else {
	                Log.d("NO INTERNET", "NO INTERNET");
	                Crouton.makeText(activity, "Internet Connectivity Issue", Style.ALERT).show();
	                return false;
	            }
	        } else {
	        	Crouton.makeText(activity, "Internet Connection Unavailable", Style.ALERT).show();
	        	return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public void logout() {
		parseSDKLogout();
		clearSQLiteTables();
		SessionManager.getInstance().logoutUser();
	}

	public void clearSQLiteTables() {
		SQLiteHelper mySQLiteHelper = SQLiteHelper.getInstance(this);
		mySQLiteHelper.onUpgrade(mySQLiteHelper.getReadableDatabase(), 1, 1);
	}

	public void parseSDKLogout() {
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.remove("userId");
		installation.remove("user");
		installation.saveInBackground();
		ParseUser.logOut();
	}

	public void showUser(Context context, User user) {
		Intent profileIntent = new Intent(context, ProfileActivity.class);
		profileIntent.putExtra(Keys.User.PARCEL, new UserParcel(user));
		getInstance().startActivity(profileIntent);
	}

	/**
	 * Adds the specified request to the global queue, if tag is specified then
	 * it is used else Default TAG is used.
	 *
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(tag.isEmpty() ? TAG : tag);

		VolleyLog.d("Adding request to queue: %s", req.getUrl());

		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 *
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important to
	 * specify a TAG so that the pending/ongoing requests can be cancelled.
	 *
	 * @param tag
	 */
	public void cancelPendingRequests(String tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}
