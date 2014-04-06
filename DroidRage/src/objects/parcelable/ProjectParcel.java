package objects.parcelable;

import objects.Meeting;
import objects.Note;
import objects.Project;
import objects.User;
import android.os.Parcel;
import android.os.Parcelable;

public class ProjectParcel extends DataParcel<Project> {

	public ProjectParcel(Project project) {
		super(project);
	}

	public ProjectParcel(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getProjectID());
		dest.writeString(data.getProjectTitle());
		dest.writeList(data.getMeetings());
		dest.writeList(data.getNotes());
		dest.writeList(data.getMembers());

	}

	@SuppressWarnings("unchecked")
	@Override
	public void readFromParcel(Parcel in) {
		data = new Project();
		data.setProjectID(in.readString());
		data.setProjectTitle(in.readString());
		data.setMeetings(in.readArrayList(Meeting.class.getClassLoader()));
		data.setNotes(in.readArrayList(Note.class.getClassLoader()));
		data.setMembers(in.readArrayList(User.class.getClassLoader()));
	}

	public static final Parcelable.Creator<ProjectParcel> CREATOR = new Parcelable.Creator<ProjectParcel>() {

		@Override
		public ProjectParcel createFromParcel(Parcel in) {
			return new ProjectParcel(in);
		}

		@Override
		public ProjectParcel[] newArray(int size) {
			return new ProjectParcel[size];
		}

	};



}
