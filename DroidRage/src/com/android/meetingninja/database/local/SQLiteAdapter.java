/**
 * Local adapter for storing notes, meetings, tasks, and users.
 * 
 * @author olsonmc
 * 
 */

package com.android.meetingninja.database.local;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAdapter {

	public static final String MYDATABASE_NAME = "LocalStore";
	public static final String TABLE_NOTES = "Notes";
	public static final int MYDATABASE_VERSION = 1;
	public static final String KEY_NOTES_CONTENT = "StringValue";
	public static final String KEY_NOTES_ID = "_id";
	public static final String KEY_NOTES_NAME = "NoteName";

	private static final String SCRIPT_CREATE_NOTES_TABLE = "create table "
			+ TABLE_NOTES + " (" + KEY_NOTES_ID
			+ " integer primary key autoincrement," + KEY_NOTES_NAME
			+ " text not null, " + KEY_NOTES_CONTENT + " text not null);";

	private static final String SCRIPT_DROP_TABLE = "drop table" + TABLE_NOTES;

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;

	public SQLiteAdapter(Context c) {
		context = c;
	}

	/*
	 * Initialize the database to be read
	 */

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,
				MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	/*
	 * Initialize the database to be written too
	 */

	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,
				MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	/*
	 * Close the database
	 */

	public void close() {
		sqLiteHelper.close();
	}

	/*
	 * Drop and re-add all tables, effectively clearing all data
	 */

	public void RebuildTable() {
		sqLiteDatabase.execSQL(SCRIPT_DROP_TABLE);
		createTable();

	}

	/*
	 * Create the tables used by the application
	 */

	public void createTable() {
		sqLiteDatabase.execSQL(SCRIPT_CREATE_NOTES_TABLE);
	}

	/*
	 * Update a note to include new content based on the id of the note
	 */

	public void updateNote(long id, String content, String name) {
		sqLiteDatabase.execSQL("update " + TABLE_NOTES + " set '"
				+ KEY_NOTES_CONTENT + "'='" + content + "','" + KEY_NOTES_NAME
				+ "'='" + name + "' where " + KEY_NOTES_ID + "=" + id);
	}

	/*
	 * Delete note based off of the id provided
	 */

	public void deleteNote(long id) {
		sqLiteDatabase.execSQL("Delete from " + TABLE_NOTES + " where "
				+ KEY_NOTES_ID + "=" + id);
	}

	/*
	 * Insert a new note to placed in the database, and ID value is returned as
	 * part of the function
	 */

	public long insertNote(String content, String name) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NOTES_NAME, name);
		contentValues.put(KEY_NOTES_CONTENT, content);
		return sqLiteDatabase.insert(TABLE_NOTES, null, contentValues);
	}

	public int clearNotes() {
		return sqLiteDatabase.delete(TABLE_NOTES, null, null);
	}

	/*
	 * 
	 * 
	 */

	public List<Note> getAllNotes() {
		String[] columns = new String[] { KEY_NOTES_CONTENT, KEY_NOTES_ID,
				KEY_NOTES_NAME };
		Cursor cursor = sqLiteDatabase.query(TABLE_NOTES, columns, null, null,
				null, null, null);

		List<Note> notes = new ArrayList<Note>();

		int idxID = cursor.getColumnIndex(KEY_NOTES_ID);
		int idxName = cursor.getColumnIndex(KEY_NOTES_NAME);
		int idxContent = cursor.getColumnIndex(KEY_NOTES_CONTENT);
		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			notes.add(Note.create(cursor.getInt(idxID),
					cursor.getString(idxName), cursor.getString(idxContent)));
		}

		return notes;
	}

	public void testThings() {
		RebuildTable();
		insertNote("A lot of interesting stuff", "Another Meeting");
		long index = insertNote("A Name of a Person", "Some Meeting");
		insertNote("A Name of another persons", "Some Meeting");
		deleteNote(index);
		insertNote(
				"Plenty of text to make sure it still over flows when it is supposed to,"
				+ "Plenty of text to make sure it still over flows when it is supposed to,"
				+ "Plenty of text to make sure it still over flows when it is supposed to,"
				+ "Plenty of text to make sure it still over flows when it is supposed to,",
				"Meeting January 9th");

	}

	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_NOTES_TABLE);
			insertNote("A lot of interesting stuff", "Another Meeting");
			long index = insertNote("A Name of a Person", "Some Meeting");
			deleteNote(index);
			insertNote(
					"Plenty of text to make sure it still over flows when it is supposed too,Plenty of text to make sure it still over flows when it is supposed too,Plenty of text to make sure it still over flows when it is supposed too,Plenty of text to make sure it still over flows when it is supposed too,",
					"Meeting January 9th");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

}