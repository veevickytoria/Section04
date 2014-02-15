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
import com.meetingninja.csse.meetings.MeetingsFragment;
import com.meetingninja.csse.notes.NotesFragment;
import com.meetingninja.csse.user.UserListFragment;

import android.os.Bundle;
import android.os.Parcel;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ViewProjectActivity extends FragmentActivity {
	ArrayList<String> navItems;
	private static int prevSelectedItem = 0;
	private ProjectTypeAdapter typeAdapter;
	private Project project;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);
		navItems = new ArrayList<String>();
		navItems.add("Meetings");
		navItems.add("Notes");
		navItems.add("Members");
		
		project = savedInstanceState.getParcelable(Keys.Project.PARCEL);
		//TODO: type adatper?
		typeAdapter = new ProjectTypeAdapter(this, navItems);
		
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setSelectedNavigationItem(prevSelectedItem);
		getActionBar().setListNavigationCallbacks(typeAdapter, new OnNavigationListener(){

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				setProjectTab(itemPosition);
				return false;
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_project, menu);
		return true;
	}
	
	private void setProjectTab(int pos){
//		Parcel parcel = new Parcel()
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
			break;
		default:
			Log.e("View Project", "Cannot change tab");
			return;
		}
		if(frag != null){
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content_frame, frag);
		}
	}

}

class ProjectTypeAdapter implements SpinnerAdapter {
	private Context context;
	private List<String> typeNames;

	public ProjectTypeAdapter(Context context, List<String> typeNames) {
		this.context = context;
		this.typeNames = typeNames;
	}

	@Override
	public int getCount() {
		return this.typeNames.size();
	}

	@Override
	public Object getItem(int position) {
		return this.typeNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView rowView = (TextView) convertView;
		if (rowView == null) {
			rowView = new TextView(this.context);
		}

		rowView.setText(typeNames.get(position));
		rowView.setTextColor(Color.WHITE);

		return rowView;
	}

	@Override
	public int getViewTypeCount() {
		return this.typeNames.size();
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this.typeNames.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView rowView = (TextView) getView(position, convertView, parent);
		rowView.setPadding((int) this.context.getResources().getDimension(R.dimen.activity_horizontal_margin),(int) this.context.getResources().getDimension(R.dimen.activity_vertical_margin),(int) this.context.getResources().getDimension(R.dimen.activity_horizontal_margin),	(int) this.context.getResources().getDimension(R.dimen.activity_vertical_margin));
		return rowView;
	}

}
