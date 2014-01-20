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
package com.android.meetingninja.notes;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.NotesDatabaseAdapter;
import com.android.meetingninja.database.UserDatabaseAdapter;

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
