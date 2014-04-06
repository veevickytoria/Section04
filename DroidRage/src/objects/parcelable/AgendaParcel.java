package objects.parcelable;

import objects.Agenda;
import objects.Topic;
import android.os.Parcel;
import android.os.Parcelable;

public class AgendaParcel extends DataParcel<Agenda> {

	public AgendaParcel(Agenda agenda) {
		super(agenda);
	}

	public AgendaParcel(Parcel in) {
		super(in);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readFromParcel(Parcel in) {
		data = new Agenda();
		data.setID(in.readString());
		data.setTitle(in.readString());
		data.setTopics(in.readArrayList(Topic.class.getClassLoader()));
		data.setAttachedMeetingID(in.readString());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getID());
		dest.writeString(data.getTitle());
		dest.writeList(data.getTopics());
		dest.writeString(data.getAttachedMeetingID());
	}

	public static final Parcelable.Creator<AgendaParcel> CREATOR = new Parcelable.Creator<AgendaParcel>() {

		@Override
		public AgendaParcel createFromParcel(Parcel in) {
			return new AgendaParcel(in);
		}

		@Override
		public AgendaParcel[] newArray(int size) {
			return new AgendaParcel[size];
		}

	};

}
