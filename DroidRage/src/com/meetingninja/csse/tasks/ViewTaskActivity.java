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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.TaskVolleyAdapter;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.MyDateUtils;
import com.meetingninja.csse.tasks.tasks.DeleteTaskTask;
import com.meetingninja.csse.tasks.tasks.UpdateTaskTask;

public class ViewTaskActivity extends Activity {
	private static final String TAG = ViewTaskActivity.class.getSimpleName();
	private TextView taskName, dateCreated, dateAssigned, deadline,
			description, completionCriteria, isCompleted, assignedLabel,
			assignedText;
	private Button taskCompleteButton;
	private Task displayedTask;
	private DateTimeFormatter dateFormat = MyDateUtils.JODA_APP_DATE_FORMAT;
	private int resultCode = Activity.RESULT_CANCELED;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_task);
		// setupActionBar();
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			displayedTask = extras.getParcelable(Keys.Task.PARCEL);
		}
		else{
			Log.w(TAG, "Error: Unable to find Task Parcel");
		}
		setupViews();
		if(displayedTask != null){
			setTask();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_item, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.edit_item_task:
			Intent editTask = new Intent(this, EditTaskActivity.class);
			editTask.putExtra(Keys.Task.PARCEL, displayedTask);
			this.startActivityForResult(editTask, 5);
			return true;
		case R.id.delete_item_task:
			DeleteTaskTask deleter = new DeleteTaskTask();
			deleter.deleteTask(displayedTask.getID());
			setResult(RESULT_OK);
			finish();
		case android.R.id.home:
			setResult(resultCode);
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
					displayedTask = data.getParcelableExtra(Keys.Task.PARCEL);
					getIntent().putExtra(Keys.Task.PARCEL, displayedTask);
					setTask();
					UpdateTaskTask tUpdate = new UpdateTaskTask();
					tUpdate.updateTask(displayedTask);
					this.resultCode = resultCode;
				}
			} else if (resultCode == RESULT_CANCELED) {
				// do nothing here
			}
		}
	}

	public void completeTask(View v) {
		UpdateTaskTask updater = new UpdateTaskTask();
		displayedTask.setIsCompleted(true);
		updater.updateTask(displayedTask);
		setTask();
		resultCode = Activity.RESULT_OK;
	}

	private void setupViews() {
		taskName = (TextView) this.findViewById(R.id.task_title_label);
		dateCreated = (TextView) this.findViewById(R.id.task_date_created_text);
		dateAssigned = (TextView) this.findViewById(R.id.task_date_assigned_text);
		deadline = (TextView) this.findViewById(R.id.task_date_deadline_text);
		description = (TextView) this.findViewById(R.id.task_desc_text);
		completionCriteria = (TextView) this.findViewById(R.id.task_comp_crit_text);
		isCompleted = (TextView) this.findViewById(R.id.task_completed_text);
		assignedLabel = (TextView) this.findViewById(R.id.task_assigned_label);
		assignedText = (TextView) this.findViewById(R.id.task_assigned_text);
		taskCompleteButton = (Button) this.findViewById(R.id.task_complete_button);
	}

	private void setTask() {
		taskName.setText(displayedTask.getTitle());
		String format = dateFormat.print(Long.parseLong(displayedTask.getDateCreated()));
		dateCreated.setText(format);
		// TODO: change this to the real date assigned
		dateAssigned.setText(displayedTask.getDateAssigned());
		format = dateFormat.print(displayedTask.getEndTimeInMillis());
		deadline.setText(format);
		description.setText(displayedTask.getDescription());
		completionCriteria.setText(displayedTask.getCompletionCriteria());
		if (displayedTask.getIsCompleted()) {
			isCompleted.setText("Yes"); // TODO: change this to use string xml
			taskCompleteButton.setVisibility(View.INVISIBLE);
		} else {
			isCompleted.setText("No"); // TODO: change this to use string xml
			taskCompleteButton.setVisibility(View.VISIBLE);
		}
		if(displayedTask.getType()!=null){
			if (displayedTask.getType().equals("ASSIGNED_TO")) {
				assignedLabel.setText("Assigned From:");
				// assignedText.setText(task.getAssignedFrom());
				fetchUserName(displayedTask.getAssignedFrom());
			} else {
				assignedLabel.setText("Assigned To:");
				// assignedText.setText(task.getAssignedTo());
				if (!displayedTask.getAssignedTo().toString().equals("")) {
					fetchUserName(displayedTask.getAssignedTo());
				} else {
					assignedText.setText("Unassigned");
				}
			}
		} else{			
			TaskVolleyAdapter.getTaskInfo(displayedTask.getID(), new AsyncResponse<Task>() {
				@Override
				public void processFinish(Task result) {
					displayedTask = result;
					setTask();
				}
			});
			


		}
		
	}

	private void fetchUserName(String userID) {
		if (!TextUtils.isEmpty(userID))
			UserVolleyAdapter.fetchUserInfo(userID, new AsyncResponse<User>() {
				@Override
				public void processFinish(User result) {
					assignedText.setText(result.getDisplayName());

				}
			});
		else
			Log.w(TAG, "[fetchUserName] userID is empty");
	}

}
