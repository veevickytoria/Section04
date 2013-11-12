package com.droidrage.meetingninja;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FullMeeting extends Activity {
	
	private String descript;
	private String dateTime;
	private String titl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_meeting);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_meeting, menu);
		return true;
	}

}
