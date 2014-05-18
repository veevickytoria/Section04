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
import objects.parcelable.NoteParcel;
import objects.parcelable.ParcelDataFactory;
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
import com.meetingninja.csse.extras.IRefreshable;
import com.meetingninja.csse.notes.tasks.DeleteNoteTask;

public class NotesFragment extends Fragment implements AsyncResponse<List<Note>>, IRefreshable {

	private static final String TAG = NotesFragment.class.getSimpleName();

	protected NoteArrayAdapter noteAdpt;
	private ImageButton notesImageButton;
	private PopulateTask populateTask;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_notes, container, false);
		setupViews(v);
		setHasOptionsMenu(true);

		Bundle args = getArguments();
		if (args != null && args.containsKey(Keys.Project.NOTES)) {
			notes.clear();
			List<NoteParcel> temp = getArguments().getParcelableArrayList(Keys.Project.NOTES);
			for (NoteParcel noteParcel : temp) {
				notes.add(noteParcel.getData());
			}
			noteAdpt.notifyDataSetChanged();
		} else {
			refresh();
		}
		return v;
	}

	public void createNote() {
		Intent createNote = new Intent(getActivity(), EditNoteActivity.class);
		createNote.putExtra(Note.CREATE_NOTE, true);
		startActivity(createNote);
	}

	private void setupViews(View v) {
		ListView notesList = (ListView) v.findViewById(R.id.notesList);
		noteAdpt = new NoteArrayAdapter(getActivity(), R.layout.list_item_note,notes);
		notesList.setAdapter(noteAdpt);

		// pretty images are better than boring text
		notesImageButton = (ImageButton) v.findViewById(android.R.id.empty);
		notesList.setEmptyView(notesImageButton);
		notesImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createNote();
			}
		});

		// Item click event
		notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,int position, long id) {
				viewNoteAtPosition(position);
			}
		});

		// make list long-pressable
		registerForContextMenu(notesList);

		// Item long-click event
		notesList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
				AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

				noteAdpt.getItem(aInfo.position);
				menu.setHeaderTitle("Note options");
				String[] labels = new String[] { "Edit", "Delete","Merge" };
				for (int i = 1; i <= labels.length; i++) {
					menu.add(MainActivity.DrawerLabel.NOTES.getPosition(), aInfo.position, i,labels[i - 1]);
				}
			}

		});
	}
	protected void viewNoteAtPosition(int position) {
		Note clickedNote = noteAdpt.getItem(position);

		Intent viewNote = new Intent(getActivity(),ViewNoteActivity.class);
		viewNote.putExtra("listPosition", position);
		viewNote.putExtra(Keys.Note.PARCEL, new NoteParcel(clickedNote));
		startActivityForResult(viewNote, ViewNoteActivity.REQUEST_CODE);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_notes_fragment, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		int position = item.getItemId();
		boolean handled = false;

		if (item.getGroupId() == MainActivity.DrawerLabel.NOTES.getPosition()) {
			switch (item.getOrder()) {
			case 1: // Add Content
				Intent editNote = new Intent(getActivity(),EditNoteActivity.class);
				editNote.putExtra("listPosition", position);
				editNote.putExtra(Keys.Note.PARCEL, new NoteParcel(noteAdpt.getItem(position)));
				startActivityForResult(editNote, EditNoteActivity.REQUEST_CODE);
				
//				Toast.makeText(getActivity(),String.format("%s", item.getTitle()),Toast.LENGTH_SHORT).show();
				handled = true;
				break;
			case 2: // Delete
				delete(noteAdpt.getItem(position));
				handled = true;
				break;
			case 3:
				Note selected = noteAdpt.getItem(position);
				tryMerging(selected);

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

	private void tryMerging(Note selected) {
		if (mergeNote == null) {
			Log.d("MERGE", "merge_a: " + selected.getID());
			mergeNote = selected;
			Toast.makeText(getActivity(),String.format("Select second note to merge."),Toast.LENGTH_LONG).show();
		} else if (mergeNote.getID().equals(selected.getID())) {
			Log.d("MERGE","merge_b: " + selected.getID() + " : " + mergeNote.getID());
			mergeNote = null;
			Toast.makeText(getActivity(),"Error: Same note selected twice. Please reselect notes to merge.",Toast.LENGTH_LONG).show();
		} else {
			Log.d("MERGE","merge_c: " + selected.getID() + " : " + mergeNote.getID());
			Toast.makeText(getActivity(),"Merging " + selected.getTitle() + " into "+ mergeNote.getTitle(), Toast.LENGTH_LONG).show();

			mergeNote.mergeWith(selected);
			updateNote(mergeNote);
			mergeNote = null;
			refresh();
		}
	}

	private void updateNote(Note note) {
		new UpdateNoteTask(this).execute(note);
		refresh();
	}

	protected void delete(Note note) {
		new DeleteNoteTask().execute(note.getID());
		refresh();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == EditNoteActivity.REQUEST_CODE) { // EditNoteActivity
//			if (resultCode == Activity.RESULT_OK) {
//				if (data != null) {
////					Note editedNote = new ParcelDataFactory(data.getExtras()).getNote();
//				}
//			} // end EditNoteActivity
//		}
		refresh();
	}

	public void refresh() {
		populateTask = new PopulateTask(this);
		populateTask.execute(SessionManager.getUserID());
	}

	@Override
	public void processFinish(List<Note> list) {
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
			List<Note> noteList = new ArrayList<Note>();
			try {
				String userID = params[0];

				noteList = UserDatabaseAdapter.getNotes(userID);
			} catch (Exception e) {
				Log.e("DB Adapter", "Error: Get Notes failed");
				Log.e("NOTES_GET_ERR", e.toString());
			}
			return noteList;
		}

		@Override
		protected void onPostExecute(List<Note> result) {
			Log.d("populatenotes", "Populating Notes...");
			if (result != null)
				delegate.processFinish(result);
			super.onPostExecute(result);
		}
	}

	private class UpdateNoteTask extends AsyncTask<Note, Void, List<Note>> {

		public UpdateNoteTask(AsyncResponse<List<Note>> del) {
		}

		@Override
		protected List<Note> doInBackground(Note... params) {
			try {
				Note n = params[0];

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
			// delegate.processFinish(result);
		}
	}
}
