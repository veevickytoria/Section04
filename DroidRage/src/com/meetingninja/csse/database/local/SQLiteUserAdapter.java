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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.volley.JsonNodeRequest;
import com.meetingninja.csse.database.volley.JsonRequestListener;

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

	public User insert(User u) {
		mDb = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		String _id = u.getID();
		values.put(KEY_ID, _id);
		values.put(KEY_NAME, u.getDisplayName());
		values.put(KEY_EMAIL, u.getEmail());
		values.put(KEY_PHONE, u.getPhone());
		values.put(KEY_COMPANY, u.getCompany());
		values.put(KEY_TITLE, u.getTitle());
		values.put(KEY_LOCATION, u.getLocation());

		mDb.insert(TABLE_NAME, null, values);
		Cursor c = mDb.query(TABLE_NAME, allColumns, KEY_ID + "=" + _id, null,
				null, null, null);
		c.moveToFirst();
		User newUser = new User(c);
		;
		c.close();
		close();
		return newUser;
	}

	public void delete(User u) {
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, KEY_ID + "=" + u.getID(), null);
		close();
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

	public List<User> getAllUsers() {
		Log.d(TAG, "Getting All Users");
		List<User> users = new ArrayList<User>();
		mDb = mDbHelper.getReadableDatabase();
		Cursor c = mDb.query(TABLE_NAME, allColumns, null, null, null, null,
				KEY_NAME + " ASC");
		System.out.println(c.getCount());

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				User u = new User(c);
				users.add(u);
			} while (c.moveToNext());
		}
		c.close();
		close();
		return users;
	}

	private void bulkInsertUsers(List<User> users, boolean isVirtual)
			throws Exception {
		mDb = mDbHelper.getWritableDatabase();
		mDb.beginTransaction();
		String sql = "";
		if (!isVirtual)
			sql = "INSERT INTO " + TABLE_NAME
					+ " VALUES (?, ?, ?, ?, ?, ?, ?);";
		else
			sql = "INSERT INTO " + FTS_TABLE_NAME + " VALUES (?, ?, ?, ?);";
		SQLiteStatement statement = mDb.compileStatement(sql);
		try {
			for (User user : users) {
				statement.clearBindings();
				if (!(user.getID() == null || user.getID().isEmpty()))
					statement.bindLong(1, Long.parseLong(user.getID()));
				statement.bindString(2, user.getDisplayName());
				statement.bindString(3, user.getEmail());
				statement.bindString(4, user.getPhone());
				if (!isVirtual) {
					statement.bindString(5, user.getCompany());
					statement.bindString(6, user.getTitle());
					statement.bindString(7, user.getCompany());
				}

			}
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			mDb.endTransaction();
			throw e;
		}

		mDb.endTransaction();
		close();
	}

	protected void loadUsers(final boolean isVirtual) {
		String _url = UserDatabaseAdapter.getBaseUri().appendPath("Users")
				.build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {
					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						VolleyLog.v("Response:%n %s", response);

						if (response != null) {
							try {
								bulkInsertUsers(UserDatabaseAdapter
										.parseUserList(response), isVirtual);
							} catch (Exception e) {
								Log.e("DB Cache Users", e.getLocalizedMessage());
							}
						} else {
							error.printStackTrace();

						}
					}
				});

		// add the request object to the queue to be executed
		ApplicationController app = ApplicationController.getInstance();
		app.addToRequestQueue(req, "JSON");
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
				selection, selectionArgs, null, null, KEY_NAME + " ASC, "
						+ KEY_EMAIL + " ASC");

		if (!(cursor == null || cursor.moveToFirst())) {
			cursor.close();
		} else {
			Log.d("Query User FTS", "Cursor is null");
		}
		return cursor;
	}
}
