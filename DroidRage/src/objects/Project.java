package objects;

import java.util.ArrayList;
import java.util.List;

import objects.parcelable.NoteParcel;
import objects.parcelable.ParcelDataFactory;
import objects.parcelable.UserParcel;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.meetingninja.csse.database.Keys;

public class Project implements Comparable<Project>, Parcelable{

	private String projectID;
	private String projectTitle;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private List<Note> notes = new ArrayList<Note>();
	private List<User> members = new ArrayList<User>();

	public Project(Parcel in){
		readFromParcel(in);
	}

	public Project() {
		// do nothign
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public void addMember(User user){
		this.members.add(user);
	}

	public void addMeeting(Meeting meeting){
		this.meetings.add(meeting);
	}

	public void addNote(Note note){
		this.notes.add(note);
	}
	@Override
	public int compareTo(Project another) {
		if (another == null) {
			return 1;
		}
		return this.getProjectTitle().compareToIgnoreCase(another.getProjectTitle());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {

		@Override
		public Project createFromParcel(Parcel in) {
			return new Project(in);
		}

		@Override
		public Project[] newArray(int size) {
			return new Project[size];
		}

	};

	@Override
	public boolean equals(Object p){
		if(p instanceof Project){
			return this.getProjectID().equals(((Project) p).getProjectID());
		}
		return false;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(getProjectID());
		dest.writeString(getProjectTitle());
		dest.writeList(getMeetings());
		
		ArrayList<NoteParcel> noteList = new ArrayList<NoteParcel>();
		for (Note note : getNotes()) {
			noteList.add(new NoteParcel(note));
		}
		dest.writeList(noteList);
		ArrayList<UserParcel> userList = new ArrayList<UserParcel>();
		for (User user:getMembers()){
			userList.add(new UserParcel(user));
		}
		dest.writeList(userList);

	}
	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel in){
		projectID = in.readString();
		projectTitle = in.readString();
		meetings = in.readArrayList(Meeting.class.getClassLoader());
		ArrayList<Note> noteParcelList = in.readArrayList(Note.class.getClassLoader());
		for (int i=0;i<noteParcelList.size();i++){
			Bundle extras = new Bundle();
			extras.putParcelable(Keys.Note.PARCEL, (Parcelable) noteParcelList.get(i));
			ParcelDataFactory dataFac = new ParcelDataFactory(extras);
			notes.add(dataFac.getNote());
		}
		ArrayList<User> userParcelList = in.readArrayList(User.class.getClassLoader());
		for (int i=0;i<userParcelList.size();i++){
			Bundle extras = new Bundle();
			extras.putParcelable(Keys.User.PARCEL, (Parcelable) userParcelList.get(i));
			ParcelDataFactory dataFac = new ParcelDataFactory(extras);
			members.add(dataFac.getUser());
		}
	}

}
