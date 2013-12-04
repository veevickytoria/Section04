package objects;

public class Task {
	private String name;
	private String content;
	// Unique ID for each Task
	private static int counterID;
	int id;

	public Task() {
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
	public Task(String name) {
		this();
		this.name = name;
	}

	/**
	 * Concatenates the given text with the current text
	 * 
	 * @param text
	 */
	public void addContent(String text) {
		this.content += " " + text;
	}

	/**
	 * Gets the content of this note
	 * 
	 * @return the content of this note
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Gets the name of this note
	 * 
	 * @return the name of this note
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the ID of this note
	 * 
	 * @return the ID of this note
	 */
	public int getID() {
		return this.id;
	}
}
