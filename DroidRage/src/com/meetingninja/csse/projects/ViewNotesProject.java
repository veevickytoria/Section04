package com.meetingninja.csse.projects;

import objects.Note;
import objects.parcelable.NoteParcel;
import android.content.Intent;

import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.notes.ViewNoteActivity;

public class ViewNotesProject extends ViewNoteActivity{

	@Override
	protected void delete(Note note) {
		Intent i = new Intent();
		i.putExtra(Keys.Note.PARCEL,new NoteParcel(note));
		setResult(5243, i);
		finish();
	}
}
