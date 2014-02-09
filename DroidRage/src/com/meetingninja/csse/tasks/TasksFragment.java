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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import objects.Task;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.MyDateUtils;

public class TasksFragment extends Fragment implements	AsyncResponse<List<Task>> {

	private HashMap<String, List<Task>> taskLists;
	private TaskItemAdapter taskAdpt;
	private TaskTypeAdapter typeAdapter;

	private TaskListFetcherTask taskListfetcher = null;
	private TaskFetcherResp taskInfoFetcher = null;
	private TaskCreateTask creator = null;
	private SessionManager session;

	private final String assignedToMe = "Assigned to me";
	private final String iAssigned = "I assigned";
	private final String iCreated = "I created";

	private int numLoading = 0;
	private int prevSelectedType = 0;

	public TasksFragment() {
		// Empty
	}

	private static TasksFragment sInstance;

	public static TasksFragment getInstance() {
		if (sInstance == null) {
			sInstance = new TasksFragment();
		}
		return sInstance;
	}
	//right now this uses getassignedto to get the userID of the user (only one right now) assigned to a task. then uses members to temperatily have multiple members 
	//assigned to a task (just in edit or create screen) then assignes the first user's ID in the members list to assignedto when saving and sending to backend (because they only save one) 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_tasks, container, false);
		setHasOptionsMenu(true);

		session = SessionManager.getInstance();
		creator = new TaskCreateTask(this);
		/* Set up the spinner selector */
		List<String> typeNames = new ArrayList<String>();
		typeNames.add(assignedToMe);
		typeNames.add(iAssigned);
		typeNames.add(iCreated);

		typeAdapter = new TaskTypeAdapter(getActivity(), typeNames);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActivity().getActionBar().setSelectedNavigationItem(prevSelectedType);
		getActivity().getActionBar().setListNavigationCallbacks(typeAdapter,new OnNavigationListener() {
					@Override
					public boolean onNavigationItemSelected(int itemPosition,long itemId) {
						setTaskList(itemPosition);
						return true;
					}
				});

		if (taskLists == null) {
			/* Set up the task list */
			taskLists = new HashMap<String, List<Task>>();
			ArrayList<Task> l1 = new ArrayList<Task>(), l2 = new ArrayList<Task>(), l3 = new ArrayList<Task>();
			taskLists.put(assignedToMe, l1);
			taskLists.put(iAssigned, l2);
			taskLists.put(iCreated, l3);

			refreshTasks();
		}

		ListView lv = (ListView) v.findViewById(R.id.task_list);
		lv.setEmptyView(v.findViewById(android.R.id.empty));
		taskAdpt = new TaskItemAdapter(getActivity(), R.layout.list_item_task,taskLists.get(iAssigned));

		lv.setAdapter(taskAdpt);
		registerForContextMenu(lv);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,int position, long id) {
				// Intent viewTask = new Intent(getActivity(),
				// ViewTaskActivity.class);
				// startActivity(viewTask);
				Task t = taskAdpt.getItem(position);
				loadTask(t);
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshTasks();
			return true;
		case R.id.action_new:
			Intent i = new Intent(getActivity(), EditTaskActivity.class);
			Task t = new Task();
			i.putExtra(Keys.Task.PARCEL, t);
			startActivityForResult(i, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		prevSelectedType = getActivity().getActionBar().getSelectedNavigationIndex();
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		super.onPause();
	}

	@Override
	public void onResume() {
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActivity().getActionBar().setSelectedNavigationItem(prevSelectedType);
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 6) {
				refreshTasks();
			} else if (requestCode == 7) {
				Task t = data.getParcelableExtra(Keys.Task.PARCEL);
				t.setCreatedBy(session.getUserID());
				creator.createTask(t);
			}
		}
	}

	private void loadTask(Task task) {
		while (task.getEndTimeInMillis() == 0L);
		Intent viewTask = new Intent(getActivity(), ViewTaskActivity.class);
		viewTask.putExtra(Keys.Task.PARCEL, task);
		startActivityForResult(viewTask, 6);
	}

	public void refreshTasks() {
		taskListfetcher = new TaskListFetcherTask(this);
		taskListfetcher.execute(session.getUserID());
		taskInfoFetcher = new TaskFetcherResp(this);
	}

	private void setTaskList(int type) {
		switch (type) {
		case 0:
			taskAdpt.setTasks(taskLists.get(assignedToMe));
			break;
		case 1:
			taskAdpt.setTasks(taskLists.get(iAssigned));
			break;
		case 2:
			taskAdpt.setTasks(taskLists.get(iCreated));
			break;
		}
		taskAdpt.notifyDataSetChanged();
	}

	@Override
	public void processFinish(List<Task> result) {
		taskLists.get(assignedToMe).clear();
		taskLists.get(iAssigned).clear();
		taskLists.get(iCreated).clear();
		Collections.sort(result);
		for (Task task : result) {
			// new TaskFetcherResp(this).loadTask(task);
			// numLoading++;
			if (task.getType().equals("ASSIGNED_TO")) {
				taskLists.get(assignedToMe).add(task);
			} else if (task.getType().equals("ASSIGNED_FROM")) {
				taskLists.get(iAssigned).add(task);
			} else {
				taskLists.get(iCreated).add(task);
			}
		}
		taskAdpt.notifyDataSetChanged();
	}

	public void notifyAdapter() {
		// taskAdpt.notifyDataSetChanged();
		numLoading--;
		if (numLoading == 0) {
			taskAdpt.notifyDataSetChanged();
		}
	}
}

class TaskTypeAdapter implements SpinnerAdapter {
	private Context context;
	private List<String> typeNames;

	public TaskTypeAdapter(Context context, List<String> typeNames) {
		this.context = context;
		this.typeNames = typeNames;
	}

	@Override
	public int getCount() {
		return this.typeNames.size();
	}

	@Override
	public Object getItem(int position) {
		return this.typeNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView rowView = (TextView) convertView;
		if (rowView == null) {
			rowView = new TextView(this.context);
		}

		rowView.setText(typeNames.get(position));
		rowView.setTextColor(Color.WHITE);

		return rowView;
	}

	@Override
	public int getViewTypeCount() {
		return this.typeNames.size();
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this.typeNames.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView rowView = (TextView) getView(position, convertView, parent);
		rowView.setPadding((int) this.context.getResources().getDimension(R.dimen.activity_horizontal_margin),(int) this.context.getResources().getDimension(R.dimen.activity_vertical_margin),(int) this.context.getResources().getDimension(R.dimen.activity_horizontal_margin),	(int) this.context.getResources().getDimension(R.dimen.activity_vertical_margin));
		return rowView;
	}

}

class TaskItemAdapter extends ArrayAdapter<Task> {
	private List<Task> tasks;
	private Context context;

	public TaskItemAdapter(Context context, int textViewResourceId,
			List<Task> tasks) {
		super(context, textViewResourceId, tasks);
		this.context = context;
		this.tasks = tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void sort(Comparator<? super Task> c) {
		Collections.sort(tasks);
	}

	@Override
	public int getCount() {
		return this.tasks.size();
	}

	@Override
	public Task getItem(int position) {
		return this.tasks.get(position);
	}

	private class ViewHolder {
		TextView title, deadline;
		View background;
	}

	ViewHolder viewHolder;

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_task, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) rowView.findViewById(R.id.list_task_title);
			viewHolder.deadline = (TextView) rowView.findViewById(R.id.list_task_deadline);
			viewHolder.background = rowView.findViewById(R.id.list_task_holder);

			rowView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) rowView.getTag();

		// Setup from the meeting_item XML file
		Task task = tasks.get(position);

		viewHolder.title.setText(task.getTitle());
		viewHolder.deadline.setText("Deadline:  "+ MyDateUtils.JODA_APP_DATE_FORMAT.print(task.getEndTimeInMillis()));

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(task.getEndTimeInMillis());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		if (task.getEndTimeInMillis() == 0L) {

		} else if (task.getIsCompleted()) {
			viewHolder.background.setBackgroundColor(Color.rgb(53, 227, 111));
		} else if (cal.before(Calendar.getInstance())) {
			viewHolder.background.setBackgroundColor(Color.rgb(255, 51, 51));
		} else {
			viewHolder.background.setBackground(null);
		}

		return rowView;
	}

}
