package com.droidrage.meetingninja.database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import com.fasterxml.jackson.core.JsonFactory;

public class DatabaseAdapter {

	protected final static String SERVER_NAME = "http://csse371-04.csse.rose-hulman.edu/";
	protected final static String USER_AGENT = "Mozilla/5.0";
	protected final static String CONTENT_TYPE = "application/json";
	protected final static String ACCEPT_TYPE = "application/json";
	protected final static JsonFactory JFACTORY = new JsonFactory();

	protected static void addRequestHeader(URLConnection connection,
			boolean isPost) {
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept", ACCEPT_TYPE);
		connection.setDoOutput(isPost);
		if (isPost) {
			connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		}
	}

	protected static int sendPostPayload(URLConnection connection,
			String payload) throws IOException {
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		return ((HttpURLConnection) connection).getResponseCode();
	}

	protected static String getServerResponse(URLConnection connection)
			throws IOException {
		// Read server response
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// return page contents
		return response.toString();
	}

}
