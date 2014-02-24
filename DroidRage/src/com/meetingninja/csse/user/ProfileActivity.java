/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.meetingninja.csse.user;

import objects.User;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.Keys;

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
		if (input != null && input.containsKey(Keys.User.PARCEL)) {
			displayedUser = (User) input.getParcelable(Keys.User.PARCEL);
		}
		ProfileFragment profFrag = new ProfileFragment();
		Bundle args = new Bundle();

		if (displayedUser != null) {
			// args.putString("userID", displayedUser.getID());
			args.putParcelable(Keys.User.PARCEL, displayedUser);
			args.putString("notMine", "yup");
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
		getMenuInflater().inflate(R.menu.menu_profile, menu);
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
			// NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
