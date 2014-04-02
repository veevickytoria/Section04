package com.meetingninja.csse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.Meeting;
import objects.Schedule;
import objects.Task;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.volley.MeetingVolleyAdapter;
import com.meetingninja.csse.database.volley.TaskVolleyAdapter;
import com.meetingninja.csse.extras.MyDateUtils;
import com.meetingninja.csse.meetings.MeetingFetcherTask;
import com.meetingninja.csse.meetings.MeetingItemAdapter;
import com.meetingninja.csse.meetings.ViewMeetingActivity;
import com.meetingninja.csse.tasks.TaskItemAdapter;
import com.meetingninja.csse.tasks.ViewTaskActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HomePage extends Fragment {

	private SessionManager session;
	private ListView taskList;
	private ListView meetingList;
	private TaskItemAdapter taskAdpt;
	private MeetingItemAdapter meetingAdpt;
	private List<Task> tasks = new ArrayList<Task>();
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.fragment_home_page, container, false);
		setHasOptionsMenu(true);

		session = SessionManager.getInstance();
		getSchedule();

		return v;
	}

	public void viewMeeting(Meeting meeting){
		while (meeting.getEndTimeInMillis() == 0L);
		Intent viewMeeting = new Intent(getActivity(),ViewMeetingActivity.class);
		viewMeeting.putExtra(Keys.Meeting.PARCEL, meeting);
		startActivityForResult(viewMeeting, 6);
	}
	private void loadMeeting(Meeting meeting) {
		MeetingVolleyAdapter.fetchMeetingInfo(meeting.getID(),new AsyncResponse<Meeting>(){
			@Override
			public void processFinish(Meeting result) {
				viewMeeting(result);
			}
		});
	}
	public void viewTask(Task task){
		while (task.getEndTimeInMillis() == 0L);
		Intent viewTask = new Intent(getActivity(), ViewTaskActivity.class);
		viewTask.putExtra(Keys.Task.PARCEL, task);
		startActivityForResult(viewTask, 6);
	}
	
	private void loadTask(Task task) {
		TaskVolleyAdapter.getTaskInfo(task.getID(), new AsyncResponse<Task>() {
			@Override
			public void processFinish(Task result) {
				viewTask(result);
			}
		});
	}

	private void setUp(Schedule sched) {
		meetingList = (ListView) v.findViewById(R.id.homepage_meetings);
		taskList = (ListView) v.findViewById(R.id.homepage_tasks);
		taskAdpt = new TaskItemAdapter(getActivity(), R.layout.list_item_task,tasks, false);
		meetingAdpt = new MeetingItemAdapter(getActivity(),	R.layout.list_item_meeting, meetings);

		taskList.setAdapter(taskAdpt);
		taskAdpt.addAll(sched.getTasks());
		taskAdpt.notifyDataSetChanged();

		meetingList.setAdapter(meetingAdpt);
		meetingAdpt.addAll(sched.getMeetings());
		meetingAdpt.notifyDataSetChanged();

		meetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter,View v, int position, long id) {
				Meeting clicked = meetingAdpt.getItem(position);
				loadMeeting(clicked);
			}
		});

		taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,int position, long id) {
				Task t = taskAdpt.getItem(position);
				loadTask(t);
			}
		});
	}

	private void getSchedule() {
		new AsyncTask<Void, Void, Schedule>() {
			@Override
			protected Schedule doInBackground(Void... arg0) {
				Schedule sched = new Schedule();
				try {
					sched = UserDatabaseAdapter.getSchedule(SessionManager.getInstance().getUserID());
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return sched;
			}

			@Override
			public void onPostExecute(Schedule result) {
				setUp(result);
			}
		}.execute();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		meetingAdpt.clear();
		taskAdpt.clear();
		getSchedule();
	}
}