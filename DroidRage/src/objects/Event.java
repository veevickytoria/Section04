package objects;

import org.joda.time.DateTime;

import com.meetingninja.csse.extras.NinjaDateUtils;
import com.meetingninja.csse.extras.NinjaTextUtils;

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
		return (!NinjaTextUtils.isEmpty(title)) ? title : "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStartTime() {
		return (!NinjaTextUtils.isEmpty(startTime)) ? startTime
				: NinjaDateUtils.JODA_SERVER_DATE_FORMAT.print(new DateTime(0L));
	}

	public long getStartTimeInMillis() {
		return (!NinjaTextUtils.isEmpty(startTime)) ? Long.parseLong(startTime) : 0L;
	}

	public void setStartTime(String datetimeStart) {
		this.startTime = datetimeStart;
	}

	public void setStartTime(long msStartTime) {
		this.startTime = Long.toString(msStartTime);
	}

	public String getEndTime() {
		return (!NinjaTextUtils.isEmpty(endTime)) ? endTime
				: NinjaDateUtils.JODA_SERVER_DATE_FORMAT.print(new DateTime(1L));
	}

	public long getEndTimeInMillis() {
		return (!NinjaTextUtils.isEmpty(endTime)) ? Long.parseLong(endTime) : 0L;
	}

	public void setEndTime(String datetimeEnd) {
		this.endTime = datetimeEnd;
	}

	public void setEndTime(long msEndTime) {
		this.endTime = Long.toString(msEndTime);
	}

	public String getDescription() {
		return (!NinjaTextUtils.isEmpty(description)) ? description : "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(Event another) {
		if (another == null) {
			return 1;
		}
		return Long.valueOf(getStartTime()).compareTo(
				Long.valueOf(another.getStartTime()));
	}
}
