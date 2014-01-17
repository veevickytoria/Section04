package objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Task extends Event implements Parcelable {
	private String taskID;
	private String title;
	private boolean isCompleted;
	private String description;
	private String deadline;
	private String dateCreated;
	private String dateAssigned;
	private String completionCriteria;
	private String assignedTo;
	private String assignedFrom;
	private String createdBy;		//title, description, deadline, compeltion criteria, assigned to
	

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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
