/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.android.meetingninja.notes;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.app.Activity;
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
import com.android.meetingninja.database.local.NoteDBAdapter;
import com.android.meetingninja.user.SessionManager;

public class NotesFragment extends Fragment implements
		AsyncResponse<List<Note>> {
	private static final String TAG = NotesFragment.class.getSimpleName();
	private SessionManager session;
	private NoteItemAdapter noteAdpt;
	private static List<Note> notes = new ArrayList<Note>();
	private NoteDBAdapter mySQLiteAdapter;

	// private View notesView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		setHasOptionsMenu(true);

		// Intent test = getActivity().getIntent();

		// if (test.getStringExtra("NoteID") != null)
		// Log.e("NOTES", test.getStringExtra("NoteID"));

		session = SessionManager.getInstance();

		// TODO: Check for internet connection before receiving notes from DB
		// TODO: Display a something saying "no notes" if there are no notes
		// instead of having no notes appear
		mySQLiteAdapter = new NoteDBAdapter(getActivity());

		ListView lv = (ListView) v.findViewById(R.id.notesList);
		noteAdpt = new NoteItemAdapter(getActivity(), R.layout.note_item, notes);

		// setup listview
		lv.setAdapter(noteAdpt);
		populateList();
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

				Intent editNote = new Intent(getActivity(),
						EditNoteActivity.class);
				editNote.putExtra("listPosition", position);
				editNote.putExtra(EditNoteActivity.EXTRA_ID, n.getID());
				editNote.putExtra(EditNoteActivity.EXTRA_NAME, n.getName());
				editNote.putExtra(EditNoteActivity.EXTRA_CONTENT,
						n.getContent());
				startActivityForResult(editNote, 1);

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
				menu.setHeaderTitle("Options for " + "'" + n.getName().trim()
						+ "'");
				menu.add(1, aInfo.position, 1, "Add Content");
				menu.add(2, aInfo.position, 2, "Delete");
				menu.add(3, aInfo.position, 3, "Version Control");
			}

		});

		return v;

	}

	// private View notesView;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		int position = item.getItemId();
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Toast.makeText(getActivity(), String.format("%s", item.getTitle()),
				Toast.LENGTH_SHORT).show();
		switch (item.getGroupId()) {
		case 1: // Add Content
			Toast.makeText(getActivity(), String.format("%s", item.getTitle()),
					Toast.LENGTH_SHORT).show();
			break;
		case 2: // Delete
			Note note = noteAdpt.getItem(position);
			mySQLiteAdapter.deleteNote(note);
			notes.remove(position);
			noteAdpt.notifyDataSetChanged();
			break;
		case 3:
			Intent versionControl = new Intent(getActivity(),
					VersionControlActivity.class);
			startActivity(versionControl);
			break;
		default:
			break;
		}

		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					int listPosition = data.getIntExtra("listPosition", -1);
					String noteID = data
							.getStringExtra(EditNoteActivity.EXTRA_ID);
					String noteName = data
							.getStringExtra(EditNoteActivity.EXTRA_NAME);
					String noteContent = data
							.getStringExtra(EditNoteActivity.EXTRA_CONTENT);
					// StringBuilder sb = new StringBuilder();
					// sb.append("[" + listPosition + "] ");
					// sb.append(noteID + " ");
					// sb.append(noteName + " ");
					// sb.append(noteContent);
					// Log.v(TAG, sb.toString());
					// populateList();
					int _id = Integer.parseInt(noteID);
					if (listPosition != -1)
						updateNote(listPosition, _id, noteName, noteContent);
					else
						updateNote(_id, noteName, noteContent);
				}
			} else {
				if (resultCode == Activity.RESULT_CANCELED) {
					// nothing to do here
				}
			}
		}
	}

	/**
	 * Initializes the list of notes. TODO: Get the notes from the database
	 */
	private void refreshNotes() {

	}

	private void removeObjectWithID(int id) {

		Note s = null;
		for (int i = 0; i < noteAdpt.getCount(); i++) {
			if (Long.toString(id).equals(noteAdpt.getItem(i).getID())) {
				s = noteAdpt.getItem(i);
				break;
			}
		}

		noteAdpt.remove(s);
		mySQLiteAdapter.deleteNote(id);

	}

	private void createNotes() {

		List<Note> contentRead = mySQLiteAdapter.getAllNotes();

		for (Note i : contentRead)
			notes.add(i);

	}

	@Override
	public void processFinish(List<Note> list) {
		Toast.makeText(getActivity(),
				String.format("Received %d notes", list.size()),
				Toast.LENGTH_SHORT).show();
		noteAdpt.clear();
		notes.clear();
		notes.addAll(list);
		noteAdpt.notifyDataSetChanged();
	}

	private boolean updateNote(int noteID, String noteName, String noteContent) {
		Log.d("NOTES", "NoteID " + noteID);

		mySQLiteAdapter.updateNote(Note.create(noteID, noteName, noteContent));
		populateList();
		return true;
	}

	private boolean updateNote(int position, int noteID, String noteName,
			String noteContent) {
		if (position < 0 || position >= notes.size())
			return false;
		notes.get(position).setName(noteName);
		notes.get(position).setContent(noteContent);
		mySQLiteAdapter.updateNote(notes.get(position));

		noteAdpt.notifyDataSetChanged();

		return true;
	}

	public Note createBlankNote() {
		return mySQLiteAdapter.insertNote("New Note", "");
	}

	public void populateList() {
		noteAdpt.clear();
		notes.clear();

		List<Note> contentRead = mySQLiteAdapter.getAllNotes();
		notes.addAll(contentRead);

		noteAdpt.notifyDataSetChanged();

	}

}
