package objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import objects.Meeting.AttendeeWrapper;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.OnClickListener;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userID", "displayName", "email", "phone", "company",
		"title", "location" })
public class User extends SimpleUser implements Parcelable, IJSONObject {
	private String email;
	private String phone;
	private String company;
	private String title;
	private String location;

	public User() {
		// required empty constructor
	}

	public User(int userID) {
		super.setUserID(userID);
	}

	public User(User copyUser) {
		super.setUserID((copyUser.getUserID() != null ? copyUser.getUserID()
				: "" + 0));
		super.setDisplayName(copyUser.getDisplayName());
		setEmail(copyUser.getEmail());
		setPhone(copyUser.getPhone());
		setCompany(copyUser.getCompany());
		setTitle(copyUser.getTitle());
		setLocation(getLocation());
	}

	public User(Parcel in) {
		readFromParcel(in);
	}

	/* Required Fields */

	@Override
	public String getUserID() {
		return super.getUserID();
	}

	@Override
	public void setUserID(String id) {
		super.setUserID(id);
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/* Optional fields */

	public String getPhone() {
		return (phone != null && !phone.isEmpty()) ? phone : "";
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return (company != null && !company.isEmpty()) ? company : "";
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getTitle() {
		return (title != null && !title.isEmpty()) ? title : "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return (location != null && !location.isEmpty()) ? location : "";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("***** User Details *****\n");
		builder.append("getUserID()\t");
		builder.append(getUserID() + "\n");
		builder.append("getDisplayName()\t");
		builder.append(getDisplayName() + "\n");
		builder.append("getEmail()\t");
		builder.append(getEmail() + "\n");
		builder.append("getPhone()\t");
		builder.append(getPhone() + "\n");
		builder.append("getCompany()\t");
		builder.append(getCompany() + "\n");
		builder.append("getTitle()\t");
		builder.append(getTitle() + "\n");
		builder.append("getLocation()\t");
		builder.append(getLocation() + "\n");
		builder.append("************************");
		return builder.toString();
	}

	public SimpleUser toSimpleUser() {
		SimpleUser simple = new SimpleUser();
		simple.setUserID(this.userID);
		simple.setDisplayName(this.displayName);
		return simple;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getUserID());
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

		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}

	};

	public String toJSON() throws JsonGenerationException, IOException {
		ByteArrayOutputStream _json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(_json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = new JsonFactory().createGenerator(ps,
				JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField("userID", getUserID());
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
		return json;
	}
}
