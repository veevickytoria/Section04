package com.meetingninja.csse.group.tasks;

public class GroupDeleteTask {
	private AsyncGroupDeleteTask deleter;

	public GroupDeleteTask() {
		this.deleter = new AsyncGroupDeleteTask();
	}

	public void deleteGroup(String groupID) {
		this.deleter.execute(groupID);
	}

}
