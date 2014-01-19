package com.android.meetingninja.tasks;

import com.android.meetingninja.MainActivity;
import com.android.meetingninja.R;
import com.android.meetingninja.R.id;
import com.android.meetingninja.R.layout;
import com.android.meetingninja.R.menu;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.user.UserInfoFetcher;

import objects.Meeting;
import objects.Task;
import objects.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ViewTaskActivity extends Activity {
	private TextView taskName, dateCreated, dateAssigned, deadline,
	description, completionCriteria, isCompleted, assignedLabel, assignedText;
	RetUserObj fetcher = null;
	private Task task = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_task);
		//setupActionBar();
		Intent i = getIntent();
		task = i.getParcelableExtra("task");
		setupViews();
		setTask();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
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
			default: return super.onOptionsItemSelected(item);
		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 5 ){
			if(resultCode == RESULT_OK){
				if(data != null){
					task = data.getParcelableExtra("task");
					setTask();
				}
			}else if (resultCode == RESULT_CANCELED){
				//do nothing here
			}
		}
	}
	
	public void completeTask(View v){
		TaskUpdater updater = new TaskUpdater();
		task.setIsCompleted(true);
		updater.updateTask(task);
		setTask();
		
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

	private void setTask(){
		taskName.setText(task.getTitle());
		dateCreated.setText(task.getDateCreated());
		dateAssigned.setText(task.getDateAssigned());
		deadline.setText(task.getEndTime());
		description.setText(task.getDescription());
		completionCriteria.setText(task.getCompletionCriteria());
		if(task.getIsCompleted()){
			isCompleted.setText("Yes"); //TODO: change this to use string xml
		}else{
			isCompleted.setText("No");  //TODO: change this to use string xml
		}
		fetcher = new RetUserObj();

		if(task.getType().equals("ASSIGNED_TO")){
			assignedLabel.setText("Assigned From:");
			//assignedText.setText(task.getAssignedFrom());
			fetcher.execute(task.getAssignedFrom());
		}else{
			assignedLabel.setText("Assigned To:");
			//assignedText.setText(task.getAssignedTo());
			fetcher.execute(task.getAssignedTo());
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
