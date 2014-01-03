/*******************************************************************************
 * .
 ******************************************************************************/
package testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import objects.Agenda;
import objects.Group;
import objects.Meeting;
import objects.Topic;
import objects.User;

import com.android.meetingninja.database.MeetingDatabaseAdapter;
import com.android.meetingninja.database.UserDatabaseAdapter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Main {

	static class UserDB {
		private static String registerTest(String name, String email,
				String pass) {
			String result = new String();
			try {
				result = UserDatabaseAdapter.register(name, email, pass);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
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

	public static void main(String[] args) throws JsonGenerationException,
			JsonMappingException, IOException {
		// boolean success = urlLogin(user);
		// if (success) {
		// System.out.println("User: " + user);
		//
		// System.out.println("\n" + user + "'s Meetings");
		// System.out.println(getMeetings(user) + "\n");
		// } else {
		// System.err.println(user + " failed to login");
		// register(user, "registerTest");
		// }

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

		final List<User> allUsers = UserDB.getAllUsersTest();
		String id;
		// for (User user : allUsers) {
		// System.out.println(user);
		// }

		id = "" + 765;
		// LinkedHashMap<String, String> map = new LinkedHashMap<String,
		// String>();
		// map.put("phone", "745-891-6123");
		// map.put("email", "   ertert@ertert.com");
		// String s = new String();
		// try {
		// s = UserDatabaseAdapter.update(id, map);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		System.out.println("{'test':'hello'}".replaceAll("\\'", "\\\""));
		// agendaTest();
	}

	private static void agendaTest() {
		Agenda ag = new Agenda();
		ag.setID(233);
		Topic topic1 = new Topic("1");
		// topic1.addTopic(topic1.new Topic("1.1"));
		// topic1.addTopic(topic1.new Topic("1.2"));
		Topic topic2 = new Topic("2");
		Topic topic3 = new Topic("3");
		// Agenda.Topic subTopic3 = topic3.new Topic("3.1");
		// subTopic3.addTopic(subTopic3.new Topic("3.1.1"));
		// topic3.addTopic(subTopic3);
		ag.addTopic(topic1);
		ag.addTopic(topic2);
		ag.addTopic(topic3);

		// ag.pprint();
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

	public static void jsonStringParsing() throws JsonParseException,
			IOException {
		String userInfo = "{" + "\"userID\":\"90876\","
				+ "\"displayName\":\"Cricket\","
				+ "\"email\":\"6584@gmail.com\"," + "\"phone\":\"7777777777\","
				+ "\"company\":\"rhit\"," + "\"title\":\"app developer\","
				+ "\"location\":\"Terre Haute, IN\"" + "}";
		// System.out.println(json);
		JsonFactory fact = new JsonFactory();
		JsonParser parser = fact.createParser(userInfo);
		User u = new User();
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String name = parser.getCurrentName();
			if ("userID".equals(name)) {
				parser.nextToken();
				u.setUserID((parser.getText()));
			} else if ("displayName".equals(name)) {
				parser.nextToken();
				u.setDisplayName(parser.getText());
			} else if ("email".equals(name)) {
				parser.nextToken();
				u.setEmail(parser.getText());
			} else if ("phone".equals(name)) {
				parser.nextToken();
				u.setPhone(parser.getText());
			} else if ("company".equals(name)) {
				parser.nextToken();
				u.setCompany(parser.getText());
			} else if ("title".equals(name)) {
				parser.nextToken();
				u.setTitle(parser.getText());
			} else if ("location".equals(name)) {
				parser.nextToken();
				u.setLocation(parser.getText());
			}
		}
		System.out.println(u);
	}

	private static void jsonToObject() throws JsonParseException,
			JsonMappingException, IOException {
		String userInfo = "{" + "\"userID\":\"12345\","
				+ "\"displayName\":\"John Jones\","
				+ "\"email\":\"1234@gmail.com\"," + "\"phone\":\"5555555555\","
				+ "\"company\":\"google\"," + "\"title\":\"master planner\","
				+ "\"location\":\"Terre Haute, IN\"" + "}";

		User user = new User();
		ObjectMapper objectMapper = new ObjectMapper();

		user = objectMapper.readValue(userInfo, User.class);
		System.out.println(user);
	}

	private static void jsonToMap() throws JsonParseException,
			JsonMappingException, IOException {
		String validMessage = "{\"userID\":\"55\"}";
		String invalidMessage = "{\"errorID\":\"3\",\"errorMessage\":\"invalid username or password\"}";

		String[] msg = new String[] { validMessage, invalidMessage };

		Map<String, String> myMap = new HashMap<String, String>();
		ObjectMapper objectMapper = new ObjectMapper();

		int rand = new Random().nextInt(2);
		String randMsg = msg[rand];
		myMap = objectMapper.readValue(randMsg,
				new TypeReference<HashMap<String, String>>() {
				});
		if (!myMap.containsKey("userID")) {
			System.out.println(myMap.get("errorMessage"));
		} else
			System.out.println(myMap);
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
