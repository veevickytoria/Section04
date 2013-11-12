package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import objects.Note;
import objects.Task;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TasksFragment extends Fragment implements AsyncResponse<List<Note>> {
	private SessionManager session;
	private List<String> meetingNames = new ArrayList<String>();
	private HashMap<String, List<Task>> taskLists = new HashMap<String, List<Task>>();
	private TaskListAdapter taskAdpt;
	//make tasks adapter
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_tasks, container, false);
		session = new SessionManager(getActivity().getApplicationContext());
		
		refreshTasks();
		getActivity();
		
		ExpandableListView lv = (ExpandableListView) v.findViewById(R.id.tasksList);
		
		taskAdpt = new TaskListAdapter(getActivity(), meetingNames, taskLists);
		
		
		lv.setAdapter(taskAdpt);
	//	lv.setEmptyView(v.findViewById(R.id.ta))
		registerForContextMenu(lv);
		
		lv.setOnChildClickListener(new OnChildClickListener() {

			@Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                Toast.makeText(getActivity(),
                        meetingNames.get(groupPosition)
                                + " : "
                                + taskLists.get(
                                        meetingNames.get(groupPosition)).get(
                                        childPosition).getName(), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
		
		return v;
	}
	
	private void refreshTasks(){
		ArrayList<Task> l1 = new ArrayList<Task>(), l2  = new ArrayList<Task>(), l3  = new ArrayList<Task>();
		l1.add(new Task("testing first"));
		l1.add(new Task("task 1"));
		l1.add(new Task("and again and again"));
		taskLists.put("meeting 1", l1);
		l2.add(new Task("task 2"));
		taskLists.put("meeting 2", l2);
		l3.add(new Task("task 3"));
		taskLists.put("meeting 3", l3);
		meetingNames.add("meeting 1");
		meetingNames.add("meeting 2");
		meetingNames.add("meeting 3");
	}

	@Override
	public void processFinish(List<Note> result) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "testing this", Toast.LENGTH_LONG);
		
	}

}


class TaskListAdapter extends BaseExpandableListAdapter{
	private Context context;
	private List<String> meetingNames;
	private HashMap<String, List<Task>> tasksLists;
	
	public TaskListAdapter(Context context, List<String> meetingNames, HashMap<String, List<Task>> tasksLists){
		this.context = context;
		this.meetingNames = meetingNames;
		this.tasksLists = tasksLists;
	}
	@Override
	public Object getChild(int groupPos, int childPos) {
		return this.tasksLists.get(this.meetingNames.get(groupPos)).get(childPos);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.task_item, null);
		}
		TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
		TextView taskDiscription = (TextView) convertView.findViewById(R.id.taskDiscription);
		final String Name = ((Task) getChild(groupPosition, childPosition)).getName();
		final String Disc = ((Task) getChild(groupPosition, childPosition)).getContent();
		taskName.setText(Name);
		taskDiscription.setText(Disc);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.tasksLists.get(this.meetingNames.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
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

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.task_sublist, null);
		}
		String name = (String) getGroup(groupPosition);
		TextView meetingName = (TextView) convertView.findViewById(R.id.task_group);
		meetingName.setText(name);
		
		return convertView;
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

