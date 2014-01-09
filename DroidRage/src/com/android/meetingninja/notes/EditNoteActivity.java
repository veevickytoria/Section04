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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.meetingninja.R;
import com.android.meetingninja.database.local.SQLiteNoteAdapter;

public class EditNoteActivity extends Activity {

	private Bundle extras;
	private EditText mTextEditor, mNoteTitle;

	private String noteID;
	private String noteName;
	private String noteContent;

	private SQLiteNoteAdapter mySQLiteAdapter;
	private Note newNote;
	private int listPosition;

	public static final String EXTRA_ID = "NoteID";
	public static final String EXTRA_NAME = "NoteName";
	public static final String EXTRA_CONTENT = "NoteContent";

	private static final String TAG = EditNoteActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		// Show the Up button in the action bar.
		setupActionBar();
		mySQLiteAdapter = new SQLiteNoteAdapter(this);

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
		
		// Scroll to the top of the note content
		// https://stackoverflow.com/a/3310376
		final ScrollView scroller = (ScrollView) findViewById(R.id.contentScroller);
		scroller.post(new Runnable() {

			@Override
			public void run() {
				scroller.fullScroll(ScrollView.FOCUS_UP);

			}
		});

		setTitle("Edit '" + noteName.trim() + "'");
	}

	public boolean onActionBarItemSelected(View v) {
		switch (v.getId()) {
		case R.id.action_done:
			save();
			break;
		case R.id.action_cancel:
			cancel();
			break;
		}

		return true;

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

	private final View.OnClickListener mActionBarListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);

		}
	};

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Make an Ok/Cancel ActionBar
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,
				new LinearLayout(this), false);

		View cancelActionView = actionBarButtons
				.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(mActionBarListener);

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mActionBarListener);

		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarButtons);
		// end Ok-Cancel ActionBar

	}

}
