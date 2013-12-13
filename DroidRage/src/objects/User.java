package objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class User {
	private long userID;
	private String displayName;
	private String email;
	private String phone = "";
	private String company = "";
	private String title = "";
	private String location = "";
	private List<Meeting> schedule;
	private List<Task> tasks;

	public long getUserID() {
		return userID;
	}

	public void setUserID(long id) {
		this.userID = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public List<Meeting> getSchedule() {
		return (schedule == null || schedule.isEmpty()) ? new ArrayList<Meeting>() : schedule;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public void setSchedule(List<Meeting> meetingsList) {
		this.schedule = meetingsList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("***** User Details *****\n");
		builder.append("getUserID()\t");
		builder.append(getUserID() + "\n");
		builder.append("getDisplayName()\t");
		builder.append(getDisplayName() + "\n");
		builder.append("getEmail()\t");
		builder.append(getEmail() + "\n");
		if (!getPhone().isEmpty()) {
			builder.append("getPhone()\t");
			builder.append(getPhone() + "\n");
		}
		if (!getCompany().isEmpty()) {
			builder.append("getCompany()\t");
			builder.append(getCompany() + "\n");
		}
		if (!getTitle().isEmpty()) {
			builder.append("getTitle()\t");
			builder.append(getTitle() + "\n");
		}
		if (!getLocation().isEmpty()) {
			builder.append("getLocation()\t");
			builder.append(getLocation() + "\n");
		}
		builder.append("************************");
		return builder.toString();
	}

	public static String toJSON(User user) throws JsonGenerationException,
			IOException {
		ByteArrayOutputStream _json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(_json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = new JsonFactory().createGenerator(ps,
				JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField("userID", Long.toString(user.getUserID()));
		jgen.writeStringField("displayName", user.getDisplayName());
		jgen.writeStringField("email", user.getEmail());
		jgen.writeStringField("phone", user.getPhone());
		jgen.writeStringField("company", user.getCompany());
		jgen.writeStringField("title", user.getTitle());
		jgen.writeStringField("location", user.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String json = _json.toString("UTF8");
		ps.close();
		return json;
	}
}
