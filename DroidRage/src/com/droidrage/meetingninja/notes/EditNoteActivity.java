package com.droidrage.meetingninja.notes;

import com.droidrage.meetingninja.MainActivity;
import com.droidrage.meetingninja.R;
import com.droidrage.meetingninja.R.id;
import com.droidrage.meetingninja.R.layout;
import com.droidrage.meetingninja.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class EditNoteActivity extends Activity {

	private Intent getNote;
	String noteContent;
	String noteName;
	int noteID;
	EditText textEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		// Show the Up button in the action bar.
		setupActionBar();

		getNote = getIntent();
		noteContent = getNote.getStringExtra("NoteContent");
		noteName = getNote.getStringExtra("NoteName");
		noteID = getNote.getIntExtra("NoteID", 0);

		textEditor = (EditText) findViewById(R.id.editText);

		textEditor.setText(noteContent);

		setTitle("Edit '" + noteName + "'");
	}

	public void save(View view) {
		Intent goNotes = new Intent(this, MainActivity.class);

		goNotes.putExtra("NoteID", noteID);
		goNotes.putExtra("NoteContent", textEditor.getText().toString());
		goNotes.putExtra("NoteName", noteName);
		goNotes.putExtra("Update", true);
		goNotes.putExtra("Fragment", "notes");

		startActivity(goNotes);
	}

	public void discard(View view) {
		Intent goNotes = new Intent(this, MainActivity.class);

		goNotes.putExtra("Update", false);
		goNotes.putExtra("Fragment", "notes");

		startActivity(goNotes);
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
		}
		return super.onOptionsItemSelected(item);
	}

}
