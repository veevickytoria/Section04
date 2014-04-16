package com.meetingninja.csse.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class JsonHelper {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static Object toJSON(Object object) throws JsonProcessingException {
		Map<String, Object> json = new HashMap<String, Object>();
		if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			for (Object key : map.keySet()) {
				json.put(key.toString(), toJSON(map.get(key)));
			}
			return mapper.writeValueAsString(json);
		} else if (object instanceof Iterable) {
			List<Object> list = new ArrayList<Object>();
			for (Object value : ((Iterable<?>) object)) {
				list.add(value);
			}
			return list;
		} else {
			return object;
		}
	}

	public static boolean isEmptyObject(JsonNode object) {
		return object.isNull();
	}

	public static Map<String, Object> getMap(JSONObject object, String key)
			throws JSONException {
		return toMap(object.getJSONObject(key));
	}

	public static Map<String, Object> toMap(JSONObject object)
			throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<?> keys = object.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, fromJson(object.get(key)));
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}

	private static Object fromJson(Object json) throws JSONException {
		if (json == JSONObject.NULL) {
			return null;
		} else if (json instanceof JSONObject) {
			return toMap((JSONObject) json);
		} else if (json instanceof JSONArray) {
			return toList((JSONArray) json);
		} else {
			return json;
		}
	}
}