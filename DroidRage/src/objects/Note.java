package objects;

import java.io.IOException;

import objects.builders.NoteBuilder;
import android.database.Cursor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.Keys;

public class Note extends AbstractJSONObject<Note> implements Comparable<Note>{

	private String noteID;
	private String createdBy;
	private String title;
	private String description;
	private String content;
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
	public String getID() {
		return noteID;
	}

	@Override
	public void setID(String id) {
		this.noteID = id;
	}

	@Override
	@JsonIgnore
	protected void setID(int id) {
		this.noteID = Integer.toString(id);
	}

	public String getCreatedBy() {
		return !(createdBy == null || createdBy.isEmpty()) ? createdBy : "";
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getTitle() {
		return !(title == null || title.isEmpty()) ? title : "";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return !(description == null || description.isEmpty()) ? description: "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return !(content == null || content.isEmpty()) ? content : "";
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDateCreated() {
		return !(dateCreated == null || dateCreated.isEmpty()) ? dateCreated: "";
	}

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

	@Override
	public int compareTo(Note another) {
		if (another == null) {
			return 1;
		}
		return getTitle().compareToIgnoreCase(another.getTitle());
	}

}
