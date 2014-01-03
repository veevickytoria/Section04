package objects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
// Generated by http://www.jsonschema2pojo.org/
@JsonPropertyOrder({ "groupID", "groupTitle", "members" })
public class Group {

	@JsonProperty("groupID")
	private String groupID;
	@JsonProperty("groupTitle")
	private String groupTitle;
	@JsonProperty("members")
	private List<SimpleUser> members = new ArrayList<SimpleUser>();

	@JsonProperty("groupID")
	public String getGroupID() {
		return groupID;
	}

	@JsonProperty("groupID")
	public void setID(String id) {
		int testInt = Integer.parseInt(id);
		setID(testInt);
	}

	@JsonProperty("groupID")
	public void setID(int id) {
		this.groupID = Integer.toString(id);
	}

	@JsonProperty("groupTitle")
	public String getGroupTitle() {
		return groupTitle;
	}

	@JsonProperty("groupTitle")
	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	@JsonProperty("members")
	public List<SimpleUser> getMembers() {
		return members;
	}

	@JsonProperty("members")
	public void setMembers(List<SimpleUser> members) {
		this.members = members;
	}

	public void addMember(SimpleUser user) {
		this.members.add(user);
	}

}
