package testing;

import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.DatabaseAdapter;
import com.droidrage.meetingninja.Meeting;

public class Main {
	private static final DatabaseAdapter dbAdpt = new DatabaseAdapter();

	public static void main(String[] args) {
		String[] users = new String[] { "cricket", "moorejm", "android", "joe",
				"cricket2"};
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
		Meeting m = new Meeting(1, "Java POST", "Bed", "Naptime");
		// createMeeting(user, m);
	}

	private static void register(String user, String pass) {
		try {
			dbAdpt.register(user, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean urlLogin(String username) {
		boolean result = false;
		try {
			result = dbAdpt.urlLogin(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean jsonLogin(String username) {
		boolean result = false;
		try {
			result = dbAdpt.jsonLogin(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void createMeeting(String user, Meeting m) {
		try {
			dbAdpt.createMeeting(user, m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Meeting> getMeetings(String user) {
		List<Meeting> meetings = new ArrayList<Meeting>();
		try {
			meetings = dbAdpt.getMeetings(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return meetings;
	}

}
