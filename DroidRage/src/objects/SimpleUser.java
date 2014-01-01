package objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userID", "displayName" })
public class SimpleUser {
	@JsonProperty("userID")
	protected String userID;
	@JsonProperty("displayName")
	protected String displayName;

	@JsonProperty("userID")
	public String getUserID() {
		return userID;
	}

	@JsonProperty("userID")
	public void setUserID(String id) {
		int testInt = Integer.parseInt(id);
		setUserID(testInt);
	}
	
	@JsonProperty("userID")
	public void setUserID(int id) {
		this.userID = Integer.toString(id);
	}

	@JsonProperty("displayName")
	public String getDisplayName() {
		return displayName;
	}

	@JsonProperty("displayName")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
