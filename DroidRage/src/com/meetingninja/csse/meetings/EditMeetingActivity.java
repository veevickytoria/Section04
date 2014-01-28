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

import java.sql.Date;
import java.util.Calendar;
import java.util.TimeZone;

import objects.Meeting;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
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
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.agenda.AgendaActivity;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteMeetingAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.MyDateUtils;

public class EditMeetingActivity extends FragmentActivity implements
		AsyncResponse<Boolean> {

	private static final String TAG = EditMeetingActivity.class.getSimpleName();

	public static final String EXTRA_EDIT_MODE = "editMode";

	private Bundle extras;
	private EditText mTitle, mLocation, mDescription;
	private Button mFromDate, mToDate, mToTime;
	private Button mFromTime;
	private boolean is24, edit_mode;
	private DateTime joda_start, joda_end;
	private DateTimeFormatter dateFormat = MyDateUtils.JODA_MEETING_DATE_FORMAT;
	private DateTimeFormatter timeFormat;

	private SQLiteMeetingAdapter mySQLiteAdapter;
	private SessionManager session;
	private Meeting displayedMeeting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);
		// Show the Up button in the action bar.
		setupActionBar();

		// Do some initializations
		is24 = android.text.format.DateFormat
				.is24HourFormat(getApplicationContext());
		session = SessionManager.getInstance();
		timeFormat = is24 ? MyDateUtils.JODA_24_TIME_FORMAT
				: MyDateUtils.JODA_12_TIME_FORMAT;
		extras = getIntent().getExtras();
		edit_mode = extras.getBoolean(EXTRA_EDIT_MODE, true);
		mySQLiteAdapter = new SQLiteMeetingAdapter(this);
		if (extras != null && !extras.isEmpty()) {
			displayedMeeting = extras.getParcelable(Keys.Meeting.PARCEL);
		}

		setupViews();

		// init the text fields
		if (displayedMeeting != null) {
			mTitle.setText(displayedMeeting.getTitle());
			mLocation.setText(displayedMeeting.getLocation());
			mDescription.setText(displayedMeeting.getDescription());
		}

		// init the date-time pickers
		joda_start = DateTime.now();
		joda_end = DateTime.now();
		joda_start = joda_start.withZone(DateTimeZone.UTC);
		joda_end = joda_end.withZone(DateTimeZone.UTC);

		if (displayedMeeting != null) {
			joda_start = joda_start.withMillis(displayedMeeting.getStartTimeInMillis());
			// start.set(Calendar.HOUR_OF_DAY,
			// Integer.parseInt(mFromTime.getText().toString()));
			joda_end = joda_end.withMillis(displayedMeeting.getEndTimeInMillis());
		} else {
			System.out.println("display metting is null");
			// start.add(Calendar.HOUR_OF_DAY, 1);
			// start.set(Calendar.MINUTE, 0);
			//
			// //end.add(Calendar.HOUR_OF_DAY, 2);
			// end.set(Calendar.MINUTE, 0);
		}

		mFromDate.setOnClickListener(new DateClickListener(mFromDate, mToDate,
				joda_start, joda_end, mFromTime, mToTime, true));
		mFromDate.setText(dateFormat.print(joda_start));

		mToDate.setOnClickListener(new DateClickListener(mToDate, mFromDate,
				joda_end, joda_start, mToTime, mFromTime, false));
		mToDate.setText(dateFormat.print(joda_end));

		// mFromTime.setOnClickListener(new TimeClickListener(mFromTime, start,
		// this, end, mToTime, true));
		// mFromTime.setText(timeFormat.print(start.getTimeInMillis()));
		//
		// mToTime.setOnClickListener(new TimeClickListener(mToTime, end, this,
		// start, mFromTime, false));

		mFromTime.setOnClickListener(new TimeClickListener(mFromTime, mToTime,
				joda_start, joda_end, true));
		mFromTime.setText(timeFormat.print(joda_start));

		mToTime.setOnClickListener(new TimeClickListener(mToTime, mFromTime,
				joda_end, joda_start, false));

		mToTime.setText(timeFormat.print(joda_end));
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
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,
				new LinearLayout(this), false);

		View cancelActionView = actionBarButtons
				.findViewById(R.id.action_cancel);
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
		// Hide RadioGroup if creating meeting
		if (edit_mode) {
			bottom.findViewById(R.id.response_row).setVisibility(View.GONE);
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
			Intent act = new Intent(EditMeetingActivity.this,
					AgendaActivity.class);

			// Todo: Get and set the agenda ID values

			act.putExtra("isCreated", false);
			act.putExtra(Keys.Agenda.ID, "55");

			startActivity(act);
			break;
		default:
			break;
		}
		return true;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.new_meeting, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// // This ID represents the Home or Up button. In the case of this
	// // activity, the Up button is shown. Use NavUtils to allow users
	// // to navigate up one level in the application structure. For
	// // more details, see the Navigation pattern on Android Design:
	// //
	// http://developer.android.com/design/patterns/navigation.html#up-vs-back
	// // NavUtils.navigateUpFromSameTask(this); return true;
	// case R.id.action_save:
	// break;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public void processFinish(Boolean result) {
		if (result) {
			finish();
		} else {
			Toast.makeText(this, "Failed to save meeting", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void save() {
		if (TextUtils.isEmpty(mTitle.getText())) {
			Toast.makeText(this, "Empty meeting not created",
					Toast.LENGTH_SHORT).show();
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
			
			newMeeting.setStartTime(joda_start.getMillis());
			newMeeting.setEndTime(joda_end.getMillis());
			newMeeting.setDescription(desc);
			if (displayedMeeting != null) {
				// mySQLiteAdapter.updateMeeting(newMeeting);
				msgIntent.putExtra("method", "update");
				newMeeting.setID(displayedMeeting.getID());
			} else {
				MeetingSaveTask task = new MeetingSaveTask(
						EditMeetingActivity.this);
				task.execute(newMeeting);
				msgIntent.putExtra("method", "insert");
			}

			msgIntent.putExtra(Keys.Meeting.PARCEL, newMeeting);
			if (extras != null) {
				msgIntent.putExtra("listPosition",
						extras.getInt("listPosition", -1));
			}
			setResult(RESULT_OK, msgIntent);
			finish();
		}
	}

	// TODO: abstract date click listener and timeclick listener
	private class DateClickListener implements OnClickListener,
			OnDateSetListener {
		Button button1, button2, timeButton1, timeButton2;
		DateTime dt, otherDT;
		boolean isStartDate;

		public DateClickListener(Button button1, Button button2,
				DateTime dateTime1, DateTime dateTime2, Button timeButton1,
				Button timeButton2, Boolean start) {
			this.button1 = button1;
			this.button2 = button2;
			this.dt = dateTime1;
			this.otherDT = dateTime2;
			this.timeButton1 = timeButton1;
			this.timeButton2 = timeButton2;
			this.isStartDate = start;
		}

		@Override
		public void onClick(View v) {
			FragmentManager fm = getSupportFragmentManager();
			CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
					.newInstance(this, // callback
							dt.getYear(), // year
							dt.getMonthOfYear(), // month
							dt.getDayOfMonth()); // day
			calendarDatePickerDialog.show(fm, "fragment_date_picker_name");
		}

		@Override
		// TODO: make functions for setting calendars or such
		public void onDateSet(CalendarDatePickerDialog dialog, int year,
				int monthOfYear, int dayOfMonth) {

			DateTime temp = DateTime.now();
			temp = temp.withDate(year, monthOfYear, dayOfMonth);
			DateTime now = DateTime.now();
			now = now.withZone(DateTimeZone.UTC);
			now = DateTime.now();

			if (temp.isAfter(now)) {
				dt = dt.withDate(year, monthOfYear, dayOfMonth);
				String format = dateFormat.print(dt);
				if ((isStartDate && dt.isAfter(otherDT))
						|| ((!isStartDate) && dt.isBefore(otherDT))) {
					otherDT = otherDT.withDate(year, monthOfYear, dayOfMonth);
					timeButton2.setText(timeFormat.print(otherDT));
					button2.setText(format);
				}
				button1.setText(format);
			} else {
				int hour, minute;
				hour = now.getHourOfDay();
				minute = now.getMinuteOfHour();
				now = now.withTime(0, 0, 0, 0);
				if (temp.isAfter(now)) {
					dt = dt.withDate(year, monthOfYear, dayOfMonth);
					dt = dt.withTime(hour, minute, 0, 0);
					String format = dateFormat.print(dt);

					if ((isStartDate && dt.isAfter(otherDT))
							|| ((!isStartDate) && dt.isBefore(otherDT))) {
						otherDT = otherDT.withDate(year, monthOfYear, dayOfMonth);
						otherDT = otherDT.withTime(hour, minute, 0, 0);
						timeButton2.setText(timeFormat.print(otherDT));
						button2.setText(format);
					}
					timeButton1.setText(timeFormat.print(dt));
					button1.setText(format);
				} else {
					AlertDialogUtil
							.showErrorDialog(EditMeetingActivity.this,
									"A Meeting can not be set to start or end before now");
				}
			}
		}
	}

	private class TimeClickListener implements OnClickListener,
			OnTimeSetListener {
		private Button button, otherButton;
		private DateTime dt, otherDT;
		boolean isStartTime;

		public TimeClickListener(Button button1, Button button2,
				DateTime datetime1, DateTime datetime2, Boolean start) {
			this.button = button1;
			this.otherButton = button2;
			is24 = android.text.format.DateFormat
					.is24HourFormat(getApplicationContext());
			this.dt = datetime1;
			this.otherDT = datetime2;
			this.isStartTime = start;
		}

		@Override
		public void onClick(View v) {
			is24 = android.text.format.DateFormat
					.is24HourFormat(getApplicationContext());
		
			FragmentManager fm = getSupportFragmentManager();
			RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
					.newInstance(TimeClickListener.this, // callback
							dt.getHourOfDay(), // hour
							dt.getMinuteOfHour(), // minute
							is24); // 24-hour mode
			timePickerDialog.show(fm, "fragment_time_picker_name");
		}

		@Override
		public void onTimeSet(RadialPickerLayout dialog, int hourOfDay,
				int minute) {
			DateTime temp = DateTime.now();
			temp = temp.withTime(hourOfDay, minute, 0, 0);

			DateTime now = DateTime.now().withZone(DateTimeZone.UTC);

			if (temp.isAfter(now)) {
				dt = dt.withHourOfDay(hourOfDay);
				dt = dt.withMinuteOfHour(minute);
				if ((isStartTime && dt.isAfter(otherDT))
						|| ((!isStartTime) && dt.isBefore(otherDT))) {
					otherDT = otherDT.withHourOfDay(hourOfDay);
					otherDT = otherDT.withMinuteOfHour(minute);
					otherButton.setText(timeFormat.print(dt));
				}
				button.setText(timeFormat.print(dt));
			} else {
				AlertDialogUtil.showErrorDialog(EditMeetingActivity.this,
						"A Meeting can not be set to start or end before now");
			}
		}

	}

	public class MeetingSaveTask extends AsyncTask<Meeting, Void, Boolean> {
		private AsyncResponse<Boolean> delegate;

		public MeetingSaveTask(AsyncResponse<Boolean> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected Boolean doInBackground(Meeting... params) {
			Meeting m = params[0];
			try {
				String userID = session.getUserID();
				MeetingDatabaseAdapter.createMeeting(userID, m);
			} catch (Exception e) {
				Log.e("MeetingSave", "Error: Failed to save meeting");
				Log.e("MEETING_ERR", e.toString());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			delegate.processFinish(result);
			super.onPostExecute(result);
		}

	}

}
