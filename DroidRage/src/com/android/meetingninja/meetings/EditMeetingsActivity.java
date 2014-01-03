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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import objects.Meeting;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.MeetingDatabaseAdapter;
import com.android.meetingninja.user.SessionManager;

public class EditMeetingsActivity extends Activity implements
		AsyncResponse<Boolean> {

	private boolean is24, edit_mode;
	private Button mFromDate, mToDate, mFromTime, mToTime;
	private EditText mLocation, mTitle;
	private Calendar start, end;
	private MeetingSaveTask creater = null;
	private SimpleDateFormat timeFormat;
	private final DateFormat dateFormat = new SimpleDateFormat(
			"EEE, MMM dd, yyyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);
		// Show the Up button in the action bar.
		setupActionBar();
		// Do some initializations
		is24 = android.text.format.DateFormat
				.is24HourFormat(getApplicationContext());
		edit_mode = getIntent().getBooleanExtra("edit", true);
		Locale en_us = Locale.US;
		timeFormat = is24 ? new SimpleDateFormat("HH:mm", en_us)
				: new SimpleDateFormat("hh:mma", en_us);

		setupViews();

		start = Calendar.getInstance();
		end = Calendar.getInstance();

		start.add(Calendar.HOUR_OF_DAY, 1);
		start.set(Calendar.MINUTE, 0);

		end.add(Calendar.HOUR_OF_DAY, 2);
		end.set(Calendar.MINUTE, 0);

		mFromDate.setOnClickListener(new DateClickListener(mFromDate, start));
		mFromDate.setText(dateFormat.format(start.getTime()));

		mToDate.setOnClickListener(new DateClickListener(mToDate, end));
		mToDate.setText(dateFormat.format(end.getTime()));

		mFromTime.setOnClickListener(new TimeClickListener(mFromTime, start));
		mFromTime.setText(timeFormat.format(start.getTime()));

		mToTime.setOnClickListener(new TimeClickListener(mToTime, end));
		mToTime.setText(timeFormat.format(end.getTime()));

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		// Hide RadioGroup if creating meeting
		if (edit_mode) {
			bottom.findViewById(R.id.response_row).setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_meeting, menu);
		return true;
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
		case R.id.action_save:
			Meeting m = new Meeting(1, mTitle.getText().toString(), mLocation
					.getText().toString(), mFromDate.getText().toString() + "@"
					+ mFromTime.getText().toString());
			creater = new MeetingSaveTask(this);
			creater.execute(m);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void processFinish(Boolean result) {
		if (result) {
			finish();
		} else {
			Toast.makeText(this, "Failed to save meeting", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private class DateClickListener implements OnClickListener,
			OnDateSetListener {
		Button button;
		Calendar cal;

		public DateClickListener(Button b, Calendar c) {
			this.button = b;
			this.cal = c;
		}

		@Override
		public void onClick(View v) {
			new DatePickerDialog(EditMeetingsActivity.this, this,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)).show();

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
				cal.set(Calendar.DAY_OF_MONTH, day);
				// error message
				return;
			}

			button.setText(dateFormat.format(cal.getTime()));

		}

	}

	private class TimeClickListener implements OnClickListener,
			OnTimeSetListener {
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
			new TimePickerDialog(EditMeetingsActivity.this, this,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					is24).show();
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
				return;
				// error message
			}

			button.setText(timeFormat.format(cal.getTime()));
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
				SessionManager session = SessionManager.getInstance();
				String user = session.getUserDetails().get(SessionManager.USER);
				MeetingDatabaseAdapter.createMeeting(user, m);
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
