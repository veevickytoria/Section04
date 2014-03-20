package objects.parcelable;

import objects.User;
import android.os.Parcel;
import android.os.Parcelable;

public class UserParcel implements Parcelable {
	private User user;

	public UserParcel(User user) {
		this.user = user;
	}

	public UserParcel(Parcel in) {
		readFromParcel(in);
	}

	public User getUser() {
		return user;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(user.getID());
		dest.writeString(user.getDisplayName());
		dest.writeString(user.getEmail());
		dest.writeString(user.getPhone());
		dest.writeString(user.getCompany());
		dest.writeString(user.getTitle());
		dest.writeString(user.getLocation());

	}

	private void readFromParcel(Parcel in) {
		user.setID(in.readString());
		user.setDisplayName(in.readString());
		user.setEmail(in.readString());
		user.setPhone(in.readString());
		user.setCompany(in.readString());
		user.setTitle(in.readString());
		user.setLocation(in.readString());
	}

	public static final Parcelable.Creator<UserParcel> CREATOR = new Parcelable.Creator<UserParcel>() {

		@Override
		public UserParcel createFromParcel(Parcel in) {
			return new UserParcel(in);
		}

		@Override
		public UserParcel[] newArray(int size) {
			return new UserParcel[size];
		}

	};

}
