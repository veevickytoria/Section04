package objects;

/**
 * A basic Note class containing a note name and textual content.
 * 
 * @author moorejm
 * 
 */
public class Note {
	private String name;
	private String content;
	// Unique ID for each note
	private static int counterID;
	private int id;

	public Note() {
		counterID++;
		this.id = counterID;
		this.name = String.format("Note %d", id);
		this.content = new String();
	}

	/**
	 * Constructs a note with the given name and a unique ID
	 * 
	 * @param name
	 */
	public Note(String name) {
		this();
		this.name = name;
	}
	
	public static Note create(int id, String name, String content) {
		Note n = new Note(name);
		n.setID(id);
		n.setContent(content);
		return n;
	}

	/**
	 * Concatenates the given text with the current text
	 * 
	 * @param text
	 */
	public void addContent(String text) {
		this.content += " " + text;
	}

	public String getContent() {
		return this.content;
	}

	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setID(int id) {
		this.id = id;
	}

}
