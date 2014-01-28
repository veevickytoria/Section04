package objects.builders;

import objects.Note;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonPOJOBuilder
public class NoteBuilder extends AbstractBuilder<Note> {
	public String noteID;
	public String createdBy;
	public String title;
	public String description;
	public String content;
	public String dateCreated;

	public NoteBuilder() {

	}

	public NoteBuilder withID(String id) {
		this.noteID = id;
		return this;
	}

	public NoteBuilder withCreatedBy(String userID) {
		this.createdBy = userID;
		return this;
	}

	public NoteBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public NoteBuilder withDescription(String desc) {
		this.description = desc;
		return this;
	}

	public NoteBuilder withContent(String content) {
		this.content = content;
		return this;
	}

	public NoteBuilder withDateModified(String dateTime) {
		this.dateCreated = dateTime;
		return this;
	}

	@Override
	public Note build() {
		return new Note(this);
	}
}