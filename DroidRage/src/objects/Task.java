package objects;

public class Task extends Event {
	private String taskID;

	public Task() {
		// Required empty constructor
	}

	public Task(String name) {
		this.title = name;
	}

	@Override
	public String getID() {
		return this.taskID;
	}

	@Override
	public void setID(int id) {
		this.taskID = Integer.toString(id);

	}

}
