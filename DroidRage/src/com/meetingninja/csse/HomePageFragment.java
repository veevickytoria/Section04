package com.meetingninja.csse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Schedule;
import objects.Task;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.volley.MeetingVolleyAdapter;
import com.meetingninja.csse.database.volley.TaskVolleyAdapter;
import com.meetingninja.csse.meetings.MeetingItemAdapter;
import com.meetingninja.csse.meetings.ViewMeetingActivity;
import com.meetingninja.csse.tasks.TaskItemAdapter;
import com.meetingninja.csse.tasks.ViewTaskActivity;

import android.content.Intent;
import android.os.AsyncTask;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View fragView = inflater.inflate(R.layout.fragment_home_page, container, false);
		setUpViews(fragView);

		getSchedule();

		return fragView;
	}

	public void viewMeeting(Meeting meeting) {
		Intent viewMeeting = new Intent(getActivity(), ViewMeetingActivity.class);
		viewMeeting.putExtra(Keys.Meeting.PARCEL, meeting);
		startActivityForResult(viewMeeting, 6);
	}

	private void loadMeeting(Meeting meeting) {
		MeetingVolleyAdapter.fetchMeetingInfo(meeting.getID(),new AsyncResponse<Meeting>() {
			@Override
			public void processFinish(Meeting result) {
				viewMeeting(result);
			}
		});
	}

	public void viewTask(Task task) {
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

	private void setUpViews(View v) {
		meetingAdpt = new MeetingItemAdapter(getActivity(),	R.layout.list_item_meeting, meetings);
		meetingList = (ListView) v.findViewById(R.id.homepage_meetings);
		meetingList.setAdapter(meetingAdpt);

		taskAdpt = new TaskItemAdapter(getActivity(), R.layout.list_item_task,	tasks, false);
		taskList = (ListView) v.findViewById(R.id.homepage_tasks);
		taskList.setAdapter(taskAdpt);

		meetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter,View v, int position, long id) {
				Meeting clicked = meetingAdpt.getItem(position);
				loadMeeting(clicked);
			}
		});

		taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v, int position, long id) {
				Task clicked = taskAdpt.getItem(position);
				loadTask(clicked);
			}
		});

	}

	private void loadSchedule(Schedule sched) {
		meetingAdpt.clear();
		meetingAdpt.addAll(sched.getMeetings());
		meetingAdpt.notifyDataSetChanged();

		taskAdpt.clear();
		taskAdpt.addAll(sched.getTasks());
		taskAdpt.notifyDataSetChanged();
	}

	private void getSchedule() {
		new AsyncTask<Void, Void, Schedule>() {
			@Override
			protected Schedule doInBackground(Void... arg0) {
				Schedule sched = new Schedule();
				try {
					sched = UserDatabaseAdapter.getSchedule(SessionManager.getUserID());
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
				loadSchedule(result);
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