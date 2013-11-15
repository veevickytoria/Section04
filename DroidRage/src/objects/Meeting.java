package objects;

public class Meeting {
	private static int uniqueID;
	private int id;
	private String title;
	private String location;
	private String datetime;

	public Meeting() {
		this(-1, "New Meeting", "Location", "Soon");

	}

	public Meeting(int id, String title, String location, String datetime) {
		++uniqueID;
		if (id < 0 || id < uniqueID)
			this.id = uniqueID;
		else
			this.id = id;
		setTitle(title);
		setLocation(location);
		setDatetime(datetime);
	}

	public int getId() {
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

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		sb.append(String.format("Title: %s, ", getTitle()));
		sb.append(String.format("ID: %d, ", getId()));
		sb.append(String.format("Location: %s, ", getLocation()));
		sb.append(String.format("DateTime: %s", getDatetime()));
		sb.append('}');
		return sb.toString();
	}

}
