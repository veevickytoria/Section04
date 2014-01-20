package objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "noteID", "createdBy", "title", "description", "content",
		"dateCreated" })
public class Note {

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
	}
}
