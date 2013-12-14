package testing;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import objects.Meeting;
import objects.User;

import com.droidrage.meetingninja.database.DatabaseAdapter;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Main {

	private static void register(String user, String pass) {

		try {
			DatabaseAdapter.register(user, pass);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static boolean urlLogin(String username) {
		boolean result = false;
		try {
			result = DatabaseAdapter.urlLogin(username);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static boolean jsonLogin(String username) {
		boolean result = false;
		try {
			result = DatabaseAdapter.jsonLogin(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void createMeeting(String user, Meeting m) {
		try {
			DatabaseAdapter.createMeeting(user, m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Meeting> getMeetings(String user) {
		List<Meeting> meetings = new ArrayList<Meeting>();
		try {
			meetings = DatabaseAdapter.getMeetings(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return meetings;
	}

	public static void main(String[] args) throws JsonGenerationException,
			JsonMappingException, IOException {
		String[] users = new String[] { "cricket", "moorejm", "android", "joe",
				"cricket2", "Android", "aaaa" };
		String user = users[users.length - 1];

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

		jsonStringParsing();
		System.out.println();
		jsonToObject();
		System.out.println();
		updateJsonFields();
		System.out.println();
		jsonToMap();

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
