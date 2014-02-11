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
package com.meetingninja.csse.database.local;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.meetingninja.csse.database.Keys;

public class SQLiteNoteAdapter extends SQLiteHelper {

	private SQLiteHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String TABLE_NAME = "notes";
	public static final String FTS_TABLE_NAME = "notes_fts";

	// Columns
	public static final String KEY_TITLE = Keys.Note.TITLE;
	public static final String KEY_CONTENT = Keys.Note.CONTENT;
	public static final String KEY_DESC = Keys.Note.DESC;
	public static final String KEY_CREATED_BY = Keys.Note.CREATED_BY;

	private final static String[] allColumns = new String[] { KEY_ID,
			KEY_TITLE, KEY_CONTENT, KEY_DESC, KEY_CREATED_BY };

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
	public long insertNote(Note note) {
		mDb = mDbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, note.getID());
		cv.put(KEY_TITLE, note.getTitle());
		cv.put(KEY_CONTENT, note.getContent());
		cv.put(KEY_DESC, note.getDescription());
		cv.put(KEY_CREATED_BY, note.getCreatedBy());

		long insertID = mDb.insert(TABLE_NAME, null, cv);
		Cursor c = mDb.query(TABLE_NAME, allColumns, KEY_ID + "=" + insertID,
				null, null, null, null);
		c.moveToFirst();
		c.close();
		close();
		return insertID;
	}

	public void updateNote(Note note) {
		mDb = mDbHelper.getWritableDatabase();
		if (note == null) {
			close();
			return;
		}
		ContentValues data = new ContentValues();
		data.put(KEY_ID, note.getID());
		data.put(KEY_TITLE, note.getTitle());
		data.put(KEY_CONTENT, note.getContent());
		data.put(KEY_DESC, note.getDescription());
		data.put(KEY_CREATED_BY, note.getCreatedBy());
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
		if (note == null) {
			close();
			return;
		}
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
		mDb = mDbHelper.getReadableDatabase();
		Cursor c = query(allColumns, null, null, null, null,
				null);
		// looping through all rows and adding to list
		if (c != null && c.moveToFirst()) {
			do {
				notes.add(new Note(c));
			} while (c.moveToNext());
		}
		c.close();
		close();
		return notes;
	}
}
