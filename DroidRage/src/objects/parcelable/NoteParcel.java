package objects.parcelable;

import objects.Note;
import android.os.Parcel;
import android.os.Parcelable;

public class NoteParcel extends DataParcel<Note> {

	public NoteParcel(Note note) {
		super(note);
	}

	public NoteParcel(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(data.getID());
		dest.writeString(data.getCreatedBy());
		dest.writeString(data.getTitle());
		dest.writeString(data.getDescription());
		dest.writeString(data.getContent());
		dest.writeString(data.getDateCreated());
	}

	@Override
	public void readFromParcel(Parcel in) {
		data = new Note();
		data.setID(in.readString());
		data.setCreatedBy(in.readString());
		data.setTitle(in.readString());
		data.setDescription(in.readString());
		data.setContent(in.readString());
		data.setDateCreated(in.readString());
	}

	public static final Parcelable.Creator<NoteParcel> CREATOR = new Parcelable.Creator<NoteParcel>() {

		@Override
		public NoteParcel createFromParcel(Parcel in) {
			return new NoteParcel(in);
		}

		@Override
		public NoteParcel[] newArray(int size) {
			return new NoteParcel[size];
		}

	};

}
