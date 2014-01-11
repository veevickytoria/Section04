package com.android.meetingninja.user;

import com.android.meetingninja.R;
import com.android.meetingninja.R.id;
import com.android.meetingninja.R.layout;
import com.android.meetingninja.R.menu;

import objects.User;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;

public class ProfileActivity extends FragmentActivity {

	private static final String TAG = ProfileActivity.class.getSimpleName();
	private User displayedUser;

	public ProfileActivity() {
		displayedUser = new User();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		// Show the Up button in the action bar.
		setupActionBar();
		
		FragmentManager fm = getSupportFragmentManager();
		Bundle input = getIntent().getExtras();
		if (input != null && input.containsKey("user")) {
			displayedUser = (User) input.getParcelable("user");
		}
		ProfileFragment profFrag = new ProfileFragment();
		Bundle args = new Bundle();

		if (displayedUser != null) {
			args.putString("userID", displayedUser.getUserID());
			profFrag.setArguments(args);
			fm.beginTransaction().replace(R.id.profile_container, profFrag)
					.commit();
			getActionBar().setTitle(displayedUser.getDisplayName());
		} else {
			Log.d(TAG, "User is null");
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("");
	}

	public void setUser(User user) {
		displayedUser = user;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
