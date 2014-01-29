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

import objects.Task;
import objects.User;

import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.extras.MyDateUtils;
import com.meetingninja.csse.user.UserInfoFetcher;

public class ViewTaskActivity extends Activity {
	private TextView taskName, dateCreated, dateAssigned, deadline,
			description, completionCriteria, isCompleted, assignedLabel,
			assignedText;
	private Button taskCompleteButton;
	RetUserObj fetcher = null;
	private Task task = null;
	private DateTimeFormatter dateFormat = MyDateUtils.JODA_APP_DATE_FORMAT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_task);
		// setupActionBar();
		Intent i = getIntent();
		task = i.getParcelableExtra("task");
		setupViews();
		setTask();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item_menu, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.edit_item_task:
			Intent editTask = new Intent(this, EditTaskActivity.class);
			editTask.putExtra("task", task);
			this.startActivityForResult(editTask, 5);
			return true;
		case R.id.delete_item_task:
			TaskDeleter deleter = new TaskDeleter();
			deleter.deleteTask(task.getID());
			setResult(RESULT_OK);
			finish();
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					task = data.getParcelableExtra("task");
					setTask();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// do nothing here
			}
		}
	}

	public void completeTask(View v) {
		TaskUpdater updater = new TaskUpdater();
		task.setIsCompleted(true);
		updater.updateTask(task);
		setTask();

	}

	private void setupViews() {
		taskName = (TextView) this.findViewById(R.id.task_title_label);
		dateCreated = (TextView) this.findViewById(R.id.task_date_created_text);
		dateAssigned = (TextView) this
				.findViewById(R.id.task_date_assigned_text);
		deadline = (TextView) this.findViewById(R.id.task_date_deadline_text);
		description = (TextView) this.findViewById(R.id.task_desc_text);
		completionCriteria = (TextView) this
				.findViewById(R.id.task_comp_crit_text);
		isCompleted = (TextView) this.findViewById(R.id.task_completed_text);
		assignedLabel = (TextView) this.findViewById(R.id.task_assigned_label);
		assignedText = (TextView) this.findViewById(R.id.task_assigned_text);
		taskCompleteButton = (Button) this
				.findViewById(R.id.task_complete_button);
	}

	private void setTask() {
		taskName.setText(task.getTitle());
		String format = dateFormat.print(Long.parseLong(task.getDateCreated()));
		dateCreated.setText(format);
		// TODO: change this to the real date assigned
		dateAssigned.setText(task.getDateAssigned());
		format = dateFormat.print(task.getEndTimeInMillis());
		deadline.setText(format);
		description.setText(task.getDescription());
		completionCriteria.setText(task.getCompletionCriteria());
		if (task.getIsCompleted()) {
			isCompleted.setText("Yes"); // TODO: change this to use string xml
			System.out.println("before");
			;
			taskCompleteButton.setVisibility(View.INVISIBLE);
			System.out.println("after");
		} else {
			isCompleted.setText("No"); // TODO: change this to use string xml
			taskCompleteButton.setVisibility(View.VISIBLE);
		}
		fetcher = new RetUserObj();

		if (task.getType().equals("ASSIGNED_TO")) {
			assignedLabel.setText("Assigned From:");
			// assignedText.setText(task.getAssignedFrom());
			fetcher.execute(task.getAssignedFrom());
		} else {
			assignedLabel.setText("Assigned To:");
			// assignedText.setText(task.getAssignedTo());
			fetcher.execute(task.getAssignedTo());
		}
	}

	private void setAssigned(String name) {
		assignedText.setText(name);
	}

	final class RetUserObj implements AsyncResponse<User> {

		private UserInfoFetcher infoFetcher;

		public RetUserObj() {
			infoFetcher = new UserInfoFetcher(this);
		}

		public void execute(String userID) {
			System.out.println("afdsafdsa     " + userID);
			infoFetcher.execute(userID);
		}

		@Override
		public void processFinish(User result) {
			setAssigned(result.getDisplayName());
		}
	}

}
