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
import android.widget.Toast;

public class EditTaskActivity extends Activity implements AsyncResponse<Boolean>{

	//TODO: 
	private Bundle extras; 
	private EditText Description, completionCriteria, Title;
	private Button tDeadline;
	//private boolean is24, edit_mode;
	//private Calendar start, end;
	//private TastSaveTask creater = null;
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
		//TODO: is 24 stuff

		extras = getIntent().getExtras();
		//edit_mode = extras.getBoolean(EXTRA_EDIT_MODE,true);
		if(extras!= null && !extras.isEmpty()){
			displayTask = extras.getParcelable(EXTRA_TASK);
		}
		setupView();

		if(displayTask != null){
			Title.setText(displayTask.getTitle());
			completionCriteria.setText(displayTask.getCompletionCriteria());
			Description.setText(displayTask.getDescription());
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
			System.out.println("got here");
			if(Title.getText().equals(null)){
				Title.setText("");
			}
			save();
			break;
		case R.id.action_cancel:
			//TODO: Resultcanceled?
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
		System.out.println("next point");
		if(TextUtils.isEmpty(Title.getText())){
			System.out.println("broken?");
			Toast.makeText(this, "Empty Task not created", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			finish();
		} else {
			System.out.println("checkpoint");
			Intent msgIntent = new Intent();
			Task newTask = new Task();
			System.out.println("check2");
			trimTextView();
			String title,desc,compCrit;
			System.out.println("check3");
			//title = Title.getText().toString();
			//desc = Description.getText().toString();
			//compCrit = completionCriteria.getText().toString();

			//TODO: setup newTask
			if(displayTask!=null){
			}
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
	public class TaskSaveTask extends AsyncTask<Task, Void, Boolean> {
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

	}
}
