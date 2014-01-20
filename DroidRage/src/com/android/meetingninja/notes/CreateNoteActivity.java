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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.meetingninja.R;
import com.android.meetingninja.database.local.SQLiteNoteAdapter;

public class CreateNoteActivity extends Activity {

	private static final String TAG = CreateNoteActivity.class.getSimpleName();

	private Bundle extras;
	private String noteContent;
	private String noteName;
	private String noteCreator;
	private EditText textEditor, creatorText;
	private SQLiteNoteAdapter sqliteAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_note);
		// Show the Up button in the action bar.
		setupActionBar();

		extras = getIntent().getExtras();
		sqliteAdapter = new SQLiteNoteAdapter(this);

		// noteContent = getNote.getStringExtra("NoteContent");
		// noteName = getNote.getStringExtra("NoteName");
		// noteID = getNote.getIntExtra("NoteID", 0);

		textEditor = (EditText) findViewById(R.id.nameText);
		creatorText = (EditText) findViewById(R.id.CreatorTest);

		textEditor.setText(noteContent);

		setTitle("Edit '" + ((noteName != null) ? noteName : "New Note") + "'");
	}

	public void createNewNote(View view) {
		Intent msgIntent = new Intent();
		// goNotes.putExtra("NoteID", noteID);
		// goNotes.putExtra("NoteContent", textEditor.getText().toString());
		// goNotes.putExtra("NoteName", noteName);

		String s = textEditor.getText().toString();
		noteCreator = creatorText.getText().toString();
		System.out.println("Echo: " + s);

		sqliteAdapter.insertNote(s, "", noteCreator);

		msgIntent.putExtra("TypeL", "Create");
		msgIntent.putExtra("Update", true);
		msgIntent.putExtra("Fragment", "notes");
		msgIntent.putExtra("NoteCreator", noteCreator);

		setResult(RESULT_OK, msgIntent);
		finish();

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

}
