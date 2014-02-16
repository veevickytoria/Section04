package com.meetingninja.csse.projects;

import objects.Note;

import com.meetingninja.csse.notes.NotesFragment;

public class NotesProjectFragment extends NotesFragment{
	
	ViewProjectActivity pCont;
	
	@Override
	protected void deleteNote(Note note){
		
	}
	
	public NotesProjectFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}
	
}
