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
package com.meetingninja.csse.meetings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

/**
 * Represents an asynchronous task to receive meetings from the database
 */
public class MeetingFetcherTask extends AsyncTask<String, Void, List<Meeting>> {
	private AsyncResponse<List<Meeting>> delegate;

	public MeetingFetcherTask(AsyncResponse<List<Meeting>> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected List<Meeting> doInBackground(String... params) {
		List<Meeting> dbMeetings = new ArrayList<Meeting>();

		try {
			dbMeetings = UserDatabaseAdapter.getMeetings(params[0]);
		} catch (IOException e) {
			Log.e("MeetingFetch", "Error: Unable to get meetings");
			Log.e("MEETINGS_ERR", e.getLocalizedMessage());
		}

		return dbMeetings;
	}

	@Override
	protected void onPostExecute(List<Meeting> list) {
		super.onPostExecute(list);
		delegate.processFinish(list);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
