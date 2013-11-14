package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.List;


import objects.Note;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class NotesFragment extends Fragment implements AsyncResponse<List<Note>> {

	public static final String ARG_USERNAME = "username";
	private static List<Note> notes = new ArrayList<Note>();
	private NoteItemAdapter noteAdpt;
	//private View notesView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		
		Intent test = getActivity().getIntent();

		if(test.getStringExtra("NoteID") != null)
		Log.e("NOTES", test.getStringExtra("NoteID"));
		
		

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
		lv.setEmptyView(v.findViewById(R.id.notes_empty));
		// make list long-pressable
		registerForContextMenu(lv);
		
//		Intent updateNote = null;
//	    Bundle bundle;
//	    if(savedInstanceState != null)  
//	    	bundle = savedInstanceState; // 1       
//	    else if(getArguments() != null) 
//	    	bundle = getArguments();     // 2
//	    else 
//	    	updateNote = this.getActivity().getIntent();
//		
//		if(updateNote != null && updateNote.getBooleanExtra("Update", false)){
//			Log.e("NOTES", "UPDATE");
//		} else
//		{
//			Log.e("NOTES", "NO UPDATE");
//		}

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
				//Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				
				Intent editNote = new Intent(v.getContext(), EditNoteActivity.class);
				
				editNote.putExtra("NoteID", n.getID());
				editNote.putExtra("NoteContent", n.getContent());
				editNote.putExtra("NoteName", n.getName());
				startActivity(editNote);

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

		}
		
		);
		
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

		for (int i = 0; i < 8; i++) {
			notes.add(new Note());
		}

		notes.get(6).addContent("The ID is working correctly!");
	}

	@Override
	public void processFinish(List<Note> list) {
		Toast.makeText(getActivity(),
				String.format("Received %d notes", list.size()),
				Toast.LENGTH_SHORT).show();
		notes.addAll(list);
		noteAdpt.notifyDataSetChanged();
		
	}

	public static boolean updateNote(int noteID, String noteName, String noteContent) {
		Log.d("NOTES", "NoteID " + noteID);
		
		if(noteID < 0 || noteID >= notes.size())
			return false;
		
		
		notes.get(noteID).setName(noteName);
		notes.get(noteID).setContent(noteContent);
		
		return true;
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
	

	/**
	 * Represents an asynchronous task to receive meetings from the database
	 */
	private class NoteFetcherTask extends
			AsyncTask<String, Void, List<Note>> {
		private AsyncResponse<List<Note>> delegate;

		public NoteFetcherTask(AsyncResponse<List<Note>> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected List<Note> doInBackground(String... params) {
			List<Note> dbNotes = new ArrayList<Note>();

			try {
				dbNotes = DatabaseAdapter.getNotes(params[0]);
			} catch (Exception e) {
				Log.e("MeetingFetch", "error getting meetings");
				e.printStackTrace();
			}

			return dbNotes;
		}

		@Override
		protected void onPostExecute(List<Note> list) {
			super.onPostExecute(list);
			delegate.processFinish(list);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

}