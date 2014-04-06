package objects.parcelable;

import com.meetingninja.csse.database.Keys;

import android.os.Bundle;
import objects.Agenda;
import objects.Group;
import objects.Meeting;
import objects.Note;
import objects.Project;
import objects.Task;
import objects.User;

public class ParcelDataFactory {
	private Bundle extras;

	public ParcelDataFactory(Bundle extras) {
		this.extras = extras;
	}

	private Object getDataFromParcel(DataParcel<?> parcel) {
		return (parcel == null) ? parcel : parcel.getData();
	}

	public Agenda getAgenda() {
		String key = Keys.Agenda.PARCEL;
		return (Agenda) getDataFromParcel((AgendaParcel) extras.get(key));

	}

	public Group getGroup() {
		String key = Keys.Group.PARCEL;
		return (Group) getDataFromParcel((GroupParcel) extras.get(key));
	}

	public Meeting getMeeting() {
		String key = Keys.Meeting.PARCEL;
		return (Meeting) getDataFromParcel((MeetingParcel) extras.get(key));
	}

	public Note getNote() {
		String key = Keys.Note.PARCEL;
		return (Note) getDataFromParcel((NoteParcel) extras.get(key));
	}

	public Project getProject() {
		String key = Keys.Project.PARCEL;
		return (Project) getDataFromParcel((ProjectParcel) extras.get(key));
	}

	public Task getTask() {
		String key = Keys.Task.PARCEL;
		return (Task) getDataFromParcel((TaskParcel) extras.get(key));
	}

	public User getUser() {
		String key = Keys.User.PARCEL;
		return (User) getDataFromParcel((UserParcel) extras.get(key));
	}

	public boolean extrasContain(String key) {
		return extras.containsKey(key);

	}

}
