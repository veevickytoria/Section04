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
package edu.meetingninja.rhit.database.local;

import java.util.ArrayList;
import java.util.List;

import edu.meetingninja.rhit.database.Keys;
import objects.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteNoteAdapter extends SQLiteHelper {

	private SQLiteHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String TABLE_NAME = "notes";

	// Columns
	public static final String KEY_TITLE = Keys.Note.TITLE;
	public static final String KEY_CONTENT = Keys.Note.CONTENT;
	public static final String KEY_DESC = Keys.Note.DESC;
	public static final String KEY_CREATED_BY = Keys.Note.CREATED_BY;

	public SQLiteNoteAdapter(Context context) {
		super(context);
		mDbHelper = SQLiteHelper.getInstance(context);

	}

	@Override
	public void close() {
		this.mDbHelper.close();
	}

	/**
	 * Insert a new note to be placed in the database
	 * 
	 * @param name
	 * @param content
	 * @return The inserted note with an id assigned
	 */
	public Note insertNote(String name, String content, String creator) {
		mDb = mDbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TITLE, name);
		contentValues.put(KEY_CONTENT, content);
		// contentValues.put(KEY_CREATED_BY, creator);
		long insertID = mDb.insert(TABLE_NAME, null, contentValues);
		Cursor c = mDb.query(TABLE_NAME, new String[] { KEY_ID, KEY_TITLE,
				KEY_CONTENT }, KEY_ID + "=" + insertID, null, null, null, null);
		c.moveToFirst();
		Note newNote = new NoteCursor(c).getModel();
		c.close();
		close();
		return newNote;
	}

	public Note insertNote(Note n) {
		return insertNote(n.getTitle(), n.getContent(), n.getCreatedBy());
	}

	public void updateNote(Note note) {
		mDb = mDbHelper.getWritableDatabase();
		if (note == null) {
			close();
			return;
		}
		ContentValues data = new ContentValues();
		data.put(KEY_TITLE, note.getTitle());
		data.put(KEY_CONTENT, note.getContent());
		// data.put(KEY_CREATED_BY, note.getCreatedBy());
		mDb.update(TABLE_NAME, data, KEY_ID + "=" + note.getID(), null);
		close();
	}

	/**
	 * Delete note based off of the id provided
	 * 
	 * @param id
	 */
	public void deleteNote(long id) {
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, KEY_ID + "=" + id, null);
		close();
	}

	public void deleteNote(Note note) {
		if (note != null)
			deleteNote(Long.parseLong(note.getID()));
	}

	/**
	 * Run a query on the notes table. See
	 * {@link SQLiteDatabase#query(String, String[], String, String[], String, String, String)}
	 * 
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor query(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		mDb = mDbHelper.getReadableDatabase();
		return mDb.query(TABLE_NAME, columns, selection, selectionArgs,
				groupBy, having, orderBy);
	}

	public void clear() {
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, null, null);
		close();
	}

	public List<Note> getAllNotes() {
		List<Note> notes = new ArrayList<Note>();
		String[] columns = new String[] { KEY_ID, KEY_TITLE, KEY_CONTENT };
		mDb = mDbHelper.getReadableDatabase();
		Cursor c = mDb.query(TABLE_NAME, columns, null, null, null, null, null);
		Note note = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				if ((note = new NoteCursor(c).getModel()) != null)
					notes.add(note);
			} while (c.moveToNext());
		}
		c.close();
		close();
		return notes;
	}

	private class NoteCursor extends ModelCursor<Note> {

		public NoteCursor(Cursor c) {
			super(c);
			this.model = new Note();
		}

		@Override
		public Note getModel() {
			int idxID = crsr.getColumnIndex(KEY_ID);
			int idxTITLE = crsr.getColumnIndex(KEY_TITLE);
			int idxCONTENT = crsr.getColumnIndex(KEY_CONTENT);
			model.setID("" + crsr.getInt(idxID));
			model.setTitle(crsr.getString(idxTITLE));
			model.setContent(crsr.getString(idxCONTENT));
			return model;
		}

	}
}
