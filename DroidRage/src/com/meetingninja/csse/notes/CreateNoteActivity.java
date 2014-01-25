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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.meetingninja.csse.database.local.SQLiteNoteAdapter;

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

		textEditor = (EditText) findViewById(R.id.nameText);
		creatorText = (EditText) findViewById(R.id.CreatorTest);

		textEditor.setText(noteContent);

		setTitle("New Note");
	}

	public void createNewNote(View view) {
		Intent msgIntent = new Intent();
		String s = textEditor.getText().toString();
		noteCreator = creatorText.getText().toString();

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
		}
		return super.onOptionsItemSelected(item);
	}
}
