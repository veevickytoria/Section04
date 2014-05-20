package objects.parcelable;

import objects.Group;
import objects.User;
import android.os.Parcel;
import android.os.Parcelable;

public class GroupParcel extends DataParcel<Group> {

	public GroupParcel(Group group) {
		super(group);
	}

	public GroupParcel(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getGroupID());
		dest.writeString(data.getTitle());
		dest.writeList(data.getMembers());

	}

	@SuppressWarnings("unchecked")
	@Override
	public void readFromParcel(Parcel in) {
		data = new Group();
		data.setID(in.readString());
		data.setTitle(in.readString());
		data.setMembers(in.readArrayList(User.class.getClassLoader()));
	}

	public static final Parcelable.Creator<GroupParcel> CREATOR = new Parcelable.Creator<GroupParcel>() {

		@Override
		public GroupParcel createFromParcel(Parcel in) {
			return new GroupParcel(in);
		}

		@Override
		public GroupParcel[] newArray(int size) {
			return new GroupParcel[size];
		}

	};

}
