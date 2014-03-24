package objects.parcelable;

import objects.Group;
import android.os.Parcel;
import android.os.Parcelable;

public class GroupParcel implements Parcelable {
	private Group group;

	public GroupParcel(Group group) {
		this.group = group;
	}

	public GroupParcel(Parcel in) {
		readFromParcel(in);
	}

	public Group getGroup() {
		return group;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub

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
