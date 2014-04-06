package objects.parcelable;

import objects.Meeting;
import objects.User;
import android.os.Parcel;
import android.os.Parcelable;

public class MeetingParcel extends DataParcel<Meeting> {

	public MeetingParcel(Meeting meeting) {
		super(meeting);
	}

	public MeetingParcel(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getID());
		dest.writeString(data.getTitle());
		dest.writeString(data.getLocation());
		dest.writeString(data.getStartTime());
		dest.writeString(data.getEndTime());
		dest.writeString(data.getDescription());
		dest.writeList(data.getAttendance());

	}

	@SuppressWarnings("unchecked")
	@Override
	public void readFromParcel(Parcel in) {
		data = new Meeting();
		data.setID(in.readString());
		data.setTitle(in.readString());
		data.setLocation(in.readString());
		data.setStartTime(in.readString());
		data.setEndTime(in.readString());
		data.setDescription(in.readString());
		data.setAttendance(in.readArrayList(User.class.getClassLoader()));

	}

	public static final Parcelable.Creator<MeetingParcel> CREATOR = new Parcelable.Creator<MeetingParcel>() {

		@Override
		public MeetingParcel createFromParcel(Parcel in) {
			return new MeetingParcel(in);
		}

		@Override
		public MeetingParcel[] newArray(int size) {
			return new MeetingParcel[size];
		}

	};

}
