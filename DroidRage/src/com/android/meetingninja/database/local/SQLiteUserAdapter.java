package com.android.meetingninja.database.local;

import java.util.ArrayList;
import java.util.List;

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
	public static final String NAME = "username";
	public static final String EMAIL = "email";
	public static final String PHONE = "phone";
	public static final String COMPANY = "company";
	public static final String TITLE = "title";
	public static final String LOCATION = "location";

	private static final String[] allColumns = new String[] { KEY_ID, NAME,
			EMAIL, PHONE, COMPANY, TITLE, LOCATION };

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
		values.put(NAME, u.getDisplayName());
		values.put(EMAIL, u.getEmail());
		values.put(PHONE, u.getPhone());
		values.put(COMPANY, u.getCompany());
		values.put(TITLE, u.getTitle());
		values.put(LOCATION, u.getLocation());

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
		mDb.delete(TABLE_NAME, KEY_ID + "=" + u.getUserID(), null);
		close();
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		mDb = mDbHelper.getReadableDatabase();
		Cursor c = mDb.query(TABLE_NAME, allColumns, null, null, null, null,
				NAME);

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
			int idxUSERNAME = crsr.getColumnIndex(NAME);
			int idxEMAIL = crsr.getColumnIndex(EMAIL);
			int idxPHONE = crsr.getColumnIndex(PHONE);
			int idxCOMPANY = crsr.getColumnIndex(COMPANY);
			int idxTITLE = crsr.getColumnIndex(TITLE);
			int idxLOCATION = crsr.getColumnIndex(LOCATION);
			model.setUserID(crsr.getInt(idxID));
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
