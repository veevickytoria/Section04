package objects.parcelable;

import objects.Agenda;
import objects.Topic;
import android.os.Parcel;
import android.os.Parcelable;

public class AgendaParcel implements Parcelable {
	private Agenda agenda;

	public AgendaParcel(Agenda agenda) {
		this.agenda = agenda;
	}

	public AgendaParcel(Parcel in) {
		readFromParcel(in);
	}

	public Agenda getAgenda() {
		return this.agenda;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(agenda.getID());
		dest.writeString(agenda.getTitle());
		dest.writeList(agenda.getTopics());
		dest.writeString(agenda.getAttachedMeetingID());

	}

	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.agenda = new Agenda();
		agenda.setID(in.readString());
		agenda.setTitle(in.readString());
		agenda.setTopics(in.readArrayList(Topic.class.getClassLoader()));
		agenda.setAttachedMeetingID(in.readString());

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
