package com.meetingninja.csse.notes;

import objects.Note;
import objects.parcelable.NoteParcel;
import objects.parcelable.ParcelDataFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.notes.tasks.DeleteNoteTask;

public class ViewNoteActivity extends Activity {

	public static final int REQUEST_CODE = 1;
	private Bundle extras;
	private TextView contentsText;
	private TextView titleText;
	private TextView creatorText;
	private TextView lastModifiedText;
	private Note displayedNote;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		// Show the Up button in the action bar.
		setupActionBar();
		setupViews();
		extras = getIntent().getExtras();
		if (extras != null) {
			// noteContent = extras.getString("NoteContent");
			// noteName = extras.getString("NoteName");
			// noteID = extras.getString("NoteID");
			// creator = extras.getString(EditNoteActivity.EXTRA_CREATOR);
			displayedNote = new ParcelDataFactory(extras).getNote();
		}
		refresh();
	}

	private void setupViews() {
		titleText = (TextView) findViewById(R.id.titleText);
		creatorText = (TextView) findViewById(R.id.note_creator);
		contentsText = (TextView) findViewById(R.id.contentsText);
		lastModifiedText = (TextView) findViewById(R.id.editedText);
	}

	protected void refresh() {
		if (displayedNote != null) {
			titleText.setText(displayedNote.getTitle());
			creatorText.setText("Created by: " + displayedNote.getCreatedBy());
			contentsText.setText(displayedNote.getContent());
			lastModifiedText.setText(displayedNote.getDateCreated());

			setTitle(displayedNote.getTitle().trim());
		}
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
		getMenuInflater().inflate(R.menu.menu_view_note, menu);

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
			finish();
			return true;

		case R.id.note_edit_action:
			edit();
			return true;
		case R.id.note_discard_action:
			AlertDialogUtil.deleteDialog(this, "noe", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					delete(displayedNote);
				}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void delete(Note note) {
		new DeleteNoteTask().execute(note.getID());
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
//			switch (resultCode) {
//			case EditNoteActivity.REQUEST_CODE:
				displayedNote = new ParcelDataFactory(data.getExtras()).getNote();
				refresh();
//				break;
//			default:
//				break;
//			}
		}
	}

	private void edit() {
		Intent editNote = new Intent(ViewNoteActivity.this,EditNoteActivity.class);
		editNote.putExtra(Keys.Note.PARCEL, new NoteParcel(displayedNote));
//		editNote.putExtras(extras);
		startActivityForResult(editNote, EditNoteActivity.REQUEST_CODE);
	}

}