package com.meetingninja.csse.meetings;

import objects.Meeting;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;

public class MeetingSaveTask extends AsyncTask<Meeting, Void, String> {
	private AsyncResponse<String> delegate;

	public MeetingSaveTask(AsyncResponse<String> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected String doInBackground(Meeting... params) {
		Meeting m = params[0];
		try {
			String userID = SessionManager.getInstance().getUserID();
			m = MeetingDatabaseAdapter.createMeeting(userID, m);
		} catch (Exception e) {
			Log.e("MeetingSave", "Error: Failed to save meeting");
			Log.e("MEETING_ERR", e.getLocalizedMessage());
			return null;
		}
		return m.getID();
	}

	@Override
	protected void onPostExecute(String result) {
		delegate.processFinish(result);
		super.onPostExecute(result);
	}

}