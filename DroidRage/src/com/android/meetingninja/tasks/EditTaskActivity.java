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
package com.android.meetingninja.tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import objects.Task;
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

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.extras.MyDateUtils;
import com.android.meetingninja.user.SessionManager;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog.OnDateSetListener;

public class EditTaskActivity extends FragmentActivity implements
		AsyncResponse<Boolean> {
	final String MARK_AS_COMPLETE = "Mark As Complete";
	final String MARK_AS_INCOMPLETE = "Mark As Incomplete";
	
	private EditText Description, completionCriteria, Title;
	private TextView assignedDateLabel, createdDateLabel, isCompleted;
	private Button tDeadline,mComplete;
	private SimpleDateFormat dateFormat = MyDateUtils.APP_DATE_FORMAT;

	private SessionManager session;
	private Task displayTask;
	Calendar cal = null;
	public static final String EXTRA_TASK = "task";

	private static final String TAG = EditTaskActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);
		setupActionBar();
		setupView();
		Intent i = getIntent();
		displayTask = i.getParcelableExtra(EXTRA_TASK);
		if (displayTask != null) {
			setTask();
			Title.setSelection(0, Title.getText().toString().length());;
			String deadline = displayTask.getEndTime();
			String monthdayyear[] = deadline.split("/");

			int month = (int) Integer.parseInt(monthdayyear[0]);
			int year = (int) Integer.parseInt(monthdayyear[2]);
			int day = (int) Integer.parseInt(monthdayyear[1]);

			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,year,Calendar.MONTH,month-1,Calendar.DAY_OF_MONTH,day);
			//cal.set(Calendar.YEAR, year);
			//cal.set(Calendar.MONTH, month - 1);
			//cal.set(Calendar.DAY_OF_MONTH, day);

			tDeadline.setText(deadline);
			tDeadline.setOnClickListener(new DateClickListener(tDeadline, cal));
			
			// findViewById(R.id.task_edit_date_created).toString();

			// assignedDateLabel.setText(dateFormat.format(assignedDate));
			// createdDateLabel.setText(dateFormat.format(createdDate));
		}
	}
	private void setTask(){
		System.out.println("got to settask");
		Title.setText(displayTask.getTitle());
		completionCriteria.setText(displayTask.getCompletionCriteria());
		Description.setText(displayTask.getDescription());
		//TODO: use string.xml
		if (displayTask.getIsCompleted()) {
			isCompleted.setText("Yes");
			mComplete.setText(MARK_AS_INCOMPLETE);
		}else{
			isCompleted.setText("No");
			mComplete.setText(MARK_AS_COMPLETE);
		}
		//TODO: fetcher for assigned to/from
	}
	public void toggleCompleted(View v){
		System.out.println("got to toggle");
		TaskUpdater updater = new TaskUpdater();
		displayTask.setIsCompleted(!displayTask.getIsCompleted());
		updater.updateTask(displayTask);
		setTask();
	}
	private void trimTextView() {
		Title.setText(Title.getText().toString().trim());
		Description.setText(Description.getText().toString().trim());
		completionCriteria.setText(completionCriteria.getText().toString().trim());
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
			if (Title.getText().equals(null)) {
				Title.setText("");
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

	private void setupView() {
		Title = (EditText) findViewById(R.id.task_edit_title);
		Description = (EditText) findViewById(R.id.task_edit_desc);
		completionCriteria = (EditText) findViewById(R.id.task_edit_comp_crit);
		tDeadline = (Button) findViewById(R.id.task_edit_deadline);
		mComplete = (Button) findViewById (R.id.task_mark_complete_button);
		isCompleted = (TextView) findViewById(R.id.task_edit_completed);
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
		if (TextUtils.isEmpty(Title.getText())) {
			Toast.makeText(this, "Empty Task not created", Toast.LENGTH_SHORT)
					.show();
			setResult(RESULT_CANCELED);
			finish();
		} else {

			trimTextView();
			displayTask.setTitle(Title.getText().toString());
			displayTask.setDescription(Description.getText().toString());
			displayTask.setCompletionCriteria(completionCriteria.getText().toString());
			displayTask.setEndTime(tDeadline.getText().toString());
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

		public DateClickListener(Button b, Calendar c) {
			this.cal = c;
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
			tempcal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			tempcal.set(Calendar.MONTH, monthOfYear);
			tempcal.set(Calendar.YEAR, year);
			Calendar now = null;
			now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, -1);
			if (tempcal.before(now)) {
			} else {
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				cal.set(Calendar.MONTH, monthOfYear);
				cal.set(Calendar.YEAR, year);
				tDeadline.setText(cal.get(Calendar.MONTH) + 1 + "/"
						+ cal.get(Calendar.DAY_OF_MONTH) + "/"
						+ cal.get(Calendar.YEAR));
			}

		}
	}
}
