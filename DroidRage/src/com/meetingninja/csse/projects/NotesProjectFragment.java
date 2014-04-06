package com.meetingninja.csse.projects;

import objects.Note;

import com.meetingninja.csse.notes.NotesFragment;

public class NotesProjectFragment extends NotesFragment{

	ViewProjectActivity pCont;

	@Override
	protected void deleteNote(Note note){
		pCont.deleteNote(note);
	}

	@Override
	public void editNote(){
		pCont.createNote();
	}

	public NotesProjectFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}

}
