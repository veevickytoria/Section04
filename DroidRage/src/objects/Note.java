package objects;

import java.io.IOException;

import objects.builders.NoteBuilder;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.meetingninja.csse.database.Keys;

@JsonDeserialize(builder = NoteBuilder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "noteID", "createdBy", "title", "description", "content",
		"dateCreated" })
public class Note extends AbstractJSONObject<Note> {

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

	public static final String CREATE_NOTE = "createNote";

	public Note() {
		// Required empty constructor
	}

	public Note(Note copyNote) {
		this.noteID = copyNote.getID();
		this.createdBy = copyNote.getCreatedBy();
		this.title = copyNote.getTitle();
		this.description = copyNote.getDescription();
		this.content = copyNote.getContent();
		this.dateCreated = copyNote.getDateCreated();
	}

	public Note(Cursor crsr) {
		int idxID = crsr.getColumnIndex(Keys._ID);
		int idxTITLE = crsr.getColumnIndex(Keys.Note.TITLE);
		int idxCONTENT = crsr.getColumnIndex(Keys.Note.CONTENT);
		int idxDESC = crsr.getColumnIndex(Keys.Note.DESC);
		int idxCREATOR = crsr.getColumnIndex(Keys.Note.CREATED_BY);
		setID("" + crsr.getInt(idxID));
		setTitle(crsr.getString(idxTITLE));
		setContent(crsr.getString(idxCONTENT));
		setDescription(crsr.getString(idxDESC));
		setCreatedBy(crsr.getString(idxCREATOR));
	}

	public Note(NoteBuilder noteBuilder) {
		this.noteID = noteBuilder.noteID;
		this.createdBy = noteBuilder.createdBy;
		this.title = noteBuilder.title;
		this.description = noteBuilder.description;
		this.content = noteBuilder.content;
		this.dateCreated = noteBuilder.dateCreated;
	}

	@Override
	@JsonProperty("noteID")
	public String getID() {
		return noteID;
	}

	@Override
	@JsonProperty("noteID")
	public void setID(String id) {
		int testInt = Integer.valueOf(id);
		setID(testInt);

	}

	@Override
	@JsonIgnore
	protected void setID(int id) {
		this.noteID = Integer.toString(id);

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

	public void mergeWith(Note selected) {
		setContent(getContent() + "\n" + selected.getContent());

	}

	@Override
	public JsonNode toJSON() throws JsonGenerationException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
