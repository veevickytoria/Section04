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

import objects.Note;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.local.SQLiteNoteAdapter;
import com.meetingninja.csse.extras.MyDateUtils;

public class EditNoteActivity extends Activity {

	private static final String TAG = EditNoteActivity.class.getSimpleName();

	private Bundle extras;
	private EditText mTextEditor, mNoteTitle;

	private SQLiteNoteAdapter mySQLiteAdapter;
	private Note displayedNote;
	private int listPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		// Show the Up button in the action bar.
		setupActionBar(false);
		mySQLiteAdapter = new SQLiteNoteAdapter(this);

		mTextEditor = (EditText) findViewById(R.id.noteContentEditor);
		mNoteTitle = (EditText) findViewById(R.id.noteTitleEditor);

		extras = getIntent().getExtras();
		if (extras != null) {
			listPosition = extras.getInt("listPosition", -1);
			displayedNote = (Note) extras.getParcelable(Keys.Note.PARCEL);
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
			cancel();
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
		displayedNote.setDateCreated(MyDateUtils.JODA_SERVER_DATE_FORMAT
				.print(now));

		Intent intentMessage = new Intent();

		intentMessage.putExtra("listPosition", listPosition);
		intentMessage.putExtra(Keys.Note.PARCEL, displayedNote);

		if (!(displayedNote.getID() == null || displayedNote.getID().isEmpty()))
			mySQLiteAdapter.updateNote(displayedNote);

		setResult(RESULT_OK, intentMessage);
		finish();
	}

	public void cancel() {
		Intent intentMessage = new Intent();

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
	private void setupActionBar(boolean okCancel) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		getActionBar().setTitle("");
		getActionBar().setHomeButtonEnabled(!okCancel);
		getActionBar().setDisplayShowHomeEnabled(!okCancel);
		getActionBar().setDisplayHomeAsUpEnabled(!okCancel);
		getActionBar().setDisplayShowTitleEnabled(!okCancel);

		if (okCancel) {
			// Make an Ok/Cancel ActionBar
			View actionBarButtons = inflater
					.inflate(R.layout.actionbar_ok_cancel, new LinearLayout(
							this), false);

			View cancelActionView = actionBarButtons
					.findViewById(R.id.action_cancel);
			cancelActionView.setOnClickListener(mActionBarListener);

			View doneActionView = actionBarButtons
					.findViewById(R.id.action_done);
			doneActionView.setOnClickListener(mActionBarListener);

			getActionBar().setCustomView(actionBarButtons);
			getActionBar().setDisplayShowCustomEnabled(okCancel);
			// end Ok-Cancel ActionBar
		}

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
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
