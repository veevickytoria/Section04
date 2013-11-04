package testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import objects.Meeting;

import com.droidrage.meetingninja.DatabaseAdapter;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Main {

	private static void register(String user, String pass) {
		try {
			DatabaseAdapter.register(user, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean urlLogin(String username) {
		boolean result = false;
		try {
			result = DatabaseAdapter.urlLogin(username);
		} catch (Exception e) {
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
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		String[] users = new String[] { "cricket", "moorejm", "android", "joe",
				"cricket2", "Android", "aaaa"};
		String user = users[users.length - 1];

		boolean success = urlLogin(user);
		if (success) {
			System.out.println("User: " + user);

			System.out.println("\n" + user +"'s Meetings");
			System.out.println(getMeetings(user) + "\n");
		} else {
			System.err.println(user + " failed to login");
//			register(user, "registerTest");
		}
		
		Meeting m = new Meeting(1, "Yourmom POST", "matts face", new Date().toString());
		 createMeeting(user, m);
	}

}
