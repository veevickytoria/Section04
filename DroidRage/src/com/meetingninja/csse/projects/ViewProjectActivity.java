package com.meetingninja.csse.projects;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Note;
import objects.Project;
import objects.User;

import com.meetingninja.csse.R;
import com.meetingninja.csse.R.layout;
import com.meetingninja.csse.R.menu;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.group.GroupUpdater;
import com.meetingninja.csse.meetings.EditMeetingActivity;
import com.meetingninja.csse.meetings.MeetingsFragment;
import com.meetingninja.csse.notes.EditNoteActivity;
import com.meetingninja.csse.notes.NotesFragment;
import com.meetingninja.csse.user.UserListFragment;

import android.os.Bundle;
import android.os.Parcel;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ViewProjectActivity extends FragmentActivity implements ActionBar.TabListener {
	ArrayList<String> navItems;
	private static int prevSelectedItem = 0;
//	private ProjectTypeAdapter typeAdapter;
	private Project project;
	private int resultCode = Activity.RESULT_CANCELED;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);
		navItems = new ArrayList<String>();
		navItems.add("Meetings");
		navItems.add("Notes");
		navItems.add("Members");
		
		project = getIntent().getExtras().getParcelable(Keys.Project.PARCEL);
		//TODO: type adatper?
//		typeAdapter = new ProjectTypeAdapter(this, navItems);
		
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().addTab(getActionBar().newTab().setText("Meetings").setTabListener(this));
		getActionBar().addTab(getActionBar().newTab().setText("Notes").setTabListener(this));
		getActionBar().addTab(getActionBar().newTab().setText("Members").setTabListener(this));
		getActionBar().setTitle(project.getProjectTitle());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_new_and_refresh, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_new:
			editProject();
			return true;
		case R.id.action_refresh:
			// TaskDeleter deleter = new TaskDeleter();
			// deleter.deleteTask(displayedTask.getID());
			setResult(RESULT_OK);
			finish();
		case android.R.id.home:
			setResult(resultCode);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void editProject(){
		switch(prevSelectedItem){
		case 0:
			AlertDialogUtil.showTwoOptionsDialog(this, "Select an option", "Would you like to create a meeting or select an existing meeting?", 
					"Create a meeting", "Select a meeting", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							createMeeting();
						}
				
			}, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectMeeting();
				}
			});
			break;
		case 1:
			AlertDialogUtil.showTwoOptionsDialog(this, "Select an option", "Would you like to create a note or select an existing note?", 
					"Create a note", "Select a note", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							createNote();
						}
				
			}, new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectNote();
				}
			});
			break;
		case 2:
			addMember();
		default: return;
		}
	}
	
	public void createMeeting(){
		Intent editMeeting = new Intent(this,
				EditMeetingActivity.class);
		editMeeting.putExtra(EditMeetingActivity.EXTRA_EDIT_MODE, true);
		startActivityForResult(editMeeting, 2);
	}
	public void selectMeeting(){
		
	}
	public void createNote(){
		Intent createNote = new Intent(this, EditNoteActivity.class);
		createNote.putExtra(Note.CREATE_NOTE, true);
		startActivityForResult(createNote, 3);
		
	}
	public void selectNote(){
		
	}
	public void addMember(){
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if(requestCode == 2){
				Meeting created = data.getParcelableExtra(Keys.Meeting.PARCEL);
				project.addMeeting(created);
				setProjectTab(0);
			}else if(requestCode == 3){
				Note created = data.getParcelableExtra(Keys.Note.PARCEL);
				project.addNote(created);
				setProjectTab(1);
			}
			if (requestCode == 8) {
				project = data.getParcelableExtra(Keys.Group.PARCEL);
//				GroupUpdater updater = new GroupUpdater();
//				updater.updateGroup(group);
				setProjectTab(prevSelectedItem);
				
			}
		}
	}
	private void setProjectTab(int pos){
//		Parcel parcel = new Parcel()
		prevSelectedItem = pos;
		Fragment frag = null;
		FragmentManager fm = getSupportFragmentManager();
		Bundle args = new Bundle();
		switch(pos){
		case 0:
			frag = new MeetingsFragment();
			args.putParcelableArrayList(Keys.Project.MEETINGS, (ArrayList<Meeting>) project.getMeetings());
			frag.setArguments(args);
			break;
		case 1:
			frag = new NotesFragment();
			args.putParcelableArrayList(Keys.Project.NOTES, (ArrayList<Note>) project.getNotes());
			frag.setArguments(args);
			break;
		case 2:
			frag = new UserListFragment();
			args.putParcelableArrayList(Keys.Project.MEMBERS, (ArrayList<User>) project.getMembers());
			frag.setArguments(args);
			break;
		default:
			Log.e("View Project", "Cannot change tab");
			return;
		}
		if(frag != null){
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content_frame, frag).commitAllowingStateLoss();
		}
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		//do nothing
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		setProjectTab(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		//do nothing
		
	}
}