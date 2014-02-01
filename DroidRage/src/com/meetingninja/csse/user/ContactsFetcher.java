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
package com.meetingninja.csse.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.User;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class ContactsFetcher extends AsyncTask<String, Void, List<User>> {

	private AsyncResponse<List<User>> delegate;

	private static final String TAG = ContactsFetcher.class.getSimpleName();

	public ContactsFetcher(AsyncResponse<List<User>> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected List<User> doInBackground(String... params) {
		List<User> contacts = new ArrayList<User>();

		try {
			System.out.println("param sent:  "+params[0]);
			String userID = params[0];
			contacts = UserDatabaseAdapter.getContacts(userID);
			//user.setID(userID);
		} catch (IOException e) {
			Log.e(TAG, "Error: Unable to get contacts");
			Log.e(TAG, e.getLocalizedMessage());
		}

		return contacts;
	}

	@Override
	protected void onPostExecute(List<User> contacts) {
		super.onPostExecute(contacts);
		delegate.processFinish(contacts);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
