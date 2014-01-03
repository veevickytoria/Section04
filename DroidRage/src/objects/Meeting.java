package objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Meeting {
	private static int uniqueID;
	private String id;
	private String title;
	private String location;
	private String datetimeStart;
	private String datetimeEnd;
	private String description;
	private List<String> attendance = new ArrayList<String>();

	public static SimpleDateFormat serverDateFormat = new SimpleDateFormat(
			"EEEE, d-MMM-yy HH:mm:ss zzz", Locale.US);

	public Meeting() {
		this(-1, "New Meeting", "Location", "Soon");

	}

	public Meeting(int id, String title, String location, String datetime) {
		++uniqueID;
		if (id < 0 || id < uniqueID)
			this.id = "" + uniqueID;
		else
			this.id = "" + id;
		setTitle(title);
		setLocation(location);
		setDatetimeStart(datetime);
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setID(String id) {
		this.id = id;

	}

	public String getDatetimeStart() {
		return datetimeStart;
	}

	public void setDatetimeStart(String datetimeStart) {
		this.datetimeStart = datetimeStart;
	}

	public String getDatetimeEnd() {
		return datetimeEnd;
	}

	public void setDatetimeEnd(String datetimeEnd) {
		this.datetimeEnd = datetimeEnd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getAttendance() {
		return attendance;
	}

	public void setAttendance(List<String> attendance) {
		this.attendance = attendance;
	}

	public void addAttendee(String userID) {
		this.attendance.add(userID);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append(String.format("ID: %d, ", getId()));
		sb.append(String.format("Title: %s, ", getTitle()));
		sb.append(String.format("Location: %s, ", getLocation()));
		sb.append(String.format("DateTime: %s", getDatetimeStart()));
		sb.append('}');
		return sb.toString();
	}

}
