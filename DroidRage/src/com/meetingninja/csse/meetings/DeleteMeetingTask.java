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

import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;

public class DeleteMeetingTask implements AsyncResponse<Boolean> {

	private MeetingDeleterTask deleter = null;

	public DeleteMeetingTask() {
		this.deleter = new MeetingDeleterTask(this);
	}

	public void deleteMeeting(String meetingID) {
		this.deleter.execute(meetingID);
	}

	@Override
	public void processFinish(Boolean result) {
		if (!result) {
			// do something?
		}
	}
}

class MeetingDeleterTask extends AsyncTask<String, Void, Boolean> {
	private AsyncResponse<Boolean> delegate;

	public MeetingDeleterTask(AsyncResponse<Boolean> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			return MeetingDatabaseAdapter.deleteMeeting(params[0]);
		} catch (IOException e) {
			Log.e("MeetingDelete", "Error: Unable to delete meeting");
			Log.e("MeetingS_ERR", e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean b) {
		super.onPostExecute(b);
		delegate.processFinish(b);
	}

}
