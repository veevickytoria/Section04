package objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "userID", "displayName" })
public class SimpleUser implements Parcelable{
	@JsonProperty("userID")
	protected String userID;
	@JsonProperty("displayName")
	protected String displayName;
	
	public SimpleUser(){
	}
	
	public SimpleUser(Parcel in){
		readFromParcel(in);
	}

	@JsonProperty("userID")
	public String getUserID() {
		return userID;
	}

	@JsonProperty("userID")
	public void setUserID(String id) {
		int testInt = Integer.valueOf(id);
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getUserID());
		dest.writeString(getDisplayName());
	}
	
	public void readFromParcel(Parcel in){
		setUserID(in.readString());
		setDisplayName(in.readString());
	}
	
	public static final Parcelable.Creator<SimpleUser> CREATOR = new Parcelable.Creator<SimpleUser>() {

		@Override
		public SimpleUser createFromParcel(Parcel in) {
			return new SimpleUser(in);
		}

		@Override
		public SimpleUser[] newArray(int size) {
			return new SimpleUser[size];
		}
		
	};
}
