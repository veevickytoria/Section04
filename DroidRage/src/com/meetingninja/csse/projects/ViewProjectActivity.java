package com.meetingninja.csse.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Note;
import objects.Project;
import objects.User;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.ProjectDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteNoteAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.meetings.EditMeetingActivity;
import com.meetingninja.csse.meetings.MeetingFetcherTask;
import com.meetingninja.csse.meetings.MeetingItemAdapter;
import com.meetingninja.csse.notes.EditNoteActivity;
import com.meetingninja.csse.notes.NoteArrayAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ViewProjectActivity extends FragmentActivity implements ActionBar.TabListener {
	ArrayList<String> navItems;
	private static int prevSelectedItem = 0;
	//	private ProjectTypeAdapter typeAdapter;
	private Project project;
	private int resultCode = Activity.RESULT_CANCELED;
	private MemberListFragment memberFrag;
	private MeetingsProjectFragment meetingFrag;
	private NotesProjectFragment notesFrag;
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
			//			addMember();
			if(memberFrag != null){
				memberFrag.addContactsOption();
			}
			break;
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
		MeetingFetcherTask fetcher = new MeetingFetcherTask(new AsyncResponse<List<Meeting>>(){
			@Override
			public void processFinish(List<Meeting> result) {
				selectMeeting(result);
			}

		});
		fetcher.execute(SessionManager.getInstance().getUserID());
	}

	public void selectMeeting(List<Meeting> meetings){
		final Dialog dlg = new Dialog(this);
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.activity_project_custom_dialog, null);
		ListView lv = (ListView) v.findViewById(R.id.dialog_list);
		final MeetingItemAdapter adpt = new MeetingItemAdapter(this,
				R.layout.list_item_meeting, meetings);
		lv.setAdapter(adpt);
		Button cancel = (Button) v.findViewById(R.id.button);
		cancel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
			
		});
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parantAdpt, View v, int position,
					long id) {
				addMeeting(adpt.getItem(position));
				dlg.dismiss();
			}
			
		});
		dlg.setContentView(v);
		dlg.show();
	}


	public void createNote(){
		Intent createNote = new Intent(this, EditNoteActivity.class);
		createNote.putExtra(Note.CREATE_NOTE, true);
		startActivityForResult(createNote, 3);

	}

	public void selectNote(){
		SQLiteNoteAdapter mySQLiteAdapter = new SQLiteNoteAdapter(this);
		selectNote(mySQLiteAdapter.getAllNotes());

	}
	
	public void selectNote(List<Note> notes){
		final Dialog dlg = new Dialog(this);
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.activity_project_custom_dialog, null);
		ListView lv = (ListView) v.findViewById(R.id.dialog_list);
		final NoteArrayAdapter adpt = new NoteArrayAdapter(this,
				R.layout.list_item_meeting, notes);
		lv.setAdapter(adpt);
		Button cancel = (Button) v.findViewById(R.id.button);
		cancel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
			
		});
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parantAdpt, View v, int position,
					long id) {
				addNote(adpt.getItem(position));
				dlg.dismiss();
			}
			
		});
		dlg.setContentView(v);
		dlg.show();
	}

	protected void addMember(User user){
		for(int i = 0; i < project.getMembers().size(); i++){
			if(user.getID().equals(project.getMembers().get(i).getID())){
				AlertDialogUtil.showErrorDialog(this, "This user is already a member of your group");
				return;
			}
		}
		project.addMember(user);
		updateProject();
	}


	protected void deleteMember(User user){
		for(int i = 0; i < project.getMembers().size(); i++){
			if(user.getID().equals(project.getMembers().get(i).getID())){
				project.getMembers().remove(i);
				updateProject();				
			}
		}
	}

	protected void deleteNote(Note note){
		for(int i = 0; i < project.getNotes().size(); i++){
			if(note.getID().equals(project.getNotes().get(i).getID())){
				project.getNotes().remove(i);
				updateProject();				
			}
		}
	}

	protected void addNote(Note note){
		project.addNote(note);
		setProjectTab(1);
		updateProject();
	}

	protected void deleteMeeting(Meeting meeting){
		for(int i = 0; i < project.getMeetings().size(); i++){
			if(meeting.getID().equals(project.getMeetings().get(i).getID())){
				project.getMeetings().remove(i);
				updateProject();				
			}
		}
	}

	protected void addMeeting(Meeting meeting){
		project.addMeeting(meeting);
		setProjectTab(0);
		updateProject();
	}

	private void updateProject(){
		new AsyncTask<Project, Void, Void>(){

			@Override
			protected Void doInBackground(Project... params) {
				try {
					ProjectDatabaseAdapter.updateProject(params[0]);
				} catch (IOException e) {
					Log.e("UPDATEPROJECT", "Could not update project");
					e.printStackTrace();
				}
				return null;
			}

		}.execute(project);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if(requestCode == 2){
				Meeting created = data.getParcelableExtra(Keys.Meeting.PARCEL);
				addMeeting(created);
			}else if(requestCode == 3){
				Note created = data.getParcelableExtra(Keys.Note.PARCEL);
				addNote(created);
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
			meetingFrag = new MeetingsProjectFragment().setProjectController(this);
			frag = meetingFrag;
			args.putParcelableArrayList(Keys.Project.MEETINGS, (ArrayList<Meeting>) project.getMeetings());
			break;
		case 1:
			notesFrag = new NotesProjectFragment().setProjectController(this);
			frag = notesFrag;
			args.putParcelableArrayList(Keys.Project.NOTES, (ArrayList<Note>) project.getNotes());
			break;
		case 2:
			memberFrag = new MemberListFragment().setProjectController(this);
			frag = memberFrag;
			args.putParcelableArrayList(Keys.Project.MEMBERS, (ArrayList<User>) project.getMembers());
			break;
		default:
			Log.e("View Project", "Cannot change tab");
			return;
		}
		if(frag != null){
			frag.setArguments(args);
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