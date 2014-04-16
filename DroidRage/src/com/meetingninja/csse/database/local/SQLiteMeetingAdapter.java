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

import objects.Meeting;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.meetingninja.csse.database.Keys;

public class SQLiteMeetingAdapter extends SQLiteHelper {

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
		contentValues.put(KEY_START_TIME, m.getStartTime());
		contentValues.put(KEY_END_TIME, m.getEndTime());
		contentValues.put(KEY_DESCRIPTION, m.getDescription());

		long insertID = mDb.insert(TABLE_NAME, null, contentValues);
		Cursor c = this.query(new String[] { KEY_ID, KEY_TITLE, KEY_LOCATION,
				KEY_START_TIME, KEY_END_TIME, KEY_DESCRIPTION }, KEY_ID + "="
				+ insertID, null, null, null, null);
		c.moveToFirst();
		Meeting newMeeting = new Meeting(c);
		c.close();
		close();
		return newMeeting;
	}

	public List<Meeting> getAllMeetings() {
		List<Meeting> meetingList = new ArrayList<Meeting>();
		String[] columns = new String[] { KEY_ID, KEY_TITLE, KEY_LOCATION,
				KEY_START_TIME, KEY_END_TIME, KEY_DESCRIPTION };
		Cursor c = this.query(columns, null, null, null, null, null);
		Meeting meeting = null;
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				if ((meeting = new Meeting(c)) != null) {
					meetingList.add(meeting);
				}
			} while (c.moveToNext());
		}
		c.close();
		close();
		return meetingList;
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
		data.put(KEY_START_TIME, meeting.getStartTime());
		data.put(KEY_END_TIME, meeting.getEndTime());
		data.put(KEY_DESCRIPTION, meeting.getDescription());

		mDb.update(TABLE_NAME, data, KEY_ID + "=" + meeting.getID(), null);
		close();
	}

	public int deleteMeeting(Meeting meeting) {
		mDb = mDbHelper.getWritableDatabase();
		int numRowsAffected = 0;
		if (meeting != null)
			numRowsAffected = mDb.delete(TABLE_NAME,
					KEY_ID + "=" + meeting.getID(), null);
		close();
		return numRowsAffected;
	}

}
