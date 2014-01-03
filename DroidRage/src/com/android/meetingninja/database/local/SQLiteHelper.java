package com.android.meetingninja.database.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static SQLiteHelper sInstance;

	// Logcat tag
	private static final String TAG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "dataStorage";

	// Common column names
	public static final String KEY_ID = "_id";

	// Table Create Statements
	// User table create statement
	private static final String CREATE_TABLE_USER = "CREATE TABLE "
			+ UserDBAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + UserDBAdapter.NAME
			+ " TEXT NOT NULL," + UserDBAdapter.EMAIL + " TEXT NOT NULL" + ");";
	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ NoteDBAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + NoteDBAdapter.TITLE
			+ " TEXT NOT NULL," + NoteDBAdapter.CONTENT + " TEXT NOT NULL"
			+ ");";

	// Notes table create statement

	public static synchronized SQLiteHelper getInstance(Context context) {
		if (sInstance == null)
			sInstance = new SQLiteHelper(context.getApplicationContext());
		return sInstance;
	}

	protected SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// create required tables
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(CREATE_TABLE_NOTE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + UserDBAdapter.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + NoteDBAdapter.TABLE_NAME);

		// create new tables
		onCreate(db);

	}

	// closing database
	@Override
	public void close() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
