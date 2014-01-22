package com.android.meetingninja.test;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.android.meetingninja.extras.MyDateUtils;

import junit.framework.TestCase;

public class DateParsingTest extends TestCase {
	DateTimeFormatter fmt;

	protected void setUp() throws Exception {
		super.setUp();
		fmt = MyDateUtils.JODA_SERVER_DATE_FORMAT;
		fmt = fmt.withZoneUTC();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testDateParsing() {
		DateTime start, end;
		start = end = new DateTime();
		long testStart, testEnd;
		testStart = testEnd = 0L;

		String serverTimeStart = "Saturday, 15-Aug-15 23:59:59 UTC";
		String serverTimeEnd = "Sunday, 16-Aug-15 23:59:59 UTC";

		start = fmt.parseDateTime(serverTimeStart);
		end = fmt.parseDateTime(serverTimeEnd);
		testStart = start.getMillis();
		testEnd = end.getMillis();

		assertTrue(start.isBefore(end));
		assertTrue(end.isAfter(start));
		assertTrue(testStart <= testEnd);
		String parsed = fmt.print(start);
		System.out.println(parsed);
		assertEquals(parsed, fmt.print(new DateTime(testStart)));
		assertEquals(fmt.print(end), fmt.print(new DateTime(testEnd)));
	}

}
