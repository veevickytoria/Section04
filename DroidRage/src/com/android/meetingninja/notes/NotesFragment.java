package com.android.meetingninja.notes;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.SQLiteAdapter;
import com.android.meetingninja.user.SessionManager;

public class NotesFragment extends Fragment implements
		AsyncResponse<List<Note>> {
	private SessionManager session;
	private NoteItemAdapter noteAdpt;
	private static List<Note> notes = new ArrayList<Note>();
	private SQLiteAdapter mySQLiteAdapter;

	// private View notesView;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		setHasOptionsMenu(true);

		// Intent test = getActivity().getIntent();

		// if (test.getStringExtra("NoteID") != null)
		// Log.e("NOTES", test.getStringExtra("NoteID"));

		session = SessionManager.newInstance(getActivity().getApplicationContext());

		// TODO: Check for internet connection before receiving notes from DB
		// TODO: Display a something saying "no notes" if there are no notes
		// instead of having no notes appear
		if (notes.size() == 0)
			createNotes();

		ListView lv = (ListView) v.findViewById(R.id.notesList);
		noteAdpt = new NoteItemAdapter(getActivity(), R.layout.note_item, notes);

		// setup listview
		lv.setAdapter(noteAdpt);
		lv.setEmptyView(v.findViewById(android.R.id.empty));
		// make list long-pressable
		registerForContextMenu(lv);

		// Intent updateNote = null;
		// Bundle bundle;
		// if(savedInstanceState != null)
		// bundle = savedInstanceState; // 1
		// else if(getArguments() != null)
		// bundle = getArguments(); // 2
		// else
		// updateNote = this.getActivity().getIntent();
		//
		// if(updateNote != null && updateNote.getBooleanExtra("Update",
		// false)){
		// Log.e("NOTES", "UPDATE");
		// } else
		// {
		// Log.e("NOTES", "NO UPDATE");
		// }

		// Item click event
		// TODO: Open a window to edit the note here
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,
					int position, long id) {
				Note n = noteAdpt.getItem(position);
				CharSequence descStr = n.getContent().isEmpty() ? "No content"
						: n.getContent();
				String msg = String.format("%s: %s", n.getName(), descStr);
				// Toast.makeText(getActivity(), msg,
				// Toast.LENGTH_SHORT).show();

				Intent editNote = new Intent(v.getContext(),
						EditNoteActivity.class);

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
				menu.add(1, n.getID(), 1, "Add Content");
				menu.add(2, n.getID(), 2, "Delete");
				menu.add(3, n.getID(), 3, "Version Control");

			}

		});

		return v;

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		int id = item.getItemId();
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Toast.makeText(getActivity(), String.format("%s", item.getTitle()),
				Toast.LENGTH_SHORT).show();
		switch (item.getGroupId()) {
		case 1:
			Toast.makeText(getActivity(), String.format("%s", item.getTitle()),
					Toast.LENGTH_SHORT).show();
			break;
		case 2:
			removeObjectWithID(id);
			break;
		case 3:
			Intent versionControl = new Intent(getActivity(),
					VersionControlActivity.class);
			startActivity(versionControl);
		default:
		}

		return false;
	}

	/**
	 * Initializes the list of notes. TODO: Get the notes from the database
	 */
	private void refreshNotes() {

	}

	private void removeObjectWithID(int id) {
		mySQLiteAdapter = new SQLiteAdapter(this.getActivity());

		Note s = null;
		for (Note i : notes) {
			if (i.getID() == id)
				s = i;

		}
		noteAdpt.remove(s);
		mySQLiteAdapter.openToWrite();
		mySQLiteAdapter.deleteNote(id);
		mySQLiteAdapter.close();

	}

	private void createNotes() {


		mySQLiteAdapter = new SQLiteAdapter(this.getActivity());
		mySQLiteAdapter.openToRead();

		List<Note> contentRead = mySQLiteAdapter.getAllNotes();

		for (Note i : contentRead)
			notes.add(i);

		mySQLiteAdapter.close();

		// notes.add(new Note("A new note"));
		//
		// for (int i = 0; i < 3; i++) {
		// notes.add(new Note());
		// }

	}

	@Override
	public void processFinish(List<Note> list) {
		Toast.makeText(getActivity(),
				String.format("Received %d notes", list.size()),
				Toast.LENGTH_SHORT).show();
		noteAdpt.clear();
		noteAdpt.addAll(list);

	}

	public static boolean updateNote(int noteID, String noteName,
			String noteContent) {
		Log.d("NOTES", "NoteID " + noteID);

		if (noteID < 0 || noteID >= notes.size())
			return false;

		notes.get(noteID).setName(noteName);
		notes.get(noteID).setContent(noteContent);

		return true;
	}

	public void createNewNote() {
		mySQLiteAdapter.openToWrite();
		long i = mySQLiteAdapter.insertNote("", "New Note");
		Note s = new Note("New Note");
		s.addContent("");
		s.setID((int) i);
		noteAdpt.add(s);
		mySQLiteAdapter.close();
	}

	public void rebuildListView() {	
		noteAdpt.clear();
		mySQLiteAdapter = new SQLiteAdapter(getActivity());
		mySQLiteAdapter.openToRead();

		List<Note> contentRead = mySQLiteAdapter.getAllNotes();

		for (Note i : contentRead)
			notes.add(i);

		mySQLiteAdapter.close();
		
	}

}