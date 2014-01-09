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
	public static final String KEY_UPDATED = "lastModified";

	// Table Create Statements
	// User table create statement
	private static final String CREATE_TABLE_USER = "CREATE TABLE "
			+ SQLiteUserAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + SQLiteUserAdapter.NAME
			+ " TEXT NOT NULL," + SQLiteUserAdapter.EMAIL + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.PHONE + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.COMPANY + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.TITLE + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.LOCATION + " TEXT NOT NULL" + ");";
	// Notes table create statement
	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ SQLiteNoteAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + SQLiteNoteAdapter.TITLE
			+ " TEXT NOT NULL," + SQLiteNoteAdapter.CONTENT + " TEXT NOT NULL"
			+ ");";
	// Meeting table create statement
	private static final String CREATE_TABLE_MEETING = "CREATE TABLE "
			+ SQLiteMeetingAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ SQLiteMeetingAdapter.TITLE + " TEXT NOT NULL,"
			+ SQLiteMeetingAdapter.LOCATION + " TEXT NOT NULL,"
			+ SQLiteMeetingAdapter.START_TIME + " INTEGER NOT NULL,"
			+ SQLiteMeetingAdapter.END_TIME + " INTEGER NOT NULL,"
			+ SQLiteMeetingAdapter.DESCRIPTION + " TEXT NOT NULL"
			// + SQLiteMeetingAdapter.ATTENDANCE + " TEXT NOT NULL"
			+ ");";

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
		db.execSQL(CREATE_TABLE_MEETING);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + SQLiteUserAdapter.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLiteNoteAdapter.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + SQLiteMeetingAdapter.TABLE_NAME);
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
