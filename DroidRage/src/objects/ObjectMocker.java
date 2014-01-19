package objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Xml.Encoding;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMocker {
	private final static ObjectMapper MAPPER = new ObjectMapper();
	private final static JsonFactory JFACT = new JsonFactory();

	public static String getMockUser() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockNote() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockTask() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockMeeting() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockComment() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockAgenda() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockNotification() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockProject() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockGroup() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		// TODO

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;
	}

	public static String getMockSchedule() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		JsonGenerator jgen = JFACT.createGenerator(ps, JsonEncoding.UTF8);

		Task task1 = new Task();
		task1.setID(1);
		task1.setTitle("Get the milk");
		task1.setDescription("2% milk from Kroger");
		task1.setStartTime(new Date(2013, 8, 23).getTime());
		task1.setEndTime(new Date(2013, 8, 24).getTime());

		Meeting meeting1 = new Meeting();
		meeting1.setID(2);
		meeting1.setTitle("Computer Science Scrum");
		meeting1.setDescription("Job Placement for new employees");
		meeting1.setStartTime(new Date(2013, 7, 23).getTime());
		meeting1.setEndTime(new Date(2013, 7, 24).getTime());

		jgen.writeStartObject(); // start schedule
		jgen.writeArrayFieldStart("schedule"); // start meeting-task array
		jgen.writeStartObject(); // start task
		jgen.writeStringField("id", task1.getID());
		jgen.writeStringField("type", "task");
		jgen.writeStringField("title", task1.getTitle());
		jgen.writeStringField("description", task1.getDescription());
		jgen.writeStringField("datetimeStart", task1.getStartTime());
		jgen.writeStringField("datetimeEnd", task1.getEndTime());
		jgen.writeEndObject(); // end task
		jgen.writeStartObject(); // start meeting
		jgen.writeStringField("id", meeting1.getID());
		jgen.writeStringField("type", "meeting");
		jgen.writeStringField("title", meeting1.getTitle());
		jgen.writeStringField("description", meeting1.getDescription());
		jgen.writeStringField("datetimeStart", meeting1.getStartTime());
		jgen.writeStringField("datetimeEnd", meeting1.getEndTime());
		jgen.writeEndObject(); // end meeting
		jgen.writeEndArray(); // end meeting-task array
		jgen.writeEndObject(); // end schedule object

		jgen.flush();
		jgen.close();
		String json = baos.toString("UTF8");
		ps.close();
		return json;

	}
}
