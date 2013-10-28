package com.droidrage.meetingninja;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MeetingsActivity extends Activity {

	private Button mFromDate, mToDate, mFromTime, mToTime;
	private Calendar cal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meetings);
		// Show the Up button in the action bar.
		setupActionBar();
		cal = GregorianCalendar.getInstance();

		mFromDate = (Button) findViewById(R.id.meeting_from_date);
		mFromDate.setOnClickListener(new DateClickListener(mFromDate));
		mToDate = (Button) findViewById(R.id.meeting_to_date);
		mToDate.setOnClickListener(new DateClickListener(mToDate));

		mFromTime = (Button) findViewById(R.id.meeting_from_time);
		mFromTime.setOnClickListener(new TimeClickListener(mFromTime));
		mToTime = (Button) findViewById(R.id.meeting_to_time);
		mToTime.setOnClickListener(new TimeClickListener(mToTime));
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
		}
		return super.onOptionsItemSelected(item);
	}

	private class DateClickListener implements OnClickListener,
			OnDateSetListener {
		Button button;

		public DateClickListener(Button b) {
			this.button = b;
		}

		@Override
		public void onClick(View v) {
			new DatePickerDialog(MeetingsActivity.this, this,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)).show();
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM dd, yyyy",
					Locale.US);
			Calendar tmp = GregorianCalendar.getInstance();
			tmp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			tmp.set(Calendar.MONTH, monthOfYear);
			tmp.set(Calendar.YEAR, year);
			button.setText(fmt.format(tmp.getTime()));

		}

	}

	private class TimeClickListener implements OnClickListener,
			OnTimeSetListener {
		Button button;
		boolean is24;

		public TimeClickListener(Button b) {
			this.button = b;
			String _24 = android.provider.Settings.System.getString(
					getContentResolver(),
					android.provider.Settings.System.TIME_12_24);
			is24 = _24.equals("24");
		}

		@Override
		public void onClick(View v) {
			new TimePickerDialog(MeetingsActivity.this, this,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					is24).show();
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			SimpleDateFormat fmt;
			Locale en_us = Locale.US;
			fmt = is24 ? new SimpleDateFormat("hh:mm", en_us)
					: new SimpleDateFormat("hh:mma", en_us);

			Calendar tmp = GregorianCalendar.getInstance();
			tmp.set(Calendar.HOUR_OF_DAY, hourOfDay);
			tmp.set(Calendar.MINUTE, minute);
			button.setText(fmt.format(tmp.getTime()));

		}

	}

}
