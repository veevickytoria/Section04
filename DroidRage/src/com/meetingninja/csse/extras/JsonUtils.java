package com.meetingninja.csse.extras;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

	/**
	 * A singleton instance of an objectmapper for JSON serialization
	 */
	private static ObjectMapper sObjMapper;

	private static JsonFactory sJSONFactory;

	/**
	 * @return An ObjectMapper singleton
	 */
	public static ObjectMapper getObjectMapper() {
		if (sObjMapper == null) {
			sObjMapper = new ObjectMapper();
			sObjMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			sObjMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		}

		return sObjMapper;
	}

	public static JsonFactory getJsonFactory() {
		if (sJSONFactory == null) {
			sJSONFactory = getObjectMapper().getFactory();
		}
		return sJSONFactory;
	}

	/**
	 * Returns empty text instead of null if the node does not contain the key
	 *
	 * @param node
	 * @param key
	 * @return
	 */
	public static String getJSONValue(JsonNode node, String key) {
		return node.hasNonNull(key) ? node.get(key).asText() : "";
	}

}
