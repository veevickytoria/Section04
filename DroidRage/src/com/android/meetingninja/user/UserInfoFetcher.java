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

import java.io.IOException;

import objects.User;
import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.UserDatabaseAdapter;

public class UserInfoFetcher extends AsyncTask<String, Void, User> {

	private AsyncResponse<User> delegate;

	private static final String TAG = UserInfoFetcher.class.getSimpleName();

	public UserInfoFetcher(AsyncResponse<User> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected User doInBackground(String... params) {
		User user = null;

		try {
			// params = userID
			user = UserDatabaseAdapter.getUserInfo(params[0]);
		} catch (IOException e) {
			Log.e(TAG, "Error: Unable to get userinfo");
			Log.e(TAG, e.getLocalizedMessage());
		}

		return user;
	}

	@Override
	protected void onPostExecute(User user) {
		super.onPostExecute(user);
		delegate.processFinish(user);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
