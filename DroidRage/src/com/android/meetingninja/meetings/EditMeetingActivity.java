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
package com.android.meetingninja.meetings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.DateTime;

import objects.Meeting;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.MeetingDatabaseAdapter;
import com.android.meetingninja.database.local.SQLiteMeetingAdapter;
import com.android.meetingninja.extras.MyDateUtils;
import com.android.meetingninja.user.SessionManager;

public class EditMeetingActivity extends FragmentActivity implements
		AsyncResponse<Boolean> {

	private Bundle extras;
	private EditText mTitle, mLocation, mDescription;
	private Button mFromDate, mToDate, mToTime;
	private Button mFromTime;
	private boolean is24, edit_mode;
	private Calendar start, end;
	private MeetingSaveTask creater = null;
	private SimpleDateFormat timeFormat;
	private SimpleDateFormat dateFormat = MyDateUtils.APP_DATE_FORMAT;

	private SQLiteMeetingAdapter mySQLiteAdapter;
	private Meeting displayedMeeting;

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_LOCATION = "location";
	public static final String EXTRA_DESCRIPTION = "description";
	public static final String EXTRA_EDIT_MODE = "editing";
	public static final String EXTRA_MEETING = "displayedMeeting";

	private static final String TAG = EditMeetingActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);
		// Show the Up button in the action bar.
		setupActionBar();

		// Do some initializations
		is24 = android.text.format.DateFormat
				.is24HourFormat(getApplicationContext());
		timeFormat = is24 ? MyDateUtils._24_TIME_FORMAT
				: MyDateUtils._12_TIME_FORMAT;
		extras = getIntent().getExtras();
		edit_mode = extras.getBoolean(EXTRA_EDIT_MODE, true);
		mySQLiteAdapter = new SQLiteMeetingAdapter(this);
		if (extras != null && !extras.isEmpty()) {
			displayedMeeting = extras.getParcelable(EXTRA_MEETING);
		}

		setupViews();

		// init the text fields
		if (displayedMeeting != null) {
			mTitle.setText(displayedMeeting.getTitle());
			mLocation.setText(displayedMeeting.getLocation());
			mDescription.setText(displayedMeeting.getDescription());
		}

		// init the date-time pickers
		start = Calendar.getInstance();
		end = Calendar.getInstance();
		if (displayedMeeting != null) {
			try {
				start.setTimeInMillis(displayedMeeting.getStartTime_Time());
				end.setTimeInMillis(displayedMeeting.getEndTime_Time());
			} catch (ParseException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		} else {
			start.add(Calendar.HOUR_OF_DAY, 1);
			start.set(Calendar.MINUTE, 0);

			end.add(Calendar.HOUR_OF_DAY, 2);
			end.set(Calendar.MINUTE, 0);
		}
		mFromDate.setOnClickListener(new DateClickListener(mFromDate, start));
		mFromDate.setText(dateFormat.format(start.getTime()));

		mToDate.setOnClickListener(new DateClickListener(mToDate, end));
		mToDate.setText(dateFormat.format(end.getTime()));

		mFromTime.setOnClickListener(new TimeClickListener(mFromTime, start));
		mFromTime.setText(timeFormat.format(start.getTime()));

		mToTime.setOnClickListener(new TimeClickListener(mToTime, end));
		mToTime.setText(timeFormat.format(end.getTime()));
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

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.new_meeting, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: // This ID represents the
	 * Home or Up button. In the case of this // activity, the Up button is
	 * shown. Use NavUtils to allow users // to navigate up one level in the
	 * application structure. For // more details, see the Navigation pattern on
	 * Android Design: // //
	 * http://developer.android.com/design/patterns/navigation.html#up-vs-back
	 * // NavUtils.navigateUpFromSameTask(this); return true; case
	 * R.id.action_save: // TODO : Create a meeting // Meeting m = new
	 * Meeting(1, mTitle.getText().toString(), mLocation //
	 * .getText().toString(), mFromDate.getText().toString() + "@" // +
	 * mFromTime.getText().toString()); creater = new MeetingSaveTask(this); //
	 * creater.execute(m); } return super.onOptionsItemSelected(item); }
	 */

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
			newMeeting.setStartTime(start.getTimeInMillis());
			newMeeting.setEndTime(end.getTimeInMillis());
			newMeeting.setDescription(desc);

			if (displayedMeeting != null) {
				newMeeting.setID(displayedMeeting.getID());
				// mySQLiteAdapter.updateMeeting(newMeeting);
				msgIntent.putExtra("method", "update");
			} else {
				newMeeting = mySQLiteAdapter.insertMeeting(newMeeting);
				msgIntent.putExtra("method", "insert");
			}

			msgIntent.putExtra(EXTRA_MEETING, newMeeting);
			if (extras != null)
				msgIntent.putExtra("listPosition",
						extras.getInt("listPosition", -1));

			setResult(RESULT_OK, msgIntent);
			finish();
		}
	}

	private class DateClickListener implements OnClickListener,
			android.app.DatePickerDialog.OnDateSetListener {
		Button button;
		Calendar cal;

		public DateClickListener(Button b, Calendar c) {
			this.button = b;
			this.cal = c;
		}

		@Override
		public void onClick(View v) {
			new DatePickerDialog(EditMeetingActivity.this, this,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)).show();
			// FragmentManager fm = getSupportFragmentManager();
			// CalendarDatePickerDialog calendarDatePickerDialog =
			// CalendarDatePickerDialog
			// .newInstance(DateClickListener.this,
			// cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
			// cal.get(Calendar.DAY_OF_MONTH));
			// calendarDatePickerDialog.show(fm, "fragment_date_picker_name");
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			int yr, month, day;
			yr = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DAY_OF_MONTH);

			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.YEAR, year);
			if (cal.before(start) || cal.after(end)) {
				cal.set(Calendar.YEAR, yr);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day); // error message return;
			}

			button.setText(dateFormat.format(cal.getTime()));

		}

		/*
		 * @Override public void onDateSet(CalendarDatePickerDialog dialog, int
		 * year, int monthOfYear, int dayOfMonth) { int yr, month, day; yr =
		 * cal.get(Calendar.YEAR); month = cal.get(Calendar.MONTH); day =
		 * cal.get(Calendar.DAY_OF_MONTH);
		 * 
		 * cal.set(Calendar.DAY_OF_MONTH, dayOfMonth); cal.set(Calendar.MONTH,
		 * monthOfYear); cal.set(Calendar.YEAR, year); if (cal.before(start) ||
		 * cal.after(end)) { cal.set(Calendar.YEAR, yr); cal.set(Calendar.MONTH,
		 * month); cal.set(Calendar.DAY_OF_MONTH, day); // error message return;
		 * }
		 * 
		 * button.setText(dateFormat.format(cal.getTime())); }
		 */

	}

	private class TimeClickListener implements OnClickListener,
			android.app.TimePickerDialog.OnTimeSetListener {
		Button button;
		Calendar cal;

		public TimeClickListener(Button b, Calendar c) {
			this.button = b;
			is24 = android.text.format.DateFormat
					.is24HourFormat(getApplicationContext());

			this.cal = c;
		}

		@Override
		public void onClick(View v) {
			is24 = android.text.format.DateFormat
					.is24HourFormat(getApplicationContext());
			new TimePickerDialog(EditMeetingActivity.this, this,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					is24).show();
			// FragmentManager fm = getSupportFragmentManager();
			// RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
			// .newInstance(TimeClickListener.this,
			// cal.get(Calendar.HOUR_OF_DAY),
			// cal.get(Calendar.MINUTE), is24);
			// timePickerDialog.show(fm, "fragment_time_picker_name");
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			// should it recheck 24 or 12 hour mode?
			int hour, min;
			hour = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);

			if (cal.before(start) || cal.after(end)) {
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, min);
				return; // error message
			}

			button.setText(timeFormat.format(cal.getTime()));
		}

		/*
		 * @Override public void onTimeSet(RadialPickerLayout dialog, int
		 * hourOfDay, int minute) { int hour, min; hour =
		 * cal.get(Calendar.HOUR_OF_DAY); min = cal.get(Calendar.MINUTE);
		 * cal.set(Calendar.HOUR_OF_DAY, hourOfDay); cal.set(Calendar.MINUTE,
		 * minute);
		 * 
		 * if (cal.before(start) || cal.after(end)) {
		 * cal.set(Calendar.HOUR_OF_DAY, hour); cal.set(Calendar.MINUTE, min);
		 * return; // error message }
		 * 
		 * button.setText(timeFormat.format(cal.getTime()));
		 * 
		 * }
		 */

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
				SessionManager session = SessionManager.getInstance();
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
