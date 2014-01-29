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

import objects.Meeting;
import objects.Schedule;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
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
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteHelper;
import com.meetingninja.csse.group.GroupsFragment;
import com.meetingninja.csse.meetings.MeetingsFragment;
import com.meetingninja.csse.notes.CreateNoteActivity;
import com.meetingninja.csse.notes.NotesFragment;
import com.meetingninja.csse.schedule.ScheduleAdapter;
import com.meetingninja.csse.tasks.TasksFragment;
import com.meetingninja.csse.user.LoginActivity;
import com.meetingninja.csse.user.ProfileFragment;
import com.meetingninja.csse.user.UserListFragment;
import com.meetingninja.csse.extras.BaseFragment;
import com.parse.ParseAnalytics;

/**
 * Main Activity Window.
 * 
 * @author moorejm
 * 
 */
public class MainActivity extends FragmentActivity implements
		BaseFragment.TaskCallbacks {

	private static final String TAG = MainActivity.class.getSimpleName();

	/**
	 * Fields for the navigation drawer(s)
	 */
	private String[] leftDrawerItemNames; // labels
	private TypedArray leftDrawerItemIcons; // icons next to text
	private DrawerLayout drawerLayout; // the "frame" of main activity
	private ListView leftDrawerList; // the listviews for the
										// drawers
	private AmazingListView rightDrawerList;
	private ActionBarDrawerToggle drawerToggle; // open&close toggle
	private CharSequence mDrawerTitle; // title for the drawer
	private ArrayList<NavDrawerItem> leftDrawerItems; // object wrapper for left
														// drawer
	private NavDrawerListAdapter leftDrawerAdapter;
	private ScheduleAdapter rightDrawerAdapter;

	public enum DrawerLabel {
		MEETINGS(0), NOTES(1), TASKS(2), PROFILE(3), GROUPS(4), PROJECTS(5), CONTACTS(
				6), SETTINGS(7), ABOUT(8), LOGOUT(9);

		private int position;

		private DrawerLabel(int position) {
			this.position = position;
		}

		public int getPosition() {
			return this.position;
		}

	}

	/**
	 * Instances of fragments contained within this activity
	 */
	private MeetingsFragment frag_meetings;
	private NotesFragment frag_notes;
	private TasksFragment frag_tasks;
	private ProfileFragment frag_profile;
	private GroupsFragment frag_groups;
	// private ProjectsFragment frag_projects;
	// private ContactsFragment frag_contacts;
	private UserListFragment frag_contacts;
	private SearchableUserFragment frag_settings;

	/**
	 * Fields local to this activity
	 */
	private Bundle icicle;
	private CharSequence mTitle;
	private SessionManager session;
	private SQLiteHelper sqliteHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		icicle = new Bundle();
		if (savedInstanceState != null)
			this.icicle.putAll(savedInstanceState);

		SessionManager.getInstance().init(MainActivity.this);
		session = SessionManager.getInstance();

		// session.createLoginSession("749");

		// Check if logged in
		if (!session.isLoggedIn()) {
			Log.v(TAG, "User is not logged in");
			Intent login = new Intent(this, LoginActivity.class);
			// Bring login to front
			login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// User cannot go back to this activity
			// login.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			// Show no animation when launching login page
			login.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(login);
			finish(); // close main activity
		} else { // Else continue
			Log.v(TAG, "UserID " + session.getUserID() + " is logged in");
			setContentView(R.layout.activity_main);
			setupActionBar();
			setupViews();
			setupLeftDrawer();
			setupRightDrawer();

			// on first time display view for first nav item
			selectItem(session.getPage());

			sqliteHelper = SQLiteHelper.getInstance(getApplicationContext());

			// Track the usage of the application with Parse SDK
			ParseAnalytics.trackAppOpened(getIntent());
		}

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
		for (int i = 0; i < leftDrawerItemNames.length; i++) {
			leftDrawerItems.add(new NavDrawerItem(leftDrawerItemNames[i],
					leftDrawerItemIcons.getResourceId(i, -1)));
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
				getActionBar().setTitle(mTitle);
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

	private void setupRightDrawer() {
		rightDrawerList.setPinnedHeaderView(LayoutInflater.from(this).inflate(
				R.layout.list_item_schedule_header, rightDrawerList, false));
		Schedule sched = new Schedule();
		try {
			sched = UserDatabaseAdapter.getSchedule("");
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
		Meeting m = new Meeting();
		m.setStartTime(0L);
		sched.addMeeting(m);
		m = new Meeting();
		m.setStartTime(918723912L);
		sched.addMeeting(m);
		m = new Meeting();
		m.setStartTime(928374983274L);
		sched.addMeeting(m);
		m = new Meeting();
		m.setStartTime(38501234L);
		for (int i = 0; i < 5; i++) {
			sched.addMeeting(m);
		}
		m = new Meeting();
		m.setStartTime(12321387L);
		sched.addMeeting(m);
		for (int i = 0; i < 3; i++) {
			sched.addMeeting(m);
		}
		sched.sort();
		rightDrawerAdapter = new ScheduleAdapter(MainActivity.this, sched);
		rightDrawerList.setAdapter(rightDrawerAdapter);
		rightDrawerAdapter.notifyDataSetChanged();
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		if (session.getPage() == position) {
			drawerLayout.closeDrawers();
			return;
		}

		// Save the state of the current fragment
		session.setPage(position);
		FragmentManager fm = getSupportFragmentManager();
		final Fragment currentPage = fm.findFragmentById(R.id.content_frame);
		// fm.putFragment(savedFragmentInstance, "saved_fragment",
		// fm.findFragmentById(R.id.content_frame));
		// System.out.println(fm.getFragments());

		Fragment nextPage = null;
		Bundle args = new Bundle();
		String tag = "default";

		DrawerLabel clickedLabel = DrawerLabel.values()[position];
		switch (clickedLabel) {
		case MEETINGS:
			if ((frag_meetings = (MeetingsFragment) fm
					.findFragmentByTag(Keys.Meeting.LIST)) == null)
				frag_meetings = new MeetingsFragment();
			nextPage = frag_meetings;
			tag = Keys.Meeting.LIST;
			break;
		case NOTES:
			if ((frag_notes = (NotesFragment) fm
					.findFragmentByTag(Keys.Note.LIST)) == null)
				frag_notes = new NotesFragment();
			nextPage = frag_notes;
			tag = Keys.Note.LIST;
			break;
		case TASKS:
			if ((frag_tasks = (TasksFragment) fm
					.findFragmentByTag(Keys.Task.LIST)) == null)
				frag_tasks = new TasksFragment();
			nextPage = frag_tasks;
			tag = Keys.Task.LIST;
			break;
		case PROFILE:
			if ((frag_profile = (ProfileFragment) fm
					.findFragmentByTag("profile")) == null)
				frag_profile = new ProfileFragment();
			nextPage = frag_profile;
			tag = "profile";
			break;
		case GROUPS:
			if ((frag_groups = (GroupsFragment) fm
					.findFragmentByTag(Keys.Group.LIST)) == null)
				frag_groups = new GroupsFragment();
			nextPage = frag_groups;
			tag = Keys.Group.LIST;
			break;
		case PROJECTS:
			nextPage = new DummyFragment();
			args.putString("Content", "TODO: Projects Page");
			nextPage.setArguments(args);
			break;
		case CONTACTS:
			// fragment = new DummyFragment();
			// args.putString("Content", "TODO: Groups Page");
			// fragment.setArguments(args);
			if ((frag_contacts = (UserListFragment) fm
					.findFragmentByTag("contacts")) == null)
				frag_contacts = new UserListFragment();
			nextPage = frag_contacts;
			tag = "contacts";
			break;
		case SETTINGS:
			// args.putString("Content", "TODO: Settings Page");
			// fragment.setArguments(args);
			if ((frag_settings = (SearchableUserFragment) fm
					.findFragmentByTag("settings")) == null)
				frag_settings = SearchableUserFragment.getInstance();
			nextPage = frag_settings;
			tag = "settings";
			break;
		case ABOUT:
			nextPage = new DummyFragment();
			args.putString("Content", "TODO: About Page");
			nextPage.setArguments(args);
			break;
		case LOGOUT:
			logout();
			break;
		default:
			Log.e(TAG + "drawerClicked", clickedLabel.toString()
					+ " is not a valid page");
			break;
		}

		if (nextPage != null) {
			FragmentTransaction ft = fm.beginTransaction();
			// if (currentPage != null)
			// ft.hide(currentPage);

//			if (fm.findFragmentByTag(tag) == null) {
//				ft.add(R.id.content_frame, nextPage, tag).commit();
//			} else {
				// ft.show(fm.findFragmentByTag(tag));
				// Insert the fragment by replacing any existing fragment
				fm.beginTransaction()
						.replace(R.id.content_frame, nextPage, tag).commit();
//			}

			// Highlight the selected item, update the title, and close the
			// drawer
			leftDrawerList.setItemChecked(position, true);
			leftDrawerList.setSelection(position);
			setTitle(leftDrawerItemNames[position]);
			drawerLayout.closeDrawer(leftDrawerList);
		} else {
			// error in creating fragment
			Log.e(TAG, "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void logout() {
		session.logoutUser();
		// clear local database
		SQLiteHelper mySQLiteHelper = SQLiteHelper
				.getInstance(MainActivity.this);
		mySQLiteHelper.onUpgrade(mySQLiteHelper.getReadableDatabase(), 1, 1);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "Resuming");
		if (session == null) {
			session = SessionManager.getInstance();
		}
		selectItem(session.getPage());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle other action bar items...
		switch (item.getItemId()) {
		case R.id.action_refresh:
			switch (DrawerLabel.values()[session.getPage()]) {
			case MEETINGS:
				Toast.makeText(this, "Refreshing Meetings", Toast.LENGTH_SHORT)
						.show();
				// meetingsFrag.fetchMeetings();
				frag_meetings.populateList();
				return true;
			case NOTES:
				Toast.makeText(this, "Refreshing Notes", Toast.LENGTH_SHORT)
						.show();
				// notesFrag.fetchNotes();
				frag_notes.populateList();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}

		case R.id.action_new_meeting:
			frag_meetings.editMeeting(null);
			return true;
		case R.id.action_new_note:
			Intent createNote = new Intent(this, CreateNoteActivity.class);
			startActivityForResult(createNote, 3);
			return true;
		case R.id.action_logout:
			logout();
			return true;
		case R.id.action_settings:
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
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			Fragment saved = getSupportFragmentManager().getFragment(
					savedInstanceState, "fragment");
			if (saved != null) {
			
			}
		}
	}

	private class LeftDrawerClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@Override
	public void onPreExecute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressUpdate(int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostExecute() {
		// TODO Auto-generated method stub

	}
}
