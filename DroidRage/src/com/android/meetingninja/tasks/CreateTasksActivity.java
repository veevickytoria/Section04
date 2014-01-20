package com.android.meetingninja.tasks;

import com.android.meetingninja.R;
import com.android.meetingninja.R.layout;
import com.android.meetingninja.R.menu;
import com.android.meetingninja.database.AsyncResponse;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class CreateTasksActivity extends FragmentActivity implements
AsyncResponse<Boolean>{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_task);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_tasks, menu);
		return true;
	}

	@Override
	public void processFinish(Boolean result) {
		if (result) {
			finish();
		} else {
			Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT)
					.show();
		}		
	}

}
