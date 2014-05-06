package com.meetingninja.csse.notes;

import java.util.ArrayList;
import java.util.List;

import objects.Note;

import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class NoteFetcher extends AsyncTask<String, Void, List<Note>>{
	private AsyncResponse<List<Note>> delegate;

	public NoteFetcher(AsyncResponse<List<Note>> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected List<Note> doInBackground(String... params) {
		List<Note> dbNotes = new ArrayList<Note>();

		try {
			dbNotes = UserDatabaseAdapter.getNotes(params[0]);
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