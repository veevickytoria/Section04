package objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.android.meetingninja.extras.MyDateUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class Meeting implements Parcelable {
	private String meetingID;
	private String title;
	private String location;
	private String startTime;
	private String endTime;
	private String description;
	private List<AttendeeWrapper> attendance = new ArrayList<AttendeeWrapper>();

	private enum Attendance_Status {
		YES(1), MAYBE(0), NO(-1), NO_RESPONSE(-2);

		Attendance_Status(int stat) {
		}

		@Override
		public String toString() {
			switch (this) {
			case YES:
				return "Yes";
			case NO:
				return "No";
			case MAYBE:
				return "Maybe";
			default:
				break;
			}
			return "No Repsonse";
		}
	}

	public Meeting() {
		// Required empty constructor
		setStartTime(0L);
		setEndTime(1L);
	}

	public Meeting(Parcel in) {
		readFromParcel(in);
	}

	public Meeting(Meeting copyMeeting) {
		setID(copyMeeting.getID());
		setTitle(copyMeeting.getTitle());
		setLocation(copyMeeting.getLocation());
		setStartTime(copyMeeting.getStartTime());
		setEndTime(copyMeeting.getEndTime());
		setDescription(copyMeeting.getDescription());
		setAttendance(copyMeeting.getAttendance());
	}

	public String getID() {
		return meetingID;
	}

	public void setID(String id) {
		int testInt = Integer.parseInt(id);
		setID(testInt);
	}

	public void setID(int id) {
		this.meetingID = Integer.toString(id);
	}

	public String getTitle() {
		return (title != null && !title.isEmpty()) ? title : "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return (location != null && !location.isEmpty()) ? location : "";
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStartTime() {
		return (startTime != null && !startTime.isEmpty()) ? startTime
				: MyDateUtils.SERVER_DATE_FORMAT.format(new Date(0L));
	}

	public long getStartTime_Time() throws ParseException {
		return MyDateUtils.SERVER_DATE_FORMAT.parse(startTime).getTime();
	}

	public void setStartTime(String datetimeStart) {
		this.startTime = datetimeStart;
	}

	public void setStartTime(long msStartTime) {
		Date start = new Date(msStartTime);
		this.startTime = MyDateUtils.SERVER_DATE_FORMAT.format(start);
	}

	public String getEndTime() {
		return (endTime != null && !endTime.isEmpty()) ? endTime
				: MyDateUtils.SERVER_DATE_FORMAT.format(new Date(1L));
	}

	public long getEndTime_Time() throws ParseException {
		return MyDateUtils.SERVER_DATE_FORMAT.parse(endTime).getTime();
	}

	public void setEndTime(String datetimeEnd) {
		this.endTime = datetimeEnd;
	}

	public void setEndTime(long msEndTime) {
		Date end = new Date(msEndTime);
		this.endTime = MyDateUtils.SERVER_DATE_FORMAT.format(end);
	}

	public String getDescription() {
		return (description != null && !description.isEmpty()) ? description
				: "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AttendeeWrapper> getAttendance() {
		return attendance;
	}

	public void setAttendance(List<AttendeeWrapper> attendance) {
		this.attendance = attendance;
	}

	public void addAttendee(String userID) {
		this.attendance.add(new AttendeeWrapper(userID,
				Attendance_Status.NO_RESPONSE));
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getID());
		dest.writeString(getTitle());
		dest.writeString(getLocation());
		dest.writeString(getStartTime());
		dest.writeString(getEndTime());
		dest.writeString(getDescription());
		dest.writeList(getAttendance());

	}

	private void readFromParcel(Parcel in) {
		meetingID = in.readString();
		title = in.readString();
		location = in.readString();
		startTime = in.readString();
		endTime = in.readString();
		description = in.readString();
		attendance = (ArrayList<AttendeeWrapper>) in
				.readArrayList(AttendeeWrapper.class.getClassLoader());
	}

	public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() {

		public Meeting createFromParcel(Parcel in) {
			return new Meeting(in);
		}

		public Meeting[] newArray(int size) {
			return new Meeting[size];
		}

	};

	public class AttendeeWrapper {
		private String _id;
		private Meeting.Attendance_Status _attending;

		public AttendeeWrapper() {
			// empty
		}

		public AttendeeWrapper(String userID, Attendance_Status attending) {
			_id = userID;
			_attending = attending;
		}

		public boolean isAttending() {
			switch (_attending) {
			case YES:
			case MAYBE:
			case NO_RESPONSE:
				return true;
			case NO:
				return false;
			}
			return false;
		}

		public String getID() {
			return _id;
		}

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Meeting [meetingID=");
		builder.append(meetingID);
		builder.append(", title=");
		builder.append(title);
		builder.append(", location=");
		builder.append(location);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}
