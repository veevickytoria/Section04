package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FullMeeting extends Fragment {

	public static final String ARG_USERNAME = "user";
	private List<Meeting> meeting = new ArrayList<Meeting>();
	private MeetingAdapter meetingAdpt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_meetings, container, false);
		setHasOptionsMenu(true);

		ListView lv = (ListView) v.findViewById(R.id.meetingsList);
		meetingAdpt = new MeetingAdapter(getActivity(), R.layout.meeting_full,
				meeting);
		// setup listview
		lv.setAdapter(meetingAdpt);

		// setup empty view
		View empty_view = v.findViewById(R.id.notes_empty);
		lv.setEmptyView(empty_view);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.meetings, menu);
	}

	/**
	 * A class to display the meeting in a specific format. This class uses the
	 * meeting_full XML file.
	 * 
	 * @author campbeeg
	 * 
	 */
	private class MeetingAdapter extends ArrayAdapter<Meeting> {
		private List<Meeting> meetings;

		/*
		 * Override the constructor to initialize the list to display
		 */
		public MeetingAdapter(Context context, int textViewResourceId,
				List<Meeting> meeting) {
			super(context, textViewResourceId);
			this.meetings = meeting;
		}

		/*
		 * We are overriding the getView method here - this is what defines how
		 * the meeting will look.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.meeting_full, null);
			}

			// Setup from the meeting_full XML file
			Meeting meeting = meetings.get(position);
			if (meeting != null) {
				TextView meetingTitle = (TextView) v
						.findViewById(R.id.meetingTitl);
				TextView meetingLocation = (TextView) v
						.findViewById(R.id.meetingLocat);
				TextView meetingDatetime = (TextView) v
						.findViewById(R.id.meetingDT);

				if (meetingTitle != null) {
					meetingTitle.setText(meeting.getTitle());
				}
				if (meetingLocation != null) {
					meetingLocation.setText(meeting.getLocation());
				}
				if (meetingDatetime != null) {
					meetingDatetime.setText(meeting.getDatetime());
				}
			}

			return v;
		}

	}

}
