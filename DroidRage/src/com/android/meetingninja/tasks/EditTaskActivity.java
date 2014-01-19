package com.android.meetingninja.tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import objects.Task;

import com.android.meetingninja.R.id;
import com.android.meetingninja.R.layout;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.TaskDatabaseAdapter;
import com.android.meetingninja.extras.MyDateUtils;
import com.android.meetingninja.user.SessionManager;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog.OnDateSetListener;
import com.android.meetingninja.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditTaskActivity extends Activity implements AsyncResponse<Boolean>{

	private EditText Description, completionCriteria, Title;
	private TextView assignedDateLabel,createdDateLabel;
	private Button tDeadline;
	private SimpleDateFormat dateFormat = MyDateUtils.APP_DATE_FORMAT;

	private SessionManager session;
	private Task displayTask;

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_DESCRIPTION = "description";
	public static final String EXTRA_EDIT_MODE = "editing";
	public static final String EXTRA_TASK = "displayTask";

	private static final String TAG = EditTaskActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);
		setupActionBar();
		Title= (EditText) findViewById(R.id.task_edit_title);
		Description = (EditText) findViewById(R.id.task_edit_desc);
		completionCriteria = (EditText) findViewById(R.id.task_edit_comp_crit);
		tDeadline = (Button) findViewById(R.id.task_edit_deadline);
		Intent i = getIntent();
		setupView(); 
		displayTask = i.getParcelableExtra("task"); 
		if(displayTask != null){
			Title.setText(displayTask.getTitle());
			completionCriteria.setText(displayTask.getCompletionCriteria());
			Description.setText(displayTask.getDescription());
			String deadline = displayTask.getEndTime();
			
			//maybe for calendar 
			int month = (int) Integer.parseInt(deadline.substring(0, 2));
			int year = (int) Integer.parseInt(deadline.substring(6));
			int day = (int) Integer.parseInt(deadline.substring(3,5));
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day);
			
			tDeadline.setText(deadline);
			//findViewById(R.id.task_edit_deadline)
			//createdDate = findViewById(R.id.task_edit_date_created).toString();
			
			//assignedDateLabel.setText(dateFormat.format(assignedDate));
			//createdDateLabel.setText(dateFormat.format(createdDate));
		}
		//TODO: calender stuff
		



	}

	private final View.OnClickListener tActionBarListener = new OnClickListener(){

		@Override
		public void onClick(View v){
			onActionBarItemSelected(v);
		}
	};

	private void setupActionBar(){
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
	private boolean onActionBarItemSelected(View v){
		switch (v.getId()){
		case R.id.action_done:
			if(Title.getText().equals(null)){
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
	private void setupView(){
		//TODO: setupviews

	}

	private void trimTextView(){
		Title.setText(Title.getText().toString().trim());
		Description.setText(Description.getText().toString().trim());
		completionCriteria.setText(completionCriteria.getText().toString().trim());
	}

	@Override
	public void processFinish(Boolean result) {
		// TODO Auto-generated method stub
		if(result){
			finish();
		} else { 
			Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();

		}
	}
	private void save(){
		if(TextUtils.isEmpty(Title.getText())){
			Toast.makeText(this, "Empty Task not created", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			finish();
		} else {
			
			trimTextView();
			//String title,desc,compCrit;
			displayTask.setTitle(Title.getText().toString());
			displayTask.setDescription(Description.getText().toString());
			System.out.println(completionCriteria.getText().toString());
			displayTask.setCompletionCriteria(completionCriteria.getText().toString());
			System.out.println(displayTask.getCompletionCriteria());
			TaskUpdater tUpdate = new TaskUpdater();
			tUpdate.updateTask(displayTask);
			
			//TODO: setup newTask
			if(displayTask!=null){
			}
			Intent msgIntent = new Intent();
			
			msgIntent.putExtra(EXTRA_TASK, displayTask.getID());
			setResult(RESULT_OK,msgIntent);
			
			finish();

		}
	}
	private class DateClickListener implements OnClickListener,OnDateSetListener{
		Button button;
		Calendar cal;
		public void DateClickListener(Button b, Calendar c){
			this.button = b;
			this.cal = c;
		}

		@Override
		public void onClick(View arg0) {
			//TODO calendar stuff
			//FragmentManager fm = getSupportFragmentManager();
			CalendarDatePickerDialog calendarDatePicketDalog = CalendarDatePickerDialog.newInstance(DateClickListener.this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


		}

		@Override
		public void onDateSet(CalendarDatePickerDialog dialog, int year,
				int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			
		}

	}
	/*public class TaskSaveTask extends AsyncTask<Task, Void, Boolean> {
		private AsyncResponse<Boolean> delegate;
		public TaskSaveTask(AsyncResponse<Boolean> delegate){
			this.delegate = delegate;
		}
		@Override
		protected Boolean doInBackground(Task... params) {
			Task t = params[0];
			try {
				String userID = session.getUserID();
				//TODO: edittask in task adapter
			} catch(Exception e){
				Log.e("MeetingSave","Error: Failed to save task");
				Log.e("MEETING_ERR", e.toString());
				return false;
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result){
			delegate.processFinish(result);
			super.onPostExecute(result);
		}

	}*/
}
