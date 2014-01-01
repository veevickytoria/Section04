package com.android.meetingninja.notes;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.NotesDatabaseAdapter;

/**
 * Represents an asynchronous task to receive notes from the database
 */
class NoteFetcherTask extends AsyncTask<String, Void, List<Note>> {
	private AsyncResponse<List<Note>> delegate;

	public NoteFetcherTask(AsyncResponse<List<Note>> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected List<Note> doInBackground(String... params) {
		List<Note> dbNotes = new ArrayList<Note>();

		try {
			dbNotes = NotesDatabaseAdapter.getNotes(params[0]);
		} catch (Exception e) {
			Log.e("NotesFetch", "Error getting notes");
			Log.e("NOTES_ERR", e.toString());
		}

		return dbNotes;
	}

	@Override
	protected void onPostExecute(List<Note> list) {
		super.onPostExecute(list);
		delegate.processFinish(list);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
