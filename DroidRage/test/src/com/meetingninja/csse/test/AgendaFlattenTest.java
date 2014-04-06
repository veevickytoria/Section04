package com.meetingninja.csse.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import objects.Agenda;
import objects.Topic;
import junit.framework.TestCase;

import org.robolectric.RobolectricTestRunner;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.JsonUtils;

@RunWith(RobolectricTestRunner.class)
public class AgendaFlattenTest extends TestCase {

	private static final ObjectMapper OBJMAPPER = JsonUtils.getObjectMapper();
	private static final JsonFactory JFACTORY = JsonUtils.getJsonFactory();
	Agenda agenda;
	String userID = "230";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		agenda = new Agenda();
		agenda.setID("404");
		agenda.setTitle("Agenda Flattening");
		agenda.setAttachedMeetingID("123");

	}

	@Test
	public void testAgenda() throws Exception {
		Topic t1 = new Topic("Topic 1");
		Topic t2 = new Topic("Topic 2");
		Topic t3 = new Topic("Topic 2.1");
		Topic t4 = new Topic("Topic 2.2");

		t2.addTopic(t3);
		t2.addTopic(t4);

		agenda.addTopic(t1);
		agenda.addTopic(t2);

		ByteArrayOutputStream json = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(json);
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject(); // start agenda
		jgen.writeStringField(Keys.Agenda.ID, agenda.getID());
		jgen.writeStringField(Keys.Agenda.TITLE, agenda.getTitle());
		jgen.writeStringField(Keys.Agenda.MEETINGID,
				agenda.getAttachedMeetingID());
		jgen.writeStringField(Keys.Agenda.USERID, userID);

		ArrayList<Topic> topics = agenda.getTopics();

		contentBuilder(jgen, topics);

		jgen.writeEndObject(); // end agenda
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		System.out.println(payload);

	}

	private void contentBuilder(JsonGenerator jgen, ArrayList<Topic> topics)
			throws IOException {
		if (topics.isEmpty()) {
			jgen.writeArrayFieldStart("content");
			jgen.writeEndArray();
		} else {
			jgen.writeObjectFieldStart("content");
			for (int i = 0; i < topics.size(); i++) {
				jgen.writeObjectFieldStart("" + (i + 1));

				jgen.writeStringField(Keys.Agenda.TITLE, topics.get(i)
						.getTitle());
				jgen.writeStringField(Keys.Agenda.TIME, topics.get(i).getTime());
				jgen.writeStringField(Keys.Agenda.DESC, "");

				contentBuilder(jgen, topics.get(i).getTopics());

				jgen.writeEndObject();

			}
			jgen.writeEndObject();
		}
	}

}
