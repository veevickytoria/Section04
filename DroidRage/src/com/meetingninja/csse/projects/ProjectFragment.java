package com.meetingninja.csse.projects;

import java.util.ArrayList;
import java.util.List;

import objects.Project;
import objects.Task;

import com.meetingninja.csse.R;
import com.meetingninja.csse.R.layout;
import com.meetingninja.csse.R.menu;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.tasks.EditTaskActivity;
import com.meetingninja.csse.tasks.TasksFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectFragment extends Fragment {
	private static ProjectFragment sInstance;
	
	
	
	
	public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_project_fragment);
		setHasOptionsMenu(true);
		View v = inflater.inflate(R.layout.fragment_project, container, false);
		
		ListView lv = (ListView) v.findViewById(R.id.project_list);
		lv.setEmptyView(v.findViewById(android.R.id.empty));
//		projectAdpt = new TaskItemAdapter(getActivity(), R.layout.list_item_task,taskLists.get(iAssigned));
//
//		lv.setAdapter(projectkAdpt);
		registerForContextMenu(lv);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,int position, long id) {
				// Intent viewTask = new Intent(getActivity(),
				// ViewTaskActivity.class);
				// startActivity(viewTask);
//				Project p = projectAdpt.getItem(position);
//				loadProject(p);
			}
		});
		return lv;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshProjects();
			return true;
		case R.id.action_new:
			Intent i = new Intent(getActivity(), EditTaskActivity.class);// yeah shouldn't be this
//			Project p = new Project();
//			i.putExtra(Keys.Project.PARCEL, p);
			startActivityForResult(i, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void refreshProjects() {
		// TODO Auto-generated method stub
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.project, menu);
		return true;
	}
	

	public static ProjectFragment getInstance() {
		if (sInstance == null) {
			sInstance = new ProjectFragment();
		}
		return sInstance;
	}

}
