package com.droidrage.meetingninja.meetings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.database.AsyncResponse;
import com.droidrage.meetingninja.database.DatabaseAdapter;
import com.droidrage.meetingninja.database.MeetingDatabaseAdapter;

import objects.Meeting;
import android.os.AsyncTask;
import android.util.Log;

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