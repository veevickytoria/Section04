package objects;

/**
 * A basic Note class containing a note name and textual content.
 * 
 * @author moorejm
 * 
 */
public class Note {
	private String title;
	private String content;
	private String noteID;

	public Note() {
		this.title = "New Note";
		this.content = "";
	}

	/**
	 * Constructs a note with the given name and a unique ID
	 * 
	 * @param name
	 */
	public Note(String title) {
		this();
		this.title = title;
	}

	public static Note create(int id, String title, String content) {
		Note n = new Note(title);
		n.setID(id);
		n.setContent(content);
		return n;
	}

	public static Note create(String id, String title, String content) {
		return Note.create(Integer.valueOf(id), title, content);
	}

	public String getID() {
		return this.noteID;
	}

	public void setID(int id) {
		this.noteID = Integer.toString(id);
	}

	public void setID(String id) {
		int testInt = Integer.parseInt(id);
		setID(testInt);
	}

	public String getName() {
		return this.title;
	}

	public void setName(String name) {
		this.title = name;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Concatenates the given text with the current text
	 * 
	 * @param text
	 */
	public void addContent(String text) {
		this.content += " " + text;
	}

}
