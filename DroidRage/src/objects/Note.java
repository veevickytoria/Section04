package objects;

import java.io.IOException;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerationException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "noteID", "createdBy", "title", "description", "content",
		"dateCreated" })
public class Note implements Parcelable, IJSONObject {

	@JsonProperty("noteID")
	private String noteID;
	@JsonProperty("createdBy")
	private String createdBy;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("content")
	private String content;
	@JsonProperty("dateCreated")
	private String dateCreated;

	public Note() {
		// Required empty constructor
	}

	public Note(Note copyNote) {
		this.noteID = copyNote.getNoteID();
		this.createdBy = copyNote.getCreatedBy();
		this.title = copyNote.getTitle();
		this.description = copyNote.getDescription();
		this.content = copyNote.getContent();
		this.dateCreated = copyNote.getDateCreated();
		
	}

	public Note(Parcel in) {
		readFromParcel(in);
	}

	public Note(NoteBuilder noteBuilder) {
		this.noteID = noteBuilder.noteID;
		this.createdBy = noteBuilder.createdBy;
		this.title = noteBuilder.title;
		this.description = noteBuilder.description;
		this.content = noteBuilder.content;
		this.dateCreated = noteBuilder.dateCreated;
	}

	@JsonProperty("noteID")
	public String getNoteID() {
		return noteID;
	}

	@JsonProperty("noteID")
	public void setNoteID(String noteID) {
		int testInt = Integer.valueOf(noteID);
		setNoteID(testInt);
	}

	@JsonIgnore
	private void setNoteID(int noteID) {
		this.noteID = Integer.toString(noteID);
	}

	@JsonProperty("createdBy")
	public String getCreatedBy() {
		return !(createdBy == null || createdBy.isEmpty()) ? createdBy : "";
	}

	@JsonProperty("createdBy")
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@JsonProperty("title")
	public String getTitle() {
		return !(title == null || title.isEmpty()) ? title : "";
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("description")
	public String getDescription() {
		return !(description == null || description.isEmpty()) ? description
				: "";
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("content")
	public String getContent() {
		return !(content == null || content.isEmpty()) ? content : "";
	}

	@JsonProperty("content")
	public void setContent(String content) {
		this.content = content;
	}

	@JsonProperty("dateCreated")
	public String getDateCreated() {
		return !(dateCreated == null || dateCreated.isEmpty()) ? dateCreated
				: "";
	}

	@JsonProperty("dateCreated")
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

		public Note createFromParcel(Parcel in) {
			return new Note(in);
		}

		public Note[] newArray(int size) {
			return new Note[size];
		}

	};

	public void readFromParcel(Parcel in) {
		this.noteID = in.readString();
		this.createdBy = in.readString();
		this.title = in.readString();
		this.description = in.readString();
		this.content = in.readString();
		this.dateCreated = in.readString();

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getNoteID());
		dest.writeString(getCreatedBy());
		dest.writeString(getTitle());
		dest.writeString(getDescription());
		dest.writeString(getContent());
		dest.writeString(getDateCreated());

	}

	public static class NoteBuilder {
		private String noteID;
		private String createdBy;
		private String title;
		private String description;
		private String content;
		private String dateCreated;

		public NoteBuilder() {

		}

		public NoteBuilder id(String id) {
			this.noteID = id;
			return this;
		}

		public NoteBuilder createdBy(String userID) {
			this.createdBy = userID;
			return this;
		}

		public NoteBuilder title(String title) {
			this.title = title;
			return this;
		}

		public NoteBuilder description(String desc) {
			this.description = desc;
			return this;
		}

		public NoteBuilder content(String content) {
			this.content = content;
			return this;
		}

		public NoteBuilder dateModified(String dateTime) {
			this.dateCreated = dateTime;
			return this;
		}
		
		public Note build() {
			return new Note(this);
		}
	}

	@Override
	public String toJSON() throws JsonGenerationException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
