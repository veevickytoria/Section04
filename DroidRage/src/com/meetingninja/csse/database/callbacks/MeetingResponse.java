package com.meetingninja.csse.database.callbacks;

import objects.Meeting;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;

public class MeetingResponse extends AbstractResponse<Meeting> {
	
	@Override
	public void processFinish(Meeting result) {
		super.processFinish(result);
		Log.i("Returning Meeting", result.getTitle());
	}
	
	@Override
	public Meeting getData() {
		return super.getData();
	}
	

};