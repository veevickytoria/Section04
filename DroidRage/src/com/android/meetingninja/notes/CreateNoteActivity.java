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
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.meetingninja.MainActivity;
import com.android.meetingninja.R;
import com.android.meetingninja.database.local.NoteDBAdapter;

public class CreateNoteActivity extends Activity {

	private Bundle extras;
	String noteContent;
	String noteName;
	int noteID;
	EditText textEditor;
	private NoteDBAdapter sqliteAdapter;
	private static final String TAG = CreateNoteActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_note);
		// Show the Up button in the action bar.
		setupActionBar();

		extras = getIntent().getExtras();
		sqliteAdapter = new NoteDBAdapter(this);

		// noteContent = getNote.getStringExtra("NoteContent");
		// noteName = getNote.getStringExtra("NoteName");
		// noteID = getNote.getIntExtra("NoteID", 0);

		textEditor = (EditText) findViewById(R.id.nameText);

		textEditor.setText(noteContent);

		setTitle("Edit '" + noteName + "'");
	}

	public void createNewNote(View view) {
		Intent goNotes = new Intent(this, MainActivity.class);

		// goNotes.putExtra("NoteID", noteID);
		// goNotes.putExtra("NoteContent", textEditor.getText().toString());
		// goNotes.putExtra("NoteName", noteName);

		String s = textEditor.getText().toString();
		System.out.println("Echo: " + s);

		sqliteAdapter.insertNote(s, "");

		goNotes.putExtra("TypeL", "Create");
		goNotes.putExtra("Update", true);
		goNotes.putExtra("Fragment", "notes");

		startActivity(goNotes);
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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_save:
			Log.i(TAG, "Save this note");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
