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
package com.meetingninja.csse.notes;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.MainActivity;
import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.JsonNodeRequest;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteNoteAdapter;
import com.meetingninja.csse.user.SessionManager;

public class NotesFragment extends Fragment implements
		AsyncResponse<List<Note>> {

	private static final String TAG = NotesFragment.class.getSimpleName();

	private SessionManager session;
	private NoteItemAdapter noteAdpt;
	private ImageButton notesImageButton;
	private SQLiteNoteAdapter mySQLiteAdapter;

	private static List<Note> notes = new ArrayList<Note>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		setHasOptionsMenu(true);
		setupViews(v);

		session = SessionManager.getInstance();
		mySQLiteAdapter = new SQLiteNoteAdapter(getActivity());

		populateList();

		return v;

	}

	private void setupViews(View v) {
		// setup listview
		ListView lv = (ListView) v.findViewById(R.id.notesList);
		noteAdpt = new NoteItemAdapter(getActivity(), R.layout.list_item_note,
				notes);
		lv.setAdapter(noteAdpt);

		// pretty images are better than boring text
		notesImageButton = (ImageButton) v.findViewById(android.R.id.empty);
		lv.setEmptyView(notesImageButton);
		notesImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent createNote = new Intent(getActivity(),
						CreateNoteActivity.class);
				startActivity(createNote);
			}
		});

		// Item click event
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,
					int position, long id) {
				Note clickedNote = noteAdpt.getItem(position);

				Intent editNote = new Intent(getActivity(),
						ViewNoteActivity.class);
				editNote.putExtra("listPosition", position);
				editNote.putExtra(Keys.Note.PARCEL, clickedNote);
				startActivityForResult(editNote, 1);

			}
		});

		// make list long-pressable
		registerForContextMenu(lv);

		// Item long-click event
		// TODO: Add additional options and click-events to these options
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

				Note n = noteAdpt.getItem(aInfo.position);
				menu.setHeaderTitle("Options for " + "'" + n.getTitle().trim()
						+ "'");
				menu.add(MainActivity.DrawerLabel.NOTES.getPosition(),
						aInfo.position, 1, "Add Content");
				menu.add(MainActivity.DrawerLabel.NOTES.getPosition(),
						aInfo.position, 2, "Delete");
				menu.add(MainActivity.DrawerLabel.NOTES.getPosition(),
						aInfo.position, 3, "Version Control");
			}

		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notes_fragment, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		int position = item.getItemId();
		boolean handled = false;
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getGroupId() == MainActivity.DrawerLabel.NOTES.getPosition()) {
			switch (item.getOrder()) {
			case 1: // Add Content
				Toast.makeText(getActivity(),
						String.format("%s", item.getTitle()),
						Toast.LENGTH_SHORT).show();
				handled = true;
				break;
			case 2: // Delete
				Note note = noteAdpt.getItem(position);
				mySQLiteAdapter.deleteNote(note);
				notes.remove(position);
				noteAdpt.notifyDataSetChanged();
				handled = true;
				break;
			case 3:
				Intent versionControl = new Intent(getActivity(),
						VersionControlActivity.class);
				startActivity(versionControl);
				handled = true;
				break;
			default:
				Log.wtf(TAG, "Invalid context menu option selected");
				break;
			}
		} else {
			Log.wtf(TAG, "What happened here?");
		}

		return handled;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) { // EditNoteActivity
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					int listPosition = data.getIntExtra("listPosition", -1);
					Note editedNote = (Note) data
							.getParcelableExtra(Keys.Note.PARCEL);

					int _id = Integer.valueOf(editedNote.getID());
					if (listPosition != -1)
						updateNote(listPosition, editedNote);
					else
						updateNote(_id, editedNote);
				}
			} else {
				if (resultCode == Activity.RESULT_CANCELED) {
					// nothing to do here
				}
			} // end EditNoteActivity
		} else if (requestCode == 3) { // CreateNoteActivity
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getActivity(), "New Note Created",
						Toast.LENGTH_SHORT).show();
			}
		} // end CreateNoteActivity
	}

	/**
	 * Initializes the list of notes. TODO: Get the notes from the database
	 */
	private void fetchNotes() {
		String _url = UserDatabaseAdapter.getBaseUri().appendPath("Notes")
				.appendPath(session.getUserID()).build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new Response.Listener<JsonNode>() {
					@Override
					public void onResponse(JsonNode response) {
						VolleyLog.v("Response:%n %s", response);

						// processFinish(NotesDatabaseAdapter.parseNoteList(noteNode));
					};
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error:%n %s", error);

					};
				});

		ApplicationController app = ApplicationController.getInstance();
		app.addToRequestQueue(req, "JSON");
	}

	private boolean updateNote(Note update) {
		mySQLiteAdapter.updateNote(update);
		populateList();
		return true;
	}

	private boolean updateNote(int position, Note update) {
		if (position < 0 || position >= notes.size())
			return false;
		notes.set(position, new Note(update));
		mySQLiteAdapter.updateNote(update);
		noteAdpt.notifyDataSetChanged();
		return true;
	}

	public void populateList() {
		List<Note> contentRead = mySQLiteAdapter.getAllNotes();
		processFinish(contentRead);
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

}
