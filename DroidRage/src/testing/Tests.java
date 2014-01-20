package testing;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.meetingninja.extras.MyDateUtils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Tests {

	public static void main(String[] args) throws JsonGenerationException,
			JsonMappingException, IOException {

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

}
