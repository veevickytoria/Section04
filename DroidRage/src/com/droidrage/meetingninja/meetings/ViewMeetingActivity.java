package com.droidrage.meetingninja.meetings;

import com.droidrage.meetingninja.R;
import com.droidrage.meetingninja.R.layout;
import com.droidrage.meetingninja.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ViewMeetingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_meeting);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_meeting, menu);
		return true;
	}

}
