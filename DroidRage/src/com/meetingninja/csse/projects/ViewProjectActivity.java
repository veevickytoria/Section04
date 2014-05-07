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
import com.meetingninja.csse.meetings.MeetingFetcherTask;
import com.meetingninja.csse.meetings.MeetingItemAdapter;
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

public class ViewProjectActivity extends FragmentActivity implements ActionBar.TabListener {
	ArrayList<String> navItems;
	private static int prevSelectedItem = 0;
	// private ProjectTypeAdapter typeAdapter;
	private Project project;
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

		project = getIntent().getExtras().getParcelable(Keys.Project.PARCEL);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().addTab(getActionBar().newTab().setText("Meetings").setTabListener(this));
		getActionBar().addTab(getActionBar().newTab().setText("Notes").setTabListener(this));
		getActionBar().addTab(getActionBar().newTab().setText("Members").setTabListener(this));
		getActionBar().setTitle(project.getProjectTitle());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		refreshProject();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_new_and_refresh, menu);
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
		case android.R.id.home:
			Intent i = new Intent();
			i.putExtra(Keys.Project.PARCEL, project);
			resultCode = Activity.RESULT_OK;
			setResult(resultCode, i);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refreshProject() {
		new AsyncTask<Project, Void, Project>() {
			@Override
			protected void onPostExecute(Project p) {
				getActionBar().setTitle(p.getProjectTitle());
				project=p;
				setProjectTab(currentpos);
			}

			@Override
			protected Project doInBackground(Project... params) {
				Project p = new Project();
				try {
					p = ProjectDatabaseAdapter.getProject(params[0].getProjectID());
				} catch (IOException e) {
					System.out.println("failed to refresh project");
					e.printStackTrace();
				}
				return p;
			}

		}.execute(project);
	}

	private void editTitle() {
		final EditText title = new EditText(this);
		title.setText(project.getProjectTitle());
		title.selectAll();
		new AlertDialog.Builder(this).setTitle("Enter a title").setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				project.setProjectTitle(title.getText().toString());
				getActionBar().setTitle(project.getProjectTitle());
				updateProject();
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
		MeetingFetcherTask fetcher = new MeetingFetcherTask(new AsyncResponse<List<Meeting>>() {
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
		for (int i = 0; i < project.getMembers().size(); i++) {
			if (user.getID().equals(project.getMembers().get(i).getID())) {
				AlertDialogUtil.showErrorDialog(this,"This user is already a member of this project");
				return;
			}
		}
		project.addMember(user);
		updateProject();
	}

	protected void deleteMember(User user) {
		for (int i = 0; i < project.getMembers().size(); i++) {
			if (user.getID().equals(project.getMembers().get(i).getID())) {
				project.getMembers().remove(i);
				updateProject();
			}
		}
	}

	protected void deleteNote(Note note) {
		for (int i = 0; i < project.getNotes().size(); i++) {
			if (note.getID().equals(project.getNotes().get(i).getID())) {
				project.getNotes().remove(i);
				updateProject();
			}
		}
	}

	protected void addNote(Note note) {
		project.addNote(note);
		setProjectTab(1);
		updateProject();
	}

	protected void deleteMeeting(Meeting meeting) {
		for (int i = 0; i < project.getMeetings().size(); i++) {
			if (meeting.getID().equals(project.getMeetings().get(i).getID())) {
				project.getMeetings().remove(i);
				updateProject();
			}
		}
	}

	protected void addMeeting(Meeting meeting) {
		// new SQLiteMeetingAdapter(this).updateMeeting(meeting);
		project.addMeeting(meeting);
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
					e.printStackTrace();
				}
				return null;
			}
		}.execute(project);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 2) {
				Meeting created = data.getParcelableExtra(Keys.Meeting.PARCEL);
				addMeeting(created);
			} else if (requestCode == 3) {
				addNote(new ParcelDataFactory(data.getExtras()).getNote());
			}
		}
		refreshProject();
	}

	private void setProjectTab(int pos) {
		// Parcel parcel = new Parcel()
		prevSelectedItem = pos;
		Fragment frag = null;
		FragmentManager fm = getSupportFragmentManager();
		Bundle args = new Bundle();
		switch (pos) {
		case 0:
			meetingFrag = new MeetingsProjectFragment().setProjectController(this);
			frag = meetingFrag;
			args.putParcelableArrayList(Keys.Project.MEETINGS,(ArrayList<Meeting>) project.getMeetings());
			break;
		case 1:
			notesFrag = new NotesProjectFragment().setProjectController(this);
			frag = notesFrag;
			ArrayList<NoteParcel> list = new ArrayList<NoteParcel>();
			for (Note note : project.getNotes()) {
				list.add(new NoteParcel(note));
			}
			args.putParcelableArrayList(Keys.Project.NOTES, list);
			break;
		case 2:
			memberFrag = new MemberListFragment().setProjectController(this);
			frag = memberFrag;
			ArrayList<UserParcel> userParcels = new ArrayList<UserParcel>();
			for (User user : project.getMembers()) {
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