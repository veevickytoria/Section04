package com.android.meetingninja.meetings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.MeetingDatabaseAdapter;

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
			dbMeetings = MeetingDatabaseAdapter.getMeetings(params[0]);
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