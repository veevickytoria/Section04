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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import objects.SerializableUser;
import objects.Task;
import objects.User;
import objects.parcelable.UserParcel;

import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog.OnDateSetListener;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.ContactTokenTextView;
import com.meetingninja.csse.extras.NinjaDateUtils;
import com.meetingninja.csse.user.AutoCompleteAdapter;
import com.meetingninja.csse.user.ProfileActivity;
import com.meetingninja.csse.user.UserArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

import de.timroes.android.listview.EnhancedListView;

public class EditTaskActivity extends FragmentActivity implements AsyncResponse<Boolean>, TokenListener {
	final String MARK_AS_COMPLETE = "Mark As Complete";
	final String MARK_AS_INCOMPLETE = "Mark As Incomplete";

	private EditText mDescription, completionCriteria, mTitle;
	private TextView assignedDateLabel, createdDateLabel, isCompleted;
	private Button mDeadlineBtn, mCompleteBtn;
	private DateTimeFormatter dateFormat = NinjaDateUtils.JODA_APP_DATE_FORMAT;
	private AutoCompleteAdapter autoAdapter;
	private ArrayList<User> allUsers = new ArrayList<User>();
	private ArrayList<User> assignedUsers = new ArrayList<User>();
	private User addedUser;

	// private SessionManager session;
	private Task displayTask;
	Calendar cal = null;
	public static final String EXTRA_TASK = Keys.Task.PARCEL;
	private UserArrayAdapter mUserAdapter;
	private EnhancedListView l;
	private String userId;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);
		setupActionBar();
		setupViews();
		Bundle extras = getIntent().getExtras();

		session = SessionManager.getInstance();
		userId = SessionManager.getUserID();

		if (extras != null) {
			displayTask = extras.getParcelable(EXTRA_TASK);
		}
		if (displayTask != null) {
			// allows keyboard to hide when not editing text
			setUpListView();
			setTask();

			cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal.setTimeInMillis(displayTask.getEndTimeInMillis());

			mDeadlineBtn.setOnClickListener(new DateClickListener(mDeadlineBtn,cal, this));
			// assignedDateLabel.setText(dateFormat.format(assignedDate));
			// createdDateLabel.setText(dateFormat.format(createdDate));
		}
	}

	private void setTask() {
		mTitle.setText(displayTask.getTitle());
		completionCriteria.setText(displayTask.getCompletionCriteria());
		mDescription.setText(displayTask.getDescription());
		String format = dateFormat.print(displayTask.getEndTimeInMillis());
		mDeadlineBtn.setText(format);
		format = dateFormat.print(Long.parseLong(displayTask.getDateCreated()));
		createdDateLabel.setText(format);
		assignedUsers.clear();
		//		TODO: change to a loop when backend allows for multiple assigned to
		assignedUsers.addAll(displayTask.getMembers());

		setCompletedViews();
		// TODO: use string.xml

	}

	private void setCompletedViews() {
		if (displayTask.getIsCompleted()) {
			isCompleted.setText("Yes");
			mCompleteBtn.setText(MARK_AS_INCOMPLETE);
		} else {
			isCompleted.setText("No");
			mCompleteBtn.setText(MARK_AS_COMPLETE);
		}
	}

	public void toggleCompleted(View v) {
		displayTask.setIsCompleted(!displayTask.getIsCompleted());
		setCompletedViews();
	}

	private void trimTextView() {
		mTitle.setText(mTitle.getText().toString().trim());
		mDescription.setText(mDescription.getText().toString().trim());
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
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,	new LinearLayout(this), false);

		View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
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
		//		assignedDateLabel = (TextView) findViewById(R.id.task_edit_date_assigned);
		createdDateLabel = (TextView) findViewById(R.id.task_edit_date_created);
	}
	@Override
	public void onTokenAdded(Object arg0) {
		SerializableUser added = null;
		if (arg0 instanceof SerializableUser){
			added = (SerializableUser) arg0;
		} 	else if (arg0 instanceof User){
			added = new SerializableUser((User) arg0);
		}

		if (added != null) {
			addedUser = added;
		}
	}

	@Override
	public void onTokenRemoved(Object arg0) {
		SerializableUser removed = null;
		if (arg0 instanceof SerializableUser){
			removed = (SerializableUser) arg0;
		} 	else if (arg0 instanceof User){
			removed = new SerializableUser((User) arg0);
		}
		if (removed != null) {
			addedUser = null;
		}

	}

	@Override
	public void processFinish(Boolean result) {
		if (result) {
			finish();
		} else {
			Toast.makeText(this, "Failed to save task", Toast.LENGTH_LONG).show();
		}
	}

	private void save() {
		if (TextUtils.isEmpty(mTitle.getText())) {
			Toast.makeText(this, "Empty Task not created", Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
			finish();
		} else {
			trimTextView();
			displayTask.setTitle(mTitle.getText().toString());
			displayTask.setDescription(mDescription.getText().toString());
			displayTask.setCompletionCriteria(completionCriteria.getText().toString());
			displayTask.setEndTime(cal.getTimeInMillis());
			displayTask.setAssignedFrom(userId);
			// TODO: change this for multiple assigned to's
			if (!displayTask.getMembers().isEmpty()) {
				displayTask.setAssignedTo(displayTask.getMembers().get(0).getID());
			} else {
				displayTask.setAssignedTo("");
			}
			// TODO: fetcher for assigned to

			Toast.makeText(this, String.format("Saving Task"),Toast.LENGTH_SHORT).show();

			Intent msgIntent = new Intent();
			msgIntent.putExtra(EXTRA_TASK, displayTask);
			setResult(RESULT_OK, msgIntent);
			finish();
		}
	}

	private void setUpListView() {
		// allows keyboard to hide when not editing text
		findViewById(R.id.edit_task_container).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		mUserAdapter = new UserArrayAdapter(this, R.layout.list_item_user,displayTask.getMembers());
		l = (EnhancedListView) findViewById(R.id.edit_task_members_list);
		l.setAdapter(mUserAdapter);
		l.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
			@Override
			public EnhancedListView.Undoable onDismiss(
					EnhancedListView listView, final int position) {

				final User item = mUserAdapter.getItem(position);
				mUserAdapter.remove(item);
				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						mUserAdapter.insert(item, position);
					}

					@Override
					public String getTitle() {
						return "Member deleted";
					}
				};
			}
		});
		l.setUndoHideDelay(5000);
		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				User clicked = mUserAdapter.getItem(position);
				Intent profileIntent = new Intent(v.getContext(),ProfileActivity.class);
				profileIntent.putExtra(Keys.User.PARCEL, new UserParcel(clicked));
				startActivity(profileIntent);

			}

		});
		l.enableSwipeToDismiss();
		l.setSwipingLayout(R.id.list_group_item_frame_1);

		l.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);

		// List<User> mems = new ArrayList<User>();
		// mems = displayTask.getMembers();
		// for(int i = 0;i<mems.size();i++){
		// loadUser(mems.get(i).toString(),false);
		// }

		// TODO: change to a loop when backend catches up
		String mem = displayTask.getAssignedTo();
		loadUser(mem, false);

	}

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}
	
	private void addMemberDialog(final View view){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Search by name or email:");
		
		View autocompleteView = this.getLayoutInflater().inflate(R.layout.fragment_autocomplete, null);
		final ContactTokenTextView input = (ContactTokenTextView) autocompleteView.findViewById(R.id.my_autocomplete);
		autoAdapter = new AutoCompleteAdapter(this, allUsers);
		input.setAdapter(autoAdapter);

		input.setTokenListener(this);
		builder.setView(autocompleteView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(addedUser == null){
					dialog.cancel();
					return;
				}
				boolean contains = false;
				for (int i = 0; i < assignedUsers.size(); i++) {
					if (assignedUsers.get(i).equals(addedUser)) {
						contains = true;
					}
				}
				if (contains) {
					AlertDialogUtil.displayDialog(view.getContext(),"Unable to add user","This user is already assigned to this task", "OK",null);
					addedUser=null;
				} else {
					addAssignedUser(addedUser);
					addedUser = null;
				}
			}
		});
		builder.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
		//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//		builder.setTitle("Enter member ID");
		//		final EditText input = new EditText(this);
		//		builder.setView(input);
		//		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		//			@Override
		//			public void onClick(DialogInterface dialog, int which) {
		//
		//				loadUser(input.getText().toString(), true);
		//				// mUserAdapter.getItem(mUserAdapter.)
		//				// member = input.getText().toString();
		//			}
		//		});
		//		builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
		//					@Override
		//					public void onClick(DialogInterface dialog, int which) {
		//						dialog.cancel();
		//					}
		//				});
		//		builder.show();
	}

	public void addMember(final View view) {
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {
			@Override
			public void processFinish(List<User> result) {
				allUsers = new ArrayList<User>(result);
				addMemberDialog(view);
			}
		});
	}
	private void addAssignedUser(User user){
		displayTask.addMember(user);
		assignedUsers.add(user);
		mUserAdapter.notifyDataSetChanged();
	}
	private void loadUser(String userID, final boolean add) {
		UserVolleyAdapter.fetchUserInfo(userID, new AsyncResponse<User>() {
			@Override
			public void processFinish(User result) {
				if (add) {
					displayTask.addMember(result);
				}
				// TODO: eliminate when i can change assignedto to a list. this is becuase members isn't intially being set to have what is in assigned to
				if (displayTask.getMembers().isEmpty()) {
					displayTask.addMember(result);
				}

				mUserAdapter.notifyDataSetChanged();
			}
		});
	}

	private class DateClickListener implements OnClickListener,	OnDateSetListener {
		Calendar cal;
		FragmentActivity activity;

		public DateClickListener(Button b, Calendar c, FragmentActivity activity) {
			this.cal = c;
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			FragmentManager fm = getSupportFragmentManager();
			CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog.newInstance(DateClickListener.this,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
			calendarDatePickerDialog.show(fm, "fragment_date_picker_name");
		}

		@Override
		public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
			Calendar tempcal = Calendar.getInstance();
			tempcal.set(year, monthOfYear, dayOfMonth);
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			if (tempcal.after(today)) {
				cal.set(year, monthOfYear, dayOfMonth);
				String format = dateFormat.print(cal.getTimeInMillis());
				mDeadlineBtn.setText(format);
			} else {
				AlertDialogUtil.displayDialog(activity, "Error","A deadline can not be set before today's date", "OK",null);
			}
		}
	}
}
