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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.meetingninja.csse.MainActivity;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.NotesDatabaseAdapter;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteNoteAdapter;

public class NotesFragment extends Fragment implements AsyncResponse<List<Note>> {

	private static final String TAG = NotesFragment.class.getSimpleName();

	private SessionManager session;
	private NoteArrayAdapter noteAdpt;
	private ImageButton notesImageButton;
	private SQLiteNoteAdapter mySQLiteAdapter;
	private PopulateTask populateTask;
	private DeleteNoteTask deleteNoteTask;
	private UpdateNoteTask updateNoteTask;
	private Note mergeNote;

	private static List<Note> notes = new ArrayList<Note>();

	public NotesFragment() {
		// Empty
	}

	private static NotesFragment sInstance;

	public static NotesFragment getInstance() {
		if (sInstance == null) {
			sInstance = new NotesFragment();
		}
		return sInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		setupViews(v);

		session = SessionManager.getInstance();
		mySQLiteAdapter = new SQLiteNoteAdapter(getActivity());
		if(getArguments() != null && getArguments().containsKey(Keys.Project.NOTES)){
			List<Note> temp = getArguments().getParcelableArrayList(Keys.Project.NOTES);
			notes.clear();
			notes.addAll(temp);
			noteAdpt.notifyDataSetChanged();
		}else{
			setHasOptionsMenu(true);
			populateList();
		}
		return v;

	}
	
	protected void createNote(){
		Log.d("createnote", "goto editactivity");
		Intent createNote = new Intent(getActivity(),
				EditNoteActivity.class);
		createNote.putExtra(Note.CREATE_NOTE, true);
		startActivity(createNote);
	}

	private void setupViews(View v) {
		// setup listview
		ListView lv = (ListView) v.findViewById(R.id.notesList);
		noteAdpt = new NoteArrayAdapter(getActivity(), R.layout.list_item_note,
				notes);
		lv.setAdapter(noteAdpt);

		// pretty images are better than boring text
		notesImageButton = (ImageButton) v.findViewById(android.R.id.empty);
		lv.setEmptyView(notesImageButton);
		notesImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createNote();
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
				menu.add(MainActivity.DrawerLabel.NOTES.getPosition(),
						aInfo.position, 4, "Merge");
			}

		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_notes_fragment, menu);
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
				delete(noteAdpt.getItem(position));
				handled = true;
				break;
			case 3:
				Intent versionControl = new Intent(getActivity(),
						VersionControlActivity.class);
				startActivity(versionControl);
				handled = true;
				break;
			case 4:
				Note n = noteAdpt.getItem(position);
				if(mergeNote == null){
					Log.d("MERGE", "merge_a: " + n.getID());
					mergeNote = n;
					Toast.makeText(getActivity(),
							String.format("Select second note to merge."),
							Toast.LENGTH_LONG).show();
				}
				else if (mergeNote.getID().equalsIgnoreCase(n.getID())){
					Log.d("MERGE", "merge_b: " + n.getID() + " : " + mergeNote.getID());
					mergeNote = null;
					Toast.makeText(getActivity(),
							String.format("Error: Same note selected twice. Please reselect notes to merge."),
							Toast.LENGTH_LONG).show();
				} else {
					Log.d("MERGE", "merge_c: " + n.getID() + " : " + mergeNote.getID());
					Toast.makeText(getActivity(),
							String.format("Merging " + n.getTitle() + " into " + mergeNote.getTitle()),
							Toast.LENGTH_LONG).show();
					mergeNote.setContent(mergeNote.getContent() + "\n" + n.getContent());
					delete(n);
					updateNote(mergeNote);
					mergeNote = null;
					populateList();
				}
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
	
	private void updateNote(Note note) {
		updateNoteTask = new UpdateNoteTask(this);
		updateNoteTask.execute(note);
		populateList();
	}

	private void delete(Note note) {
		deleteNoteTask = new DeleteNoteTask(this);
		deleteNoteTask.execute(note.getID());
		populateList();
	}
	
	protected void deleteNote(Note note){
		mySQLiteAdapter.deleteNote(note);
		notes.remove(note);
		noteAdpt.notifyDataSetChanged();
		populateList();
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

					// if (listPosition != -1)
					// updateNote(listPosition, editedNote);
					// else
					// updateNote(_id, editedNote);

					populateList();
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
				populateList();
			}
		} // end CreateNoteActivity
	}

	// private boolean updateNote(Note update) {
	// mySQLiteAdapter.updateNote(update);
	// populateList();
	// return true;
	// }
	//
	// private boolean updateNote(int position, Note update) {
	// if (position < 0 || position >= notes.size())
	// return false;
	// notes.set(position, new Note(update));
	// mySQLiteAdapter.updateNote(update);
	// noteAdpt.notifyDataSetChanged();
	// return true;
	// }

	public void populateList() {
		populateTask = new PopulateTask(this);
		populateTask.execute(this.session.getUserID());
	}

	@Override
	public void processFinish(List<Note> list) {
//		int numNotes = 0;
//		try {
//			numNotes = list.size();
//		} catch (java.lang.NullPointerException e){
//			
//		}
//		Toast.makeText(getActivity(),
//				String.format("Received %d notes", numNotes),
//				Toast.LENGTH_SHORT).show();
		noteAdpt.clear();
		notes.clear();
		notes.addAll(list);
		noteAdpt.notifyDataSetChanged();
	}
	
	private class PopulateTask extends AsyncTask<String, Void, List<Note>> {

		private AsyncResponse<List<Note>> delegate;

		public PopulateTask(AsyncResponse<List<Note>> del) {
			this.delegate = del;
		}

		@Override
		protected List<Note> doInBackground(String... params) {
			try {
				String userID = (String) params[0];

				return UserDatabaseAdapter.getNotes(userID);
			} catch (IOException e) {
				Log.e("DB Adapter", "Error: Register failed");
				Log.e("REGISTER_ERR", e.toString());
			} catch (Exception e) {
				Log.e("REGISTER_ERR", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Note> result) {
			Log.d("populatenotes", "stuff");
			super.onPostExecute(result);
			if(result != null)
				delegate.processFinish(result);
		}
	}
	
	private class DeleteNoteTask extends AsyncTask<String, Void, List<Note>> {

		private AsyncResponse<List<Note>> delegate;

		public DeleteNoteTask(AsyncResponse<List<Note>> del) {
			this.delegate = del;
		}

		@Override
		protected List<Note> doInBackground(String... params) {
			try {
				String noteID = (String) params[0];
				
				NotesDatabaseAdapter.deleteNote(noteID).toString();
			} catch (IOException e) {
				Log.e("DB Adapter", "Error: DeleteNote failed");
				Log.e("DeleteNoteIO", e.toString());
			} catch (Exception e) {
				Log.e("DeleteNoteE", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Note> result) {
			super.onPostExecute(result);
			//delegate.processFinish(result);
		}
	}
	
	private class UpdateNoteTask extends AsyncTask<Note, Void, List<Note>> {

		private AsyncResponse<List<Note>> delegate;

		public UpdateNoteTask(AsyncResponse<List<Note>> del) {
			this.delegate = del;
		}

		@Override
		protected List<Note> doInBackground(Note... params) {
			try {
				Note n = (Note) params[0];
				
				NotesDatabaseAdapter.updateNote(n);
			} catch (IOException e) {
				Log.e("DB Adapter", "Error: CreateNote failed");
				Log.e("CreateNoteIO", e.toString());
			} catch (Exception e) {
				Log.e("CreateNoteE", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Note> result) {
			super.onPostExecute(result);
			//delegate.processFinish(result);
		}
	}
}
