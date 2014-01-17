package com.android.meetingninja.database;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import objects.Task;

import android.net.Uri;
import android.util.Log;
public class TaskDatabaseAdapter  extends AbstractDatabaseAdapter{
	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();
	
	protected final static String KEY_ID_LIST = "id";
	protected final static String KEY_ID = "taskID";
	protected final static String KEY_TITLE = "title";
	protected final static String KEY_DESC = "description";
	protected final static String KEY_DEADLINE = "deadline";
	protected final static String KEY_DATECREATED = "dateCreated";
	protected final static String KEY_DATEASSIGNED = "dateAssigned";
	protected final static String KEY_COMPCRIT = "complitionCriteria";
	protected final static String KEY_ASSIGNEDTO = "assignedTo";
	protected final static String KEY_ASSIGNEDFROM = "assignedFrom";
	protected final static String KEY_CREATEDBY = "createdBy";
	protected final static String KEY_ISCOMPLEATED = "isCompleted";
	
	public static String getBaseUrl(){
		return BASE_URL;
	}
	public static Uri.Builder getBaseUri(){
		return Uri.parse(getBaseUrl()).buildUpon();
	}
	
	
	
	
	public static void getTask(Task t) throws JsonParseException, JsonMappingException, IOException{
		//Server URL setup
		String _url = getBaseUri().appendPath("Task").appendPath(t.getID()).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn,false);
		
		//Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		
		//Initialize ObjectMapper
		//List<Task> taskList = new ArrayList<Task>();
		final JsonNode taskNode = MAPPER.readTree(response);
		
		//if(taskArray.isArray()){
		//	for(final JsonNode taskNode : taskArray){
		parseTask(taskNode,t);
		
		//		if(t!=null){
		//			taskList.add(t);
		//		}
		//	}
		//}
		conn.disconnect();
		//return void;
		
	}
	
	public static List<Task> getTasks(String userID) throws JsonParseException, JsonMappingException, IOException{
		//Server URL setup
		String _url = getBaseUri().appendPath("User").appendPath("Tasks").appendPath(userID).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn,false);
		
		//Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		
		//Initialize ObjectMapper
		List<Task> taskList = new ArrayList<Task>();
		final JsonNode taskArray = MAPPER.readTree(response).get("tasks");
		
		if(taskArray.isArray()){
			for(final JsonNode taskNode : taskArray){
				Task t = parseTasks(taskNode);
				if(t!=null){
					taskList.add(t);
				}
			}
		}else{
			System.out.println("bla bla bla");
		}
		
		conn.disconnect();
		return taskList;	
	}
	

	/*public static void createTask(String userID, Task t) throws IOException,MalformedURLException {
		//server url setup
		String _url = getBaseUri().appendPath(userID).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn,true);
		
		//prepare the post for posting
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		//this type of print stream allow us to get a string easily
		PrintStream ps = new PrintStream(json);
		//Create a generator to build the Json string
		JsonGenerator jgen = JFACTORY.createGenerator(ps,JsonEncoding.URF8);
		//Build Json Object
		jgen.writeStartObject();
		jgen.writeStringField("userID",userID);
		jgen.writeStringField(KEY_TITLE, t.getTitle());
		jgen.writeStringField(KEY_DESC, t.getDescription());
		jgen.writeStringField(KEY_;)
		
		
		// TODO Implement this method
		throw new Exception("createNote: Unimplemented");
	}*/
	
	public static Task parseTask(JsonNode node,Task t){
		//start parsing a task
		if (node.hasNonNull(KEY_ID)){
			String id = node.get(KEY_ID).asText();
			
			t.setDescription(node.hasNonNull(KEY_DESC)? node.get(KEY_DESC).asText() : "");
			t.setTitle(node.hasNonNull(KEY_TITLE) ? node.get(KEY_TITLE).asText() : "");
			t.setDeadline(node.hasNonNull(KEY_DEADLINE) ? node.get(KEY_DEADLINE).asText() : "");
			t.setDateCreated(node.hasNonNull(KEY_DATECREATED) ? node.get(KEY_DATECREATED).asText() : "");
			t.setDateAssigned(node.hasNonNull(KEY_DATEASSIGNED)? node.get(KEY_DATEASSIGNED).asText() : "");
			t.setCompletionCriteria(node.hasNonNull(KEY_COMPCRIT) ? node.get(KEY_COMPCRIT).asText() : "");
			t.setAssignedTo(node.hasNonNull(KEY_ASSIGNEDTO) ? node.get(KEY_ASSIGNEDTO).asText() : "");
			t.setAssignedFrom(node.hasNonNull(KEY_ASSIGNEDFROM) ? node.get(KEY_ASSIGNEDFROM).asText() : "");
			t.setCreatedBy(node.hasNonNull(KEY_CREATEDBY) ? node.get(KEY_CREATEDBY).asText() : "");
			t.setIsCompleted(node.hasNonNull(KEY_ISCOMPLEATED)? node.get(KEY_ISCOMPLEATED).asBoolean() : false);
		}else{
			Log.w(TAG, "Parsed null");
			return null;
		}
		return t;
	
	}
	public static Task parseTasks(JsonNode node){
		Task t = new Task(); //start parsing a task
		if (node.hasNonNull(KEY_ID_LIST)){
			String id = node.get(KEY_ID_LIST).asText();
			t.setID(id);
			t.setTitle(node.hasNonNull(KEY_TITLE) ? node.get(KEY_TITLE).asText():"");
		}else{
			Log.w(TAG, "Parsed null");
			return null;
		}
		return t;

	}


}

