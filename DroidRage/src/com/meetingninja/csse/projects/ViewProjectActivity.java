package com.meetingninja.csse.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Note;
import objects.Project;
import objects.User;
import objects.parcelable.NoteParcel;
import objects.parcelable.ParcelDataFactory;
import objects.parcelable.UserParcel;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.ProjectDatabaseAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.meetings.EditMeetingActivity;
import com.meetingninja.csse.meetings.MeetingItemAdapter;
import com.meetingninja.csse.meetings.tasks.MeetingsFetcherTask;
import com.meetingninja.csse.notes.EditNoteActivity;
import com.meetingninja.csse.notes.NoteArrayAdapter;
import com.meetingninja.csse.notes.NoteFetcher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ViewProjectActivity extends FragmentActivity implements ActionBar.TabListener {
	protected static final String TAG = ViewProjectActivity.class.getSimpleName();
	ArrayList<String> navItems;
	private static int prevSelectedItem = 0;
	private Project displayedProject;
	private int resultCode = Activity.RESULT_CANCELED;
	private int currentpos=0;
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

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//			displayedProject = new ParcelDataFactory(extras).getProject();
			displayedProject = extras.getParcelable(Keys.Project.PARCEL);

		}


		getActionBar().setTitle(displayedProject.getProjectTitle());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (String name : navItems) {
			getActionBar().addTab(getActionBar().newTab().setText(name).setTabListener(this));
		}
		refreshProject();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_view_project, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			editProject();
			return true;
		case R.id.action_refresh:
			refreshProject();
			return true;
		case R.id.action_edit:
			editTitle();
			return true;
		case R.id.action_delete:
			AlertDialogUtil.deleteDialog(this, "project", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					delete(displayedProject);
				}
			});
			return true;
		case android.R.id.home:
			Intent i = new Intent();
			i.putExtra(Keys.Project.PARCEL, displayedProject);
			resultCode = Activity.RESULT_OK;
			setResult(resultCode, i);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void delete(Project project) {
		new AsyncTask<Project, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Project... params) {
				try {
					return ProjectDatabaseAdapter.deleteProject(params[0]);
				} catch (IOException e) {
					Log.e(TAG, "Error: Unable to delete project");
					Log.e(TAG, e.getLocalizedMessage());
				}
				return false;
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					Toast.makeText(ViewProjectActivity.this, "Project Deleted", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}.execute(this.displayedProject);
	}

	private void refreshProject() {
		new AsyncTask<Project, Void, Project>() {
			@Override
			protected void onPostExecute(Project p) {
				getActionBar().setTitle(p.getProjectTitle());
				displayedProject=p;
				setProjectTab(currentpos);
			}

			@Override
			protected Project doInBackground(Project... params) {
				Project p = new Project();
				try {
					p = ProjectDatabaseAdapter.getProject(params[0].getProjectID());
				} catch (IOException e) {
					Log.e(TAG, "failed to refresh project");
					Log.e(TAG, e.getLocalizedMessage());
				}
				return p;
			}

		}.execute(displayedProject);
	}

	private void editTitle() {
		final EditText title = new EditText(this);
		title.setText(displayedProject.getProjectTitle());
		title.selectAll();
		new AlertDialog.Builder(this).setTitle("Enter a title").setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!title.getText().toString().trim().equals("")) {
					displayedProject.setProjectTitle(title.getText().toString());
					getActionBar().setTitle(displayedProject.getProjectTitle());
					updateProject();
				}
			}
		}).setView(title).show();
	}

	private void editProject() {
		switch (prevSelectedItem) {
		case 0:
			AlertDialogUtil.showTwoOptionsDialog(this,"Select an option","Would you like to create a meeting or select an existing meeting?","Create a meeting", "Select a meeting",new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
					createMeeting();
				}

			}, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
					selectMeeting();
				}
			});
			break;
		case 1:
			AlertDialogUtil.showTwoOptionsDialog(this,"Select an option","Would you like to create a note or select an existing note?","Create a note", "Select a note",new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
					createNote();
				}
			}, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int which) {
					selectNote();
				}
			});
			break;
		case 2:
			// addMember();
			if (memberFrag != null) {
				memberFrag.addContactsOption();
			}
			break;
		default:
			return;
		}
	}

	public void createMeeting() {
		Intent editMeeting = new Intent(this, EditMeetingActivity.class);
		editMeeting.putExtra(EditMeetingActivity.EXTRA_EDIT_MODE, true);
		startActivityForResult(editMeeting, 2);
	}

	public void selectMeeting() {
		MeetingsFetcherTask fetcher = new MeetingsFetcherTask(new AsyncResponse<List<Meeting>>() {
			@Override
			public void processFinish(List<Meeting> result) {
				selectMeeting(result);
			}
		});
		fetcher.execute(SessionManager.getUserID());
	}

	public void selectMeeting(List<Meeting> meetings) {
		final Dialog dlg = new Dialog(this);
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.activity_project_custom_dialog, null);
		ListView lv = (ListView) v.findViewById(R.id.dialog_list);
		final MeetingItemAdapter adpt = new MeetingItemAdapter(this,R.layout.list_item_meeting, meetings);
		lv.setAdapter(adpt);
		Button cancel = (Button) v.findViewById(R.id.button);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parantAdpt, View v,int position, long id) {
				addMeeting(adpt.getItem(position));
				dlg.dismiss();
			}

		});
		dlg.setContentView(v);
		dlg.show();
	}

	public void createNote() {
		Intent createNote = new Intent(this, EditNoteActivity.class);
		createNote.putExtra(Note.CREATE_NOTE, true);
		startActivityForResult(createNote, 3);
	}

	public void selectNote() {
		NoteFetcher fetcher = new NoteFetcher(new AsyncResponse<List<Note>>() {
			@Override
			public void processFinish(List<Note> result) {
				selectNote(result);
			}
		});
		fetcher.execute(SessionManager.getUserID());
	}

	public void selectNote(List<Note> notes) {
		final Dialog dlg = new Dialog(this);
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.activity_project_custom_dialog, null);
		ListView lv = (ListView) v.findViewById(R.id.dialog_list);
		final NoteArrayAdapter adpt = new NoteArrayAdapter(this,R.layout.list_item_meeting, notes);
		lv.setAdapter(adpt);
		Button cancel = (Button) v.findViewById(R.id.button);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parantAdpt, View v,int position, long id) {
				addNote(adpt.getItem(position));
				dlg.dismiss();
			}
		});
		dlg.setContentView(v);
		dlg.show();
	}

	protected void addMember(User user) {
		for (int i = 0; i < displayedProject.getMembers().size(); i++) {
			if (user.getID().equals(displayedProject.getMembers().get(i).getID())) {
				AlertDialogUtil.showErrorDialog(this,"This user is already a member of this project");
				return;
			}
		}
		displayedProject.addMember(user);
		updateProject();
	}

	protected void deleteMember(User user) {
		for (int i = 0; i < displayedProject.getMembers().size(); i++) {
			if (user.getID().equals(displayedProject.getMembers().get(i).getID())) {
				displayedProject.getMembers().remove(i);
				updateProject();
			}
		}
	}

	protected void deleteNote(String noteID) {
		for (int i = 0; i < displayedProject.getNotes().size(); i++) {
			if (noteID.equals(displayedProject.getNotes().get(i).getID())) {
				displayedProject.getNotes().remove(i);
				updateProject();
			}
		}
	}

	protected void addNote(Note note) {
		for(Note note1:displayedProject.getNotes()){
			if(note1.getID().equals(note.getID())){
				Toast.makeText(this, "This note is already added to this project", Toast.LENGTH_LONG).show();
				return;
			}
		}
		displayedProject.addNote(note);
		setProjectTab(1);
		updateProject();
	}

	protected void deleteMeeting(Meeting meeting) {
		for (int i = 0; i < displayedProject.getMeetings().size(); i++) {
			if (meeting.getID().equals(displayedProject.getMeetings().get(i).getID())) {
				displayedProject.getMeetings().remove(i);
				updateProject();
			}
		}
	}

	protected void addMeeting(Meeting meeting) {
		for(Meeting meeting1:displayedProject.getMeetings()){
			if(meeting1.getID().equals(meeting.getID())){
				Toast.makeText(this, "This meeting is already added to this project", Toast.LENGTH_LONG).show();
				return;
			}
		}
		displayedProject.addMeeting(meeting);
		setProjectTab(0);
		updateProject();
	}

	private void updateProject() {
		resultCode = Activity.RESULT_OK;
		new AsyncTask<Project, Void, Void>() {
			@Override
			protected void onPostExecute(Void v) {
				refreshProject();
			}
			@Override
			protected Void doInBackground(Project... params) {
				try {
					ProjectDatabaseAdapter.updateProject(params[0]);
				} catch (IOException e) {
					Log.e("UPDATEPROJECT", "Could not update project");
					Log.e(TAG, e.getLocalizedMessage());
				}
				return null;
			}
		}.execute(displayedProject);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==10){
			Note note = new ParcelDataFactory(data.getExtras()).getNote();
			deleteNote(note.getID());
		}else if(resultCode==11){
			Meeting meeting = new ParcelDataFactory(data.getExtras()).getMeeting();
			deleteMeeting(meeting);
		}
		if (resultCode == Activity.RESULT_OK) {

			if(data!=null){
				if (requestCode == 2) {
					Meeting created = data.getParcelableExtra(Keys.Meeting.PARCEL);
					addMeeting(created);
				} else if (requestCode == 3) {
					addNote(new ParcelDataFactory(data.getExtras()).getNote());
				}
			}
		}

		refreshProject();
	}

	private void setProjectTab(int pos) {
		prevSelectedItem = pos;
		Fragment frag = null;
		FragmentManager fm = getSupportFragmentManager();
		Bundle args = new Bundle();
		switch (pos) {
		case 0:
			meetingFrag = new MeetingsProjectFragment().setProjectController(this);
			frag = meetingFrag;
			args.putParcelableArrayList(Keys.Project.MEETINGS,(ArrayList<Meeting>) displayedProject.getMeetings());
			break;
		case 1:
			notesFrag = new NotesProjectFragment().setProjectController(this);
			frag = notesFrag;
			ArrayList<NoteParcel> list = new ArrayList<NoteParcel>();
			for (Note note : displayedProject.getNotes()) {
				list.add(new NoteParcel(note));
			}
			args.putParcelableArrayList(Keys.Project.NOTES, list);
			break;
		case 2:
			memberFrag = new MemberListFragment().setProjectController(this);
			frag = memberFrag;
			ArrayList<UserParcel> userParcels = new ArrayList<UserParcel>();
			for (User user : displayedProject.getMembers()) {
				userParcels.add(new UserParcel(user));
			}
			args.putParcelableArrayList(Keys.Project.MEMBERS, userParcels);
			break;
		default:
			Log.e("View Project", "Cannot change tab");
			return;
		}
		currentpos = pos;
		if (frag != null) {
			frag.setArguments(args);
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content_frame, frag).commitAllowingStateLoss();
		}
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// do nothing
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		setProjectTab(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// do nothing

	}
}