package com.droidrage.meetingninja.meetings;

import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.R;
import com.droidrage.meetingninja.R.id;
import com.droidrage.meetingninja.R.layout;
import com.droidrage.meetingninja.R.menu;
import com.droidrage.meetingninja.database.AsyncResponse;
import com.droidrage.meetingninja.user.SessionManager;

import objects.Meeting;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MeetingsFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private MeetingItemAdapter meetingAdpt;
	private ImageButton meetingImageButton;

	private MeetingFetcherTask fetcher = null;

	private SessionManager session;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_meetings, container, false);
		setHasOptionsMenu(true);

		session = new SessionManager(this.getActivity().getApplicationContext());
		meetingImageButton = (ImageButton) v.findViewById(R.id.imageButton);
		meetingImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				createMeeting();

			}
		});

		// setup listview
		meetingList = (ListView) v.findViewById(R.id.meetingsList);
		meetingAdpt = new MeetingItemAdapter(getActivity(),
				R.layout.meeting_item, meetings);
		meetingList.setAdapter(meetingAdpt);

		// TODO: Check for internet connection before receiving meetings from DB
		refreshMeetings();

		// setup empty view
		// View empty = inflater.inflate(R.layout.meetings_list_empty,
		// container, false);
		// getActivity().addContentView(empty, new
		// LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// lv.setEmptyView(empty);

		// make list long-pressable
		registerForContextMenu(meetingList);

		// Item click event
		// TODO: Open a window to edit the meeting here
		meetingList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parentAdapter,
							View v, int position, long id) {
						Meeting m = meetingAdpt.getItem(position);
						String msg = m.getTitle();
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
								.show();
						Intent viewMeeting = new Intent(getActivity(),
								ViewMeetingActivity.class);
						startActivity(viewMeeting);

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

						Meeting n = meetingAdpt.getItem(aInfo.position);

						menu.setHeaderTitle("Options for " + n.getTitle());
						menu.add(1, 1, 1, "Edit");
						menu.add(1, 2, 2, "Delete");

					}
				});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.meetings, menu);
	}

	/**
	 * Initializes the list of meetings.
	 */
	public void refreshMeetings() {
		fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserDetails().get(SessionManager.USER));
	}

	public void createMeeting() {
		Intent createMeeting = new Intent(getActivity(), MeetingsActivity.class);
		createMeeting.putExtra("edit", true);
		startActivity(createMeeting);
	}

	@Override
	public void processFinish(List<Meeting> output) {
		meetingAdpt.clear();
		meetingAdpt.addAll(output);

		if (meetingAdpt.isEmpty()) {
			meetingList.setVisibility(View.GONE);
			meetingImageButton.setVisibility(View.VISIBLE);
		} else {
			meetingList.setVisibility(View.VISIBLE);
			meetingImageButton.setVisibility(View.GONE);
		}
	}

}