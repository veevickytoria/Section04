package testing;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

		DateTime start, end;
		start = end = new DateTime();
		long testStart, testEnd;
		testStart = testEnd = 0L;

		DateTimeFormatter fmt = MyDateUtils.JODA_SERVER_DATE_FORMAT;
		start = fmt.parseDateTime(serverTimeStart);
		end = fmt.parseDateTime(serverTimeEnd);
		testStart = start.getMillis();
		testEnd = end.getMillis();
		System.out.println(fmt.print(start));
		System.out.println(testStart);
		System.out.println(fmt.print(new DateTime(testStart)));
		System.out.println(fmt.print(end));
		System.out.println(testEnd);
		System.out.println(fmt.print(new DateTime(testEnd)));
	}

}
