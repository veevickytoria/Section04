package objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schedule {
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private List<Task> tasks = new ArrayList<Task>();

	public Schedule() {
		// Required empty constructor
	}

	public Schedule(Schedule copySchedule) {
		this.meetings = copySchedule.getMeetings();
		this.tasks = copySchedule.getTasks();
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public List<Event> getEvents() {
		List<Event> events = new ArrayList<Event>();
		sort();
		events.addAll(getMeetings());
		events.addAll(getTasks());
		return events;
	}
	
	public void sort() {
		Collections.sort(meetings);
		Collections.sort(tasks);
	}

	public void addMeeting(Meeting m) {
		this.meetings.add(m);
	}

	public void addTask(Task t) {
		this.tasks.add(t);
	}

}
