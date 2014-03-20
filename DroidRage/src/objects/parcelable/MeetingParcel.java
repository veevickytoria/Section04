package objects.parcelable;

import objects.Meeting;
import android.os.Parcel;
import android.os.Parcelable;

public class MeetingParcel implements Parcelable {
	private Meeting meeting;

	public MeetingParcel(Meeting meeting) {
		this.meeting = meeting;
	}

	public MeetingParcel(Parcel in) {
		readFromParcel(in);
	}

	public Meeting getMeeting() {
		return meeting;
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

	public static final Parcelable.Creator<MeetingParcel> CREATOR = new Parcelable.Creator<MeetingParcel>() {

		@Override
		public MeetingParcel createFromParcel(Parcel in) {
			return new MeetingParcel(in);
		}

		@Override
		public MeetingParcel[] newArray(int size) {
			return new MeetingParcel[size];
		}

	};

}
