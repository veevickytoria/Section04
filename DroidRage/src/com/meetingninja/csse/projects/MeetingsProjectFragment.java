package com.meetingninja.csse.projects;

import objects.Meeting;

import com.meetingninja.csse.meetings.MeetingsFragment;

public class MeetingsProjectFragment extends MeetingsFragment{
	ViewProjectActivity pCont;
	@Override
	protected void deleteMeeting(Meeting item){
		this.pCont.deleteMeeting(item);
	}
	
	
	public MeetingsProjectFragment setProjectController(ViewProjectActivity pCont){
		this.pCont = pCont;
		return this;
	}
}
