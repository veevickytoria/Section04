package com.droidrage.meetingninja;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.droidrage.meetingninja.MeetingsFragment.MeetingFetcherTask;

import objects.Meeting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
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

public class MeetingsActivity extends Activity implements AsyncResponse<Boolean>{
	
	private Button mFromDate, mToDate, mFromTime, mToTime;
	private EditText mLocation, mTitle;
	private Calendar cal;
	private MeetingSaveTask creater = null;

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
		
		mLocation = (EditText) findViewById(R.id.meeting_location);
		mTitle = (EditText) findViewById(R.id.meeting_title);
		
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
			Meeting m = new Meeting(1, mTitle.getText().toString(), mLocation.getText().toString(), mFromDate.getText().toString() + "@" + mFromTime.getText().toString());
			creater = new MeetingSaveTask(this);
			creater.execute(m);
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
			is24 = (!(_24 == null));
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
	
	
	public class MeetingSaveTask extends
			AsyncTask<Meeting, Void, Boolean> {
		private AsyncResponse<Boolean> delegate;

		public MeetingSaveTask(AsyncResponse<Boolean> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected Boolean doInBackground(Meeting... params) {
			Meeting m = params[0];
			try {
				SessionManager session = new SessionManager(getApplicationContext());
				String user = session.getUserDetails().get(SessionManager.USER);
				DatabaseAdapter.createMeeting(user, m);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			delegate.processFinish(result);
			super.onPostExecute(result);
		}


	}

	@Override
	public void processFinish(Boolean result) {
		// TODO Auto-generated method stub
		if(result){
			//Intent main = new Intent(this, MainActivity.class);
			//startActivity(main);
			finish();
		}else{
			Toast.makeText(this, "Failed to save meeting", Toast.LENGTH_SHORT).show();
		}
		
	}

}
