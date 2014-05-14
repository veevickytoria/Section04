package com.meetingninja.csse.projects;

import objects.Meeting;
import objects.Note;
import objects.parcelable.MeetingParcel;
import objects.parcelable.NoteParcel;
import android.content.Intent;

import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.meetings.ViewMeetingActivity;
import com.meetingninja.csse.notes.ViewNoteActivity;

public class ViewMeetingProjectActivity extends ViewMeetingActivity{
	
	public static final int REQUEST_CODE = 6;

	@Override
	protected void deleteMeeting(Meeting meeting) {
		Intent i = new Intent();
		i.putExtra(Keys.Meeting.PARCEL,new MeetingParcel(meeting));
		setResult(11, i);
		finish();
	}
}
