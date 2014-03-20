package com.meetingninja.csse.test;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import objects.Agenda;
import objects.Topic;
import junit.framework.TestCase;

import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AgendaFlattenTest extends TestCase {

	Agenda agenda;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		agenda = new Agenda();
		agenda.setID("404");
		agenda.setTitle("Agenda Flattening");
	}

	@Test
	public void testAgenda() throws Exception {
		Topic t1 = new Topic("1");
		Topic t2 = new Topic("1.1");
		Topic t5 = new Topic("1.1.1");
		Topic t3 = new Topic("2");
		Topic t4 = new Topic("2.1");
		Topic t6 = new Topic("3");

		t2.addTopic(t5);
		t1.addTopic(t2);

		t3.addTopic(t4);

		agenda.addTopic(t1);
		agenda.addTopic(t2);
		agenda.addTopic(t3);
		agenda.addTopic(t6);

		agenda.pprint();
		ArrayList<Topic> topics = agenda.flatten();
		for (Topic topic : topics) {
			System.out.println(topic);
		}

	}

}
