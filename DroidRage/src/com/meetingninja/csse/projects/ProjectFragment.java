package com.meetingninja.csse.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.Project;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.ProjectDatabaseAdapter;
import com.meetingninja.csse.database.UserDatabaseAdapter;


import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectFragment extends Fragment{
	
	
	private static ProjectFragment sInstance= null;
	private List<Project> projectsList = new ArrayList<Project>();
	private ProjectItemAdapter projectAdpt;
	private SessionManager session;
	public ProjectFragment() {
		// Empty
	}
	public static ProjectFragment getInstance() {
		if (sInstance == null) {
			sInstance = new ProjectFragment();
		}
		return sInstance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_project_fragment);
		setHasOptionsMenu(true);
		session = SessionManager.getInstance();
		View v = inflater.inflate(R.layout.fragment_project, container, false);
		
		
		
		ListView lv = (ListView) v.findViewById(R.id.project_list);
		lv.setEmptyView(v.findViewById(android.R.id.empty));
		projectAdpt = new ProjectItemAdapter(getActivity(), R.layout.list_item_task,projectsList);

		lv.setAdapter(projectAdpt);
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,int position, long id) {
//				Intent viewProject = new Intent(getActivity(), ViewProjectActivity.class);
//				startActivity(viewProject);
				Project p = projectAdpt.getItem(position);
				loadProject(p);
			}
		});
		refreshProjects();
		return v;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshProjects();
			return true;
		case R.id.action_new:
//			Intent i = new Intent(getActivity(), EditTaskActivity.class);// yeah shouldn't be this
//			Project p = new Project();
//			i.putExtra(Keys.Project.PARCEL, p);
//			startActivityForResult(i, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	public void setProjects(List<Project> listOfProjects){
		projectsList.clear();
		projectsList.addAll(listOfProjects);
		projectAdpt.notifyDataSetChanged();
	}

	private void refreshProjects() {

		AsyncTask<String,Void,List<Project>> AsyncTaskGetUserProjects = new AsyncTask<String,Void,List<Project>>(){
			@Override
			protected void onPostExecute(List<Project> projectList) {
				super.onPostExecute(projectList);
				setProjects(projectList);
			}
			@Override
			protected List<Project> doInBackground(String... params){
				List<Project> projectList = new ArrayList<Project>();
				try {
					projectList = UserDatabaseAdapter.getProject(params[0]);
				} catch (IOException e) {
					// TODO: elim try/catch?
					e.printStackTrace();
				}
				for(int i=0;i<projectList.size();i++){
					Project p=null;
					try {
						p=ProjectDatabaseAdapter.getProject(projectList.get(i));
					} catch (IOException e) {
						e.printStackTrace();
					}
					projectList.set(i, p);
				}
				return projectList;
			}

		}.execute(session.getUserID());
		
//		System.out.println("projects size: "+ projectsList.size());
//		
//		AsyncTask<Project,Void,Project> AsyncTaskProjectInfo = new AsyncTask<Project,Void,Project>(){
//			@Override
//			protected void onPostExecute(Project project) {
//				super.onPostExecute(project);
//			}
//			@Override
//			protected Project doInBackground(Project... params){
//				System.out.println("getting id as: "+ params[0].getProjectID());
//				Project project = null;
//				try {
//					project = ProjectDatabaseAdapter.getProject(params[0]);
//				} catch (IOException e) {
//					// TODO: elim try/catch?
//					e.printStackTrace();
//				}
//				System.out.println("the project is: "+ project.getMembers().get(0).getEmail());
//				return project;
//			}
//		};
//		
//		for(int i=0;i<projectsList.size();i++){
//			AsyncTaskProjectInfo.execute(projectsList.get(i));
//		}
		
		
	}
	
	private void loadProject(Project project) {
//		while (project.getEndTimeInMillis() == 0L);
		Intent viewProject = new Intent(getActivity(), ViewProjectActivity.class);
		viewProject.putExtra(Keys.Project.PARCEL, project);
		startActivityForResult(viewProject, 6);	
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}

}

	
class ProjectItemAdapter extends ArrayAdapter<Project> {
	private List<Project> projects;
	private Context context;

	public ProjectItemAdapter(Context context, int textViewResourceId,	List<Project> projects) {
		super(context, textViewResourceId, projects);
		this.context = context;
		this.projects = projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	@Override
	public void sort(Comparator<? super Project> c) {
		Collections.sort(projects);
	}

	@Override
	public int getCount() {
		return this.projects.size();
		
	}

	@Override
	public Project getItem(int position) {
		return this.projects.get(position);
	}

	private class ViewHolder {
		TextView title, deadline;
		View background;
	}

	ViewHolder viewHolder;

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {//TODO: yeah change all a dis'
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_task, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) rowView.findViewById(R.id.list_task_title);
			viewHolder.deadline = (TextView) rowView.findViewById(R.id.list_task_deadline);
			viewHolder.background = rowView.findViewById(R.id.list_task_holder);

			rowView.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Setup from the meeting_item XML file
		Project project = projects.get(position);

		viewHolder.title.setText(project.getProjectTitle());
	
		return rowView;
	}
}