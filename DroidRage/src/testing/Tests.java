package testing;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import objects.Agenda;
import objects.Group;
import objects.Meeting;
import objects.Topic;
import objects.User;

import com.android.meetingninja.database.MeetingDatabaseAdapter;
import com.android.meetingninja.database.UserDatabaseAdapter;
import com.android.meetingninja.database.UserExistsException;
import com.android.meetingninja.extras.MyDateUtils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Tests {

	static class UserDB {
		private static User registerTest(User user, String pass) {
			User registered = null;
			try {
				registered = UserDatabaseAdapter.register(user, pass);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UserExistsException e) {
				e.printStackTrace();
			}
			return registered;
		}

		private static User getUserInfoTest(String userID) {
			User getUser = new User();
			try {
				getUser = UserDatabaseAdapter.getUserInfo(userID);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return getUser;
		}

		private static String loginTest(String email, String pass) {
			String result = new String();
			try {
				result = UserDatabaseAdapter.login(email, pass);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;

		}

		private static List<User> getAllUsersTest() {
			List<User> result = new ArrayList<User>();
			try {
				result = UserDatabaseAdapter.getAllUsers();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
	}

	static class MeetingDB {
		private static void createMeeting(String user, Meeting m) {
			try {
				MeetingDatabaseAdapter.createMeeting(user, m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private static List<Meeting> getMeetings(String user) {
			List<Meeting> meetings = new ArrayList<Meeting>();
			try {
				meetings = MeetingDatabaseAdapter.getMeetings(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return meetings;
		}

	}

	public static void main(String[] args) throws JsonGenerationException,
			JsonMappingException, IOException {
		// Meeting m = new Meeting(1, "New Meeting", "Location", new
		// Date().toString());
		// createMeeting(user, m);

		// String r = UserDB.registerTest("Ethan","ethan@example.com", "pass");
		// System.out.println(r);

		// String loginTest = UserDB.loginTest("ethan@example.com",
		// "password");
		// System.out.println(loginTest);
		//
		// User registered = UserDB.getUserInfoTest("812");
		// System.out.println(registered);

		// for (User user : allUsers) {
		// System.out.println(user);
		// }
		agendaTest();
		groupTest();
		dateTimeParsingTest();
	}
	
	private static void dateTimeParsingTest() {
		String serverTimeStart = "Saturday, 15-Aug-15 23:59:59 UTC";
		String serverTimeEnd = "Sunday, 16-Aug-15 23:59:59 UTC";

		Date start, end;
		start = end = new Date();
		long testStart, testEnd;
		testStart = testEnd = 0L;

		SimpleDateFormat fmt = MyDateUtils.SERVER_DATE_FORMAT;
		try {
			start = fmt.parse(serverTimeStart);
			end = fmt.parse(serverTimeEnd);
			testStart = start.getTime();
			testEnd = end.getTime();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fmt.format(start));
		System.out.println(testStart);
		System.out.println(fmt.format(new Date(testStart)));
		System.out.println(fmt.format(end));
		System.out.println(testEnd);
		System.out.println(fmt.format(new Date(testEnd)));
	}

	private static void agendaTest() {
		Agenda ag = new Agenda();
		ag.setID(233);
		Topic topic1 = new Topic("1");
		topic1.addTopic(new Topic("1.1"));
		topic1.addTopic(new Topic("1.2"));
		topic1.addTopic(0, new Topic("1.01"));
		Topic topic2 = new Topic("2");
		Topic topic3 = new Topic("3");
		Topic subTopic3 = new Topic("3.1");
		subTopic3.addTopic(new Topic("3.1.1"));
		topic3.addTopic(subTopic3);
		ag.addTopic(topic1);
		ag.addTopic(topic2);
		ag.addTopic(topic3);

		ag.pprint();
	}

	private static void groupTest() {
		User test = new User();
		test.setUserID(123);
		test.setDisplayName("Test");
		Group g = new Group();
		g.addMember(test.toSimpleUser());
		g.setID(100);
		g.setGroupTitle("TestGroup");
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		try {
			System.out.println(writer.writeValueAsString(g));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void updateJsonFields() throws JsonProcessingException,
			IOException {
		String userInfo = "{" + "\"userID\":\"12345\","
				+ "\"displayName\":\"John Jones\","
				+ "\"email\":\"1234@gmail.com\"," + "\"phone\":\"5555555555\","
				+ "\"company\":\"google\"," + "\"title\":\"master planner\","
				+ "\"location\":\"Terre Haute, IN\"" + "}";

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(userInfo);

		((ObjectNode) rootNode).put("displayName", "Mr. Jones");
		((ObjectNode) rootNode).put("company", "microsoft");
		System.out.println(rootNode);

	}

}
