package com.meetingninja.csse.group;

import com.meetingninja.csse.R;
import com.meetingninja.csse.R.layout;
import com.meetingninja.csse.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditGroupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_group, menu);
		return true;
	}

}
