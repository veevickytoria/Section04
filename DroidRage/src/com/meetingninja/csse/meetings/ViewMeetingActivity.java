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
import java.util.List;

import org.joda.time.format.DateTimeFormatter;

import objects.Meeting;
import objects.User;
import objects.parcelable.MeetingParcel;
import objects.parcelable.ParcelDataFactory;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.MeetingVolleyAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.NinjaDateUtils;
import com.meetingninja.csse.meetings.tasks.DeleteMeetingTask;
import com.meetingninja.csse.user.adapters.UserArrayAdapter;

public class ViewMeetingActivity extends Activity {
	private static final String TAG = ViewMeetingActivity.class.getSimpleName();
	private Meeting displayedMeeting;
	private TextView meetingName, location, startDate, endDate, startTime,endTime, description;
	private ListView attendeesList;
	private DateTimeFormatter dateFormat = NinjaDateUtils.JODA_APP_DATE_FORMAT;
	private DateTimeFormatter timeFormat;
	private Boolean is24;
	private UserArrayAdapter adpt;
	private ArrayList<User> attendance = new ArrayList<User>();
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_meeting);

		extras = getIntent().getExtras();
		if (extras != null) {
			displayedMeeting = extras.getParcelable(Keys.Meeting.PARCEL);
		} else {
			Log.w(TAG, "Error: Unable to find Meeting Parcel");
			displayedMeeting = new Meeting();
		}

		is24 = DateFormat.is24HourFormat(getApplicationContext());

		timeFormat = is24 ? NinjaDateUtils.JODA_24_TIME_FORMAT: NinjaDateUtils.JODA_12_TIME_FORMAT;

		getActionBar().setTitle("");
		setupViews();

		setMeeting(displayedMeeting);

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
		attendeesList.setEmptyView(this.findViewById(android.R.id.empty));
		adpt = new UserArrayAdapter(this, R.layout.list_item_user, attendance);
		attendeesList.setAdapter(adpt);
	}

	private void setMeeting(Meeting meeting) {
		if (meeting != null) {
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

	private void setAttendees(List<User> attendance) {
		Log.i(TAG, attendance.toString());
		this.attendance.clear();
		this.adpt.clear();
		this.attendance.addAll(attendance);
		this.adpt.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_view_meeting, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	private void edit() {
		Intent editMeeting = new Intent(ViewMeetingActivity.this,EditMeetingActivity.class);
		editMeeting.putExtra(Keys.Meeting.PARCEL, displayedMeeting);
		startActivityForResult(editMeeting, 5);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					displayedMeeting = new ParcelDataFactory(data.getExtras()).getMeeting();
					getIntent().putExtra(Keys.Meeting.PARCEL, displayedMeeting);
					setMeeting(displayedMeeting);
				}
			} else if (resultCode == RESULT_CANCELED) {
				// do nothing here
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(Keys.Meeting.PARCEL, new MeetingParcel(displayedMeeting));
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.edit_meeting_action:
			edit();
			return true;
		case R.id.delete_meeting_action:
			AlertDialogUtil.deleteDialog(this, "meeting", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteMeeting(displayedMeeting);					
				}
				
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void deleteMeeting(Meeting meeting) {
//		MeetingVolleyAdapter.deleteMeeting(meeting.getID());
		new DeleteMeetingTask().deleteMeeting(meeting.getID());
		finish();
	}

}
