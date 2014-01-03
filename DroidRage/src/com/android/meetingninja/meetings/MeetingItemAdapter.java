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

import java.util.List;

import objects.Meeting;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.meetingninja.R;

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
						meeting.getLocation(), meeting.getDatetimeStart());
				meetingDesc.setText(content);
			}
		}

		return v;
	}

}
