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

import objects.User;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;

public class SQLiteUserAdapter extends SQLiteHelper {
	private static final String TAG = SQLiteUserAdapter.class.getSimpleName();

	private SQLiteHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String TABLE_NAME = "users";
	protected static final String FTS_TABLE_NAME = "users_fts";

	// Columns
	public static final String KEY_NAME = Keys.User.NAME;
	public static final String KEY_EMAIL = Keys.User.EMAIL;
	public static final String KEY_PHONE = Keys.User.PHONE;
	public static final String KEY_COMPANY = Keys.User.COMPANY;
	public static final String KEY_TITLE = Keys.User.TITLE;
	public static final String KEY_LOCATION = Keys.User.LOCATION;

	private static final String[] allColumns = new String[] { KEY_ID, KEY_NAME,
			KEY_EMAIL, KEY_PHONE, KEY_COMPANY, KEY_TITLE, KEY_LOCATION };

	public SQLiteUserAdapter(Context context) {
		super(context);
		mDbHelper = SQLiteHelper.getInstance(context);
	}

	@Override
	public void close() {
		this.mDbHelper.close();
	}

	/**
	 * Run a query on the users table. See
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
		Log.d(TAG, "Clearing Users Table");
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, null, null);
		close();
	}

	public void cacheUsers() {
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {

			@Override
			public void processFinish(List<User> result) {
				try {
					bulkInsertUsers(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private synchronized void bulkInsertUsers(List<User> userList)
			throws Exception {
		String sql = "INSERT INTO " + TABLE_NAME
				+ " VALUES (?, ?, ?, ?, ?, ?, ?);";
		String fts_sql = "INSERT INTO " + FTS_TABLE_NAME
				+ " VALUES (?, ?, ?, ?);";

		mDb = mDbHelper.getWritableDatabase();
		mDb.beginTransaction();
		SQLiteStatement statement = mDb.compileStatement(sql);
		SQLiteStatement fts_statement = mDb.compileStatement(fts_sql);
		try {
			for (User user : userList) {
				statement.clearBindings();
				fts_statement.clearBindings();
				if (!(user.getID() == null || user.getID().isEmpty())) {
					statement.bindLong(1, Long.parseLong(user.getID()));
					fts_statement.bindLong(1, Long.parseLong(user.getID()));
				}
				statement.bindString(2, user.getDisplayName());
				statement.bindString(3, user.getEmail());
				statement.bindString(4, user.getPhone());

				fts_statement.bindString(2, user.getDisplayName());
				fts_statement.bindString(3, user.getEmail());
				fts_statement.bindString(4, user.getPhone());
				fts_statement.executeInsert(); // done with fts data

				statement.bindString(5, user.getCompany());
				statement.bindString(6, user.getTitle());
				statement.bindString(7, user.getCompany());

				statement.executeInsert(); // done with all data
			}
			mDb.setTransactionSuccessful();
			mDb.endTransaction();
		} catch (Exception e) {
			mDb.endTransaction(); // may not reach end transaction
			throw e;
		} finally {
			close(); // close db connection
		}
	}

	public ArrayList<User> getAllUsers() {
		ArrayList<User> users = new ArrayList<User>();
		Cursor c = query(allColumns, null, null, null, null, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				users.add(new User(c));
			} while (c.moveToNext());
		}
		c.close();
		close();
		return users;
	}

	public Cursor getNameMatches(String query, String[] columns) {
		assert !TextUtils.isEmpty(query) : "query must not be an empty string!";
		String selection = KEY_NAME + " MATCH ?";

		String[] selectionArgs = new String[] { query + "*" };

		return queryFTS(selection, selectionArgs, columns);
	}

	private Cursor queryFTS(String selection, String[] selectionArgs,
			String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(FTS_TABLE_NAME);

		Cursor cursor = builder.query(getReadableDatabase(), columns,
				selection, selectionArgs, null, null, KEY_NAME + ", "
						+ KEY_EMAIL);

		if (!(cursor == null || cursor.moveToFirst())) {
			cursor.close();
		} else {
			Log.d("Query User FTS", "Cursor is null");
		}
		return cursor;
	}
}
