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
package com.android.meetingninja.database.local;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.android.meetingninja.database.Keys;

import objects.Meeting;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteMeetingAdapter extends SQLiteHelper {

	private static final String TAG = SQLiteMeetingAdapter.class
			.getSimpleName();

	private SQLiteHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String TABLE_NAME = "meetings";

	// Columns
	public static final String KEY_TITLE = Keys.Meeting.TITLE;
	public static final String KEY_LOCATION = Keys.Meeting.LOCATION;
	public static final String KEY_START_TIME = Keys.Meeting.START;
	public static final String KEY_END_TIME = Keys.Meeting.END;
	public static final String KEY_DESCRIPTION = Keys.Meeting.DESC;

	// public static final String ATTENDANCE = "attendance";

	public SQLiteMeetingAdapter(Context context) {
		super(context);
		mDbHelper = SQLiteHelper.getInstance(context);
	}

	@Override
	public void close() {
		this.mDbHelper.close();
	}

	public void clear() {
		mDb = mDbHelper.getWritableDatabase();
		mDb.delete(TABLE_NAME, null, null);
		close();
	}

	public Meeting insertMeeting(Meeting m) {
		mDb = mDbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TITLE, m.getTitle());
		contentValues.put(KEY_LOCATION, m.getLocation());
		try {
			contentValues.put(KEY_START_TIME, m.getStartTime_Time());
		} catch (ParseException e) {
			Log.e(TAG, e.getLocalizedMessage());
			return null;
		}
		try {
			contentValues.put(KEY_END_TIME, m.getEndTime_Time());
		} catch (ParseException e) {
			Log.e(TAG, e.getLocalizedMessage());
			return null;
		}
		contentValues.put(KEY_DESCRIPTION, m.getDescription());

		long insertID = mDb.insert(TABLE_NAME, null, contentValues);
		Cursor c = this.query(new String[] { KEY_ID, KEY_TITLE, KEY_LOCATION,
				KEY_START_TIME, KEY_END_TIME, KEY_DESCRIPTION }, KEY_ID + "=" + insertID,
				null, null, null, null);
		c.moveToFirst();
		Meeting newMeeting = new MeetingCursor(c).getModel();
		c.close();
		close();
		return newMeeting;
	}

	public List<Meeting> getAllMeetings() {
		List<Meeting> meetings = new ArrayList<Meeting>();
		String[] columns = new String[] { KEY_ID, KEY_TITLE, KEY_LOCATION, KEY_START_TIME,
				KEY_END_TIME, KEY_DESCRIPTION };
		Cursor c = this.query(columns, null, null, null, null, null);
		Meeting meeting = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				if ((meeting = new MeetingCursor(c).getModel()) != null) {
					meetings.add(meeting);
					Log.d(TAG + " getAll", meeting.toString());
				}
			} while (c.moveToNext());
		}
		c.close();
		close();
		return meetings;
	}

	/**
	 * Run a query on the meetings table. See
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

	public void updateMeeting(Meeting meeting) {
		mDb = mDbHelper.getWritableDatabase();
		if (meeting == null) {
			close();
			return;
		}
		ContentValues data = new ContentValues();
		data.put(KEY_TITLE, meeting.getTitle());
		data.put(KEY_LOCATION, meeting.getLocation());
		try {
			data.put(KEY_START_TIME, meeting.getStartTime_Time());
		} catch (ParseException e) {
			Log.e(TAG, e.getLocalizedMessage());
			return;
		}
		try {
			data.put(KEY_END_TIME, meeting.getEndTime_Time());
		} catch (ParseException e) {
			Log.e(TAG, e.getLocalizedMessage());
			return;
		}
		data.put(KEY_DESCRIPTION, meeting.getDescription());

		mDb.update(TABLE_NAME, data, KEY_ID + "=" + meeting.getID(), null);
		close();
	}

	/**
	 * Delete meeting based off of the id provided
	 * 
	 * @param id
	 */
	public int deleteMeeting(long id) {
		mDb = mDbHelper.getWritableDatabase();
		int numRowsAffected = mDb.delete(TABLE_NAME, KEY_ID + "=" + id, null);
		close();
		return numRowsAffected;
	}

	public int deleteMeeting(Meeting meeting) {
		int numRowsAffected = 0;
		if (meeting != null)
			numRowsAffected = deleteMeeting(Long.parseLong(meeting.getID()));
		return numRowsAffected;
	}

	private class MeetingCursor extends ModelCursor<Meeting> {

		public MeetingCursor(Cursor c) {
			super(c);
			this.model = new Meeting();
		}

		@Override
		public Meeting getModel() {
			int idxID = crsr.getColumnIndexOrThrow(KEY_ID);
			int idxTITLE = crsr.getColumnIndexOrThrow(KEY_TITLE);
			int idxLOCATION = crsr.getColumnIndexOrThrow(KEY_LOCATION);
			int idxSTART_TIME = crsr.getColumnIndexOrThrow(KEY_START_TIME);
			int idxEND_TIME = crsr.getColumnIndexOrThrow(KEY_END_TIME);
			int idxDESCRIPTION = crsr.getColumnIndexOrThrow(KEY_DESCRIPTION);
			model.setID(crsr.getInt(idxID));
			model.setTitle(crsr.getString(idxTITLE));
			model.setLocation(crsr.getString(idxLOCATION));
			model.setStartTime(crsr.getLong(idxSTART_TIME));
			model.setEndTime(crsr.getLong(idxEND_TIME));
			model.setDescription(crsr.getString(idxDESCRIPTION));
			return model;
		}

	}

}
