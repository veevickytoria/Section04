package com.droidrage.meetingninja;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import objects.Meeting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
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
import android.support.v4.app.NavUtils;

public class MeetingsActivity extends Activity implements
		AsyncResponse<Boolean> {

	private boolean is24;
	private Button mFromDate, mToDate, mFromTime, mToTime;
	private EditText mLocation, mTitle;
	private Calendar cal,start,end;
	private MeetingSaveTask creater = null;
	private SimpleDateFormat timeFormat;
	private final DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meetings);
		// Show the Up button in the action bar.
		setupActionBar();
		cal = GregorianCalendar.getInstance();
		

		String _24 = android.provider.Settings.System.getString(
				getContentResolver(),
				android.provider.Settings.System.TIME_12_24);
		is24 = _24 .equals("24");
		
		Locale en_us = Locale.US;
		timeFormat = is24 ? new SimpleDateFormat("HH:mm", en_us)
							: new SimpleDateFormat("hh:mma", en_us);
		//get current date time with Calendar()
		start= Calendar.getInstance();
		end= Calendar.getInstance();
		
		start.add(Calendar.HOUR_OF_DAY, 1);
		start.set(Calendar.MINUTE, 0);
		
		end.add(Calendar.HOUR_OF_DAY, 2);
		end.set(Calendar.MINUTE, 0);
		
		mFromDate = (Button) findViewById(R.id.meeting_from_date);
		mFromDate.setOnClickListener(new DateClickListener(mFromDate,start));
		mToDate = (Button) findViewById(R.id.meeting_to_date);
		mToDate.setOnClickListener(new DateClickListener(mToDate,end));
		mFromTime = (Button) findViewById(R.id.meeting_from_time);
		
				
		
		mFromDate.setText(dateFormat.format(start.getTime()));
		mFromTime.setText(timeFormat.format(start.getTime()));
		
		
		mFromTime.setOnClickListener(new TimeClickListener(mFromTime,start));
		
		mToTime = (Button) findViewById(R.id.meeting_to_time);
		mToTime.setOnClickListener(new TimeClickListener(mToTime,end));

		mLocation = (EditText) findViewById(R.id.meeting_location);
		mTitle = (EditText) findViewById(R.id.meeting_title);
		
		mToDate.setText(dateFormat.format(end.getTime()));
		mToTime.setText(timeFormat.format(end.getTime()));
		
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
		case R.id.action_save:
			Meeting m = new Meeting(1, mTitle.getText().toString(), mLocation
					.getText().toString(), mFromDate.getText().toString() + "@"
					+ mFromTime.getText().toString());
			creater = new MeetingSaveTask(this);
			creater.execute(m);
		}
		return super.onOptionsItemSelected(item);
	}

	private class DateClickListener implements OnClickListener,
			OnDateSetListener {
		Button button;
		Calendar c;
		
		public DateClickListener(Button b,Calendar c) {
			this.button = b;
			this.c=c;
		}

		@Override
		public void onClick(View v) {
			new DatePickerDialog(MeetingsActivity.this, this,
					c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH)).show();
			
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			c.set(Calendar.MONTH, monthOfYear);
			c.set(Calendar.YEAR, year);
			button.setText(dateFormat.format(c.getTime()));

		}

	}

	private class TimeClickListener implements OnClickListener,
			OnTimeSetListener {
		Button button;
		Calendar c;
		
		public TimeClickListener(Button b,Calendar c) {
			this.button = b;
			String _24 = android.provider.Settings.System.getString(
					getContentResolver(),
					android.provider.Settings.System.TIME_12_24);
			is24 = _24 .equals("24");

			this.c=c;
		}

		@Override
		public void onClick(View v) {
			new TimePickerDialog(MeetingsActivity.this, this,
					c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
					is24).show();					
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Locale en_us = Locale.US;

			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			button.setText(timeFormat.format(c.getTime()));
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
				SessionManager session = new SessionManager(
						getApplicationContext());
				String user = session.getUserDetails().get(SessionManager.USER);
				DatabaseAdapter.createMeeting(user, m);
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

	@Override
	public void processFinish(Boolean result) {
		if (result) {
			finish();
		} else {
			Toast.makeText(this, "Failed to save meeting", Toast.LENGTH_SHORT)
					.show();
		}

	}

}
