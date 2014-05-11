package com.meetingninja.csse.projects;

import objects.Meeting;
import objects.parcelable.MeetingParcel;
import android.content.Intent;

import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.meetings.MeetingsFragment;
import com.meetingninja.csse.meetings.ViewMeetingActivity;

public class MeetingsProjectFragment extends MeetingsFragment{
	ViewProjectActivity pCont;
	@Override
	protected void deleteMeeting(Meeting item){
		this.pCont.deleteMeeting(item);
	}
	@Override
	public void editMeeting(Meeting editMe) {
		this.pCont.createMeeting();
	}
	public MeetingsProjectFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}
	@Override
	protected void loadMeeting(Meeting meeting) {
		while (meeting.getEndTimeInMillis() == 0L);
		Intent viewMeeting = new Intent(getActivity(),ViewMeetingProjectActivity.class);
		viewMeeting.putExtra(Keys.Meeting.PARCEL, new MeetingParcel(meeting));
		startActivityForResult(viewMeeting, 6);
	}
}
