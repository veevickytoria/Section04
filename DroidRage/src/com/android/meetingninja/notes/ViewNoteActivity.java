package com.android.meetingninja.notes;




import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import com.android.meetingninja.R;
import com.android.meetingninja.R.id;
import com.android.meetingninja.R.layout;
import com.android.meetingninja.R.menu;

public class ViewNoteActivity extends Activity {

	private Intent getNote;
	String noteContent;
	String noteName;
	int noteID;
	String creator;
	String editor;
	TextView contentsText;
	TextView titleText;
	TextView createdText;
	TextView editedText;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		// Show the Up button in the action bar.
		setupActionBar();
		refresh();
	}
	
	protected void refresh(){
		getNote = getIntent();
		noteContent = getNote.getStringExtra("NoteContent");
		noteName = getNote.getStringExtra("NoteName");
		noteID = getNote.getIntExtra("NoteID", 0);
		creator = getNote.getStringExtra(EditNoteActivity.EXTRA_CREATOR);

		contentsText = (TextView) findViewById(R.id.contentsText);
		titleText = (TextView) findViewById(R.id.titleText);
		createdText = (TextView) findViewById(R.id.createdText);
		editedText = (TextView) findViewById(R.id.editedText);

		contentsText.setText(noteContent);
		titleText.setText(noteName);
		createdText.setText("Created by: " + creator);
		editedText.setText("");

		setTitle("Viewing note '" + noteName + "'");
		
		Log.v("VIEW_NOTE", "Content: " + noteContent);
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
		getMenuInflater().inflate(R.menu.view_note, menu);
		
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
			
		case R.id.edit_note_action_edit:
			edit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setResult(resultCode, data);
		finish();
	}

	private void edit() {
		Intent editNote = new Intent(getWindow().getDecorView().getRootView().getContext(), EditNoteActivity.class);

		editNote.putExtras(getIntent().getExtras());
		startActivityForResult(editNote, 1);
	}

}