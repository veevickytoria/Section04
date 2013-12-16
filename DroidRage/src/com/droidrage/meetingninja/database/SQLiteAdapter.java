/**
 * Local adapter for storing notes, meetings, tasks, and users.
 * 
 * @author olsonmc
 * 
 */

package com.droidrage.meetingninja.database;

import java.util.ArrayList;
import java.util.List;

import objects.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

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

	private static final String SCRIPT_DROP_TABLE = "drop table Notes";

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;

	public SQLiteAdapter(Context c) {
		context = c;
	}

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,
				MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,
				MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		sqLiteHelper.close();
	}

	public void RebuildTable() {
		sqLiteDatabase.execSQL(SCRIPT_DROP_TABLE);
		sqLiteDatabase.execSQL(SCRIPT_CREATE_NOTES_TABLE);

	}

	public void createTable() {
		sqLiteDatabase.execSQL(SCRIPT_CREATE_NOTES_TABLE);
	}

	public void updateNote(int id, String content, String name) {
		sqLiteDatabase.execSQL("update " + TABLE_NOTES + " set '"
				+ KEY_NOTES_CONTENT + "'='" + content + "','" + KEY_NOTES_NAME
				+ "'='" + name + "' where " + KEY_NOTES_ID + "=" + id);
	}

	public void deleteNote(int id, String content, String name) {
		sqLiteDatabase.execSQL("Delete from " + TABLE_NOTES + " where "
				+ KEY_NOTES_ID + "=" + id);
	}

	public void insertNote(String content, String Name) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NOTES_CONTENT, content);
		contentValues.put(KEY_NOTES_NAME, Name);
		sqLiteDatabase.insert(TABLE_NOTES, null, contentValues);
	}

	public int deleteAll() {
		return sqLiteDatabase.delete(TABLE_NOTES, null, null);
	}

	public List<Note> QuerryNotes() {
		String[] columns = new String[] { KEY_NOTES_CONTENT, KEY_NOTES_ID,
				KEY_NOTES_NAME };
		Cursor cursor = sqLiteDatabase.query(TABLE_NOTES, columns, null, null,
				null, null, null);

		List<Note> notes = new ArrayList<Note>();

		int index_CONTENT = cursor.getColumnIndex(KEY_NOTES_CONTENT);
		int noteID = cursor.getColumnIndex(KEY_NOTES_NAME);
		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			Note n = new Note(cursor.getString(noteID));
			n.addContent(cursor.getString(index_CONTENT));
			notes.add(n);
		}

		return notes;
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

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

}