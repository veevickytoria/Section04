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
import objects.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteUserAdapter extends SQLiteHelper {

	private SQLiteHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String TABLE_NAME = "users";

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

	public User insertUser(User u) {
		mDb = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, u.getDisplayName());
		values.put(KEY_EMAIL, u.getEmail());
		values.put(KEY_PHONE, u.getPhone());
		values.put(KEY_COMPANY, u.getCompany());
		values.put(KEY_TITLE, u.getTitle());
		values.put(KEY_LOCATION, u.getLocation());

		long insertId = mDb.insert(TABLE_NAME, null, values);
		Cursor c = mDb.query(TABLE_NAME, allColumns, KEY_ID + "=" + insertId,
				null, null, null, null);
		c.moveToFirst();
		User newUser = new UserCursor(c).getModel();
		c.close();
		close();
		return newUser;
	}

	public void deleteUser(User u) {
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, KEY_ID + "=" + u.getID(), null);
		close();
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		mDb = mDbHelper.getReadableDatabase();
		Cursor c = mDb.query(TABLE_NAME, allColumns, null, null, null, null,
				KEY_NAME);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				User u = new UserCursor(c).getModel();
				users.add(u);
			} while (c.moveToNext());
		}
		c.close();
		close();
		return users;
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

	private class UserCursor extends ModelCursor<User> {

		public UserCursor(Cursor c) {
			super(c);
			this.model = new User();
		}

		@Override
		public User getModel() {
			int idxID = crsr.getColumnIndex(KEY_ID);
			int idxUSERNAME = crsr.getColumnIndex(KEY_NAME);
			int idxEMAIL = crsr.getColumnIndex(KEY_EMAIL);
			int idxPHONE = crsr.getColumnIndex(KEY_PHONE);
			int idxCOMPANY = crsr.getColumnIndex(KEY_COMPANY);
			int idxTITLE = crsr.getColumnIndex(KEY_TITLE);
			int idxLOCATION = crsr.getColumnIndex(KEY_LOCATION);
			model.setID("" + crsr.getInt(idxID));
			model.setDisplayName(crsr.getString(idxUSERNAME));
			model.setEmail(crsr.getString(idxEMAIL));
			model.setPhone(crsr.getString(idxPHONE));
			model.setCompany(crsr.getString(idxCOMPANY));
			model.setTitle(crsr.getString(idxTITLE));
			model.setLocation(crsr.getString(idxLOCATION));
			return model;
		}

	}
}
