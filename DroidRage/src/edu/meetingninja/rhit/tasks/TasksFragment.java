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
package edu.meetingninja.rhit.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import objects.Task;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import edu.meetingninja.rhit.R;
import edu.meetingninja.rhit.database.AsyncResponse;
import edu.meetingninja.rhit.user.SessionManager;

public class TasksFragment extends Fragment implements
		AsyncResponse<List<Task>> {

	private List<String> meetingNames = new ArrayList<String>();
	private HashMap<String, List<Task>> taskLists = new HashMap<String, List<Task>>();
	private TaskListAdapter taskAdpt;

	private TaskListFetcherTask taskListfetcher = null;
	private TaskFetcherResp taskInfoFetcher = null;
	private SessionManager session;

	private final String assignedToMe = "Assigned to me";
	private final String iAssigned = "I assigned";
	private final String iCreated = "I created";

	// make tasks adapter

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_tasks, container, false);
		setHasOptionsMenu(true);

		session = SessionManager.getInstance();
		ArrayList<Task> l1 = new ArrayList<Task>(), l2 = new ArrayList<Task>(), l3 = new ArrayList<Task>();
		taskLists.put(assignedToMe, l1);
		taskLists.put(iAssigned, l2);
		taskLists.put(iCreated, l3);
		meetingNames.add(assignedToMe);
		meetingNames.add(iAssigned);
		meetingNames.add(iCreated);

		refreshTasks();

		ExpandableListView lv = (ExpandableListView) v
				.findViewById(R.id.tasksList);

		taskAdpt = new TaskListAdapter(getActivity(), meetingNames, taskLists);

		lv.setAdapter(taskAdpt);
		registerForContextMenu(lv);

		lv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// Intent viewTask = new Intent(getActivity(),
				// ViewTaskActivity.class);
				// startActivity(viewTask);
				Task t = (Task) taskAdpt.getChild(groupPosition, childPosition);
				loadTask(t);
				return false;
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.task_fragment_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_task:
			refreshTasks();
			return true;
		case R.id.action_new_task:
			// max put your stuff here
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 6) {
			if (resultCode == Activity.RESULT_OK) {
				refreshTasks();
			}
		}
	}

	private void loadTask(Task task) {
		this.taskInfoFetcher.openTask(task);
	}

	private void refreshTasks() {
		taskListfetcher = new TaskListFetcherTask(this);
		System.out.println(session.getUserID());
		taskListfetcher.execute(session.getUserID());
		taskInfoFetcher = new TaskFetcherResp(this);
	}

	@Override
	public void processFinish(List<Task> result) {
		taskLists.get(assignedToMe).clear();
		taskLists.get(iAssigned).clear();
		taskLists.get(iCreated).clear();
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
}

class TaskListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<String> meetingNames;
	private HashMap<String, List<Task>> tasksLists;

	public TaskListAdapter(Context context, List<String> meetingNames,
			HashMap<String, List<Task>> tasksLists) {
		this.context = context;
		this.meetingNames = meetingNames;
		this.tasksLists = tasksLists;
	}

	@Override
	public Object getChild(int groupPos, int childPos) {
		return this.tasksLists.get(this.meetingNames.get(groupPos)).get(
				childPos);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// class for caching the views in a row
	private class ChildViewHolder {
		TextView taskName, taskDescription;
	}

	ChildViewHolder viewHolder;

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.task_item, null);
			viewHolder = new ChildViewHolder();
			viewHolder.taskName = (TextView) rowView
					.findViewById(R.id.taskName);
			viewHolder.taskDescription = (TextView) rowView
					.findViewById(R.id.taskDiscription);

			rowView.setTag(viewHolder);
		} else
			viewHolder = (ChildViewHolder) rowView.getTag();

		Task t = (Task) getChild(groupPosition, childPosition);

		viewHolder.taskName.setText(t.getTitle());
		viewHolder.taskDescription.setText(t.getDescription());
		return rowView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.tasksLists.get(this.meetingNames.get(groupPosition)).size();
	}

	@Override
	public String getGroup(int groupPosition) {
		return this.meetingNames.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.meetingNames.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	private class GroupViewHolder {
		TextView meetingName;
	}

	GroupViewHolder groupViewHolder;

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View groupView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (groupView == null) {
			groupView = inflater.inflate(R.layout.task_sublist, null);
			groupViewHolder = new GroupViewHolder();
			groupViewHolder.meetingName = (TextView) groupView
					.findViewById(R.id.task_group);

			groupView.setTag(groupViewHolder);
		} else
			groupViewHolder = (GroupViewHolder) groupView.getTag();

		String name = (String) getGroup(groupPosition);
		groupViewHolder.meetingName.setText(name);

		return groupView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
