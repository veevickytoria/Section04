package com.meetingninja.csse;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Task;




import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.TaskVolleyAdapter;
import com.meetingninja.csse.meetings.MeetingItemAdapter;
import com.meetingninja.csse.meetings.ViewMeetingActivity;
import com.meetingninja.csse.meetings.tasks.GetMeetingInfoTask;
import com.meetingninja.csse.meetings.tasks.MeetingsFetcherTask;
import com.meetingninja.csse.tasks.TaskItemAdapter;
import com.meetingninja.csse.tasks.ViewTaskActivity;
import com.meetingninja.csse.tasks.tasks.GetTaskListTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class HomePageFragment extends Fragment {

	private ListView taskList;
	private ListView meetingList;
	private TaskItemAdapter taskAdpt;
	private MeetingItemAdapter meetingAdpt;
	private List<Task> tasks = new ArrayList<Task>();
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private String userID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View fragView = inflater.inflate(R.layout.fragment_home_page, container, false);
		setUpViews(fragView);
		refresh();
		return fragView;
	}

	public void viewMeeting(Meeting meeting) {
		Intent viewMeeting = new Intent(getActivity(), ViewMeetingActivity.class);
		viewMeeting.putExtra(Keys.Meeting.PARCEL, meeting);
		startActivityForResult(viewMeeting, 6);
	}

//	private void loadMeeting(Meeting meeting) {
//		new GetMeetingFetcherTask(new AsyncResponse<Meeting>(){
//			@Override
//			public void processFinish(Meeting result) {
//				viewMeeting(result);				
//			}
//		}).execute(meeting.getID());
//	}

	public void viewTask(Task task) {
		Intent viewTask = new Intent(getActivity(), ViewTaskActivity.class);
		viewTask.putExtra(Keys.Task.PARCEL, task);
		startActivityForResult(viewTask, 6);
	}

//	private void loadTask(Task task) {
//		TaskVolleyAdapter.getTaskInfo(task.getID(), new AsyncResponse<Task>() {
//			@Override
//			public void processFinish(Task result) {
//				viewTask(result);
//			}
//		});
//	}

	private void setUpViews(View v) {
		View meetingView = v.findViewById(R.id.homepage_meetings_view);
		meetingAdpt = new MeetingItemAdapter(getActivity(),	R.layout.list_item_meeting, this.meetings);
		meetingList = (ListView) v.findViewById(R.id.homepage_meetingList);
		meetingList.setEmptyView(meetingView.findViewById(android.R.id.empty));
		meetingList.setAdapter(meetingAdpt);
		View taskView = v.findViewById(R.id.homepage_tasks_view);
		taskAdpt = new TaskItemAdapter(getActivity(), R.layout.list_item_task,	this.tasks, false);
		taskList = (ListView) v.findViewById(R.id.homepage_tasksList);
		taskList.setEmptyView(taskView.findViewById(android.R.id.empty));
		taskList.setAdapter(taskAdpt);

		meetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter,View v, int position, long id) {
				Meeting clicked = meetingAdpt.getItem(position);
//				loadMeeting(clicked);
				viewMeeting(clicked);
			}
		});

		taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v, int position, long id) {
				Task clicked = taskAdpt.getItem(position);
//				loadTask(clicked);
				viewTask(clicked);
			}
		});

	}
	public void refresh(){
		meetingAdpt.clear();
		taskAdpt.clear();
		SessionManager.getInstance();
		userID=SessionManager.getUserID();
		getMeetings(userID);
		getTasks(userID);
	}

	private void getTasks(String userID){
		new GetTaskListTask(new AsyncResponse<List<Task>>(){
			@Override
			public void processFinish(List<Task> result) {
				for(Task task: result){
					if(task.getType().equals("ASSIGNED_TO")){
						taskAdpt.add(task);
					}
				}
				taskAdpt.notifyDataSetChanged();
			}
		}).execute(userID);
	}
	private void getMeetings(String userID){
		new MeetingsFetcherTask(new AsyncResponse<List<Meeting>>(){
			@Override
			public void processFinish(List<Meeting> result) {
				meetingAdpt.addAll(result);
				meetingAdpt.notifyDataSetChanged();
				
			}
		}).execute(userID);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh();
	}
}