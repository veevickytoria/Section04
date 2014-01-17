package com.android.meetingninja;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.user.UserInfoFetcher;

import objects.Task;
import objects.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ViewTaskActivity extends Activity {
	private TextView taskName, dateCreated, dateAssigned, deadline,
				description, completionCriteria, isCompleted, assignedLabel, assignedText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_task);
		Intent i = getIntent();
		Task task = i.getParcelableExtra("task");
		setupViews();
		setTask(task);
		System.out.println(task.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_task, menu);
		return true;
	}
	
	private void setupViews(){
		taskName = (TextView) this.findViewById(R.id.task_title_label);
		dateCreated = (TextView) this.findViewById(R.id.task_date_created_text);
		dateAssigned = (TextView) this.findViewById(R.id.task_date_assigned_text);
		deadline = (TextView) this.findViewById(R.id.task_date_deadline_text);
		description = (TextView) this.findViewById(R.id.task_desc_text);
		completionCriteria = (TextView) this.findViewById(R.id.task_comp_crit_text);
		isCompleted = (TextView) this.findViewById(R.id.task_completed_text);
		assignedLabel = (TextView) this.findViewById(R.id.task_assigned_label);
		assignedText = (TextView) this.findViewById(R.id.task_assigned_text);
	}
	
	private void setTask(Task task){
		taskName.setText(task.getTitle());
		dateCreated.setText(task.getDateCreated());
		dateAssigned.setText(task.getDateAssigned());
		deadline.setText(task.getDeadline());
		description.setText(task.getDescription());
		completionCriteria.setText(task.getCompletionCriteria());
		
		if(task.getIsCompleted()){
			isCompleted.setText("Yes"); //TODO: change this to use string xml
		}else{
			isCompleted.setText("No");  //TODO: change this to use string xml
		}
		
		if(task.getType().equals("ASSIGNED TO")){
			assignedLabel.setText("Assigned From:");
			assignedText.setText(task.getAssignedFrom());
		}else{
			assignedLabel.setText("Assigned To:");
			assignedText.setText(task.getAssignedTo());
		}
	}
	
	private void setAssigned(String name){
		assignedText.setText(name);
	}
	
	final class RetUserObj implements AsyncResponse<User> {

		private UserInfoFetcher infoFetcher;

		public RetUserObj() {
			infoFetcher = new UserInfoFetcher(this);
		}

		public void execute(String userID) {
			infoFetcher.execute(userID);
		}

		@Override
		public void processFinish(User result) {
			setAssigned(result.getDisplayName());
		}
	}

}
