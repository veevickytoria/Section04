package objects.parcelable;

import objects.User;
import android.os.Parcel;
import android.os.Parcelable;

public class UserParcel extends DataParcel<User> {

	public UserParcel(User user) {
		super(user);
	}

	public UserParcel(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getID());
		dest.writeString(data.getDisplayName());
		dest.writeString(data.getEmail());
		dest.writeString(data.getPhone());
		dest.writeString(data.getCompany());
		dest.writeString(data.getTitle());
		dest.writeString(data.getLocation());

	}

	@Override
	public void readFromParcel(Parcel in) {
		data = new User();
		data.setID(in.readString());
		data.setDisplayName(in.readString());
		data.setEmail(in.readString());
		data.setPhone(in.readString());
		data.setCompany(in.readString());
		data.setTitle(in.readString());
		data.setLocation(in.readString());
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
