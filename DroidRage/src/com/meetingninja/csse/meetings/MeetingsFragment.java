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
package com.meetingninja.csse.meetings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.Meeting;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.MainActivity;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteMeetingAdapter;
import com.meetingninja.csse.extras.Connectivity;

import de.timroes.android.listview.EnhancedListView;

public class MeetingsFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private static final String TAG = MeetingsFragment.class.getSimpleName();

	private EnhancedListView meetingList;
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
		meetingList = (EnhancedListView) v.findViewById(android.R.id.list);
		meetingAdpt = new MeetingItemAdapter(getActivity(),
				R.layout.list_item_meeting, meetings);
		meetingList.setAdapter(meetingAdpt);
		populateList();
		// check for internet access before getting meetings from remote
		// database
		if (Connectivity.isConnected(getActivity()) && isAdded()) {
			fetchMeetings();
		}
		// pretty images are better than boring text
		meetingImageButton = (ImageButton) v.findViewById(android.R.id.empty);
		meetingList.setEmptyView(meetingImageButton);
		meetingImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editMeeting(null);
			}
		});
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
		meetingList
				.setDismissCallback(new EnhancedListView.OnDismissCallback() {
					@Override
					public EnhancedListView.Undoable onDismiss(
							EnhancedListView listView, final int position) {

						final Meeting item = (Meeting) meetingAdpt
								.getItem(position);
						meetingAdpt.remove(item);
						return new EnhancedListView.Undoable() {
							@Override
							public void undo() {
								meetingAdpt.insert(item, position);
							}

							@Override
							public String getTitle() {
								return "Meeting deleted";
							}

							@Override
							public void discard() {
								deleteMeeting(item.getID());
								meetings.remove(item);
								meetingAdpt.notifyDataSetChanged();
							}
						};
					}
				});
		meetingList.enableSwipeToDismiss();
		meetingList.setSwipingLayout(R.id.list_meeting_item_frame_1);
		meetingList.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
		return v;
	}

	@Override
	public void onStop() {
		meetingList.discardUndo();
		super.onStop();
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
		if (item.getGroupId() == MainActivity.DrawerLabel.MEETINGS
				.getPosition()) {
			switch (item.getOrder()) {
			case 1: // Edit
				Toast.makeText(getActivity(), item.getTitle(),
						Toast.LENGTH_SHORT).show();
				handled = true;
				break;
			case 2: // Delete
				Meeting meeting = meetingAdpt.getItem(position);
				// mySQLiteAdapter.deleteMeeting(meeting); Need to implement
				deleteMeeting(meeting.getID());
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
							.getParcelableExtra(Keys.Meeting.PARCEL);

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

	public void fetchMeetings() {
		fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserID()); // calls processFinish()
		// UserDatabaseAdapter.fetchUserMeetings(this, session.getUserID());
	}

	public void editMeeting(Meeting editMe) {
		editMeeting(editMe, -1);
	}

	public void editMeeting(Meeting editMe, int position) {
		Intent editMeeting = new Intent(getActivity(),
				EditMeetingActivity.class);
		if (null != editMe) {
			editMeeting.putExtra(Keys.Meeting.PARCEL, editMe);
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

	public void deleteMeeting(String meetingID) {
		String url = MeetingDatabaseAdapter.getBaseUri().appendPath(meetingID)
				.build().toString();
		StringRequest dr = new StringRequest(Request.Method.DELETE, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// response
						Toast.makeText(getActivity(), response,
								Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// error.

					}
				});

		ApplicationController.getInstance().addToRequestQueue(dr);
	}

	@Override
	public void processFinish(List<Meeting> result) {
		meetings.clear();
		meetingAdpt.clear();
		Collections.sort(result, new Comparator<Meeting>() {
			@Override
			public int compare(Meeting lhs, Meeting rhs) {
				return lhs.compareTo(lhs);
			}
		});

		meetings.addAll(result);

		meetingAdpt.notifyDataSetChanged();
	}

	public void populateList() {
		List<Meeting> meets = new ArrayList<Meeting>();

	}

}
