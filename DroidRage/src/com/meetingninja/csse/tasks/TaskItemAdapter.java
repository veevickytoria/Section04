package com.meetingninja.csse.tasks;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.Task;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.extras.NinjaDateUtils;


public class TaskItemAdapter extends ArrayAdapter<Task> {
	private List<Task> tasks;
	private Context context;
	private Boolean colorit;

	public TaskItemAdapter(Context context, int textViewResourceId,	List<Task> tasks, Boolean colorit) {
		super(context, textViewResourceId, tasks);
		this.context = context;
		this.tasks = tasks;
		this.colorit=colorit;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void sort(Comparator<? super Task> c) {
		Collections.sort(tasks);
	}

	@Override
	public int getCount() {
		return this.tasks.size();
	}

	@Override
	public Task getItem(int position) {
		return this.tasks.get(position);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_task, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) rowView.findViewById(R.id.list_task_title);
			viewHolder.deadline = (TextView) rowView.findViewById(R.id.list_task_deadline);
			viewHolder.background = rowView.findViewById(R.id.list_task_holder);

			rowView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) rowView.getTag();

		Task task = tasks.get(position);

		viewHolder.title.setText(task.getTitle());
		viewHolder.deadline.setText("Deadline:  "+ NinjaDateUtils.JODA_APP_DATE_FORMAT.print(task.getEndTimeInMillis()));


		if(colorit){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(task.getEndTimeInMillis());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			if (task.getEndTimeInMillis() == 0L) {

			} else if (task.getIsCompleted()) {
				viewHolder.background.setBackgroundColor(Color.rgb(53, 227, 111));
			} else if (cal.before(Calendar.getInstance())) {
				viewHolder.background.setBackgroundColor(Color.rgb(255, 51, 51));
			} else {
				viewHolder.background.setBackground(null);
			}
		}
		return rowView;
	}

}