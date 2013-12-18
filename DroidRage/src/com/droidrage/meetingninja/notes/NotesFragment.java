package com.droidrage.meetingninja.notes;

import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.R;
import com.droidrage.meetingninja.R.id;
import com.droidrage.meetingninja.R.layout;
import com.droidrage.meetingninja.database.AsyncResponse;
import com.droidrage.meetingninja.database.DatabaseAdapter;
import com.droidrage.meetingninja.database.NotesDatabaseAdapter;
import com.droidrage.meetingninja.user.SessionManager;
import com.droidrage.meetingninja.database.SQLiteAdapter;

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

public class NotesFragment extends Fragment implements
		AsyncResponse<List<Note>> {
	private SessionManager session;
	private NoteItemAdapter noteAdpt;
	private static List<Note> notes = new ArrayList<Note>();
	private SQLiteAdapter mySQLiteAdapter;

	// private View notesView;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		
		
//		meetingImageButton = (ImageButton) v.findViewById(R.id.imageButton);
//		meetingImageButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				System.out.println("Echo: Test");
//
//			}
//		});

		// Intent test = getActivity().getIntent();

		// if (test.getStringExtra("NoteID") != null)
		// Log.e("NOTES", test.getStringExtra("NoteID"));

		session = new SessionManager(getActivity().getApplicationContext());

		
		
		
		// TODO: Check for internet connection before receiving notes from DB
		// TODO: Display a something saying "no notes" if there are no notes
		// instead of having no notes appear
		if (notes.size() == 0)
			createNotes();

		ListView lv = (ListView) v.findViewById(R.id.notesList);
		noteAdpt = new NoteItemAdapter(getActivity(), R.layout.note_item, notes);

		// setup listview
		lv.setAdapter(noteAdpt);
		lv.setEmptyView(v.findViewById(R.id.notes_empty));
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
				menu.add(1, 1, 1, "Add Content");
				menu.add(1, 2, 2, "Delete");
				menu.add(1, 3, 3, "Version Control");

			}

			public boolean onContextItemSelected(MenuItem item) {
				Toast.makeText(getActivity(), "testing", Toast.LENGTH_LONG)
						.show();
				AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
						.getMenuInfo();

				switch (item.getItemId()) {
				case 1:
					break;
				case 2:
					break;
				case 3:
					Intent versionControl = new Intent(getActivity(),
							VersionControlActivity.class);
					startActivity(versionControl);
				default:
				}

				return false;
			}
		});

		return v;

	}

	/**
	 * Initializes the list of notes. TODO: Get the notes from the database
	 */
	private void refreshNotes() {

	}
	
	private void createNotes() {
		mySQLiteAdapter = new SQLiteAdapter(this.getActivity());

//		mySQLiteAdapter.openToWrite();
//		mySQLiteAdapter.close();

		
		mySQLiteAdapter = new SQLiteAdapter(this.getActivity());
		mySQLiteAdapter.openToRead();
		List<Note> contentRead = mySQLiteAdapter.QuerryNotes();
		
		for(Note i: contentRead)
			notes.add(i);
		
		mySQLiteAdapter.close();
		
		notes.add(new Note("A new note"));

		for (int i = 0; i < 3; i++) {
			notes.add(new Note());
		}
		
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

}