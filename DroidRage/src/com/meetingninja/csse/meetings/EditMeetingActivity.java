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
import java.util.Calendar;
import java.util.List;

import objects.Meeting;
import objects.SerializableUser;
import objects.User;
import objects.parcelable.MeetingParcel;

import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog.OnDateSetListener;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.meetingninja.csse.R;
import com.meetingninja.csse.agenda.AgendaActivity;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.ContactTokenTextView;
import com.meetingninja.csse.extras.NinjaDateUtils;
import com.meetingninja.csse.meetings.tasks.MeetingSaveTask;
import com.meetingninja.csse.meetings.tasks.UpdateMeetingTask;
import com.meetingninja.csse.user.adapters.AutoCompleteAdapter;
import com.meetingninja.csse.user.adapters.UserArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

public class EditMeetingActivity extends FragmentActivity implements
AsyncResponse<String>, TokenListener {

	private Bundle extras;
	private EditText mTitle, mLocation, mDescription;
	private Button mFromDate, mToDate, mToTime;
	private Button mFromTime;
	private boolean is24;
	private Calendar start, end;
	private DateTimeFormatter timeFormat;
	private DateTimeFormatter dateFormat = NinjaDateUtils.JODA_APP_DATE_FORMAT;

	private ContactTokenTextView mGuestsComplete;

	private AutoCompleteAdapter autoAdapter;
	private UserArrayAdapter addedAdapter;

	private ArrayList<User> allUsers = new ArrayList<User>();
	private ArrayList<User> addedUsers = new ArrayList<User>();
	private ArrayList<String> addedIds = new ArrayList<String>();

	private Meeting displayedMeeting;

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_LOCATION = "location";
	public static final String EXTRA_DESCRIPTION = "description";
	public static final String EXTRA_EDIT_MODE = "editing";
	public static final String EXTRA_MEETING = Keys.Meeting.PARCEL;

	private void init() {
		extras = getIntent().getExtras();
		if (extras != null && !extras.isEmpty()) {
			displayedMeeting = extras.getParcelable(EXTRA_MEETING);
		}

		is24 = android.text.format.DateFormat.is24HourFormat(getApplicationContext());
		timeFormat = is24 ? NinjaDateUtils.JODA_24_TIME_FORMAT: NinjaDateUtils.JODA_12_TIME_FORMAT;

		getAllUsers();
	}

	private void getAllUsers() {
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {

			@Override
			public void processFinish(List<User> result) {
				allUsers.clear();
				autoAdapter.clear();
				allUsers.addAll(result);
				autoAdapter.notifyDataSetChanged();
			}
		});

	}

	private void textFieldinit() {
		if (displayedMeeting != null) {
			mTitle.setText(displayedMeeting.getTitle());
			if(!mTitle.getText().toString().trim().isEmpty()){
				mTitle.setSelection(mTitle.getText().length());
			}
			mLocation.setText(displayedMeeting.getLocation());
			mDescription.setText(displayedMeeting.getDescription());
		}
	}

	private void dateTimePicker() {
		start = Calendar.getInstance();
		end = Calendar.getInstance();
		if (displayedMeeting != null) {
			start.setTimeInMillis(displayedMeeting.getStartTimeInMillis());
			end.setTimeInMillis(displayedMeeting.getEndTimeInMillis());
		}
		mFromDate.setOnClickListener(new DateClickListener(mFromDate, start,end, mToDate, true, mFromTime, mToTime));
		mFromDate.setText(dateFormat.print(start.getTimeInMillis()));

		mToDate.setOnClickListener(new DateClickListener(mToDate, end, start,mFromDate, false, mToTime, mFromTime));
		mToDate.setText(dateFormat.print(end.getTimeInMillis()));

		mFromTime.setOnClickListener(new TimeClickListener(mFromTime, start,this, end, mToTime, true));
		mFromTime.setText(timeFormat.print(start.getTimeInMillis()));

		mToTime.setOnClickListener(new TimeClickListener(mToTime, end, this,start, mFromTime, false));
		mToTime.setText(timeFormat.print(end.getTimeInMillis()));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);
		// Show the Up button in the action bar.
		setupActionBar();
		init();
		setupViews();
		textFieldinit();
		dateTimePicker();
		setupAutoComplete();
	}

	private void setupAutoComplete() {
		getAllUsers();
		autoAdapter = new AutoCompleteAdapter(EditMeetingActivity.this,allUsers);
		mGuestsComplete.setAdapter(autoAdapter);
		mGuestsComplete.setTokenListener(this);

		// token listener when autocompleted

		addedAdapter = new UserArrayAdapter(EditMeetingActivity.this,R.layout.list_item_user, addedUsers);

	}

	private final View.OnClickListener mActionBarListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);
		}
	};

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Make an Ok/Cancel ActionBar
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,new LinearLayout(this), false);

		View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(mActionBarListener);

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mActionBarListener);

		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarButtons);
		// end Ok-Cancel ActionBar
	}

	private boolean onActionBarItemSelected(View v) {
		switch (v.getId()) {
		case R.id.action_done:
			save();
			break;
		case R.id.action_cancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}

		return true;
	}

	private void setupViews() {
		mTitle = (EditText) findViewById(R.id.meeting_title);
		mLocation = (EditText) findViewById(R.id.meeting_location);
		mFromDate = (Button) findViewById(R.id.start_date);
		mToDate = (Button) findViewById(R.id.end_date);
		mFromTime = (Button) findViewById(R.id.start_time);
		mToTime = (Button) findViewById(R.id.end_time);

		// Get the bottom half of the meeting page (radiogroup - privacy)
		View bottom = findViewById(R.id.attendees_group);
		mDescription = (EditText) findViewById(R.id.description);
		mGuestsComplete = (ContactTokenTextView) findViewById(R.id.guests_Complete);
		if (displayedMeeting != null) {
			for (User user : displayedMeeting.getAttendance()) {
				mGuestsComplete.addObject(user);
			}
		}
	}

	private void trimTextViews() {
		mTitle.setText(mTitle.getText().toString().trim());
		mLocation.setText(mLocation.getText().toString().trim());
		mDescription.setText(mDescription.getText().toString().trim());
	}

	public boolean handleClick(View v) {
		switch (v.getId()) {
		case R.id.add_agenda_button:
			Intent toAgenda = new Intent(EditMeetingActivity.this,AgendaActivity.class);

			// TODO: Get and set the agenda ID values

			toAgenda.putExtra("isCreated", false);
			toAgenda.putExtra("AgendaID", "55");

			startActivity(toAgenda);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void processFinish(String result) {
		if (!TextUtils.isEmpty(result)) {
			displayedMeeting.setID(result);
			Intent msgIntent = new Intent();
			msgIntent.putExtra("method", "insert");
			msgIntent.putExtra(Keys.Meeting.PARCEL, displayedMeeting);
			setResult(RESULT_OK, msgIntent);
			finish();
		} else {
			Toast.makeText(this, "Failed to save meeting", Toast.LENGTH_SHORT).show();
		}
	}

	private void save() {
		if (mTitle.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Empty meeting not created",Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			finish();
		} else {
			Intent msgIntent = new Intent();
			Meeting newMeeting = new Meeting();

			trimTextViews();
			String title, location, desc;
			title = mTitle.getText().toString();
			location = mLocation.getText().toString();
			desc = mDescription.getText().toString();

			newMeeting.setTitle(title);
			newMeeting.setLocation(location);
			newMeeting.setStartTime(start.getTimeInMillis());
			newMeeting.setEndTime(end.getTimeInMillis());
			newMeeting.setDescription(desc);
			newMeeting.setAttendance(addedUsers);

			if (displayedMeeting != null) {
				msgIntent.putExtra("method", "update");
				newMeeting.setID(displayedMeeting.getID());
				UpdateMeetingTask task = new UpdateMeetingTask();
				newMeeting.setAttendance(addedUsers);
				task.updateMeeting(newMeeting);
				displayedMeeting = newMeeting;

			} else {
				MeetingSaveTask task = new MeetingSaveTask(EditMeetingActivity.this);
				task.execute(newMeeting);
				msgIntent.putExtra("method", "insert");
				displayedMeeting = newMeeting;
				return;
			}
			Toast.makeText(this, String.format("Saving Meeting"),Toast.LENGTH_SHORT).show();

			msgIntent.putExtra(Keys.Meeting.PARCEL, new MeetingParcel(newMeeting));
			if (extras != null) {
				msgIntent.putExtra("listPosition",extras.getInt("listPosition", -1));
			}
			setResult(RESULT_OK, msgIntent);
			finish();
		}
	}

	// TODO: abstract date click listener and timeclick listener
	private class DateClickListener implements OnClickListener,
	OnDateSetListener {
		Button button, otherButton, timeButton, otherTimeButton;
		Calendar cal, other;
		boolean start;

		public DateClickListener(Button b, Calendar c, Calendar other,Button b1, Boolean start, Button timeButton,Button otherTimeButton) {
			this.button = b;
			this.otherButton = b1;
			this.other = other;
			this.cal = c;
			this.start = start;
			this.timeButton = timeButton;
			this.otherTimeButton = otherTimeButton;
		}

		@Override
		public void onClick(View v) {
			FragmentManager fm = getSupportFragmentManager();
			CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(this, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
			calendarDatePickerDialog.show(fm, "fragment_date_picker_name");
		}

		@Override
		// TODO: make functions for setting calendars or such
		public void onDateSet(CalendarDatePickerDialog dialog, int year,int monthOfYear, int dayOfMonth) {
			Calendar tempcal = Calendar.getInstance();
			tempcal.set(year, monthOfYear, dayOfMonth,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
			Calendar now = Calendar.getInstance();
			now = Calendar.getInstance();
			if (tempcal.after(now)) {
				cal.set(year, monthOfYear, dayOfMonth);
				String format = dateFormat.print(cal.getTimeInMillis());
				if ((start && cal.after(other))|| ((!start) && cal.before(other))) {
					other.set(year, monthOfYear, dayOfMonth,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
					otherTimeButton.setText(timeFormat.print(other.getTimeInMillis()));
					otherButton.setText(format);
				}
				button.setText(format);
			} else {
				int hour, minute;
				hour = now.get(Calendar.HOUR_OF_DAY);
				minute = now.get(Calendar.MINUTE);
				now.set(Calendar.HOUR_OF_DAY, 0);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
				if (tempcal.after(now)) {
					cal.set(year, monthOfYear, dayOfMonth, hour, minute);
					String format = dateFormat.print(cal.getTimeInMillis());
					if ((start && cal.after(other))|| ((!start) && cal.before(other))) {
						other.set(year, monthOfYear, dayOfMonth, hour, minute);
						otherTimeButton.setText(timeFormat.print(other.getTimeInMillis()));
						otherButton.setText(format);
					}
					timeButton.setText(timeFormat.print(cal.getTimeInMillis()));
					button.setText(format);
				} else {
					AlertDialogUtil.displayDialog(EditMeetingActivity.this,"Error","A Meeting can not be set to start or end before now","OK", null);
				}
			}
		}
	}

	private class TimeClickListener implements OnClickListener,
	OnTimeSetListener {
		Button button, otherButton;
		Calendar cal, other;
		boolean start;

		public TimeClickListener(Button b, Calendar c,FragmentActivity activity, Calendar other, Button b1,Boolean start) {
			this.button = b;
			is24 = android.text.format.DateFormat.is24HourFormat(getApplicationContext());
			this.cal = c;
			this.otherButton = b1;
			this.other = other;
			this.start = start;
		}

		@Override
		public void onClick(View v) {
			is24 = android.text.format.DateFormat.is24HourFormat(getApplicationContext());
			FragmentManager fm = getSupportFragmentManager();
			RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(TimeClickListener.this,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE), is24);
			timePickerDialog.show(fm, "fragment_time_picker_name");
		}

		@Override
		public void onTimeSet(RadialPickerLayout dialog, int hourOfDay,int minute) {
			Calendar tempcal = Calendar.getInstance();
			tempcal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
			Calendar now = Calendar.getInstance();
			now = Calendar.getInstance();

			if (tempcal.after(now)) {
				cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cal.set(Calendar.MINUTE, minute);
				if ((start && cal.after(other))|| ((!start) && cal.before(other))) {
					other.set(Calendar.HOUR_OF_DAY, hourOfDay);
					other.set(Calendar.MINUTE, minute);
					otherButton.setText(timeFormat.print(cal.getTimeInMillis()));
				}
				button.setText(timeFormat.print(cal.getTimeInMillis()));
			} else {
				AlertDialogUtil.displayDialog(EditMeetingActivity.this,"Error","A Meeting can not be set to start or end before now","OK", null);
			}
		}

	}

	@Override
	public void onTokenAdded(Object arg0) {
		SerializableUser added = null;
		if (arg0 instanceof SerializableUser)
			added = (SerializableUser) arg0;
		else if (arg0 instanceof User)
			added = new SerializableUser((User) arg0);

		if (added != null) {
			addedUsers.add(added);
			if (!TextUtils.equals(added.getID(), "")) {
				addedIds.add(added.getID());
			}
		}
		addedAdapter.notifyDataSetChanged();
	}

	@Override
	public void onTokenRemoved(Object arg0) {
		SerializableUser removed = null;
		if (arg0 instanceof SerializableUser)
			removed = (SerializableUser) arg0;
		else if (arg0 instanceof User)
			removed = new SerializableUser((User) arg0);

		if (removed != null) {
			addedUsers.remove(removed);
			if (!TextUtils.equals(removed.getID(), "")) {
				addedIds.remove(removed.getID());
			}
			addedAdapter.notifyDataSetChanged();
		}
	}
}
