package com.meetingninja.csse.projects;

import objects.Note;
import objects.parcelable.NoteParcel;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.notes.NotesFragment;
import com.meetingninja.csse.notes.ViewNoteActivity;
import com.meetingninja.csse.notes.tasks.DeleteNoteTask;

public class NotesProjectFragment extends NotesFragment{

	private ViewProjectActivity pCont;
	@Override
	protected void delete(Note note) {
		pCont.deleteNote(note.getID());
	}
	@Override
	public void createNote(){
		pCont.createNote();
	}

	public NotesProjectFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		return; // don't want to duplicate super menu
	}
	@Override
	protected void clickedNote(int position){
		Intent intent = new Intent(getActivity(),ViewNoteProjectActivity.class);
		Note clickedNote = noteAdpt.getItem(position);
		intent.putExtra(Keys.Note.PARCEL, new NoteParcel(clickedNote));
		
		startActivityForResult(intent, 8);
	}
}
