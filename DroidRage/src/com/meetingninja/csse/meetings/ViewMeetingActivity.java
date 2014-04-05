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
package com.meetingninja.csse.meetings;

import java.util.ArrayList;

import org.joda.time.format.DateTimeFormatter;

import objects.Meeting;
import objects.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.MyDateUtils;
import com.meetingninja.csse.user.UserArrayAdapter;

public class ViewMeetingActivity extends Activity {
	private static final String TAG = ViewMeetingActivity.class.getSimpleName();
	private Meeting displayedMeeting;
	private TextView meetingName, location, startDate, endDate, startTime,
			endTime, description;
	private ListView attendeesList;
	private DateTimeFormatter dateFormat = MyDateUtils.JODA_APP_DATE_FORMAT;
	private DateTimeFormatter timeFormat;
	private int resultCode = Activity.RESULT_CANCELED;
	private Boolean is24;
	UserArrayAdapter adpt;
	ArrayList<User> attendance = new ArrayList<User>();
	Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_meeting);
		extras = getIntent().getExtras();
		if (extras != null) {
			displayedMeeting = extras.getParcelable(Keys.Meeting.PARCEL);
		} else {
			Log.w(TAG, "Error: Unable to find Meeting Parcel");
		}
		is24 = android.text.format.DateFormat
				.is24HourFormat(getApplicationContext());
		timeFormat = is24 ? MyDateUtils.JODA_24_TIME_FORMAT
				: MyDateUtils.JODA_12_TIME_FORMAT;
		setupViews();
		setMeeting();
	}

	private void setupViews() {
		meetingName = (TextView) this.findViewById(R.id.meeting_title_view);
		startDate = (TextView) this.findViewById(R.id.meeting_from_date_view);
		endDate = (TextView) this.findViewById(R.id.meeting_to_date_view);
		description = (TextView) this.findViewById(R.id.meeting_desc_view);
		location = (TextView) this.findViewById(R.id.meeting_location_view);
		startTime = (TextView) this.findViewById(R.id.meeting_from_time_view);
		endTime = (TextView) this.findViewById(R.id.meeting_to_time_view);
		attendeesList = (ListView) this.findViewById(R.id.guests_attending);
		adpt = new UserArrayAdapter(this, R.layout.list_item_user, attendance);
		attendeesList.setAdapter(adpt);
	}

	private void setMeeting() {
		if (displayedMeeting != null) {
			Long sTime = displayedMeeting.getStartTimeInMillis();
			Long eTime = displayedMeeting.getEndTimeInMillis();
			
			meetingName.setText(displayedMeeting.getTitle());
			String format = dateFormat.print(sTime);
			startDate.setText(format);
			format = timeFormat.print(sTime);
			startTime.setText(format);
			format = dateFormat.print(eTime);
			endDate.setText(format);
			format = timeFormat.print(eTime);
			endTime.setText(format);
			
			location.setText(displayedMeeting.getLocation());
			description.setText(displayedMeeting.getDescription());
			setAttendees(displayedMeeting.getAttendance());
		}
	}

	private void setAttendees(ArrayList<User> attendance) {
		this.attendance.clear();
//		this.attendance.addAll(attendance);
		this.adpt.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_view_meeting, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					displayedMeeting = data.getParcelableExtra(Keys.Meeting.PARCEL);
					getIntent().putExtra(Keys.Meeting.PARCEL, displayedMeeting);
					setMeeting();
					this.resultCode = resultCode;
				}
			} else if (resultCode == RESULT_CANCELED) {
				// do nothing here
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.edit_note_action_edit:
			edit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void edit() {
		Intent editMeeting = new Intent(ViewMeetingActivity.this,
				EditMeetingActivity.class);
		editMeeting.putExtra(Keys.Meeting.PARCEL, displayedMeeting);
		startActivityForResult(editMeeting, 5);
	}

}
