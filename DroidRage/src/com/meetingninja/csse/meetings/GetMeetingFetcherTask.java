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
import com.meetingninja.csse.database.MeetingDatabaseAdapter;
import com.meetingninja.csse.database.UserDatabaseAdapter;

/**
 * Represents an asynchronous task to receive meetings from the database
 */
public class GetMeetingFetcherTask extends AsyncTask<String, Void, Meeting> {
	private AsyncResponse<Meeting> delegate;

	public GetMeetingFetcherTask(AsyncResponse<Meeting> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Meeting doInBackground(String... params) {
		Meeting meeting = new Meeting();

		try {
			meeting = MeetingDatabaseAdapter.getMeetingInfo(params[0]);
		} catch (IOException e) {
			Log.e("MeetingFetch", "Error: Unable to get meeting info");
			Log.e("MEETINGS_ERR", e.getLocalizedMessage());
		}

		return meeting;
	}

	@Override
	protected void onPostExecute(Meeting meeting) {
		super.onPostExecute(meeting);
		delegate.processFinish(meeting);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
