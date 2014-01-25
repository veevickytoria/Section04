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
package com.meetingninja.csse.tasks;

import java.util.Calendar;

import objects.Task;

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
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog.OnDateSetListener;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.extras.MyDateUtils;

public class EditTaskActivity extends FragmentActivity implements
		AsyncResponse<Boolean> {
	final String MARK_AS_COMPLETE = "Mark As Complete";
	final String MARK_AS_INCOMPLETE = "Mark As Incomplete";

	private EditText mDescription, completionCriteria, mTitle;
	private TextView assignedDateLabel, createdDateLabel, isCompleted;
	private Button mDeadlineBtn, mCompleteBtn;
	private DateTimeFormatter dateFormat = MyDateUtils.JODA_MEETING_DATE_FORMAT;

	// private SessionManager session;
	private Task displayTask;
	Calendar cal = null;
	public static final String EXTRA_TASK = "task";

	// private static final String TAG = EditTaskActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);
		setupActionBar();
		setupViews();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			displayTask = extras.getParcelable(EXTRA_TASK);
		}
		if (displayTask != null) {
			setTask();
			mTitle.setSelection(0, mTitle.getText().toString().length());

			cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.parseLong(displayTask.getEndTime()));

			// int month = deadline.getMonthOfYear();
			// int year = deadline.getYear();
			// int day = deadline.getDayOfMonth();
			// cal = Calendar.getInstance();
			// cal.set(Calendar.YEAR, year, Calendar.MONTH, month - 1,
			// Calendar.DAY_OF_MONTH, day);
			// cal.set(Calendar.YEAR, year);
			// cal.set(Calendar.MONTH, month - 1);
			// cal.set(Calendar.DAY_OF_MONTH, day);

			mDeadlineBtn.setOnClickListener(new DateClickListener(mDeadlineBtn,
					cal, this));
			// assignedDateLabel.setText(dateFormat.format(assignedDate));
			// createdDateLabel.setText(dateFormat.format(createdDate));
		}
	}

	private void setTask() {
		mTitle.setText(displayTask.getTitle());
		completionCriteria.setText(displayTask.getCompletionCriteria());
		mDescription.setText(displayTask.getDescription());
		String format = dateFormat.print(Long.parseLong(displayTask
				.getEndTime()));
		mDeadlineBtn.setText(format);
		// String format =
		// dateFormat.print(Long.parseLong(displayTask.getDateAssigned()));
		// assignedDateLabel.setText(format);
		format = dateFormat.print(Long.parseLong(displayTask.getDateCreated()));
		createdDateLabel.setText(format);
		// TODO: use string.xml
		if (displayTask.getIsCompleted()) {
			isCompleted.setText("Yes");
			mCompleteBtn.setText(MARK_AS_INCOMPLETE);
		} else {
			isCompleted.setText("No");
			mCompleteBtn.setText(MARK_AS_COMPLETE);
		}
		// TODO: fetcher for assigned to/from
	}

	public void toggleCompleted(View v) {
		TaskUpdater updater = new TaskUpdater();
		displayTask.setIsCompleted(!displayTask.getIsCompleted());
		updater.updateTask(displayTask);
		setTask();
	}

	private void trimTextView() {
		mTitle.setText(mTitle.getText().toString().trim());
		mDescription.setText(mDescription.getText().toString().trim());
		completionCriteria.setText(completionCriteria.getText().toString()
				.trim());
	}

	private final View.OnClickListener tActionBarListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);
		}
	};

	private void setupActionBar() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Make an Ok/Cancel ActionBar
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,
				new LinearLayout(this), false);

		View cancelActionView = actionBarButtons
				.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(tActionBarListener);

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(tActionBarListener);

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
			if (mTitle.getText().equals(null)) {
				mTitle.setText("");
			}
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
		mTitle = (EditText) findViewById(R.id.task_edit_title);
		mDescription = (EditText) findViewById(R.id.task_edit_desc);
		completionCriteria = (EditText) findViewById(R.id.task_edit_comp_crit);
		mDeadlineBtn = (Button) findViewById(R.id.task_edit_deadline);
		mCompleteBtn = (Button) findViewById(R.id.task_mark_complete_button);
		isCompleted = (TextView) findViewById(R.id.task_edit_completed);
		assignedDateLabel = (TextView) findViewById(R.id.task_edit_date_assigned);
		createdDateLabel = (TextView) findViewById(R.id.task_edit_date_created);
	}

	@Override
	public void processFinish(Boolean result) {
		if (result) {
			finish();
		} else {
			Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT)
					.show();

		}
	}

	private void save() {
		if (TextUtils.isEmpty(mTitle.getText())) {
			Toast.makeText(this, "Empty Task not created", Toast.LENGTH_SHORT)
					.show();
			setResult(RESULT_CANCELED);
			finish();
		} else {
			trimTextView();
			displayTask.setTitle(mTitle.getText().toString());
			displayTask.setDescription(mDescription.getText().toString());
			displayTask.setCompletionCriteria(completionCriteria.getText()
					.toString());
			displayTask.setEndTime(cal.getTimeInMillis());
			Toast.makeText(this, String.format("Saving Task"),
					Toast.LENGTH_SHORT).show();

			TaskUpdater tUpdate = new TaskUpdater();
			tUpdate.updateTask(displayTask);

			Intent msgIntent = new Intent();
			msgIntent.putExtra(EXTRA_TASK, displayTask);
			setResult(RESULT_OK, msgIntent);

			finish();

		}
	}

	private class DateClickListener implements OnClickListener,
			OnDateSetListener {
		Calendar cal;
		FragmentActivity activity;

		public DateClickListener(Button b, Calendar c, FragmentActivity activity) {
			this.cal = c;
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			FragmentManager fm = getSupportFragmentManager();
			CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
					.newInstance(DateClickListener.this,
							cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
							cal.get(Calendar.DAY_OF_MONTH));
			calendarDatePickerDialog.show(fm, "fragment_date_picker_name");
		}

		@Override
		public void onDateSet(CalendarDatePickerDialog dialog, int year,
				int monthOfYear, int dayOfMonth) {
			Calendar tempcal = Calendar.getInstance();
			// TODO: maybe the better set?
			tempcal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			tempcal.set(Calendar.MONTH, monthOfYear);
			tempcal.set(Calendar.YEAR, year);
			Calendar now = null;
			now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, -1);
			if (!tempcal.before(now)) {
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.YEAR, year);
				String format = dateFormat.print(cal.getTimeInMillis());
				mDeadlineBtn.setText(format);
			} else {
				Toast.makeText(
						activity,
						String.format("A Deadline can not be set before today's date"),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
