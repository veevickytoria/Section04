package com.meetingninja.csse.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import objects.Project;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.ProjectDatabaseAdapter;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.extras.IRefreshable;

import de.timroes.android.listview.EnhancedListView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectFragment extends Fragment implements IRefreshable {

	protected static final String TAG = ProjectFragment.class.getSimpleName();
	private static ProjectFragment sInstance = null;
	private List<Project> projectsList = new ArrayList<Project>();
	private ProjectItemAdapter projectAdpt;
	private EnhancedListView l;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		SessionManager.getInstance();
		View v = inflater.inflate(R.layout.fragment_project, container, false);
		setUpListView(v);

		refresh();
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ViewProjectActivity.REQUEST_CODE - 1) {
				refresh();
			} else if (requestCode == ViewProjectActivity.REQUEST_CODE) {
				Project p = data.getParcelableExtra(Keys.Project.PARCEL);
				projectsList.add(p);
				Collections.sort(projectsList);
				projectAdpt.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refresh();
			return true;
		case R.id.action_new:
			createProjectOption();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void setProjects(List<Project> listOfProjects) {
		projectsList.clear();
		projectsList.addAll(listOfProjects);
		projectAdpt.notifyDataSetChanged();
	}

	public void createProjectOption() {
		final EditText title = new EditText(getActivity());
		new AlertDialog.Builder(getActivity()).setTitle("Enter a title")
				.setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!title.getText().toString().trim().equals("")) {
							createProject(title.getText().toString());
						} else {
							Toast.makeText(getActivity(),
									"Project Title can't be empty",
									Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setView(title).show();
	}

	public void createProject(String title) {
		Project project = new Project();
		project.setProjectTitle(title);
		new AsyncTask<Project, Void, Project>() {
			@Override
			protected void onPostExecute(Project p) {
				super.onPostExecute(p);
				Intent i = new Intent(getActivity(), ViewProjectActivity.class);
				i.putExtra(Keys.Project.PARCEL, p);
				startActivityForResult(i, ViewProjectActivity.REQUEST_CODE);
				// TODO: check if you add yourself to a project if it
				// automatically updates fragment list
			}

			@Override
			protected Project doInBackground(Project... params) {
				Project p = new Project();
				try {
					p = ProjectDatabaseAdapter.createProject(params[0]);
				} catch (IOException e) {
					Log.e(TAG, "failed to create project");
					e.printStackTrace();
				}
				return p;
			}
		}.execute(project);
	}

	@Override
	public void refresh() {

		new AsyncTask<String, Void, List<Project>>() {
			@Override
			protected void onPostExecute(List<Project> projectList) {
				super.onPostExecute(projectList);
				Collections.sort(projectList);
				setProjects(projectList);
			}

			@Override
			protected List<Project> doInBackground(String... params) {
				List<Project> projectList = new ArrayList<Project>();
				try {
					projectList = UserDatabaseAdapter.getProject(params[0]);
				} catch (IOException e) {
					Log.e(TAG, "failed to get projects");
					Log.e(TAG, e.getLocalizedMessage());
				}
				for (int i = 0; i < projectList.size(); i++) {
					Project p = null;
					try {
						p = ProjectDatabaseAdapter.getProject(projectList.get(i).getProjectID());
					} catch (IOException e) {
						Log.e(TAG, "failed to get project info");
						Log.e(TAG, e.getLocalizedMessage());
					}
					projectList.set(i, p);
				}
				return projectList;
			}

		}.execute(SessionManager.getUserID());

	}

	private void deleteProject(Project p) {

		new AsyncTask<Project, Void, Void>() {
			@Override
			protected void onPostExecute(Void result) {
				refresh();
			}

			@Override
			protected Void doInBackground(Project... params) {
				try {
					ProjectDatabaseAdapter.deleteProject(params[0]);
				} catch (IOException e) {
					Log.e(TAG, "failed to delete project");
					Log.e(TAG, e.getLocalizedMessage());
				}
				return null;
			}
		}.execute(p);
	}

	private void loadProject(Project project) {
		// while (project.getEndTimeInMillis() == 0L);
		Intent viewProject = new Intent(getActivity(), ViewProjectActivity.class);
		viewProject.putExtra(Keys.Project.PARCEL, project);
		startActivityForResult(viewProject, ViewProjectActivity.REQUEST_CODE - 1);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}

	private void setUpListView(View v) {
		projectAdpt = new ProjectItemAdapter(getActivity(),
				R.layout.list_item_task, projectsList);

		l = (EnhancedListView) v.findViewById(R.id.project_list);
		l.setAdapter(projectAdpt);
		l.setEmptyView(v.findViewById(android.R.id.empty));

		l.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
			@Override
			public EnhancedListView.Undoable onDismiss(
					EnhancedListView listView, final int position) {

				final Project item = projectAdpt.getItem(position);
				projectsList.remove(item);

				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						projectsList.add(item);
						projectAdpt.notifyDataSetChanged();
					}

					@Override
					public String getTitle() {
						return "Project Deleted";
					}

					@Override
					public void discard() {
						deleteProject(item);
					}
				};
			}
		});
		l.setRequireTouchBeforeDismiss(false);
		l.setUndoHideDelay(5000);
		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Project p = projectAdpt.getItem(position);
				loadProject(p);
			}

		});
		l.enableSwipeToDismiss();
		l.setSwipingLayout(R.id.list_group_item_frame_1);

		l.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
	}

}

class ProjectItemAdapter extends ArrayAdapter<Project> {
	private List<Project> projects;
	private Context context;

	public ProjectItemAdapter(Context context, int textViewResourceId,
			List<Project> projects) {
		super(context, textViewResourceId, projects);
		this.context = context;
		this.projects = projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
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
		TextView title;
	}

	ViewHolder viewHolder;

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_project, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView
					.findViewById(R.id.list_project_title);

			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		// Setup from the meeting_item XML file
		Project project = projects.get(position);

		viewHolder.title.setText(project.getProjectTitle());

		return rowView;
	}
}