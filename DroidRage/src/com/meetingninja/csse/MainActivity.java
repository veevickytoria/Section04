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
package com.meetingninja.csse;

import java.io.IOException;
import java.util.ArrayList;

import objects.Event;
import objects.Note;
import objects.Schedule;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.foound.widget.AmazingListView;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteHelper;
import com.meetingninja.csse.extras.NinjaToastUtil;
import com.meetingninja.csse.group.GroupsFragment;
import com.meetingninja.csse.meetings.MeetingsFragment;
import com.meetingninja.csse.notes.EditNoteActivity;
import com.meetingninja.csse.notes.NotesFragment;
import com.meetingninja.csse.projects.ProjectFragment;
import com.meetingninja.csse.schedule.ScheduleAdapter;
import com.meetingninja.csse.tasks.TasksFragment;
import com.meetingninja.csse.user.LoginActivity;
import com.meetingninja.csse.user.ProfileFragment;
import com.meetingninja.csse.user.UserListFragment;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	/**
	 * Fields for the navigation drawer(s)
	 */
	private String[] leftDrawerItemNames;
	private TypedArray leftDrawerItemIcons;
	private DrawerLayout drawerLayout;
	private ListView leftDrawerList;
	private AmazingListView rightDrawerList;
	private ActionBarDrawerToggle drawerToggle;
	private CharSequence mDrawerTitle;
	private ArrayList<NavDrawerItem> leftDrawerItems;
	private NavDrawerListAdapter leftDrawerAdapter;
	private ScheduleAdapter rightDrawerAdapter;

	public enum DrawerLabel {
		HOMEPAGE(0, homepage), MEETINGS(1, frag_meetings), NOTES(2, frag_notes), TASKS(
				3, frag_tasks), PROFILE(4, frag_profile), GROUPS(5, frag_groups), PROJECTS(
				6, frag_projects), CONTACTS(7, frag_contacts), LOGOUT(8,
				new LogoutFragment());

		private int position;
		private Fragment frag;

		private DrawerLabel(int position, Fragment frag) {
			this.position = position;
			this.frag = frag;
		}

		public int getPosition() {
			return this.position;
		}

		public Fragment getFragment() {
			return this.frag;
		}

	}

	// Instances of fragments contained within this activity
	private static final HomePageFragment homepage = new HomePageFragment();
	private static final MeetingsFragment frag_meetings = MeetingsFragment
			.getInstance();
	private static final NotesFragment frag_notes = NotesFragment.getInstance();
	private static final TasksFragment frag_tasks = new TasksFragment();
	private static final ProfileFragment frag_profile = new ProfileFragment();
	private static final GroupsFragment frag_groups = new GroupsFragment();
	private static final ProjectFragment frag_projects = new ProjectFragment();
	private static final UserListFragment frag_contacts = new UserListFragment();

	// Fields local to this activity
	private CharSequence actionBarTitle;
	private SessionManager session = SessionManager.getInstance();
	private boolean isDataCached;
	private static final String KEY_SQL_CACHE = "SQL_DATA_CACHE";

	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if logged in
		if (!session.isLoggedIn()) {
			Log.v(TAG, "User is not logged in");
			showLogin();
		} else { // Else continue
			Log.v(TAG, "UserID " + SessionManager.getUserID() + " is logged in");

			setContentView(R.layout.activity_main);
			setupActionBar();
			setupViews();

			// on first time display view for first nav item
			selectItem(session.getPage());

			checkAndPreloadData(savedInstanceState);

			ParseAnalytics.trackAppOpened(getIntent());
		}

	}

	/**
	 * // Check to see if data has been cached in the local database
	 *
	 * @param icicle
	 */
	private void checkAndPreloadData(Bundle icicle) {
		if (icicle != null) {
			isDataCached = icicle.getBoolean(KEY_SQL_CACHE, false);
		}
		if (!isDataCached || session.needsSync()) {
			ApplicationController.getInstance().loadUsers();

			// TODO : Preload more data

			isDataCached = true;
			session.setSynced();
		}

	}

	/**
	 * Route the user to the login screen
	 */
	private void showLogin() {
		Intent login = new Intent(this, LoginActivity.class);
		login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// User cannot go back to this activity
		// login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		login.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(login);
		finish();
	}

	private void setupActionBar() {
		// Set up the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	private void setupViews() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawerList = (ListView) findViewById(R.id.left_drawer);
		rightDrawerList = (AmazingListView) findViewById(R.id.right_drawer);
		setupLeftDrawer();
		setupRightDrawer();
	}

	private void setupLeftDrawer() {
		// Get the strings
		leftDrawerItemNames = getResources().getStringArray(
				R.array.nav_drawer_items);
		// Get the icons
		leftDrawerItemIcons = getResources().obtainTypedArray(
				R.array.nav_drawer_icons);

		// Get the views
		leftDrawerItems = new ArrayList<NavDrawerItem>();
		DrawerLabel[] labels = DrawerLabel.values();
		for (int i = 0; i < leftDrawerItemNames.length; i++) {
			String label = leftDrawerItemNames[i];
			int icon_Id = leftDrawerItemIcons.getResourceId(i, -1);
			Fragment page = labels[i].getFragment();

			leftDrawerItems.add(new NavDrawerItem(label, icon_Id, page));
		}
		// recycle the typed array
		leftDrawerItemIcons.recycle();

		leftDrawerList.setOnItemClickListener(new LeftDrawerClickListener());

		// setup the list
		leftDrawerAdapter = new NavDrawerListAdapter(getApplicationContext(),
				leftDrawerItems);
		leftDrawerList.setAdapter(this.leftDrawerAdapter);

		drawerToggle = new ActionBarDrawerToggle(this, // host activity
				drawerLayout, // DrawerLayout object
				R.drawable.ic_drawer, // nav drawer icon
				R.string.drawer_open, // "open drawer" description
				R.string.drawer_close) // "closed drawer" description
		{
			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(actionBarTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		// drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		// GravityCompat.END);

	}

	private void setupRightDrawer(Schedule sched) {
		rightDrawerList.setOnItemClickListener(new RightDrawerClickListener());
		rightDrawerList.setPinnedHeaderView(LayoutInflater.from(this).inflate(
				R.layout.list_item_schedule_header, rightDrawerList, false));
		rightDrawerAdapter = new ScheduleAdapter(MainActivity.this, sched);
		rightDrawerList.setAdapter(rightDrawerAdapter);
		rightDrawerAdapter.notifyDataSetChanged();
	}

	private void setupRightDrawer() {
		new AsyncTask<Void, Void, Schedule>() {

			@Override
			protected Schedule doInBackground(Void... arg0) {
				Schedule sched = new Schedule();
				try {
					sched = UserDatabaseAdapter.getSchedule(SessionManager
							.getUserID());
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return sched;
			}

			@Override
			public void onPostExecute(Schedule result) {
				setupRightDrawer(result);
			}

		}.execute();

	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		// Save the state of the current fragment
		session.setPage(position);

		String clickedLabel = leftDrawerItems.get(position).getTitle();

		if (selectFromLeftDrawer(position, getSupportFragmentManager())) {
			Log.d(TAG, "Transition: " + clickedLabel);

		} else {
			Log.e(TAG, "Navigation Transition error.\nFragment does not exist.");
		}
	}

	/**
	 * Highlight the selected item, update the title, and close the drawer
	 *
	 * @param position
	 */
	private boolean selectFromLeftDrawer(int position, FragmentManager fm) {
		boolean validSelection = leftDrawerItems.get(position).select(fm);
		leftDrawerList.setItemChecked(position, true);
		leftDrawerList.setSelection(position);
		setTitle(leftDrawerItemNames[position]);
		drawerLayout.closeDrawer(leftDrawerList);
		return validSelection;
	}

	@Override
	public void setTitle(CharSequence title) {
		actionBarTitle = title;
		getActionBar().setTitle(actionBarTitle);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 3)
			frag_notes.populateList();

		if (data != null) {

			if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
					&& resultCode == RESULT_OK) {
				ArrayList<String> speechArray = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				handleSpeech(speechArray, data);
			}
		}
	}


	private void handleSpeech(ArrayList<String> speechArray, Intent data) {
		if (speechArray.contains("meetings")) {
			selectItem(DrawerLabel.MEETINGS.getPosition());
		} else if (speechArray.contains("groups")) {
			selectItem(DrawerLabel.GROUPS.getPosition());
		} else if (speechArray.contains("notes")) {
			selectItem(DrawerLabel.NOTES.getPosition());
		} else if (speechArray.contains("profile")) {
			selectItem(DrawerLabel.PROFILE.getPosition());
		} else if (speechArray.contains("tasks")) {
			selectItem(DrawerLabel.TASKS.getPosition());
		} else if (speechArray.contains("projects")) {
			selectItem(DrawerLabel.PROJECTS.getPosition());
		} else if (speechArray.contains("contacts")) {
			selectItem(DrawerLabel.CONTACTS.getPosition());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle other action bar items...
		switch (item.getItemId()) {
		case R.id.action_refresh:
			NinjaToastUtil.show(this, "Refreshing View");
			switch (DrawerLabel.values()[session.getPage()]) {
			case MEETINGS:
				frag_meetings.refresh();
				return true;
			case NOTES:
				frag_notes.refresh();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}

		case R.id.action_new_meeting:
			frag_meetings.editMeeting(null);
			return true;
		case R.id.action_new_note:
			Intent createNote = new Intent(this, EditNoteActivity.class);
			createNote.putExtra(Note.CREATE_NOTE, true);
			startActivityForResult(createNote, 3);
			return true;
		case R.id.action_logout:
			ApplicationController.getInstance().logout();
			return true;
		case R.id.action_speak:
			Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go to...");
			try {
				startActivityForResult(speechIntent, VOICE_RECOGNITION_REQUEST_CODE);

			} catch (Exception e) {
				NinjaToastUtil.show(this,
						"Error initializing speech to text engine.");
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = drawerLayout.isDrawerOpen(leftDrawerList)
				|| drawerLayout.isDrawerOpen(rightDrawerList);
		for (int i = 0; i < menu.size(); i++) {
			menu.getItem(i).setVisible(!drawerOpen);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(KEY_SQL_CACHE, isDataCached);
	}

	private class LeftDrawerClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private class RightDrawerClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			Event clicked = rightDrawerAdapter.getItem(position);
			// TODO: View the event
		}
	}
}
