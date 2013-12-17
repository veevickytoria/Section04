package com.droidrage.meetingninja;

import java.util.List;

import objects.Meeting;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A class to display the meetings in a specific format for the items of the
 * list. This class uses the meeting_item XML file.
 * 
 * @author moorejm
 * 
 */
public class MeetingItemAdapter extends ArrayAdapter<Meeting> {
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
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.meeting_item, null);
		}

		// Setup from the meeting_item XML file
		Meeting meeting = meetings.get(position);
		if (meeting != null) {
			TextView meetingTitle = (TextView) v
					.findViewById(R.id.meetingTitle);
			TextView meetingDesc = (TextView) v.findViewById(R.id.meetingDesc);

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