package objects.parcelable;

import objects.Task;
import objects.User;
import android.os.Parcel;
import android.os.Parcelable;

public class TaskParcel extends DataParcel<Task> {

	public TaskParcel(Task task) {
		super(task);
	}

	public TaskParcel(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getTitle());
		dest.writeString(Boolean.toString(data.getIsCompleted()));
		dest.writeString(data.getDescription());
		dest.writeString(data.getEndTime());
		dest.writeString(data.getDateCreated());
		dest.writeString(data.getDateAssigned());
		dest.writeString(data.getCompletionCriteria());
		dest.writeString(data.getAssignedTo());
		dest.writeString(data.getAssignedFrom());
		dest.writeString(data.getCreatedBy());
		dest.writeString(data.getID());
		dest.writeString(data.getType());
		dest.writeList(data.getMembers());

	}

	@SuppressWarnings("unchecked")
	@Override
	public void readFromParcel(Parcel in) {
		data = new Task();
		data.setTitle(in.readString());
		data.setIsCompleted(Boolean.parseBoolean(in.readString()));
		data.setDescription(in.readString());
		data.setEndTime(in.readString());
		data.setCreatedBy(in.readString());
		data.setDateAssigned(in.readString());
		data.setCompletionCriteria(in.readString());
		data.setAssignedTo(in.readString());
		data.setAssignedFrom(in.readString());
		data.setCreatedBy(in.readString());
		data.setID(in.readString());
		data.setType(in.readString());
		data.setMembers(in.readArrayList(User.class.getClassLoader()));

	}

	public static final Parcelable.Creator<TaskParcel> CREATOR = new Parcelable.Creator<TaskParcel>() {

		@Override
		public TaskParcel createFromParcel(Parcel in) {
			return new TaskParcel(in);
		}

		@Override
		public TaskParcel[] newArray(int size) {
			return new TaskParcel[size];
		}

	};

}
