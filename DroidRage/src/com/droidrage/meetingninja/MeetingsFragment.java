package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeetingsFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private List<Meeting> meetings = new ArrayList<Meeting>();
	private MeetingItemAdapter meetingAdpt;

	private MeetingFetcherTask fetcher = null;
	
	private SessionManager session;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_meetings, container, false);
		setHasOptionsMenu(true);
		
		session = new SessionManager(this.getActivity().getApplicationContext());
		
		// TODO: Check for internet connection before receiving meetings from DB
		refreshMeetings();

		ListView lv = (ListView) v.findViewById(R.id.meetingsList);
		meetingAdpt = new MeetingItemAdapter(getActivity(), R.layout.meeting_item,
				meetings);
		// setup listview
		lv.setAdapter(meetingAdpt);
		
		//setup empty view
		View empty_view = v.findViewById(R.id.notes_empty);
		lv.setEmptyView(empty_view);
		
		// make list long-pressable
		registerForContextMenu(lv);

		// Item click event
		// TODO: Open a window to edit the meeting here
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,
					int position, long id) {
				Meeting m = meetingAdpt.getItem(position);
				String msg = m.getTitle();
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

			}
		});

		// Item long-click event
		// TODO: Add additional options and click-events to these options
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

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
	public void refreshMeetings(){
		
		fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserDetails().get(SessionManager.USER));
	}
	
	@Override
	public void processFinish(List<Meeting> output) {
		Toast.makeText(getActivity(),
				String.format("Received %d meetings", output.size()),
				Toast.LENGTH_SHORT).show();
		meetingAdpt.clear();
		meetingAdpt.addAll(output);
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
			} catch (Exception e) {
				Log.e("MeetingFetch", "error getting meetings");
				e.printStackTrace();
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