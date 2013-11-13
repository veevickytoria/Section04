package objects;

public class Version {
	private String date;
	private String editor;
	private static int counterID;
	int id;

	public Version() {
		counterID++;
		this.id = counterID;
	}

	public Version(String date, String editor) {
		this();
		this.date = date;
		this.editor = editor;
	}

	public String getDate() {
		return this.date;
	}

	public String getEditor() {
		return this.editor;
	}

}
