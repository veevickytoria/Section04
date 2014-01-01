package com.android.meetingninja.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.meetingninja.MainActivity;
import com.android.meetingninja.R;
import com.android.meetingninja.database.local.SQLiteAdapter;

public class CreateNoteActivity extends Activity {

	private Intent getNote;
	String noteContent;
	String noteName;
	int noteID;
	EditText textEditor;
	private SQLiteAdapter mySQLiteAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mySQLiteAdapter = new SQLiteAdapter(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note);
		// Show the Up button in the action bar.
		setupActionBar();

		getNote = getIntent();

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
		System.out.println("Echo: "+s);
		
		mySQLiteAdapter.openToWrite();
		mySQLiteAdapter.insertNote("", s);

		mySQLiteAdapter.close();
		
		goNotes.putExtra("TypeL", "Create");
		goNotes.putExtra("Update", true);
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
