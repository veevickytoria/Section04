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
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + SQLiteUserAdapter.KEY_NAME
			+ " TEXT NOT NULL," + SQLiteUserAdapter.KEY_EMAIL + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.KEY_PHONE + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.KEY_COMPANY + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.KEY_TITLE + " TEXT NOT NULL,"
			+ SQLiteUserAdapter.KEY_LOCATION + " TEXT NOT NULL" + ");";
	
	// Notes table create statement
	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ SQLiteNoteAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ SQLiteNoteAdapter.KEY_TITLE + " TEXT NOT NULL,"
			+ SQLiteNoteAdapter.KEY_CONTENT + " TEXT NOT NULL" + ");";
	
	// Meeting table create statement
	private static final String CREATE_TABLE_MEETING = "CREATE TABLE "
			+ SQLiteMeetingAdapter.TABLE_NAME + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ SQLiteMeetingAdapter.KEY_TITLE + " TEXT NOT NULL,"
			+ SQLiteMeetingAdapter.KEY_LOCATION + " TEXT NOT NULL,"
			+ SQLiteMeetingAdapter.KEY_START_TIME + " INTEGER NOT NULL,"
			+ SQLiteMeetingAdapter.KEY_END_TIME + " INTEGER NOT NULL,"
			+ SQLiteMeetingAdapter.KEY_DESCRIPTION + " TEXT NOT NULL"
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
