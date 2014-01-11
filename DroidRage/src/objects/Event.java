package objects;

import java.text.ParseException;
import java.util.Date;

import com.android.meetingninja.extras.MyDateUtils;

public abstract class Event {
	protected String title;
	protected String location;
	protected String startTime;
	protected String endTime;
	protected String description;
	
	public Event() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract String getID();
	public abstract void setID(int id);
	
	public void setID(String id) {
		int testInt = Integer.valueOf(id);
		setID(testInt);
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
}
