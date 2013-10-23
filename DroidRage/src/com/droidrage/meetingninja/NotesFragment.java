package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotesFragment extends Fragment {

	private List<Note> notes = new ArrayList<Note>();
	private NoteItemAdapter noteAdpt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.activity_notes, container, false);

		// TODO: Check for internet connection before receiving notes from DB
		// TODO: Display a something saying "no notes" if there are no notes
		// instead of having no notes appear
		initList();

		// if (notes.isEmpty()) {
		// v.findViewById(R.id.notes_empty).setVisibility(0);
		// v.findViewById(R.id.notes_empty).bringToFront();
		// } else {
		ListView lv = (ListView) v.findViewById(R.id.notesList);
		// lv.bringToFront();
		noteAdpt = new NoteItemAdapter(getActivity(), R.layout.note_item, notes);

		// setup listview
		lv.setAdapter(noteAdpt);
		// make list long-pressable
		registerForContextMenu(lv);

		// Item click event
		// TODO: Open a window to edit the note here
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,
					int position, long id) {
				// we know we are clicking a text view
				// RelativeLayout clicked = (RelativeLayout) v;
				// TextView name = (TextView) clicked.getChildAt(0);
				// TextView desc = (TextView) clicked.getChildAt(1);
				Note n = noteAdpt.getItem(position);
				CharSequence descStr = n.getContent().isEmpty() ? "No content"
						: n.getContent();
				String msg = String.format("%s: %s", n.getName(), descStr);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

			}
		});

		// Item long-click event
		// TODO: Add additional options and click-events to these options
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

				Note n = noteAdpt.getItem(aInfo.position);

				menu.setHeaderTitle("Options for " + n.getName());
				menu.add(1, 1, 1, "Add Content");
				menu.add(1, 2, 2, "Delete");

			}
		});

		// } // end else

		return v;
	}

	/**
	 * Initializes the list of notes. TODO: Get the notes from the database
	 */
	private void initList() {
		notes.add(new Note("Oct 7 PM Meeting"));
		Note meeting = new Note("Oct 9 Team Meeting");
		meeting.addContent("Max and Jordan worked on use cases.");
		meeting.addContent("Chris and Ruji worked on the frontend and backend.");
		meeting.addContent("Milestone 2 is due this Friday.");
		meeting.addContent("If this note is too lengthy, then it should be shortened to a reasonable length.");
		meeting.addContent("The question is: \"How long is considered lengthy?\"");
		notes.add(meeting);

		Note journal = new Note("Personal Journal");
		journal.addContent("This is private!");
		notes.add(journal);

		for (int i = 0; i < 10; i++) {
			notes.add(new Note());
		}

		notes.get(6).addContent("The ID is working correctly!");
	}

}

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
				if (content.length() > 200)
					noteContent.setText(content.substring(0, 200) + "...");
				else
					noteContent.setText(content);
			}
		}

		return v;
	}

}