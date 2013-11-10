package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.List;

import objects.Note;
import objects.Task;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TasksFragment extends Fragment implements AsyncResponse<List<Note>> {
	private SessionManager session;
	private List<Task> tasks = new ArrayList<Task>();
	private TaskItemAdapter taskAdpt;
	//make tasks adapter
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_tasks, container, false);
		session = new SessionManager(getActivity().getApplicationContext());
		
		refreshTasks();
		
		
		ListView lv = (ListView) v.findViewById(R.id.tasksList);
		taskAdpt = new TaskItemAdapter(getActivity(), R.layout.task_item, tasks);
		
		lv.setAdapter(taskAdpt);
	//	lv.setEmptyView(v.findViewById(R.id.ta))
		registerForContextMenu(lv);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View v,
					int position, long id) {
				Task n = taskAdpt.getItem(position);
				CharSequence descStr = n.getContent().isEmpty() ? "No content"
						: n.getContent();
				String msg = String.format("%s: %s", n.getName(), descStr);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

			}
		});
		
		return v;
	}
	
	private void refreshTasks(){
		for(int i = 0; i<8; i++){
			tasks.add(new Task("task numba " + i));
		}
	}

	@Override
	public void processFinish(List<Note> result) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "testing this", Toast.LENGTH_LONG);
		
	}

}


class TaskItemAdapter extends ArrayAdapter<Task> {
	private List<Task> tasks;
	
	public TaskItemAdapter(Context context, int textViewResourceId,
			List<Task> tasks) {
		super(context, textViewResourceId, tasks);
		this.tasks = tasks;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.note_item, null);
		}

		// Setup from the note_item XML file
		Task task = tasks.get(position);
		if (task != null) {
			TextView taskName = (TextView) v.findViewById(R.id.taskName);
			TextView taskContent = (TextView) v.findViewById(R.id.taskContent);

			if (taskName != null) {
				taskName.setText(task.getName());
			}
			if (taskContent != null) {
				String content = task.getContent();
				int max_length = 200;
				if (content.length() > max_length)
					taskContent.setText(content.substring(0, max_length) + "...");
				else
					taskContent.setText(content);
			}
		}

		return v;
	}
}