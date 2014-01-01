package com.android.meetingninja.notes;

import java.util.List;

import objects.Note;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.meetingninja.R;

/**
 * A class to display the Notes in a specific format for the items of the list.
 * This class uses the note_item XML file.
 * 
 * @author moorejm
 * 
 */
class NoteItemAdapter extends ArrayAdapter<Note> {
	// declaring our ArrayList of items
	private List<Note> notes;

	/*
	 * Override the constructor to initialize the list to display
	 */
	public NoteItemAdapter(Context context, int textViewResourceId,
			List<Note> notes) {
		super(context, textViewResourceId, notes);
		this.notes = notes;
	}

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.note_item, null);
		}

		// Setup from the note_item XML file
		Note note = notes.get(position);
		if (note != null) {
			TextView noteName = (TextView) v.findViewById(R.id.noteName);
			TextView noteContent = (TextView) v.findViewById(R.id.noteContent);

			if (noteName != null) {
				noteName.setText(note.getName());
			}
			if (noteContent != null) {
				String content = note.getContent();
				int max_length = 200;
				if (content.length() > max_length)
					noteContent.setText(content.substring(0, max_length)
							+ "...");
				else
					noteContent.setText(content);
			}
		}

		return v;
	}
}