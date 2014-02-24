package objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.Keys;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userID", "displayName", "email", "phone", "company",
		"title", "location" })
public class User extends AbstractJSONObject<User> implements Parcelable {
	private String userID;
	private String displayName;
	private String email;
	private String phone;
	private String company;
	private String title;
	private String location;

	public User() {
		// required empty constructor
	}

	public User(User copyUser) {
		setID((copyUser.getID()));
		setDisplayName(copyUser.getDisplayName());
		setEmail(copyUser.getEmail());
		setPhone(copyUser.getPhone());
		setCompany(copyUser.getCompany());
		setTitle(copyUser.getTitle());
		setLocation(getLocation());
	}

	public User(Parcel in) {
		readFromParcel(in);
	}

	public User(Cursor crsr) {
		// get cursor columns
		int idxID = crsr.getColumnIndex(Keys._ID);
		int idxUSERNAME = crsr.getColumnIndex(Keys.User.NAME);
		int idxEMAIL = crsr.getColumnIndex(Keys.User.EMAIL);
		int idxPHONE = crsr.getColumnIndex(Keys.User.PHONE);
		int idxCOMPANY = crsr.getColumnIndex(Keys.User.COMPANY);
		int idxTITLE = crsr.getColumnIndex(Keys.User.TITLE);
		int idxLOCATION = crsr.getColumnIndex(Keys.User.LOCATION);
		// set the fields
		setID("" + crsr.getInt(idxID));
		setDisplayName(crsr.getString(idxUSERNAME));
		setEmail(crsr.getString(idxEMAIL));
		setPhone(crsr.getString(idxPHONE));
		setCompany(crsr.getString(idxCOMPANY));
		setTitle(crsr.getString(idxTITLE));
		setLocation(crsr.getString(idxLOCATION));
	}

	// public User(JsonNode node) {
	// // if they at least have an id, email, and name
	// if (node.hasNonNull(Keys.User.EMAIL) && node.hasNonNull(Keys.User.NAME)
	// // && node.hasNonNull(KEY_ID)
	// ) {
	// String email = node.get(Keys.User.EMAIL).asText();
	// // if their email is in a reasonable format
	// if (!TextUtils.isEmpty(node.get(Keys.User.NAME).asText())
	// && Utilities.isValidEmailAddress(email)) {
	// // set the required fields
	// if (node.hasNonNull(Keys.User.ID))
	// setID(node.get(Keys.User.ID).asText());
	// setDisplayName(node.get(Keys.User.NAME).asText());
	// setEmail(email);
	//
	// // check and set the optional fields
	// setLocation(JsonUtils.getJSONValue(node, Keys.User.LOCATION));
	// setPhone(JsonUtils.getJSONValue(node, Keys.User.PHONE));
	// setCompany(JsonUtils.getJSONValue(node, Keys.User.COMPANY));
	// setTitle(JsonUtils.getJSONValue(node, Keys.User.TITLE));
	// }
	// }
	// }

	/* Required Fields */

	@Override
	public String getID() {
		return this.userID;
	}

	@Override
	public void setID(String id) {
		int testInt = (id != null) ? Integer.parseInt(id) : -1;
		setID(testInt);
	}

	@Override
	protected void setID(int id) {
		this.userID = (id != -1) ? Integer.toString(id) : "";
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/* Optional fields */

	public String getPhone() {
		return !TextUtils.isEmpty(phone) ? phone : "";
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return !TextUtils.isEmpty(company) ? company : "";
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getTitle() {
		return !TextUtils.isEmpty(title) ? title : "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return !TextUtils.isEmpty(location) ? location : "";
	}

	public SerializableUser toSimpleUser() {
		SerializableUser simple = new SerializableUser();
		simple.setID(this.getID());
		simple.setDisplayName(this.getDisplayName());
		simple.setEmail(this.getEmail());
		return simple;
	}
	
	@Override
	public boolean equals(Object u){
		if(u instanceof User){
			return getID().equals(((User) u).getID());
		}
		return false;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getID());
		dest.writeString(getDisplayName());
		dest.writeString(getEmail());
		dest.writeString(getPhone());
		dest.writeString(getCompany());
		dest.writeString(getTitle());
		dest.writeString(getLocation());

	}

	private void readFromParcel(Parcel in) {
		userID = in.readString();
		displayName = in.readString();
		email = in.readString();
		phone = in.readString();
		company = in.readString();
		title = in.readString();
		location = in.readString();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}

	};

	@Override
	public JsonNode toJSON() throws JsonGenerationException, IOException {
		ByteArrayOutputStream _json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(_json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = new JsonFactory().createGenerator(ps,
				JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		if (!(getID() == null || getID().isEmpty()))
			jgen.writeStringField("userID", getID());
		jgen.writeStringField("displayName", getDisplayName());
		jgen.writeStringField("email", getEmail());
		jgen.writeStringField("phone", getPhone());
		jgen.writeStringField("company", getCompany());
		jgen.writeStringField("title", getTitle());
		jgen.writeStringField("location", getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String json = _json.toString("UTF8");
		ps.close();
		// return json;

		return MAPPER.readTree(json);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (!(obj instanceof User))
//			return false;
//		User other = (User) obj;
//		if (company == null) {
//			if (other.company != null)
//				return false;
//		} else if (!company.equals(other.company))
//			return false;
//		if (displayName == null) {
//			if (other.displayName != null)
//				return false;
//		} else if (!displayName.equals(other.displayName))
//			return false;
//		if (email == null) {
//			if (other.email != null)
//				return false;
//		} else if (!email.equals(other.email))
//			return false;
//		if (location == null) {
//			if (other.location != null)
//				return false;
//		} else if (!location.equals(other.location))
//			return false;
//		if (phone == null) {
//			if (other.phone != null)
//				return false;
//		} else if (!phone.equals(other.phone))
//			return false;
//		if (title == null) {
//			if (other.title != null)
//				return false;
//		} else if (!title.equals(other.title))
//			return false;
//		if (userID == null) {
//			if (other.userID != null)
//				return false;
//		} else if (!userID.equals(other.userID))
//			return false;
//		return true;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [getID()=");
		builder.append(getID());
		builder.append(", getDisplayName()=");
		builder.append(getDisplayName());
		builder.append(", getEmail()=");
		builder.append(getEmail());
		builder.append("]");
		return builder.toString();
	}

}
