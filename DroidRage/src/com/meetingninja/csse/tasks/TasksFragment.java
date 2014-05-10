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
import java.util.Collections;
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
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.IRefreshable;
import com.meetingninja.csse.tasks.tasks.CreateTaskTask;
import com.meetingninja.csse.tasks.tasks.GetTaskListTask;

public class TasksFragment extends Fragment implements AsyncResponse<List<Task>>, IRefreshable {

	private HashMap<String, List<Task>> taskLists;
	private TaskItemAdapter taskAdpt;
	private TaskTypeAdapter typeAdapter;
	private ActionBar actionBar;

	private GetTaskListTask taskListfetcher;
	private CreateTaskTask creator;

	private final String assignedToMe = "Assigned to me";
	private final String iAssigned = "I assigned";
	private final String iCreated = "I created";

	private int numLoading = 0;
	private int prevSelectedType = 0;

	public TasksFragment() {
		// Empty
	}

	// right now this uses getassignedto to get the userID of the user (only oneright now) assigned to a task. then uses members to temperatily have multiple members
	// assigned to a task (just in edit or create screen) then assignes the first user's ID in the members list to assignedto when saving and sending to backend (because they only save one)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_tasks, container, false);
		setupActionBar();

		creator = new CreateTaskTask(this);

		setupTaskLists();

		setupListView(v);

		return v;
	}

	private void setupActionBar() {
		actionBar = getActivity().getActionBar();
		List<String> typeNames = new ArrayList<String>();
		typeNames.add(assignedToMe);
		typeNames.add(iAssigned);
		typeNames.add(iCreated);

		typeAdapter = new TaskTypeAdapter(getActivity(), typeNames);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setSelectedNavigationItem(prevSelectedType);
		actionBar.setListNavigationCallbacks(typeAdapter,new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,long itemId) {
				setTaskList(itemPosition);
				return true;
			}
		});
		setHasOptionsMenu(true);
	}

	private void setupTaskLists() {
		if (taskLists == null) {
			/* Set up the task list */
			taskLists = new HashMap<String, List<Task>>();
			taskLists.put(assignedToMe, new ArrayList<Task>());
			taskLists.put(iAssigned, new ArrayList<Task>());
			taskLists.put(iCreated, new ArrayList<Task>());

			refresh();
		}

	}

	private void setupListView(View v) {
		taskAdpt = new TaskItemAdapter(getActivity(), R.layout.list_item_task, taskLists.get(iAssigned), true);

		ListView lv = (ListView) v.findViewById(R.id.task_list);
		lv.setEmptyView(v.findViewById(android.R.id.empty));
		lv.setAdapter(taskAdpt);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v, int position, long id) {
				Task clicked = taskAdpt.getItem(position);
				viewTask(clicked);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refresh();
			return true;
		case R.id.action_new:
			Intent editIntent = new Intent(getActivity(), EditTaskActivity.class);
			Task newTask = new Task();
			editIntent.putExtra(Keys.Task.PARCEL, newTask);
			newTask.setID(-1);
			startActivityForResult(editIntent, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		prevSelectedType = actionBar.getSelectedNavigationIndex();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		super.onPause();
	}

	@Override
	public void onResume() {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setSelectedNavigationItem(prevSelectedType);
		super.onResume();
		refresh();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				if (requestCode == 6) {
					refresh();
				} else if (requestCode == 7) {
					Task t = data.getParcelableExtra(Keys.Task.PARCEL);
					t.setCreatedBy(SessionManager.getUserID());
					creator.createTask(t);
				}
			}
		}
	}

	private void viewTask(Task task) {
		Intent viewTask = new Intent(getActivity(), ViewTaskActivity.class);
		viewTask.putExtra(Keys.Task.PARCEL, task);
		startActivityForResult(viewTask, 6);
	}

	@Override
	public void refresh() {
		taskListfetcher = new GetTaskListTask(this);
		taskListfetcher.execute(SessionManager.getUserID());
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
		rowView.setPadding((int) this.context.getResources().getDimension(R.dimen.activity_horizontal_margin),(int) this.context.getResources().getDimension(R.dimen.activity_vertical_margin),(int) this.context.getResources().getDimension(R.dimen.activity_horizontal_margin),(int) this.context.getResources().getDimension(R.dimen.activity_vertical_margin));
		return rowView;
	}

}