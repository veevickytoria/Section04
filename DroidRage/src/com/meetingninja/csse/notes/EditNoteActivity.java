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
package com.meetingninja.csse.notes;

import java.io.IOException;

import objects.Note;
import objects.parcelable.NoteParcel;
import objects.parcelable.ParcelDataFactory;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.NotesDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteNoteAdapter;
import com.meetingninja.csse.extras.NinjaDateUtils;

public class EditNoteActivity extends Activity implements AsyncResponse<String> {

	private Bundle extras;
	private EditText mTextEditor, mNoteTitle;

	private Note displayedNote;
	private int listPosition;
	private boolean isCreationMode = false;
	private CreateNoteTask createNoteTask;
	private UpdateNoteTask updateNoteTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		// Show the Up button in the action bar.
		setupActionBar(false);
		new SQLiteNoteAdapter(this);

		mTextEditor = (EditText) findViewById(R.id.noteContentEditor);
		mNoteTitle = (EditText) findViewById(R.id.noteTitleEditor);

		extras = getIntent().getExtras();
		
		if (extras.getBoolean(Note.CREATE_NOTE, false)) {
			displayedNote = new Note();
			isCreationMode = true;
			displayedNote.setCreatedBy(SessionManager.getUserID());
		} else if (extras != null) {
			listPosition = extras.getInt("listPosition", -1);
			displayedNote = new ParcelDataFactory(extras).getNote();
			mNoteTitle.setText(displayedNote.getTitle());
			mTextEditor.setText(displayedNote.getContent());
		} else {
			displayedNote = new Note();
		}

		mNoteTitle.setText(displayedNote.getTitle());
		mTextEditor.setText(displayedNote.getContent());

		// Scroll to the top of the note content
		// https://stackoverflow.com/a/3310376
		final ScrollView scroller = (ScrollView) findViewById(R.id.contentScroller);
		scroller.post(new Runnable() {

			@Override
			public void run() {
				scroller.fullScroll(View.FOCUS_UP);
			}
		});
	}

	public boolean onActionBarItemSelected(View v) {
		switch (v.getId()) {
		case R.id.action_done:
			save();
			break;
		case R.id.action_cancel:
			discard();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		save();
	}

	public void save() {
		String title = mNoteTitle.getText().toString().trim();
		String content = mTextEditor.getText().toString().trim();
		DateTime now = DateTime.now();

		displayedNote.setTitle(title);
		displayedNote.setContent(content);
		displayedNote.setDateCreated(NinjaDateUtils.JODA_SERVER_DATE_FORMAT.print(now));

		Intent backToNotes = new Intent();

		if (isCreationMode) {
			createNoteTask = new CreateNoteTask(this);
			createNoteTask.execute(displayedNote);
			return;
		} else {
			updateNoteTask = new UpdateNoteTask(this);
			updateNoteTask.execute(displayedNote);
		}

		backToNotes.putExtra("listPosition", listPosition);
		backToNotes.putExtra(Keys.Note.PARCEL, new NoteParcel(displayedNote));

		setResult(RESULT_OK, backToNotes);
		finish();
	}

	public void discard() {
		// Check if modifications have been made
		if (TextUtils.equals(displayedNote.getTitle(),this.mNoteTitle.getText())&& TextUtils.equals(displayedNote.getContent(),this.mTextEditor.getText())) {
			Intent intentMessage = new Intent();
			setResult(RESULT_CANCELED, intentMessage);
			finish();
		} else {
			new AlertDialog.Builder(this).setMessage("Are you sure you want to discard changes?").setCancelable(true).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int id) {
					Intent intentMessage = new Intent();
					setResult(RESULT_CANCELED, intentMessage);
					finish();
				}
			}).setNegativeButton("No", null).show();
		}
	}

	private final View.OnClickListener mActionBarListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);

		}
	};

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar(boolean okCancel) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		getActionBar().setTitle("");
		getActionBar().setHomeButtonEnabled(!okCancel);
		getActionBar().setDisplayShowHomeEnabled(!okCancel);
		getActionBar().setDisplayHomeAsUpEnabled(!okCancel);
		getActionBar().setDisplayShowTitleEnabled(!okCancel);

		if (okCancel) {
			// Make an Ok/Cancel ActionBar
			View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel, new LinearLayout(this), false);

			View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
			cancelActionView.setOnClickListener(mActionBarListener);

			View doneActionView = actionBarButtons.findViewById(R.id.action_done);
			doneActionView.setOnClickListener(mActionBarListener);

			getActionBar().setCustomView(actionBarButtons);
			getActionBar().setDisplayShowCustomEnabled(okCancel);
			// end Ok-Cancel ActionBar
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_note, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			save();
			return true;

		case R.id.edit_note_action_save:
			save();
			return true;

		case R.id.edit_note_action_discard:
			discard();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class CreateNoteTask extends AsyncTask<Note, Void, String> {

		private AsyncResponse<String> delegate;

		public CreateNoteTask(AsyncResponse<String> del) {
			this.delegate = del;
		}

		@Override
		protected String doInBackground(Note... params) {
			try {
				Note n = params[0];

				return NotesDatabaseAdapter.createNote(n);
			} catch (IOException e) {
				Log.e("DB Adapter", "Error: CreateNote failed");
				Log.e("CreateNoteIO", e.toString());
			} catch (Exception e) {
				Log.e("CreateNoteE", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			delegate.processFinish(result);
		}
	}

	private class UpdateNoteTask extends AsyncTask<Note, Void, String> {

		private AsyncResponse<String> delegate;

		public UpdateNoteTask(AsyncResponse<String> del) {
			this.delegate = del;
		}

		@Override
		protected String doInBackground(Note... params) {
			try {
				Note n = params[0];

				NotesDatabaseAdapter.updateNote(n);
				return n.getID();
			} catch (Exception e) {
				Log.e("update_ERR", e.toString());
			}
			return "-1";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			delegate.processFinish(result);
		}
	}

	@Override
	public void processFinish(String result) {
		Intent backToNotes = new Intent();

		backToNotes.putExtra("listPosition", listPosition);
		backToNotes.putExtra(Keys.Note.PARCEL, new NoteParcel(displayedNote));

		if (result != null) {
			//test with regular note creation
			if (isCreationMode){
				displayedNote.setID(result);
				setResult(RESULT_OK, backToNotes);
				finish();
			}
			setResult(RESULT_OK, backToNotes);
		} else {
			setResult(RESULT_CANCELED, backToNotes);
		}

		finish();
	}

}
