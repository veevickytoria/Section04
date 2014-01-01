package com.android.meetingninja.meetings;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.android.meetingninja.R;

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
