package com.meetingninja.csse.notes.tasks;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.database.NotesDatabaseAdapter;
import com.meetingninja.csse.extras.NinjaToastUtil;

public class DeleteNoteTask extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... params) {
		boolean result = false;
		try {
			String noteID = params[0];
			result = NotesDatabaseAdapter.deleteNote(noteID).booleanValue();
		} catch (IOException e) {
			Log.e("DB Adapter", "Error: DeleteNote failed");
			Log.e("DeleteNoteIO", e.toString());
		} catch (Exception e) {
			Log.e("DeleteNoteE", e.toString());
		}

		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Context app = ApplicationController.getInstance().getApplicationContext();
		if (result) {
			NinjaToastUtil.show(app, "Note deleted");
		}
	}
}
