package objects;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.DateTime;

import android.util.Log;

import com.android.meetingninja.extras.MyDateUtils;

public abstract class Event implements Comparable<Event> {
	protected String title;
	protected String startTime;
	protected String endTime;
	protected String description;

	public Event() {
		// TODO Auto-generated constructor stub
	}

	public abstract String getID();

	protected abstract void setID(int id);

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

	public String getStartTime() {
		return (startTime != null && !startTime.isEmpty()) ? startTime
				: MyDateUtils.JODA_SERVER_DATE_FORMAT.print(new DateTime(0L));
	}

	public long getStartTime_Time() throws ParseException {
		return MyDateUtils.JODA_SERVER_DATE_FORMAT.parseMillis(startTime);
	}

	public void setStartTime(String datetimeStart) {
		this.startTime = datetimeStart;
	}

	public void setStartTime(long msStartTime) {
		this.startTime = MyDateUtils.JODA_SERVER_DATE_FORMAT.print(msStartTime);
	}

	public String getEndTime() {
		return (endTime != null && !endTime.isEmpty()) ? endTime
				: MyDateUtils.JODA_SERVER_DATE_FORMAT.print(new DateTime(1L));
	}

	public long getEndTime_Time() throws ParseException {
		return MyDateUtils.JODA_SERVER_DATE_FORMAT.parseMillis(endTime);
	}

	public void setEndTime(String datetimeEnd) {
		this.endTime = datetimeEnd;
	}

	public void setEndTime(long msEndTime) {
		this.endTime = MyDateUtils.JODA_SERVER_DATE_FORMAT.print(msEndTime);
	}

	public String getDescription() {
		return (description != null && !description.isEmpty()) ? description
				: "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int compareTo(Event another) {
		if (another == null)
			return 1;
		try {
			return Long.valueOf(getStartTime_Time()).compareTo(
					Long.valueOf(another.getStartTime_Time()));
		} catch (ParseException e) {
			Log.e("Meeting compareTo ", e.getLocalizedMessage());
		}
		return 1;
	}
}
