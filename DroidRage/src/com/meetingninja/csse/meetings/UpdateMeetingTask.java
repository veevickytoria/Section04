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

import objects.Meeting;

import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;

public class UpdateMeetingTask implements AsyncResponse<Meeting> {
	private MeetingUpdateTask updater;

	public UpdateMeetingTask() {
		this.updater = new MeetingUpdateTask(this);
	}

	public void updateMeeting(Meeting meeting) {
		this.updater.execute(meeting);
	}

	@Override
	public void processFinish(Meeting result) {
		// TODO Auto-generated method stub

	}

}

class MeetingUpdateTask extends AsyncTask<Meeting, Void, Meeting> {

	private AsyncResponse<Meeting> delegate;

	public MeetingUpdateTask(AsyncResponse<Meeting> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Meeting doInBackground(Meeting... params) {
		Meeting m = null;
		try {
			m = MeetingDatabaseAdapter.editMeeting(params[0]);
		} catch (IOException e) {
			System.out.println(params[0]);
			Log.e("MeetingUpdate", "Error: Unable to update Meeting info");
			Log.e("MeetingS_ERR", e.getLocalizedMessage());
		}
		return m;
	}

	@Override
	protected void onPostExecute(Meeting m) {
		super.onPostExecute(m);
		delegate.processFinish(m);
	}

}
