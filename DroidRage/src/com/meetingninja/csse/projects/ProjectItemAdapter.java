package com.meetingninja.csse.projects;

import java.util.List;

import objects.Project;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meetingninja.csse.R;

public class ProjectItemAdapter extends ArrayAdapter<Project> {
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
		TextView count_meetings;
		TextView count_notes;
		TextView count_members;
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
			viewHolder.title = (TextView) rowView.findViewById(R.id.list_project_title);
			viewHolder.count_meetings = (TextView) rowView.findViewById(R.id.project_num_meetings);
			viewHolder.count_notes = (TextView) rowView.findViewById(R.id.project_num_notes);
			viewHolder.count_members = (TextView) rowView.findViewById(R.id.project_num_members);

			rowView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) rowView.getTag();
		}

		Project project = projects.get(position);

		viewHolder.title.setText(project.getProjectTitle());
		viewHolder.count_meetings.setText(Integer.toString(project.getMeetings().size()));
		viewHolder.count_notes.setText(Integer.toString(project.getNotes().size()));
		viewHolder.count_members.setText(Integer.toString(project.getMembers().size()));

		return rowView;
	}
}
