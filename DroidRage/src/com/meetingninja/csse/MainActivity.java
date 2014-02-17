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
import com.meetingninja.csse.extras.BaseFragment;
import com.meetingninja.csse.group.GroupsFragment;
import com.meetingninja.csse.meetings.MeetingsFragment;
import com.meetingninja.csse.notes.CreateNoteActivity;
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

	// Instances of fragments contained within this activity
	private MeetingsFragment frag_meetings;
	private NotesFragment frag_notes;
	private TasksFragment frag_tasks;
	private ProfileFragment frag_profile;
	private GroupsFragment frag_groups;
	private ProjectFragment frag_project;
	// private ProjectsFragment frag_projects;
	// private ContactsFragment frag_contacts;
	private UserListFragment frag_contacts;
	private SearchableUserFragment frag_settings;

	// Fields local to this activity
	private Bundle icicle;
	private CharSequence mTitle;
	private SessionManager session;
	private boolean isDataCached;
	private static final String KEY_SQL_CACHE = "SQL_DATA_CACHE";

	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SessionManager.getInstance().init(MainActivity.this);
		session = SessionManager.getInstance();

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

			// on first time display view for first nav item
			selectItem(session.getPage());

			// Check to see if data has been cached in the local database
			if (savedInstanceState != null) {
				isDataCached = savedInstanceState.getBoolean(KEY_SQL_CACHE,
						false);
			}
			if (!isDataCached && session.needsSync()) {
				ApplicationController.getInstance().loadUsers();
				isDataCached = true;
				session.setSynced();
			}

			// Track the usage of the application with Parse SDK
			ParseAnalytics.trackAppOpened(getIntent());
			ParseUser parseUser = ParseUser.getCurrentUser();
			if (parseUser != null) {
				ParseInstallation installation = ParseInstallation
						.getCurrentInstallation();
				installation.put("user", parseUser);
				installation.put("userId", parseUser.getObjectId());
				installation.saveEventually();
			}
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

	private void setupRightDrawer(Schedule sched) {
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
							.getInstance().getUserID());
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
		FragmentManager fm = getSupportFragmentManager();
		final Fragment currentPage = fm.findFragmentById(R.id.content_frame);

		Fragment nextPage = null;
		Bundle args = new Bundle();
		String tag = "default";

		DrawerLabel clickedLabel = DrawerLabel.values()[position];
		switch (clickedLabel) {
		case MEETINGS:
			nextPage = new MeetingsFragment();
			frag_meetings = (MeetingsFragment) nextPage;
			break;
		case NOTES:
			nextPage = new NotesFragment();
			frag_notes = (NotesFragment) nextPage;
			break;
		case TASKS:
			// nextPage = new TasksFragment();
			nextPage = TasksFragment.getInstance();
			frag_tasks = new TasksFragment();
			break;
		case PROFILE:
			nextPage = new ProfileFragment();
			frag_profile = (ProfileFragment) nextPage;
			break;
		case GROUPS:
			nextPage = new GroupsFragment();
			frag_groups = (GroupsFragment) nextPage;
			break;
		case PROJECTS:
			nextPage = ProjectFragment.getInstance();
			// args.putString("Content", "TODO: Projects Page");
			// nextPage.setArguments(args);
			frag_project = new ProjectFragment();
			break;
		case CONTACTS:
			// fragment = new DummyFragment();
			// args.putString("Content", "TODO: Groups Page");
			// fragment.setArguments(args);
			nextPage = new UserListFragment();
			break;
		case SETTINGS:
			nextPage = SearchableUserFragment.getInstance();
			// args.putString("Content", "TODO: Settings Page");
			// fragment.setArguments(args);
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

			// if (fm.findFragmentByTag(tag) == null) {
			// ft.add(R.id.content_frame, nextPage, tag).commit();
			// } else {
			// ft.show(fm.findFragmentByTag(tag));
			// Insert the fragment by replacing any existing fragment
			ft.replace(R.id.content_frame, nextPage).commit();
			// }

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

		if (requestCode == 3)
			frag_notes.populateList();

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			ArrayList<String> thingsYouSaid = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (thingsYouSaid.contains("meetings")) {
				selectItem(DrawerLabel.MEETINGS.getPosition());
			} else if (thingsYouSaid.contains("groups")) {
				selectItem(DrawerLabel.GROUPS.getPosition());
			} else if (thingsYouSaid.contains("notes")) {
				selectItem(DrawerLabel.NOTES.getPosition());
			} else if (thingsYouSaid.contains("profile")) {
				selectItem(DrawerLabel.PROFILE.getPosition());
			} else if (thingsYouSaid.contains("tasks")) {
				selectItem(DrawerLabel.TASKS.getPosition());
			} else if (thingsYouSaid.contains("projects")) {
				selectItem(DrawerLabel.PROJECTS.getPosition());
			}
		}
	}

	private void logout() {
		session.logoutUser();
		// clear local database
		SQLiteHelper mySQLiteHelper = SQLiteHelper
				.getInstance(MainActivity.this);
		mySQLiteHelper.onUpgrade(mySQLiteHelper.getReadableDatabase(), 1, 1);
		// disassociate Parse SDK
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.remove("userId");
		installation.remove("user");
		installation.saveInBackground();
		ParseUser.logOut();
		finish();
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
			switch (DrawerLabel.values()[session.getPage()]) {
			case MEETINGS:
				Toast.makeText(this, "Refreshing Meetings", Toast.LENGTH_SHORT)
						.show();
				frag_meetings.fetchMeetings();
				// frag_meetings.populateList();
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
			Intent createNote = new Intent(this, EditNoteActivity.class);
			createNote.putExtra(Note.CREATE_NOTE, true);
			startActivityForResult(createNote, 3);
			return true;
		case R.id.action_logout:
			logout();
			return true;
		case R.id.action_settings:
			return true;
		case R.id.action_speak:
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go to...");
			try {
				startActivityForResult(i, VOICE_RECOGNITION_REQUEST_CODE);

			} catch (Exception e) {
				Toast.makeText(this,
						"Error initializing speech to text engine.",
						Toast.LENGTH_LONG).show();
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
