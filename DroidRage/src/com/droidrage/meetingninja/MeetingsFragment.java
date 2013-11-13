package com.droidrage.meetingninja;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.caverock.androidsvg.SVGImageView;

import objects.Meeting;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
						Intent viewMeeting = new Intent(getActivity(), ViewMeetingActivity.class);
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
		startActivity(createMeeting);
	}

	@Override
	public void processFinish(List<Meeting> output) {
		meetingAdpt.clear();
		meetingAdpt.addAll(output);

		if (meetingAdpt.isEmpty()) {
			meetingList.setVisibility(View.INVISIBLE);
			meetingImageButton.setVisibility(View.VISIBLE);
		} else {
			meetingList.setVisibility(View.VISIBLE);
			meetingImageButton.setVisibility(View.GONE);
		}
	}

	/**
	 * A class to display the meetings in a specific format for the items of the
	 * list. This class uses the meeting_item XML file.
	 * 
	 * @author moorejm
	 * 
	 */
	private class MeetingItemAdapter extends ArrayAdapter<Meeting> {
		// declaring our ArrayList of items
		private List<Meeting> meetings;

		/*
		 * Override the constructor to initialize the list to display
		 */
		public MeetingItemAdapter(Context context, int textViewResourceId,
				List<Meeting> meetings) {
			super(context, textViewResourceId, meetings);
			this.meetings = meetings;
		}

		/*
		 * we are overriding the getView method here - this is what defines how
		 * each list item will look.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.meeting_item, null);
			}

			// Setup from the meeting_item XML file
			Meeting meeting = meetings.get(position);
			if (meeting != null) {
				TextView meetingTitle = (TextView) v
						.findViewById(R.id.meetingTitle);
				TextView meetingDesc = (TextView) v
						.findViewById(R.id.meetingDesc);

				if (meetingTitle != null) {
					meetingTitle.setText(meeting.getTitle());
				}
				if (meetingDesc != null) {
					String content = String.format("%s : %s",
							meeting.getLocation(), meeting.getDatetime());
					meetingDesc.setText(content);
				}
			}

			return v;
		}

	}

	/**
	 * Represents an asynchronous task to receive meetings from the database
	 */
	public class MeetingFetcherTask extends
			AsyncTask<String, Void, List<Meeting>> {
		private AsyncResponse<List<Meeting>> delegate;

		public MeetingFetcherTask(AsyncResponse<List<Meeting>> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected List<Meeting> doInBackground(String... params) {
			List<Meeting> dbMeetings = new ArrayList<Meeting>();

			try {
				dbMeetings = DatabaseAdapter.getMeetings(params[0]);
			} catch (IOException e) {
				Log.e("MeetingFetch", "Error: Unable to get meetings");
				Log.e("MEETINGS_ERR", e.getLocalizedMessage());
			}

			return dbMeetings;
		}

		@Override
		protected void onPostExecute(List<Meeting> list) {
			super.onPostExecute(list);
			delegate.processFinish(list);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}
}