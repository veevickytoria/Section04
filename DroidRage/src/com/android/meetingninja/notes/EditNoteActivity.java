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

import objects.Note;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.meetingninja.R;
import com.android.meetingninja.database.local.NoteDBAdapter;

public class EditNoteActivity extends Activity {

	private Bundle extras;
	private EditText mTextEditor, mNoteTitle;

	private String noteID;
	private String noteName;
	private String noteContent;

	private NoteDBAdapter mySQLiteAdapter;
	private Note newNote;
	private int listPosition;

	public static final String EXTRA_ID = "NoteID";
	public static final String EXTRA_NAME = "NoteName";
	public static final String EXTRA_CONTENT = "NoteContent";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mySQLiteAdapter = new NoteDBAdapter(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		// Show the Up button in the action bar.
		setupActionBar();

		mTextEditor = (EditText) findViewById(R.id.noteContentEditor);
		mNoteTitle = (EditText) findViewById(R.id.noteTitleEditor);

		extras = getIntent().getExtras();
		if (extras != null) {
			listPosition = extras.getInt("listPosition", -1);
			noteID = extras.getString(EXTRA_ID);
			noteName = extras.getString(EXTRA_NAME);
			noteContent = extras.getString(EXTRA_CONTENT);
		} else {
			noteName = "New Note";
			noteContent = "";
		}

		mNoteTitle.setText(noteName);
		mTextEditor.setText(noteContent);

		setTitle("Edit '" + noteName.trim() + "'");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			save();
			break;
		case R.id.btnCancel:
			cancel();
			break;
		default:
			break;
		}

	}

	public void save() {
		String content = mTextEditor.getText().toString();
		String title = mNoteTitle.getText().toString();
		Intent intentMessage = new Intent();

		intentMessage.putExtra("Fragment", "notes");
		intentMessage.putExtra("listPosition", listPosition);
		intentMessage.putExtra(EXTRA_ID, noteID);
		intentMessage.putExtra(EXTRA_NAME, title);
		intentMessage.putExtra(EXTRA_CONTENT, content);

		newNote = Note.create(noteID, title, content);

		mySQLiteAdapter.updateNote(newNote);

		setResult(RESULT_OK, intentMessage);
		finish();
	}

	public void cancel() {
		Intent intentMessage = new Intent();
		intentMessage.putExtra("Fragment", "notes");

		setResult(RESULT_CANCELED, intentMessage);
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.edit_note_action_save:
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
