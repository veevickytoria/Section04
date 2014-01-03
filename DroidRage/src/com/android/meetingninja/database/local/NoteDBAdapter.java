package com.android.meetingninja.database.local;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteDBAdapter extends SQLiteHelper {

	private SQLiteHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String TABLE_NAME = "notes";

	// Columns
	public static final String TITLE = "title";
	public static final String CONTENT = "content";

	public NoteDBAdapter(Context context) {
		super(context);
		mDbHelper = SQLiteHelper.getInstance(context);
	}

	@Override
	public void close() {
		this.mDbHelper.close();
	}

	/**
	 * Insert a new note to placed in the database
	 * 
	 * @param content
	 * @param name
	 * @return The inserted note with an id assigned
	 */
	public Note insertNote(String name, String content) {
		mDb = mDbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TITLE, name);
		contentValues.put(CONTENT, content);
		long insertID = mDb.insert(TABLE_NAME, null, contentValues);
		Cursor c = mDb.query(TABLE_NAME,
				new String[] { KEY_ID, TITLE, CONTENT }, KEY_ID + "="
						+ insertID, null, null, null, null);
		c.moveToFirst();
		Note newNote = new NoteCursor(c).getModel();
		c.close();
		close();
		return newNote;
	}

	public void updateNote(Note note) {
		mDb = mDbHelper.getWritableDatabase();
		if (note == null) {
			close();
			return;
		}
		ContentValues data = new ContentValues();
		data.put(TITLE, note.getName());
		data.put(CONTENT, note.getContent());
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

	public void clear() {
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, null, null);
		close();
	}

	public List<Note> getAllNotes() {
		List<Note> notes = new ArrayList<Note>();
		String[] columns = new String[] { KEY_ID, TITLE, CONTENT };
		mDb = mDbHelper.getReadableDatabase();
		Cursor c = mDb.query(TABLE_NAME, columns, null, null, null, null, null);
		Note note = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				if ((note = new NoteCursor(c).getModel()) != null)
					;
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
			int idxTITLE = crsr.getColumnIndex(TITLE);
			int idxCONTENT = crsr.getColumnIndex(CONTENT);
			model.setID(crsr.getInt(idxID));
			model.setName(crsr.getString(idxTITLE));
			model.setContent(crsr.getString(idxCONTENT));
			return model;
		}

	}
}
