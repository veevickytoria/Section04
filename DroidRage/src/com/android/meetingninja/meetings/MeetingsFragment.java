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
package com.android.meetingninja.meetings;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Note;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.meetingninja.MainActivity;
import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.local.SQLiteMeetingAdapter;
import com.android.meetingninja.extras.Connectivity;
import com.android.meetingninja.user.SessionManager;

public class MeetingsFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private static final String TAG = MeetingsFragment.class.getSimpleName();

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private MeetingItemAdapter meetingAdpt;
	private ImageButton meetingImageButton;

	private MeetingFetcherTask fetcher = null;

	private SessionManager session;
	private SQLiteMeetingAdapter mySQLiteAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_meetings, container, false);
		setHasOptionsMenu(true);

		session = SessionManager.getInstance();
		mySQLiteAdapter = new SQLiteMeetingAdapter(getActivity());

		// setup listview
		meetingList = (ListView) v.findViewById(R.id.meetingsList);
		meetingAdpt = new MeetingItemAdapter(getActivity(),
				R.layout.list_item_meeting, meetings);
		meetingList.setAdapter(meetingAdpt);

		populateList();

		// pretty images are better than boring text
		meetingImageButton = (ImageButton) v.findViewById(android.R.id.empty);
		meetingList.setEmptyView(meetingImageButton);
		meetingImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editMeeting(null);
			}
		});

		// check for internet access before getting meetings from remote
		// database
		if (Connectivity.isConnected(getActivity()))
			fetchMeetings();

		// make list long-pressable
		registerForContextMenu(meetingList);

		// Item click event
		// TODO: Open a window to edit the meeting here
		meetingList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parentAdapter,
							View v, int position, long id) {
						Meeting clicked = meetingAdpt.getItem(position);
						editMeeting(clicked, position);

					}
				});

		// Item long-click event
		// TODO: Add additional options and click-events to these options
		meetingList
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

						Meeting longClicked = meetingAdpt
								.getItem(aInfo.position);

						menu.setHeaderTitle("Options for "
								+ longClicked.getTitle());
						menu.add(
								MainActivity.DrawerLabel.MEETINGS.getPosition(),
								aInfo.position, 1, "Edit");
						menu.add(
								MainActivity.DrawerLabel.MEETINGS.getPosition(),
								aInfo.position, 2, "Delete");

					}
				});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.meetings_fragment, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = item.getItemId();
		boolean handled = false;
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getGroupId() == MainActivity.DrawerLabel.MEETINGS.getPosition()) {
			switch (item.getOrder()) {
			case 1: // Edit
				Toast.makeText(getActivity(), item.getTitle(),
						Toast.LENGTH_SHORT).show();
				handled = true;
				break;
			case 2: // Delete
				Meeting meeting = meetingAdpt.getItem(position);
				mySQLiteAdapter.deleteMeeting(meeting);
				meetings.remove(position);
				meetingAdpt.notifyDataSetChanged();
				handled = true;
				break;
			default:
				Log.wtf(TAG, "Invalid context menu option selected");
				break;
			}
		} else {
			Log.wtf(TAG, "What happened here?");
		}

		return handled;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					int listPosition = data.getIntExtra("listPosition", -1);
					Meeting created = data
							.getParcelableExtra(EditMeetingActivity.EXTRA_MEETING);

					if (data.getStringExtra("method").equals("update")) {
						Log.d(TAG, "Updating Meeting #" + created.getID());
						if (listPosition != -1)
							updateMeeting(listPosition, created);
						else
							updateMeeting(created);
					} else if (data.getStringExtra("method").equals("insert")) {
						Log.d(TAG, "Inserting Meeting #" + created.getID());
						// created = mySQLiteAdapter.insertMeeting(created);
						populateList();
					}
				}
			} else {
				if (resultCode == Activity.RESULT_CANCELED) {
					// nothing to do here
				}
			}
		}
	}

	/**
	 * TODO: Gets the meetings from the remote database
	 */
	public void fetchMeetings() {
		// fetcher = new MeetingFetcherTask(this);
		// fetcher.execute(session.getUserID());
	}

	public void editMeeting(Meeting editMe) {
		editMeeting(editMe, -1);
	}

	public void editMeeting(Meeting editMe, int position) {
		Intent editMeeting = new Intent(getActivity(),
				EditMeetingActivity.class);
		if (null != editMe) {
			editMeeting.putExtra(EditMeetingActivity.EXTRA_MEETING, editMe);
		}
		if (position >= 0) {
			editMeeting.putExtra("listPosition", position);
		}

		editMeeting.putExtra(EditMeetingActivity.EXTRA_EDIT_MODE, true);
		startActivityForResult(editMeeting, 2);
	}

	private boolean updateMeeting(Meeting updated) {
		mySQLiteAdapter.updateMeeting(updated);
		populateList();
		return true;
	}

	private boolean updateMeeting(int position, Meeting updated) {
		if (position < 0 || position >= meetings.size())
			return false;
		meetings.set(position, updated);
		mySQLiteAdapter.updateMeeting(meetings.get(position));

		meetingAdpt.notifyDataSetChanged();

		return true;
	}

	@Override
	public void processFinish(List<Meeting> output) {
		meetings.clear();
		meetingAdpt.clear();

		meetings.addAll(output);

		meetingAdpt.notifyDataSetChanged();
	}

	public void populateList() {
		List<Meeting> contentRead = mySQLiteAdapter.getAllMeetings();
		processFinish(contentRead);
	}

}
